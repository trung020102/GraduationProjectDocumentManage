package com.doc.mamagement.entity.document;

import com.doc.mamagement.document.dto.DocumentFile;
import com.doc.mamagement.entity.RelationshipConstant;
import com.doc.mamagement.entity.category.Category;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.utility.json_converter.DocumentFileConverter;
import com.doc.mamagement.utility.json_converter.DocumentFileListConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Node("Document")
public class Document {
    //mã số
    @Id
    @Property("code")
    private String code;

    //Ký hiệu
    @Property("number_sign")
    private String numberSign;

    //Ngày ban hành
    @Property("issued_date")
    private LocalDate issuedDate;

    //Độ khẩn
    @Property("urgency_level")
    private String urgencyLevel;

    //Độ mật
    @Property("security_level")
    private String securityLevel;

    //Số trang
    @Property("page_number")
    private int pageNumber;

    //Tên văn bản
    @Property("title")
    private String title;

    //File văn bản chính
    @ConvertWith(converter = DocumentFileConverter.class)
    @Property("main_document_file")
    private DocumentFile mainDocumentFile;

    //File văn bản đính kèm
    @ConvertWith(converter = DocumentFileListConverter.class)
    @Property("attached_document_files")
    private List<DocumentFile> attachedDocumentFiles;

    //Người ký
    @Property("signer")
    private String signer;

    //Nơi nhận
    @Property("recipient")
    private String recipient;

    @CreatedDate //TODO Not working
    @Property("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate //TODO Not working
    @Property("updated_at")
    private LocalDateTime updatedAt;

    //Các văn bản liên quan
    @Relationship(type = RelationshipConstant.RELATES_WITH, direction = Relationship.Direction.OUTGOING)
    private List<Document> relatedDocuments;

    //Quan hệ với các danh mục con trong Loại văn bản, Cơ quan ban hành, Lĩnh vực hoạt động,...
    @Relationship(type = RelationshipConstant.IN_SUB_CATEGORIES, direction = Relationship.Direction.OUTGOING)
    private List<Category> subCategories;

    //Quan hệ từ danh mục con trong Loại văn bản, Cơ quan ban hành, Lĩnh vực hoạt động,.., đến văn bản
    @Relationship(type = RelationshipConstant.CONTAINS_DOCUMENTS, direction = Relationship.Direction.INCOMING)
    private List<Category> incomingSubCategories;

    //Quan hệ với các user được xem văn bản
    @Relationship(type = RelationshipConstant.HAS_VIEWERS, direction = Relationship.Direction.OUTGOING)
    private List<User> viewers;
}