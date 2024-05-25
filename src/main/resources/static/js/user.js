import {App} from "./app.js";
import {DatatableAttribute} from "./app.js";
import {FormHandler} from "./form.js";
import {Url} from "./url.js";

export const UserCreation = (function () {
    const module = {
        getAllUserRoleUrl: '/api/users/getAllRoles',
        createNewUserUrl: '/api/users/register',
        importUserUrl: '/api/users/import',
        getUserFileSampleUrl: '/api/users/getFileSample',

        userRegisterFormSelector: $('#user-register-form'),
        usernameSelector: $('#username'),
        passwordSelector: $('#password'),
        fullNameSelector: $('#full-name'),
        roleCodeSelector: $('#role-code'),

        createUserButtonSelector: $('#btn-create-user'),
        resetButtonSelector: $('#btn-reset-user'),
        importButtonSelector: $('#btn-import'),
        submitImportButtonSelector: $('#btn-submit-import'),

        fileUploadingModalSelector: $('#file-uploading-modal'),
        fileDownloadingSampleSelector: $('#link-download-sample'),
    };

    module.init = () => {
        getAllUserRoles();
        handleSubmitUserButton();
        handleSubmitImportButton();
        handleResetFormButton();
        handleDownloadFileSampleLink();
        App.enableEnterKeyboard(module.userRegisterFormSelector, module.createUserButtonSelector);
    }

    const handleSubmitImportButton = () => {
        module.submitImportButtonSelector.on('click', function () {
            const fileSelector = module.fileUploadingModalSelector.find('input');
            FormHandler.handleFileUploading(fileSelector, module.importUserUrl, Url.web.manageUser);
        });
    }

    const handleSubmitUserButton = () => {
        module.createUserButtonSelector.on('click', function () {
            const userRegister = {
                username: module.usernameSelector.val().trim().toUpperCase(),
                password: module.passwordSelector.val().trim(),
                fullName: module.fullNameSelector.val().trim(),
                roleCode: module.roleCodeSelector.val().trim(),
            }

            createNewUser(userRegister);
        });
    }

    const handleResetFormButton = () => {
        module.resetButtonSelector.on('click', function () {
            FormHandler.clearAllInputs(module.userRegisterFormSelector);
            FormHandler.clearAllErrors(module.userRegisterFormSelector);
        });
    }

    const handleDownloadFileSampleLink = () => {
        module.fileDownloadingSampleSelector.on('click', function () {
            FormHandler.handleFileDownloading(module.getUserFileSampleUrl);
        });
    }

    const getAllUserRoles = () => {
        $.ajax({
            url: module.getAllUserRoleUrl
        })
            .done((response) => {
                for (const role of response.data) {
                    module.roleCodeSelector.append(new Option(role.name, role.code))
                }
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const createNewUser = (userRegister) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'POST',
            url: module.createNewUserUrl,
            data: JSON.stringify(userRegister),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                FormHandler.clearAllInputs(module.userRegisterFormSelector);
                window.location.href = Url.web.manageUser;
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.userRegisterFormSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    return module;
}());

export const UserUpdating = (function () {
    const module = {
        changeOwnPasswordUrl: '/api/users/change-password',
        updateUserInfoUrl: '/api/users',
        updateUserAvatarUrl: '/api/users/saveAvatar',

        userChangeOwnPasswordModal: $('#change-own-password-modal'),
        userChangeOwnPasswordFormSelector: $('#user-change-own-password-form'),
        changeOwnPasswordButtonSelector: $('#btn-submit-change-password'),
        oldPasswordSelector: $('#old-password'),
        newPasswordSelector: $('#new-password'),
        confirmNewPasswordSelector: $('#confirm-new-password'),

        userInfoFormSelector: $('#user-info-form'),
        userFullNameSelector: $('#user-full-name'),
        userEmailSelector: $('#user-email'),
        updateUserButtonSelector: $('#btn-update-user'),

        userAvatarAreaSelector: $('.avatar-wrapper'),
        userAvatarImageSelector: $('.profile-pic'),
        userAvatarInputSelector: $('#user-avatar'),
        updateAvatarButtonSelector: $('#update-avatar-btn'),

        sidebarImageSelector: $('.sidebar img'),
    };

    module.init = () => {
        handleEditInputButton();
        handleChangeOwnPasswordButton();
        handleUpdateUserButton();
        handleAvatarUpload();
        handleUpdateAvatarButton();
        App.enableEnterKeyboard(module.userInfoFormSelector, module.updateUserButtonSelector);
    }

    const handleEditInputButton = () => {
        $('.edit-input-button').on('click', function () {
            $(this).siblings('.edit-input-text').prop('hidden', true);
            $(this).siblings('.edit-input').prop('hidden', false);
            $('.submit-edit-button').prop('hidden', false);
            $(this).prop('hidden', true);
        });
    }

    const handleAvatarUpload = () => {
        let readURL = function (input) {
            if (input.files && input.files[0]) {
                let reader = new FileReader();
                reader.onload = function (e) {
                    module.userAvatarImageSelector.attr('src', e.target.result);
                }
                reader.readAsDataURL(input.files[0]);
            }
        }

        $(".file-upload").on('change', function () {
            readURL(this);
            module.updateAvatarButtonSelector.prop('hidden', false);
        });

        $(".upload-button").on('click', function () {
            $(".file-upload").click();
        });
    }

    const handleUpdateAvatarButton = () => {
        module.updateAvatarButtonSelector.on('click', function () {
            let formData = new FormData();
            const avatar = module.userAvatarInputSelector.prop('files')[0];
            formData.append('avatar', avatar);
            updateAvatar(formData);
        })
    }

    const updateAvatar = (avatarFormData) => {
        $.ajax({
            url: module.updateUserAvatarUrl,
            data: avatarFormData,
            type: 'PUT',
            processData: false,
            mimeType: 'multipart/form-data',
            contentType: false,
            dataType: 'json',
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                module.updateAvatarButtonSelector.prop('hidden', true);
                module.sidebarImageSelector.prop('src', `/api/users/getAvatar/${response.data.fileName}`);
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.userInfoFormSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const handleChangeOwnPasswordButton = () => {
        module.changeOwnPasswordButtonSelector.on('click', function () {
            const userPasswordParam = {
                oldPassword: module.oldPasswordSelector.val(),
                newPassword: module.newPasswordSelector.val(),
                confirmNewPassword: module.confirmNewPasswordSelector.val(),
            }

            changeOwnPassword(userPasswordParam);
        });
    }

    const changeOwnPassword = (userPasswordParam) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'PUT',
            url: module.changeOwnPasswordUrl,
            data: JSON.stringify(userPasswordParam),
        })
            .done((response) => {
                FormHandler.clearAllInputs(module.userChangeOwnPasswordFormSelector);
                FormHandler.clearAllErrors(module.userChangeOwnPasswordFormSelector);
                module.userChangeOwnPasswordModal.modal('hide');
                App.showSuccessMessage(response.message);
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.userChangeOwnPasswordFormSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const handleUpdateUserButton = () => {
        module.updateUserButtonSelector.on('click', function () {
            const userInfoParam = {
                fullName: module.userFullNameSelector.val().trim(),
                email: module.userEmailSelector.val().trim(),
            };

            updateUserInfo(userInfoParam);
        });
    }

    const updateUserInfo = (userInfoParam) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'PUT',
            url: module.updateUserInfoUrl,
            data: JSON.stringify(userInfoParam),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                location.reload();
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.userInfoFormSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    return module;
})();

// con module se co nhung phuong thuc hoac thuoc tinh co the dung dc o ngoai
export const UserList = (function () {
    const module = {
        findAllUsersByFilterUrl: '/api/users',
        updateUserByAdminUrl: '/api/users/admin/update-user',

        searchInputSelector: $('#search-input'),
        roleSelectSelector: $('#role-select'),
        statusSelectSelector: $('#status-select'),
        searchSubmitSelector: $('#search-submit-btn'),

        userListTableSelector: $('#user-list-table'),

        editUserModalSelector: $('#edit-user-modal'),
        editUserModal: {
            formEditSelector: $('#edit-user-modal form'),
            usernameSelector: $('#edit-user-modal .username-input'),
            fullNameSelector: $('#edit-user-modal .full-name-input'),
            emailSelector: $('#edit-user-modal .email-input'),
            newPasswordInputSelector: $('#edit-user-modal .new-password-input'),
            roleInputSelector: $('#edit-user-modal .role-select'),
            statusInputSelector: $('#edit-user-modal .status-select'),
            submitButtonSelector: $('#edit-user-modal .btn-save'),
        },
    };

    module.init = () => {
        initSelect2();
        renderUserListTable();
        handleSearchSubmissionButton();
        openEditUserModalButton();
        handleEditUserSubmitButton();
    }

    const initSelect2 = () => {
        $('.select2').each(function (index, item) {
            const label = $(this).data('label');
            $(item).select2({
                placeholder: label
            });
        })
    }

    const openEditUserModalButton = () => {
        module.userListTableSelector.on('click', '.open-edit-user-modal-btn', function () {
            const rowData = module.userListTableSelector.DataTable().row($(this).closest('tr')).data();
            renderRowDataToEditUserModalInputs(rowData);
            module.editUserModalSelector.modal('show');
        })
    }

    const handleEditUserSubmitButton = () => {
        module.editUserModal.submitButtonSelector.on('click', function () {
            updateUserInfo(getUserUpdatingInfo());
        });
    }

    const getUserUpdatingInfo = () => {
        return {
            username: module.editUserModal.usernameSelector.val().trim(),
            newPassword: module.editUserModal.newPasswordInputSelector.val().trim(),
            role: module.editUserModal.roleInputSelector.val().trim(),
            isDisabled: module.editUserModal.statusInputSelector.val().trim(),
        };
    }

    const updateUserInfo = (userInfo) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'PUT',
            url: module.updateUserByAdminUrl,
            data: JSON.stringify(userInfo),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                module.editUserModalSelector.modal('hide');
                renderUserListTable();
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.editUserModal.formEditSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const renderRowDataToEditUserModalInputs = (rowData) => {
        module.editUserModal.usernameSelector.val(rowData.username);
        module.editUserModal.emailSelector.val(rowData.email);
        module.editUserModal.fullNameSelector.val(rowData.fullName);
        module.editUserModal.statusInputSelector.val(rowData.isDisabled.toString());
        module.editUserModal.roleInputSelector.val(rowData.roleCode);
    }

    const handleSearchSubmissionButton = () => {
        module.searchSubmitSelector.on('click', function () {
            renderUserListTable();
        });
    }

    const getUserListFilter = () => {
        return {
            keyword: module.searchInputSelector.val().trim(),
            roles: module.roleSelectSelector.val(),
            status: module.statusSelectSelector.val(),
            /* These three attributes below are not in user list filter but need setting default value
            so that back-end can handle without error */
            keywordBy: ['username', 'fullName'],
            startDate: '2000-01-01',
            endDate: moment().format('YYYY-MM-DD'),
        }
    }

    const renderUserListTable = () => {
        const userFilter = getUserListFilter();
        const userListDatatable = module.userListTableSelector.DataTable({
            ajax: {
                contentType: 'application/json',
                type: 'POST',
                url: module.findAllUsersByFilterUrl,
                data: function (d) {
                    d.keyword = userFilter.keyword;
                    d.roles = userFilter.roles;
                    d.status = userFilter.status;
                    d.keywordBy = userFilter.keywordBy;
                    d.startDate = userFilter.startDate;
                    d.endDate = userFilter.endDate;
                    return JSON.stringify(d);
                }
            },
            columns: [
                {data: null},
                {data: 'username'},
                {data: 'fullName'},
                {data: 'role'},
                {data: 'createdAt'},
                {data: 'isDisabled'},
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
                    targets: [0, 3, 4],
                    className: "text-center"
                },
                {
                    targets: -2,
                    className: "text-center",
                    render: (data, type, row) => {
                        return data ?
                            `<span class="text-danger">${disabled}</span>` :
                            `<span class="text-success">${active}</span>`;
                    }
                },
                {
                    targets: -1,
                    className: "text-center",
                    render: () =>
                        `<button class="open-edit-user-modal-btn btn btn-sm btn-outline-primary border-0">
                            <i class="fa fa-edit"></i>
                        </button>`
                },
            ],
            language: DatatableAttribute.language
        });

        DatatableAttribute.renderOrdinalColumn(userListDatatable);
    }

    return module;
})();


