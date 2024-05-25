package com.doc.mamagement.document.schedule;

import com.doc.mamagement.document.DocumentService;
import com.doc.mamagement.document.PendingDocumentFileRepository;
import com.doc.mamagement.entity.document.PendingDocumentFile;
import com.doc.mamagement.utility.file_handling.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentFileSchedule {
    private final PendingDocumentFileRepository pendingDocumentFileRepository;
    private final StorageService storageService;

    /* Delete all old pending document in database and files in storage at midnight everyday */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional("transactionManager")
    public void deleteAllOldPendingDesignPhotos() {
        List<PendingDocumentFile> pendingDocumentFiles = pendingDocumentFileRepository
                .getAllByCreatedAtLessThan(LocalDateTime.now())
                .collectList()
                .block();

        if (pendingDocumentFiles == null || pendingDocumentFiles.isEmpty())
            return;

        for (PendingDocumentFile pendingDocumentFile: pendingDocumentFiles) {
            try {
                storageService.deleteByName(DocumentService.DOCUMENT_FILE_FOLDER, pendingDocumentFile.getUuidFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        pendingDocumentFileRepository.deleteAllByCreatedAtLessThan(LocalDateTime.now()).block();
    }
}
