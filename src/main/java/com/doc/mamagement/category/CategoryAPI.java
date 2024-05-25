package com.doc.mamagement.category;

import com.doc.mamagement.category.dto.CategoryParam;
import com.doc.mamagement.category.dto.CategoryResult;
import com.doc.mamagement.category.dto.SubCategoryParam;
import com.doc.mamagement.category.dto.SubCategoryResult;
import com.doc.mamagement.security.RoleAuthorization;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.rananu.spring.mvc.annotation.Result;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/categories")
public class CategoryAPI {
    private final CategoryService categoryService;

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PostMapping
    /* Create new category */
    @Result(message = "message.success.category.create")
    public CategoryResult createNew(@Valid @RequestBody CategoryParam categoryParam) {
        return categoryService.create(categoryParam);
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping("/getAllParents")
    /* Get all categories with sub-categories */
    @Result
    public List<CategoryResult> getAllParentCategory() {
        return categoryService.getAllParentCategory();
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PutMapping("/{id}")
    /* Update category by ID */
    @Result(message = "message.success.category.update")
    public CategoryResult update(@Valid @RequestBody CategoryParam categoryParam) {
        return categoryService.update(categoryParam);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @DeleteMapping("/{id}")
    /* Delete category by ID */
    @Result(message = "message.success.category.delete")
    public void delete(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PostMapping("/create-sub-category")
    /* Create new sub-category */
    @Result(message = "message.success.category.create")
    public SubCategoryResult createNewSubCategory(@Valid @RequestBody SubCategoryParam subCategoryParam) {
        return categoryService.createSubCategory(subCategoryParam);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @PutMapping("/update-sub-category")
    /* Create new sub-category */
    @Result(message = "message.success.category.update")
    public SubCategoryResult updateSubCategory(@Valid @RequestBody SubCategoryParam subCategoryParam) {
        return categoryService.updateSubCategory(subCategoryParam);
    }

    @RoleAuthorization.AdminAndModeratorAuthorization
    @DeleteMapping("/delete-sub-category/{id}")
    /* Delete sub-category by ID */
    @Result(message = "message.success.category.delete")
    public void deleteSubCategory(@PathVariable Long id) {
        categoryService.deleteSubCategory(id);
    }
}
