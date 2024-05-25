import {Url} from "./url.js"

/* This file contains component with equivalent html elements of component layout */

/*
    If there is any change from a component file (in /layout/component),
    the corresponding component in PageComponent must be modified the same.
*/

export const PageComponent = (function () {
    const module = {};

    module.sidebarDropdown = (mainCategory) =>
        `<li class="nav-item menu-dropdown" data-main_category="${mainCategory.id}">
            <a href="#" class="nav-link">
                <p class="text-uppercase">
                    ${mainCategory.title}
                    <i class="fas fa-angle-left right"></i>
                    <span class="badge badge-info right">${mainCategory.documentCount ?? 0}</span>
                </p>
            </a>
            <ul class="nav nav-treeview">{0}</ul>
            <!-- {0} is the replacement of sub-categories (sidebarDropdownItem) -->
        </li>`;

    module.sidebarDropdownItem = (subCategory) =>
        `<li class="nav-item" data-sub_category="${subCategory.id}">
            <a href="${Url.web.homePage}?categoryId=${subCategory.id}" class="nav-link">
                <p>${subCategory.title}</p>
                <span class="badge badge-warning ml-2">${subCategory.documentCount}</span>
            </a>
        </li>`;

    module.collapseCategory = (mainCategory) =>
        `<div class="category-card-item card card-gray col-4 mx-auto collapsed-card p-0" data-main_category="${mainCategory.id}">
            <div class="card-header d-flex px-3">
                <div class="category-title card-title my-auto mr-auto text-uppercase">${mainCategory.title}</div>
                <div class="category-input with-validation w-100 mr-3 d-none">
                    <input class="form-control form-control-sm" type="text" value="${mainCategory.title}" name="title">
                </div>
                <button class="confirm-edit-category-btn btn btn-sm btn-outline-success border-0 d-none"
                        onclick="updateCategory(this)">
                    <i class="fa fa-check"></i>
                </button>
                <button class="cancel-edit-category-btn btn btn-sm btn-outline-info border-0 d-none"
                        onclick="handleCancelEditCategoryButton(this)" data-main_category_title="${mainCategory.title}">
                    <i class="fa fa-times"></i>
                </button>
                <button class="edit-category-btn btn btn-sm btn-outline-warning border-0"
                        onclick="handleEditCategoryButton(this)">
                    <i class="fa fa-edit"></i>
                </button>
                <button class="delete-category-btn btn btn-sm btn-outline-danger border-0"
                        onclick="deleteMainCategory(this)">
                    <i class="fa fa-trash-alt"></i>
                </button>
                <div class="card-tools">
                    <button type="button" class="btn btn-tool" data-card-widget="collapse">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
            </div>
            <div class="card-body p-0" style="display: none">
                <ul class="sub-category-item nav nav-pills flex-column">
                    {0} <!-- {0} is the replacement of sub-categories (collapseCategoryItem) -->
                    <li class="nav-item active">
                        <div class="input-group input-group-sm px-3 py-2 with-validation">
                            <input type="text" class="sub-category-creation-input form-control" 
                                placeholder="${enterChildCategoryName}" name="title">
                            <div class="input-group-append">
                                <button type="button" class="create-sub-category-btn btn btn-info" 
                                    onclick="createSubCategory(this)">${createButton}</button>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>`;

    module.collapseCategoryItem = (subCategory) =>
        `<li class="nav-item active d-flex with-validation" data-sub_category_id="${subCategory.id}">
            <div class="sub-category-title nav-link">${subCategory.title}</div>
            <input class="sub-category-input col form-control form-control-sm mx-3 my-2 d-none"
                   type="text" value="${subCategory.title}" name="title">
            <div class="ml-auto mr-2 my-auto">
                <i class="confirm-sub-category-btn fa fa-check-circle btn btn-sm text-success my-auto d-none"
                    onclick="updateSubCategory(this)"></i>
                <i class="cancel-edit-sub-category-btn fa fa-times-circle btn btn-sm text-info my-auto d-none"
                    onclick="handleCancelEditSubCategoryButton(this)" data-sub_category_title="${subCategory.title}"></i>
                <i class="edit-sub-category-btn edit fa fa-edit btn btn-sm text-gray my-auto"
                    onclick="handleEditSubCategoryButton(this)"></i>
                <i class="delete-sub-category-btn fa fa-trash-alt btn btn-sm text-danger my-auto"
                   onclick="deleteSubCategory(this)"></i>
            </div>
        </li>`;

    return module;
})();

export const DocumentDetailComponent = (function () {
    const module = {};

    module.categoryRow = (subCategory) =>
        `<tr>
            <td>${subCategory.parentTitle}</td>
            <td>${subCategory.title}</td>
        </tr>`;

    module.documentFileLink = (link, originalFileName) =>
        `<div><a href="${link}" target="_blank">${originalFileName}</a></div>`;

    module.documentTitleInTable = title =>
        `<a class="open-document-detail" href="javascript:void(0)" >${title}</a>`

    return module;
})();