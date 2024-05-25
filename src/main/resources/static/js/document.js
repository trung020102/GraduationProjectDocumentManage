import {App, DatatableAttribute, LocalStorage} from "./app.js";
import {FormHandler} from "./form.js";
import {TimeHelper} from "./time_helper.js";
import {DocumentDetailComponent} from "./component.js";
import {Url} from "./url.js";

export const DocumentConstant = (function () {
    return {
        mainDocumentFileData: 'main_document_file_data',
        attachedDocumentFileData: 'attached_document_file_data',
    };
})();

export const DocumentCreation = (function () {
    const module = {
        createNewDocumentUrl: '/api/documents',
        uploadDocumentFileUrl: '/api/documents/upload-file?isMainFile={0}',

        mainDocumentFileSelector: $('#main-document-file'),
        attachedDocumentFileSelector: $('#attached-document-file'),
        pageNumberSelector: $('#number-page'),

        idDocumentSelector: $('#id-document'),
        numberSymbolSelector: $('#number-symbol'),
        dateIssuedSelector: $('#date-issued'),
        documentNameSelector: $('#document-name'),
        documentRelatedSelector: $('#document-related'),
        signerSelector: $('#signer'),
        recipientsSelector: $('#recipients'),
        viewerSelector: $('#viewer'),
        urgencyLevelSelector: $('#urgency-level'),
        securityLevelSelector: $('#security-level'),
        categoriesSelector: $('select[name=categories]'),

        btnCreateDocumentSelector: $('#btn-create-document'),
        btnResetDocumentSelector: $('#btn-reset-document'),
        createDocumentAreaSelector: $('#create-document-area')
    }

    module.init = () => {
        module.handleMainDocumentFileUploading();
        module.handleAttachedDocumentFileUploading();
        handleCreateDocumentButton();
        handleResetDocumentButton();
    };

    const handleResetDocumentButton = () => {
        module.btnResetDocumentSelector.on('click', function () {
            FormHandler.clearAllInputs(module.createDocumentAreaSelector);
        })
    }

    const handleCreateDocumentButton = () => {
        module.btnCreateDocumentSelector.on('click', function () {
            const documentParam = module.getDocumentParam();
            createNewDocument(documentParam);
        })
    }

    module.getDocumentParam = () =>
        ({
            code: module.idDocumentSelector.val().trim().toUpperCase(),
            numberSign: module.numberSymbolSelector.val().trim().toUpperCase(),
            issuedDate: module.dateIssuedSelector.val(),
            urgencyLevel: module.urgencyLevelSelector.val(),
            securityLevel: module.securityLevelSelector.val(),
            subCategoryIds: getAllSubCategoryIds(),
            title: module.documentNameSelector.val().trim(),
            relatedDocumentCodes: module.documentRelatedSelector.val(),
            signer: module.signerSelector.val().trim(),
            recipient: module.recipientsSelector.val().trim(),
            viewers: module.viewerSelector.val(),
            pageNumber: module.pageNumberSelector.val(),
            mainDocumentFile: LocalStorage.getJsonItem(DocumentConstant.mainDocumentFileData),
            attachedDocumentFiles: LocalStorage.getJsonItem(DocumentConstant.attachedDocumentFileData),
        });

    const getAllSubCategoryIds = () => {
        const subCategoryIds = [];
        module.categoriesSelector.each((index, item) => subCategoryIds.push($(item).val()));
        /* Remove empty element from array by using filter(Boolean) */
        return subCategoryIds.filter(Boolean);
    }

    const createNewDocument = (documentParam) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'POST',
            url: module.createNewDocumentUrl,
            data: JSON.stringify(documentParam),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                FormHandler.clearAllInputs(module.createDocumentAreaSelector);
                window.location.href = Url.web.manageDocument;
                /* Remove all document file uploading data after successful creation */
                LocalStorage.removeAll();
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.createDocumentAreaSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    module.handleMainDocumentFileUploading = () => {
        module.mainDocumentFileSelector.on('change', function (e) {
            /* Count main document file page number */
            const file = e.target.files[0]
            if (file.type != "application/pdf") {
                App.showErrorMessage(choosePdfFileOnly);
                return;
            }

            const fileReader = new FileReader();

            fileReader.onload = function () {
                const typedarray = new Uint8Array(this.result);

                pdfjsLib.getDocument(typedarray).then(function (pdf) {
                    module.pageNumberSelector.val(pdf.numPages);
                });
            };

            fileReader.readAsArrayBuffer(file);

            /* Upload main category file */
            let formData = new FormData();
            formData.append('files', file);

            uploadDocumentFile(formData, true).then((data) => {
                /* Save main document file response data to local storage */
                LocalStorage.removeItem(DocumentConstant.mainDocumentFileData);
                LocalStorage.setJsonItem(DocumentConstant.mainDocumentFileData, data);
            });

            /* This statement is for document updating page */
            DocumentUpdating.oldMainDocumentFileAreaSelector.remove();
        });
    }

    module.handleAttachedDocumentFileUploading = () => {
        module.attachedDocumentFileSelector.on('change', function (e) {
            const files = e.target.files;
            let formData = new FormData();
            for (const fileElement of files) {
                formData.append('files', fileElement);
            }

            uploadDocumentFile(formData, false).then((data) => {
                /* Save attached document file response data to local storage */
                LocalStorage.removeItem(DocumentConstant.attachedDocumentFileData);
                LocalStorage.setJsonItem(DocumentConstant.attachedDocumentFileData, data);
            });

            /* This statement is for document updating page */
            DocumentUpdating.oldAttachedDocumentFileAreaSelector.remove();
        });
    }

    const uploadDocumentFile = (formData, isMainFile) => {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: module.uploadDocumentFileUrl.replace('{0}', isMainFile),
                data: formData,
                type: 'POST',
                processData: false,
                mimeType: 'multipart/form-data',
                contentType: false,
                dataType: 'json',
            })
                .done((response) => {
                    resolve(response.data);
                })
                .fail((jqXHR) => {
                    FormHandler.handleServerValidationError(module.createDocumentAreaSelector, jqXHR)
                    App.handleResponseMessageByStatusCode(jqXHR);
                    reject(jqXHR);
                })
        });
    }

    return module;
})();

