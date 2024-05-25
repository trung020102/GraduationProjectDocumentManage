package com.doc.mamagement.web;

import com.doc.mamagement.category.CategoryService;
import com.doc.mamagement.category.dto.CategoryResult;
import com.doc.mamagement.document.DocumentService;
import com.doc.mamagement.document.dto.DocumentSelection;
import com.doc.mamagement.user.UserService;
import com.doc.mamagement.user.dto.UserSelection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final DocumentService documentService;

    @ModelAttribute("categories")
    public List<CategoryResult> getAllCategories() {
        return categoryService.getAllParentCategory();
    }

    @ModelAttribute("documentSelection")
    public List<DocumentSelection> getAllDocumentsForSelection() {
        return documentService.getAllForSelection();
    }

    @ModelAttribute("userSelection")
    public List<UserSelection> getAllUsersForSelection() {
        return userService.getAllUserSelection();
    }

    @GetMapping("/manage")
    public ModelAndView toAdminManageDocument() {
        return new ModelAndView("document/manage");
    }

    @GetMapping("/create")
    public ModelAndView toAdminCreateDocument() {
        return new ModelAndView("document/create");
    }

    @GetMapping("/edit/{code}")
    public ModelAndView toAdminEditDocument(@PathVariable String code) {
        ModelAndView modelAndView = new ModelAndView("document/edit");
        modelAndView.addObject("document", documentService.getDocumentForEditing(code));
        return modelAndView;
    }
}
