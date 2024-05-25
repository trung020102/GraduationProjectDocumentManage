package com.doc.mamagement.utility.validation.document;

import com.doc.mamagement.document.DocumentRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;

public class Validator {
    @RequiredArgsConstructor
    public static class ValidDocumentCodesValidator implements ConstraintValidator<RuleAnnotation.ValidDocumentCodes, List<String>> {
        private final DocumentRepository documentRepository;

        @Override
        public boolean isValid(List<String> inputCodes, ConstraintValidatorContext context) {
            List<String> availableCodes = documentRepository.getAllCodes().collectList().block();
            if (availableCodes == null || availableCodes.isEmpty() || inputCodes == null || inputCodes.isEmpty())
                return true;

            return new HashSet<>(availableCodes).containsAll(inputCodes);
        }
    }

    @RequiredArgsConstructor
    public static class CheckDuplicateDocumentCodeValidator implements ConstraintValidator<RuleAnnotation.CheckDuplicateDocumentCode, String> {
        private final DocumentRepository documentRepository;

        @Override
        public boolean isValid(String inputCode, ConstraintValidatorContext context) {
            // Cách 1: Sử dụng repository để lấy toàn bộ document sau đó dùng map() để chuyển đổi thành string list
            //           List<Document> availableDocuments = documentRepository.findAll().collectList().block();
//            List<String> availableCodes = availableDocuments.stream().map(Document::getCode).toList();
            // Cách 2: Viết câu query lấy trưc tiếp string list từ data base
            List<String> availableCodes = documentRepository.getAllCodes().collectList().block();
            // Nếu trong db chưa có văn bản nào thì hợp lệ, không cần kiểm tra
            if (availableCodes == null || availableCodes.isEmpty())
                return true;
            // Kiểm tra nếu code nhập vào chưa tồn tại trong hệ thống thì hợp lệ
            return !availableCodes.contains(inputCode);
        }
    }

    @RequiredArgsConstructor
    public static class CheckExistedDocumentCodeValidator implements ConstraintValidator<RuleAnnotation.CheckExistedDocumentCode, String> {
        private final DocumentRepository documentRepository;

        @Override
        public boolean isValid(String inputCode, ConstraintValidatorContext context) {
            List<String> availableCodes = documentRepository.getAllCodes().collectList().block();
            // Nếu trong db chưa có văn bản nào thì hợp lệ, không cần kiểm tra
            if (availableCodes == null || availableCodes.isEmpty())
                return true;
            // Kiểm tra nếu code nhập vào có tồn tại trong hệ thống thì hợp lệ
            return availableCodes.contains(inputCode);
        }
    }
}
