package com.doc.mamagement.document;

import com.doc.mamagement.document.dto.DocumentDetailResult;
import com.doc.mamagement.document.dto.DocumentFilter;
import com.doc.mamagement.document.dto.DocumentParam;
import com.doc.mamagement.document.validation_group.DocumentCreation;
import com.doc.mamagement.document.validation_group.DocumentUpdating;
import com.doc.mamagement.security.RoleAuthorization;
import com.doc.mamagement.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import vn.rananu.spring.mvc.annotation.Result;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/documents")
public class DocumentAPI {
    private final DocumentService documentService;

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PostMapping
    /* Create new document */
    @Result(message = "message.success.document.create")
    public void createDocument(@Validated(DocumentCreation.class) @RequestBody DocumentParam documentParam) {
        documentService.createDocument(documentParam);
    }

    @RoleAuthorization.AuthenticatedUser
    @PostMapping("/getAllByFilter")
    /* Get document list by filter */
    /* Param 'categoryId' is to filter documents that in a selected sub-category (chosen in sidebar) */
    public ResponseEntity<?> getAllDocument(
            @RequestParam(required = false) Long categoryId,
            @RequestBody DocumentFilter documentFilter,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok().body(documentService.getAllDocument(categoryId, documentFilter, principal));
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping("/{code}")
    /* Get document detail */
    @Result
    public DocumentDetailResult getDocumentDetail(@PathVariable String code) {
        return documentService.getDocumentDetail(code);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PostMapping("/upload-file")
    /* Upload document main file or attached files */
    @Result
    public Object uploadDocumentFile(
            @RequestParam("isMainFile") Boolean isMainFile,
            @RequestParam("files") MultipartFile[] files
    ) {
        if (isMainFile)
            return documentService.saveMainDocumentFile(files[0]);

        return documentService.saveAttachedDocumentFiles(files);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PutMapping
    /* Update document */
    @Result(message = "message.success.document.update")
    public void updateDocument(@Validated(DocumentUpdating.class) @RequestBody DocumentParam documentParam) {
        documentService.updateDocument(documentParam);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @DeleteMapping("{code}")
    /* Delete permanently document */
    @Result(message = "message.success.document.delete")
    public void deleteDocument(@PathVariable String code, @RequestParam String confirmCode) {
        documentService.deleteByCode(code, confirmCode);
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping("/file/{fileName}")
    /* Get document file from storage */
    public ResponseEntity<Resource> getDocumentFile(@PathVariable("fileName") String fileName) {
        Resource resource = documentService.getDocumentFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
