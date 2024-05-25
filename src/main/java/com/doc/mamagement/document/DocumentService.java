package com.doc.mamagement.document;

import com.doc.mamagement.category.CategoryRepository;
import com.doc.mamagement.document.dto.*;
import com.doc.mamagement.entity.category.Category;
import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.entity.document.PendingDocumentFile;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.security.UserPrincipal;
import com.doc.mamagement.user.UserRepository;
import com.doc.mamagement.utility.file_handling.StorageService;
import com.doc.mamagement.utility.datatable.DatatableResult;
import com.doc.mamagement.web.exception.UnprocessableException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentMapper documentMapper;
    private final DocumentRepository documentRepository;
    private final PendingDocumentFileRepository pendingDocumentFileRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public static final String DOCUMENT_FILE_FOLDER = "document_file";

    @Transactional("transactionManager")
    public void createDocument(DocumentParam documentParam) {
        Document newDocument = documentMapper.toEntity(documentParam);
        setDocumentOutgoingRelationships(newDocument, documentParam);

        newDocument.setCreatedAt(LocalDateTime.now());

        documentRepository.save(newDocument).block();

        deletePendingDocumentFiles(documentParam);
    }

    @Transactional("transactionManager")
    public void updateDocument(DocumentParam documentForUpdating) {
        Document updatedDocument = documentMapper.toEntity(documentForUpdating);
        documentRepository.deleteAllRelationship(documentForUpdating.getCode()).subscribe();
        setDocumentOutgoingRelationships(updatedDocument, documentForUpdating);
        updatedDocument.setUpdatedAt(LocalDateTime.now());

        documentRepository.save(updatedDocument).block();

        deletePendingDocumentFiles(documentForUpdating);
    }

    private void setDocumentOutgoingRelationships(Document document, DocumentParam documentParam) {
        // Tạo quan hệ với các danh mục con
        List<Category> subCategories = categoryRepository
                .getAllByIdIn(documentParam.getSubCategoryIds())
                .collectList()
                .block();
        document.setSubCategories(subCategories);
        document.setIncomingSubCategories(subCategories);

        // Tạo quan hệ với các văn bản liên quan khi relatedDocumentCodes không rỗng
        List<String> relatedDocumentCodes = documentParam.getRelatedDocumentCodes();
        if (!relatedDocumentCodes.isEmpty()) {
            List<Document> relatedDocuments = documentRepository
                    .getAllByCodeIn(documentParam.getRelatedDocumentCodes())
                    .collectList()
                    .block();
            document.setRelatedDocuments(relatedDocuments);
        }

        //Tạo quan hệ với các User đc xem văn bản
        List<User> usersCanReadDoc = userRepository
                .getAllByIdIn(documentParam.getViewers())
                .collectList()
                .block();
        document.setViewers(usersCanReadDoc);
    }

    /* Xóa các node có uuid file name (file văn bản chính và đính kèm) tương ứng trong 'PendingDocumentFile'
        sau khi tạo thành công văn bản mới */
    private void deletePendingDocumentFiles(DocumentParam documentParam) {
        List<String> pendingFileNames = new ArrayList<>();
        pendingFileNames.add(documentParam.getMainDocumentFile().getUuidFileName());
        List<DocumentFile> documentFiles = documentParam.getAttachedDocumentFiles();
        if (documentFiles != null)
            pendingFileNames.addAll(documentFiles.stream().map(DocumentFile::getUuidFileName).toList());
        pendingDocumentFileRepository.deleteAllByUuidFileNameIn(pendingFileNames).block();
    }

    private void addNewDocumentToCategories(List<Category> subCategories, Document newDocument) {
        for (Category subCategory : subCategories) {
            subCategory.getDocuments().add(newDocument);
        }
        categoryRepository.saveAll(subCategories).subscribe();
    }

    private void addNewDocumentToViewers(List<User> viewers, Document newDocument) {
        for (User viewer : viewers) {
            viewer.getPossibleViewedDocuments().add(newDocument);
        }
        userRepository.saveAll(viewers).subscribe();
    }

    public List<DocumentSelection> getAllForSelection() {
        List<Document> documents = documentRepository.findAll().collectList().block();
        return documents == null || documents.isEmpty()
                ? null
                : documents.stream().map(documentMapper::toDocumentSelection).toList();
    }

    public DatatableResult<DocumentResult> getAllDocument(Long categoryId, DocumentFilter documentFilter, UserPrincipal userPrincipal) {
        List<Document> documents = documentRepository.findAllByOrderByCreatedAtDesc().collectList().block();

        if (documents == null || documents.isEmpty())
            return new DatatableResult<>();

        if (userPrincipal.isNormalUser())
            documents = filterByUserRole(documents, userPrincipal.getUsername());

        String keyword = documentFilter.getKeyword().toLowerCase();
        LocalDate startDate = documentFilter.getStartDate();
        LocalDate endDate = documentFilter.getEndDate();
        documents = documents
                .stream()
                .filter(document -> {
                    boolean matchTitle = document.getTitle().toLowerCase().contains(keyword);
                    boolean matchNumberSign = document.getNumberSign().toLowerCase().contains(keyword);
                    boolean matchDateTime = document.getIssuedDate().isAfter(startDate.minusDays(1))
                            && document.getIssuedDate().isBefore(endDate.plusDays(1));
                    boolean matchSubCategory = categoryId == null || document.getSubCategories().contains(new Category(categoryId));

                    return matchDateTime && (matchNumberSign || matchTitle) && matchSubCategory;
                })
                .toList();
        List<DocumentResult> documentResults = documents.stream().map(documentMapper::toDTO).toList();

        /* Calculate the size of each page and get page data from the userInfoResults according to the size */
        int start = documentFilter.getStart();
        int length = documentFilter.getLength();
        int page = start / length;
        Pageable pageRequest = PageRequest.of(page, length);
        int end = Math.min((start + pageRequest.getPageSize()), documentResults.size());
        List<DocumentResult> pageContent = documentResults.subList(start, end);

        /* Response data for datatable rendering */
        DatatableResult<DocumentResult> datatableResult = new DatatableResult<>();
        datatableResult.setData(pageContent);
        datatableResult.setDraw(documentFilter.getDraw());
        Long totalRecord = (long) documentResults.size();
        datatableResult.setRecordsFiltered(totalRecord);
        datatableResult.setRecordsTotal(totalRecord);

        return datatableResult;
    }

    private List<Document> filterByUserRole(List<Document> documents, String username) {
        return documents.stream()
                .filter(document -> document.getViewers().contains(new User(username)))
                .toList();
    }

    public DocumentFile saveMainDocumentFile(MultipartFile mainFile) {
        String originalFileName = mainFile.getOriginalFilename();
        String uuidFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(originalFileName);

        storageService.store(mainFile, DOCUMENT_FILE_FOLDER, uuidFileName);

        pendingDocumentFileRepository.save(new PendingDocumentFile(uuidFileName)).block();

        return new DocumentFile(originalFileName, uuidFileName);
    }

    public List<DocumentFile> saveAttachedDocumentFiles(MultipartFile[] attachedFiles) {
        List<PendingDocumentFile> pendingDocumentFiles = new ArrayList<>();
        List<DocumentFile> documentFiles = new ArrayList<>();

        for (MultipartFile attachedFile : attachedFiles) {
            String originalFileName = attachedFile.getOriginalFilename();
            String uuidFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(originalFileName);

            storageService.store(attachedFile, DOCUMENT_FILE_FOLDER, uuidFileName);

            pendingDocumentFiles.add(new PendingDocumentFile(uuidFileName));
            documentFiles.add(new DocumentFile(originalFileName, uuidFileName));
        }

        pendingDocumentFileRepository.saveAll(pendingDocumentFiles).subscribe();

        return documentFiles;
    }

    private Document findByCode(String code) {
        return documentRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new UnprocessableException("validation.notExisted.documentCode")))
                .block();
    }

    public DocumentDetailResult getDocumentDetail(String code) {
        Document document = findByCode(code);
        return documentMapper.toDocumentResultDetail(document);
    }

    public DocumentForEditResult getDocumentForEditing(String code) {
        Document document = findByCode(code);
        return documentMapper.toDocumentForEditResult(document);
    }

    @Transactional("transactionManager")
    public void deleteByCode(String code, String confirmCode) {
        if (!code.equals(confirmCode))
            throw new UnprocessableException("validation.invalid.confirmCode");

        Document document = findByCode(code);
        documentRepository.deleteAllRelationship(code).subscribe();
        documentRepository.delete(document).subscribe();
    }

    public Resource getDocumentFile(String fileName) {
        return storageService.loadAsResource(fileName, DOCUMENT_FILE_FOLDER);
    }
}
