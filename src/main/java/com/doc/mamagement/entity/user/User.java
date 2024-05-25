package com.doc.mamagement.entity.user;


import com.doc.mamagement.entity.RelationshipConstant;
import com.doc.mamagement.entity.document.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Node("User")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Property("username")
    private String username;

    @Property("password_hash")
    private String passwordHash;

    @Property("full_name")
    private String fullName;

    @Property("email")
    private String email;

    @Property("is_disabled")
    private Boolean isDisabled;

    @Property("avatar")
    private String avatar;

    @CreatedDate //TODO Not working
    @Property("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate //TODO Not working
    @Property("updated_at")
    private LocalDateTime updatedAt;

    @Relationship(type = RelationshipConstant.HAS_ROLE, direction = Relationship.Direction.OUTGOING)
    private Role role;

    // Quan hệ user có thể xem được các văn bản được cho phép
    @Relationship(type = RelationshipConstant.CAN_VIEW_DOCUMENTS, direction = Relationship.Direction.OUTGOING)
    private List<Document> possibleViewedDocuments;

    public User(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
