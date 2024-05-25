package com.doc.mamagement.category;

import com.doc.mamagement.category.dto.CategoryParam;
import com.doc.mamagement.category.dto.CategoryResult;
import com.doc.mamagement.category.dto.SubCategoryParam;
import com.doc.mamagement.category.dto.SubCategoryResult;
import com.doc.mamagement.entity.category.Category;
import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.utility.comparator.StringComparator;
import com.doc.mamagement.web.exception.UnprocessableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResult create(CategoryParam newCategory) {
        Category category = categoryMapper.toEntity(newCategory);
        category = categoryRepository.save(category).block();

        return categoryMapper.toDTO(category);
    }

    public List<CategoryResult> getAllParentCategory() {
        List<Category> categories = categoryRepository.findAllByIsParentTrue().collectList().block();

        return categories != null
                ? categories
                    .stream()
                    .map(parentCategory -> {
                        CategoryResult categoryResult = categoryMapper.toDTO(parentCategory);
                        /* Convert subCategories in every category to subCategoryResults */
                        List<SubCategoryResult> subCategoryResults = toSubCategoryResultList(
                                parentCategory.getSubCategories(),
                                parentCategory.getId(),
                                null
                        );
                        categoryResult.setSubCategories(subCategoryResults);
                        /* Sum document count of all sub-categories */
                        int documentCount = subCategoryResults != null
                                ? subCategoryResults
                                    .stream()
                                    .mapToInt(SubCategoryResult::getDocumentCount)
                                    .sum()
                                : 0;
                        categoryResult.setDocumentCount(documentCount);
                        return categoryResult;
                    })
                    /* Ascending sort category list by title */
                    .sorted((o1, o2) -> StringComparator.compareVietnameseString(o1.getTitle(), o2.getTitle()))
                    .toList()
                : null;
    }

    public CategoryResult update(CategoryParam categoryParam) {
        Category parentCategory = findByIdFromRepo(categoryParam.getId());
        parentCategory.setTitle(categoryParam.getTitle());
        parentCategory = categoryRepository.save(parentCategory).block();

        List<SubCategoryResult> categoryResults = toSubCategoryResultList(parentCategory.getSubCategories(), parentCategory.getId(), parentCategory.getTitle());
        CategoryResult categoryResult =  categoryMapper.toDTO(parentCategory);
        categoryResult.setSubCategories(categoryResults);

        return categoryResult;
    }

    public List<SubCategoryResult> toSubCategoryResultList(List<Category> subCategories, Long parentId, String parentTitle) {
        return subCategories != null
                ? subCategories
                    .stream()
                    .map(subCategory -> categoryMapper.toSubCategoryResult(subCategory, parentId, parentTitle))
                    /* Ascending sort category list by title */
                    .sorted((o1, o2) -> StringComparator.compareVietnameseString(o1.getTitle(), o2.getTitle()))
                    .toList()
                : null;
    }

    public Category findByIdFromRepo(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new UnprocessableException("validation.noExisted.categoryId")))
                .block();
    }

    public void deleteById(Long id) {
        Category parentCategory = findByIdFromRepo(id);
        if (!parentCategory.getSubCategories().isEmpty())
            throw new UnprocessableException("message.error.category.notEmpty");

        categoryRepository.deleteById(id).block();
    }

    public SubCategoryResult createSubCategory(SubCategoryParam subCategoryParam) {
        Category parentCategory = findByIdFromRepo(subCategoryParam.getParentId());
        Category subCategory = categoryMapper.toEntity(subCategoryParam);
        subCategory.setParentCategory(parentCategory);
        parentCategory.getSubCategories().add(subCategory);

        categoryRepository.save(parentCategory).block();

        return categoryMapper.toSubCategoryResult(subCategory, parentCategory.getId(), subCategoryParam.getTitle());
    }

    public SubCategoryResult updateSubCategory(SubCategoryParam subCategoryParam) {
        Category subCategory = categoryMapper.toEntity(subCategoryParam);
        subCategory = categoryRepository.save(subCategory).block();

        return categoryMapper.toSubCategoryResult(subCategory, subCategoryParam.getParentId(), null);
    }

    public void deleteSubCategory(Long id) {
        Category subCategory = findByIdFromRepo(id);
        if (subCategory.getIsParent())
            throw new UnprocessableException("message.error.category.notSubCategory");

        List<Document> documentsInside = subCategory.getDocuments();
        if (!documentsInside.isEmpty())
            throw new UnprocessableException("message.error.documentInside.notEmpty");

        categoryRepository.delete(subCategory).block();
    }
}
