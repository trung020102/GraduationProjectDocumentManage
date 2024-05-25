package com.doc.mamagement.entity.category;

import com.doc.mamagement.entity.RelationshipConstant;
import com.doc.mamagement.entity.document.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Node("Category")
public class Category {
    @Id
    @GeneratedValue
    private Long id;

    @Property("title")
    private String title;

    @Property("is_parent")
    private Boolean isParent;

    @Relationship(type = RelationshipConstant.IS_PARENT_CATEGORY_OF, direction = Relationship.Direction.OUTGOING)
    private List<Category> subCategories;

    @Relationship(type = RelationshipConstant.IS_SUB_CATEGORY_OF, direction = Relationship.Direction.OUTGOING)
    private Category parentCategory;

    @Relationship(type = RelationshipConstant.CONTAINS_DOCUMENTS, direction = Relationship.Direction.OUTGOING)
    private List<Document> documents;

    public Category(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
