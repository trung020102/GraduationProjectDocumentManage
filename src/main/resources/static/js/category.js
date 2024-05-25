import {App} from "./app.js";
import {FormHandler} from "./form.js";
import {PageComponent} from "./component.js";

const MainCategory = (function () {
    const module = {
        createMainCategoryUrl: '/api/categories',
        updateMainCategoryUrl: '/api/categories/{id}',
        deleteMainCategoryUrl: '/api/categories/{id}',

        categoryMenuSelector: $('#category-menu'),
        categoryListSelector: $('#category-list'),

        createMainCategoryCardSelector: $('.create-main-category-card'),
        createMainCategoryButtonSelector: $('#create-main-category-btn'),
        newMainCategoryTitleSelector: $('#new-main-category-title'),

        categoryInputSelector: $('.category-input'),
    };

    module.init = () => {
        App.enableEnterKeyboard(module.newMainCategoryTitleSelector, module.createMainCategoryButtonSelector);
    }

    module.createMainCategory = () => {
        const categoryParam = {title: module.newMainCategoryTitleSelector.val().trim()};

        $.ajax({
            contentType: 'application/json',
            type: 'POST',
            url: module.createMainCategoryUrl,
            data: JSON.stringify(categoryParam),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                FormHandler.clearAllInputs(module.createMainCategoryCardSelector);
                const mainCategory = response.data;
                /* Render new category from response data */
                module.categoryMenuSelector.append(createSidebarDropdown(mainCategory));
                module.categoryListSelector.append(createCollapseCategory(mainCategory));
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.createMainCategoryCardSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    /* Publish this method for inline onclick event of button: confirm-edit-category-btn */
    module.updateCategory = (currentButton) => {
        const categoryId = $(currentButton).closest('.category-card-item').data('main_category');
        const categoryTitle = $(currentButton).siblings('.category-input').find('input').val().trim();
        const categoryParam = {
            id: categoryId,
            title: categoryTitle
        };

        $.ajax({
            contentType: 'application/json',
            type: 'PUT',
            url: module.updateMainCategoryUrl.replace('{id}', categoryId),
            data: JSON.stringify(categoryParam)
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                const mainCategory = response.data;
                reRenderUpdatedMainCategory(mainCategory);
            })
            .fail((jqXHR) => {
                const containerSelector = $(currentButton)
                    .closest(`.category-card-item[data-main_category=${categoryId}]`)
                    .find('.card-header');
                FormHandler.handleServerValidationError(containerSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const reRenderUpdatedMainCategory = (mainCategory) => {
        const updatedCategoryDropdown = createSidebarDropdown(mainCategory);
        module.categoryMenuSelector
            .find(`li.menu-dropdown[data-main_category=${mainCategory.id}]`)
            .replaceWith(updatedCategoryDropdown);

        const updatedCategoryCardItem = createCollapseCategory(mainCategory);
        module.categoryListSelector
            .find(`div.category-card-item[data-main_category=${mainCategory.id}]`)
            .replaceWith(updatedCategoryCardItem);
    }

    const createSidebarDropdown = (mainCategory) => {
        /* Use component from component.js file to modify UI after success operation */
        const categoryDropdownMenu = PageComponent.sidebarDropdown(mainCategory);
        const categoryDropdownItems = createSidebarDropdownItems(mainCategory.subCategories);

        return categoryDropdownMenu.replace('{0}', categoryDropdownItems);
    }

    const createSidebarDropdownItems = (subCategories) => {
        let sidebarDropdownItems = '';
        $.each(subCategories, function (index, subCategory) {
            sidebarDropdownItems += PageComponent.sidebarDropdownItem(subCategory);
        });

        return sidebarDropdownItems;
    }

    const createCollapseCategory = (mainCategory) => {
        /* Use component from component.js file to modify UI after success operation */
        const collapseCategoryMenu = PageComponent.collapseCategory(mainCategory);
        const collapseCategoryItems = createCollapseCategoryItems(mainCategory.subCategories);

        return collapseCategoryMenu.replace('{0}', collapseCategoryItems);
    }

    const createCollapseCategoryItems = (subCategories) => {
        let collapseCategoryItems = '';
        $.each(subCategories, function (index, subCategory) {
            collapseCategoryItems += PageComponent.collapseCategoryItem(subCategory);
        });

        return collapseCategoryItems;
    }

    /* Publish this method for inline onclick event of button: delete-category-btn */
    module.deleteCategory = (currentButton) => {
        const categoryId = $(currentButton).closest('.category-card-item').data('main_category');
        $.ajax({
            type: 'DELETE',
            url: module.deleteMainCategoryUrl.replace('{id}', categoryId),
        })
            .done((response) => {
                removeMainCategoryInSidebarAndCard(categoryId);
                App.showSuccessMessage(response.message);
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const removeMainCategoryInSidebarAndCard = (categoryId) => {
        const sidebarDropdown = $(`.main-sidebar li[data-main_category=${categoryId}]`);
        sidebarDropdown.remove();
        const collapseCard = $(`div.category-card-item[data-main_category=${categoryId}]`);
        collapseCard.remove();
    }

    /* Publish this method for inline onclick event of button: edit-category-btn */
    module.handleEditCategoryButton = (currentButton) => {
        $(currentButton).addClass('d-none');
        $(currentButton).siblings('.category-title').addClass('d-none');
        $(currentButton).siblings('.category-input').removeClass('d-none');
        $(currentButton).siblings('.confirm-edit-category-btn').removeClass('d-none');
        $(currentButton).siblings('.cancel-edit-category-btn').removeClass('d-none');
    }

    /* Publish this method for inline onclick event of button: cancel-edit-category-btn */
    module.handleCancelEditCategoryButton = (currentButton) => {
        $(currentButton).addClass('d-none');
        const oldTitleValue = $(currentButton).data('main_category_title');
        $(currentButton).siblings('.category-title')
            .text(oldTitleValue)
            .removeClass('d-none');
        $(currentButton).siblings('.category-input').find('input').val(oldTitleValue);
        $(currentButton).siblings('.category-input').addClass('d-none');
        $(currentButton).siblings('.confirm-edit-category-btn').addClass('d-none');
        $(currentButton).siblings('.edit-category-btn').removeClass('d-none');
    }

    return module;
})();

const SubCategory = (function () {
    const module = {
        createSubCategoryUrl: '/api/categories/create-sub-category',
        updateSubCategoryUrl: '/api/categories/update-sub-category',
        deleteSubCategoryUrl: '/api/categories/delete-sub-category/{id}',
    };

    /* Publish this method for inline onclick event of button: create-sub-category-btn */
    module.createSubCategory = (currentButton) => {
        const subCategoryParam = getSubCategoryParam(currentButton);

        $.ajax({
            contentType: 'application/json',
            type: 'POST',
            url: module.createSubCategoryUrl,
            data: JSON.stringify(subCategoryParam),
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                FormHandler.clearAllInputs(MainCategory.categoryListSelector);
                const subCategory = response.data;
                /* Render new sub-category from response data */
                renderInSidebarDropdown(subCategory);
                renderInCollapseCard(subCategory);
            })
            .fail((jqXHR) => {
                const containerSelector = $(currentButton).closest('li');
                FormHandler.handleServerValidationError(containerSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const renderInSidebarDropdown = (subCategory) => {
        const mainCategoryId = subCategory.parentId;
        const sidebarDropdownMenuItem = $(`#category-menu .menu-dropdown[data-main_category=${mainCategoryId}] .nav-treeview`)
        sidebarDropdownMenuItem.prepend(PageComponent.sidebarDropdownItem(subCategory));
    }

    const renderInCollapseCard = (subCategory) => {
        const mainCategoryId = subCategory.parentId;
        const collapseCardItem = $(`#category-list .category-card-item[data-main_category=${mainCategoryId}] .sub-category-item`)
        collapseCardItem.prepend(PageComponent.collapseCategoryItem(subCategory));
    }

    /* Publish this method for inline onclick event of button: confirm-sub-category-btn */
    module.updateSubCategory = (currentButton) => {
        const mainCategoryId = $(currentButton).closest('.category-card-item').data('main_category');
        const subCategoryId = $(currentButton).closest('li').data('sub_category_id');
        const subCategoryTitle = $(currentButton).closest('li').find('.sub-category-input').val().trim();

        const subCategoryParam = {
            id: subCategoryId,
            title: subCategoryTitle,
            parentId: mainCategoryId
        };

        $.ajax({
            contentType: 'application/json',
            type: 'PUT',
            url: module.updateSubCategoryUrl,
            data: JSON.stringify(subCategoryParam)
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                const subCategory = response.data;
                reRenderUpdatedSubCategory(subCategory);
            })
            .fail((jqXHR) => {
                const containerSelector = $(currentButton)
                    .closest(`.category-card-item[data-main_category=${categoryId}]`)
                    .find('.card-header');
                FormHandler.handleServerValidationError(containerSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const reRenderUpdatedSubCategory = (subCategory) => {
        const updatedSubCategoryDropdown = PageComponent.sidebarDropdownItem(subCategory);
        MainCategory.categoryMenuSelector
            .find(`li.menu-dropdown[data-main_category=${subCategory.parentId}]`)
            .find(`li[data-sub_category=${subCategory.id}]`)
            .replaceWith(updatedSubCategoryDropdown);

        const updatedCategoryCardItem = PageComponent.collapseCategoryItem(subCategory);
        MainCategory.categoryListSelector
            .find(`div.category-card-item[data-main_category=${subCategory.parentId}]`)
            .find(`.sub-category-item li[data-sub_category_id=${subCategory.id}]`)
            .replaceWith(updatedCategoryCardItem);
    }

    const getSubCategoryParam = (currentButton) => {
        const mainCategoryContainer = $(currentButton).closest('.category-card-item');
        const mainCategoryId = mainCategoryContainer.data('main_category');
        const subCategoryTitle = mainCategoryContainer.find('.sub-category-creation-input').val().trim();

        return {
            parentId: mainCategoryId,
            title: subCategoryTitle
        }
    }

    /* Publish this method for inline onclick event of button: edit-sub-category-btn */
    module.handleEditSubCategoryButton = (currentButton) => {
        $(currentButton).addClass('d-none');
        $(currentButton).closest('.nav-item').find('.sub-category-title').addClass('d-none');
        $(currentButton).closest('.nav-item').find('.sub-category-input').removeClass('d-none');
        $(currentButton).siblings('.confirm-sub-category-btn').removeClass('d-none');
        $(currentButton).siblings('.cancel-edit-sub-category-btn').removeClass('d-none');
    }

    /* Publish this method for inline onclick event of button: cancel-edit-sub-category-btn */
    module.handleCancelEditSubCategoryButton = (currentButton) => {
        $(currentButton).addClass('d-none');
        const oldTitleValue = $(currentButton).data('sub_category_title');
        $(currentButton).closest('.nav-item').find('.sub-category-title')
            .text(oldTitleValue)
            .removeClass('d-none');
        $(currentButton).closest('.nav-item').find('.sub-category-input')
            .val(oldTitleValue)
            .addClass('d-none');
        $(currentButton).siblings('.confirm-sub-category-btn').addClass('d-none');
        $(currentButton).siblings('.edit-sub-category-btn').removeClass('d-none');
    }

    /* Publish this method for inline onclick event of button: delete-sub-category-btn */
    module.deleteSubCategory = (currentButton) => {
        const mainCategoryId = $(currentButton).closest('.category-card-item').data('main_category');
        const subCategoryId = $(currentButton).closest('li').data('sub_category_id');
        $.ajax({
            type: 'DELETE',
            url: module.deleteSubCategoryUrl.replace('{id}', subCategoryId),
        })
            .done((response) => {
                removeSubCategoryInSidebarAndCard(mainCategoryId, subCategoryId);
                App.showSuccessMessage(response.message);
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    const removeSubCategoryInSidebarAndCard = (mainCategoryId, subCategoryId) => {
        MainCategory.categoryMenuSelector
            .find(`li.menu-dropdown[data-main_category=${mainCategoryId}]`)
            .find(`li[data-sub_category=${subCategoryId}]`)
            .remove();

        MainCategory.categoryListSelector
            .find(`div.category-card-item[data-main_category=${mainCategoryId}]`)
            .find(`.sub-category-item li[data-sub_category_id=${subCategoryId}]`)
            .remove();
    }

    return module;
})();

(function () {
    MainCategory.init();
    /* Publish MainCategory methods */
    window.createMainCategory = () => MainCategory.createMainCategory();
    window.updateCategory = (currentButton) => MainCategory.updateCategory(currentButton);
    window.deleteMainCategory = (currentButton) => MainCategory.deleteCategory(currentButton);
    window.handleEditCategoryButton = (currentButton) => MainCategory.handleEditCategoryButton(currentButton);
    window.handleCancelEditCategoryButton = (currentButton) => MainCategory.handleCancelEditCategoryButton(currentButton);
    /* Publish SubCategory methods */
    window.createSubCategory = (currentButton) => SubCategory.createSubCategory(currentButton);
    window.updateSubCategory = (currentButton) => SubCategory.updateSubCategory(currentButton);
    window.handleEditSubCategoryButton = (currentButton) => SubCategory.handleEditSubCategoryButton(currentButton);
    window.handleCancelEditSubCategoryButton = (currentButton) => SubCategory.handleCancelEditSubCategoryButton(currentButton);
    window.deleteSubCategory = (currentButton) => SubCategory.deleteSubCategory(currentButton);
})();