export const DocumentDetail = (function () {
    const module = {
        getDocumentDetailUrl: '/api/documents/{id}',
        getDocumentFileUrl: '/api/documents/file/{fileName}',

        modalDetailDocumentSelector: $('#document-detail-modal'),
        documentCodeSelector: $('.document-code'),
        numberSignSelector: $('.number-sign'),
        createdAtSelector: $('.created-at'),
        issuedDateSelector: $('.issued-date'),
        documentNameSelector: $('.document-name'),
        urgencyLevelSelector: $('.urgency-level'),
        securityLevelSelector: $('.security-level'),
        categoriesSelector: $('.categories'),
        signerSelector: $('.signer'),
        recipientSelector: $('.recipient'),
        mainDocumentFileSelector: $('.main-document-file'),
        attachedDocumentFileSelector: $('.attached-document-file'),
        relatedDocumentSelector: $('.related-document'),
        pageNumberSelector: $('.page-number'),
    };

    module.init = () => {
        const tableSelector = isAdminOrModerator ? DocumentList.documentListTableSelector : DocumentList.userDocumentListTableSelector;
        handleDocumentDetailButton(tableSelector);
    }

    //Mở Modal detail document
    const handleDocumentDetailButton = (tableSelector) => {
        tableSelector.on('click', '.open-document-detail', function () {
            const documentCode = tableSelector.DataTable().row($(this).closest('tr')).data().code;
            /* Wait for the ajax event */
            getDocumentDetail(documentCode).then(() => module.modalDetailDocumentSelector.modal('show'));
        })
    }

    const getDocumentDetail = (code) => {
        return $.ajax({
            url: module.getDocumentDetailUrl.replace('{id}', code),
        })
            .done((response) => {
                renderDocumentDetail(response.data)
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const renderDocumentDetail = (data) => {
        module.documentCodeSelector.text(data.code);
        module.numberSignSelector.text(data.numberSign);
        const modifiedCreatedAt = TimeHelper.convertDateTimeToPattern(data.createdAt, TimeHelper.pattern.dayMonthYearWithSlash);
        module.createdAtSelector.text(modifiedCreatedAt);
        const modifiedIssuedDate = TimeHelper.convertDateTimeToPattern(data.issuedDate, TimeHelper.pattern.dayMonthYearWithSlash);
        module.issuedDateSelector.text(modifiedIssuedDate);
        module.documentNameSelector.text(data.title);
        module.urgencyLevelSelector.text(data.urgencyLevel);
        module.securityLevelSelector.text(data.securityLevel);
        /* Render category row after security level row */
        module.securityLevelSelector.parent('tr').after(createCategoryRow(data.categories));
        module.signerSelector.text(data.signer);
        module.recipientSelector.text(data.recipient);
        module.mainDocumentFileSelector.html(createDocumentFileLink(data.mainDocumentFile));
        module.attachedDocumentFileSelector.html(createDocumentFileLinkList(data.attachedDocumentFiles));
        /* Modify the related document data to reuse createDocumentFileLinkList() */
        const relatedDocuments = modifyRelatedDocumentData(data.relatedDocuments);
        module.relatedDocumentSelector.html(createDocumentFileLinkList(relatedDocuments));
        module.pageNumberSelector.text(data.pageNumber);
    }

    const modifyRelatedDocumentData = (relatedDocumentData) => {
        return relatedDocumentData.map(item => ({
            originalFileName: item.title,
            uuidFileName: item.mainDocumentFile.uuidFileName,
        }));
    }

    const createDocumentFileLink = (documentFileData) => {
        const link = module.getDocumentFileUrl.replace('{fileName}', documentFileData.uuidFileName);

        return DocumentDetailComponent.documentFileLink(link, documentFileData.originalFileName);
    }

    const createDocumentFileLinkList = (documentFileDataList) => {
        let documentFileLinks = '';

        /* Do not handle when documentFileDataList is null or empty */
        if (!documentFileDataList)
            return;

        for (const documentFileData of documentFileDataList) {
            documentFileLinks += createDocumentFileLink(documentFileData);
        }

        return documentFileLinks;
    }

    const createCategoryRow = (categoryData) => {
        let categoryRows = '';
        for (const category of categoryData) {
            categoryRows += DocumentDetailComponent.categoryRow(category);
        }

        return categoryRows;
    }

    return module;
})()

export const DocumentList = (function () {
    const module = {
        /* Use window.location.search in order to add params from current URL (categoryID) */
        findAllDocumentsByFilterUrl: "/api/documents/getAllByFilter" + window.location.search,
        deleteDocumentUrl: "/api/documents/{id}",
        editDocumentPageUrl: '/document/edit/{code}',

        documentListTableSelector: $('#document-list-table'),
        userDocumentListTableSelector: $('#user-document-list-table'),
        keywordSelector: $('#keyword'),
        startDateSelector: $('#start-date'),
        endDateSelector: $('#end-date'),
        btnFilterDocumentSelector: $('#filter-document'),

        deleteDocumentConfirmModalSelector: $('#delete-document-confirm-modal'),
        confirmDeleteDocumentCodeSelector: $('#confirm-delete-document-code'),
        deleteDocumentCodeSelector: $('#delete-document-code'),
        confirmDeleteDocumentButtonSelector: $('#btn-submit-document-delete'),
    }

    module.init = () => {
        if (isAdminOrModerator)
            renderDocumentListTable();
        else
            renderUserDocumentListTable();

        handleFilterDocumentButton();
        handleOpenDeleteDocumentModal();
        handleConfirmDeleteDocumentButton();
        handleToEditDocumentPageButton();
    }

    const renderDocumentListTable = () => {
        const documentFilter = getDocumentFilter();

        const documentListDatatable = module.documentListTableSelector.DataTable({
            ajax: {
                contentType: 'application/json',
                type: 'POST',
                url: module.findAllDocumentsByFilterUrl,
                data: function (d) {
                    d.keyword = documentFilter.keyword;
                    d.startDate = documentFilter.startDate;
                    d.endDate = documentFilter.endDate;
                    return JSON.stringify(d);
                },
            },
            columns: [
                {data: null},
                {data: 'code'},
                {data: 'numberSign'},
                {data: 'issuedDate'},
                {data: 'title'},
                {data: null},
            ],
            serverSide: true,
            bJQueryUI: true,
            destroy: true,
            paging: true,
            searching: false,
            lengthChange: false,
            info: false,
            ordering: false,
            pageLength: 10, // Set the default records displaying on each page
            pagingType: 'simple_numbers',
            columnDefs: [
                {
                    targets: [0, 1, 2, 3, 5],
                    className: 'align-middle text-center w-auto'
                },
                {
                    targets: 3,
                    render: data => TimeHelper.convertDateTimeToPattern(data, TimeHelper.pattern.dayMonthYearWithSlash)
                },
                {
                    targets: 4,
                    className: 'text-justify w-50',
                    render: data => DocumentDetailComponent.documentTitleInTable(data),
                },
                {
                    targets: 5,
                    className: "text-center",
                    render: () =>
                        `<div class="btn-group">
                            <button class="btn-to-edit-document-page btn btn-sm btn-outline-primary border-0">
                                <i class="fa fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger border-0"
                                    data-toggle="modal" data-target="#delete-document-confirm-modal">
                                <i class="fa fa-trash-alt"></i>
                            </button>
                        </div>`
                },
            ],
            language: DatatableAttribute.language
        });

        DatatableAttribute.renderOrdinalColumn(documentListDatatable);
    }

    const renderUserDocumentListTable = () => {
        const documentFilter = getDocumentFilter();

        module.documentListTableSelector.prop('hidden', true);
        module.userDocumentListTableSelector.prop('hidden', false);

        const userDocumentListDatatable = module.userDocumentListTableSelector.DataTable({
            ajax: {
                contentType: 'application/json',
                type: 'POST',
                url: module.findAllDocumentsByFilterUrl,
                data: function (d) {
                    d.keyword = documentFilter.keyword;
                    d.startDate = documentFilter.startDate;
                    d.endDate = documentFilter.endDate;
                    return JSON.stringify(d);
                },
            },
            columns: [
                {data: null},
                {data: 'numberSign'},
                {data: 'issuedDate'},
                {data: 'title'},
            ],
            serverSide: true,
            bJQueryUI: true,
            destroy: true,
            paging: true,
            searching: false,
            lengthChange: false,
            info: false,
            ordering: false,
            pageLength: 10, // Set the default records displaying on each page
            pagingType: 'simple_numbers',
            columnDefs: [
                {
                    targets: [0, 1, 2],
                    className: 'align-middle text-center w-auto'
                },
                {
                    targets: 2,
                    render: data => TimeHelper.convertDateTimeToPattern(data, TimeHelper.pattern.dayMonthYearWithSlash)
                },
                {
                    targets: 3,
                    className: 'text-justify',
                    render: data => DocumentDetailComponent.documentTitleInTable(data),
                }
            ],
            language: DatatableAttribute.language
        });

        DatatableAttribute.renderOrdinalColumn(userDocumentListDatatable);
    }

    const handleToEditDocumentPageButton = () => {
        module.documentListTableSelector.on('click', '.btn-to-edit-document-page', function () {
            const documentCode = module.documentListTableSelector.DataTable().row($(this).closest('tr')).data().code;
            window.location.href = module.editDocumentPageUrl.replace('{code}', documentCode);
        });
    }

    const handleOpenDeleteDocumentModal = () => {
        module.deleteDocumentConfirmModalSelector.on('show.bs.modal', function (e) {
            const openDeleteDocumentModalButton = e.relatedTarget;
            const documentCode = DocumentList.documentListTableSelector
                .DataTable().row($(openDeleteDocumentModalButton).closest('tr'))
                .data().code;
            module.deleteDocumentCodeSelector.val(documentCode);
        });
    }

    const handleConfirmDeleteDocumentButton = () => {
        module.confirmDeleteDocumentButtonSelector.on('click', function () {
            const documentCode = module.deleteDocumentCodeSelector.val();
            const confirmDocumentCode = module.confirmDeleteDocumentCodeSelector.val().trim();
            deleteDocument(documentCode, confirmDocumentCode);
        });
    }

    const deleteDocument = (documentCode, confirmDocumentCode) => {
        $.ajax({
            type: 'DELETE',
            data: {
                confirmCode: confirmDocumentCode
            },
            url: module.deleteDocumentUrl.replace('{id}', documentCode),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                location.reload();
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const getDocumentFilter = () => {
        /* Set default value for startDate and endDate if client doesn't select any value */
        const startDate = module.startDateSelector.val() ? module.startDateSelector.val() : '01/01/2000'
        const endDate = module.endDateSelector.val()
            ? module.endDateSelector.val()
            : TimeHelper.timeNowToPattern(TimeHelper.pattern.monthDayYearWithSlash);

        return {
            keyword: module.keywordSelector.val().trim(),
            startDate: TimeHelper.convertDateTimeStringToPattern(
                startDate,
                TimeHelper.pattern.monthDayYearWithSlash,
                TimeHelper.pattern.yearMonthDayWithHyphen
            ),
            endDate: TimeHelper.convertDateTimeStringToPattern(
                endDate,
                TimeHelper.pattern.monthDayYearWithSlash,
                TimeHelper.pattern.yearMonthDayWithHyphen
            )
        }
    }

    const handleFilterDocumentButton = () => {
        module.btnFilterDocumentSelector.on('click', function () {
            if (isAdminOrModerator)
                renderDocumentListTable();
            else
                renderUserDocumentListTable();
        })
    }

    return module;
})()

export const DocumentUpdating = (function () {
    const module = {
        updateDocumentUrl: '/api/documents',

        updateDocumentAreaSelector: $('#update-document-area'),
        oldMainDocumentFileAreaSelector: $('#old-main-document-file'),
        oldAttachedDocumentFileAreaSelector: $('#old-attached-document-file'),
        btUpdateDocumentSelector: $('#btn-update-document'),
        btnResetDocumentSelector: $('#btn-reset-document'),
    };

    module.init = () => {
        handleMainDocumentFileUploadingForEdit();
        handleResetDocumentButton();
        handleUpdateDocumentButton();
    };

    const handleUpdateDocumentButton = () => {
        module.btUpdateDocumentSelector.on('click', function () {
            const documentParam = DocumentCreation.getDocumentParam();
            documentParam.code = module.btUpdateDocumentSelector.data('document_code');
            updateDocument(documentParam);
        })
    }

    const updateDocument = (documentParam) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'PUT',
            url: module.updateDocumentUrl,
            data: JSON.stringify(documentParam),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                window.location.href = Url.web.manageDocument;
                /* Remove all document file uploading data after successful updating */
                LocalStorage.removeAll();
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.updateDocumentAreaSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const handleResetDocumentButton = () => {
        module.btnResetDocumentSelector.on('click', function () {
            location.reload();
        })
    }

    const handleMainDocumentFileUploadingForEdit = () => {
        DocumentCreation.handleMainDocumentFileUploading();
        DocumentCreation.handleAttachedDocumentFileUploading();
    }

    return module;
})();
$(function () {
    $('.selectpicker').selectpicker('setStyle', 'btn-outline-dark');
    $('.selectpicker').addClass('w-100').selectpicker('setStyle');
    $('.datepicker').datepicker();
});
