import {FormHandler} from "./form.js";

export const App = (function () {
    const module = {
        errorToastSelector: $('#error-toast'),
        successToastSelector: $('#success-toast'),
    };

    module.handleResponseMessageByStatusCode = (jqXHR) => {
        let message = jqXHR.responseJSON.message;
        switch (jqXHR.status) {
            case 401:
            case 403:
                /* Do not use break to modify message in case 401 and 403 that can be null in response */
                message = message ?? ERROR_403;
            case 422:
            case 500:
                module.showErrorMessage(message);
                break;
            case 200:
                module.showSuccessMessage(message);
                break;
        }
    }

    module.showSuccessMessage = (message) => {
        module.successToastSelector.find('.toast-body').text('').text(message);
        module.successToastSelector.toast('show');
    }

    module.showErrorMessage = (message) => {
        module.errorToastSelector.find('.toast-body').text('').text(message);
        module.errorToastSelector.toast('show');
    }

    module.clearAllModalInputsAfterClosing = () => {
        $('.modal').on('hidden.bs.modal', function () {
            FormHandler.clearAllInputs($(this));
            FormHandler.clearAllErrors($(this));
        })
    }

    module.enableEnterKeyboard = (formSelector, buttonSelector) => {
        formSelector.keypress(function (event) {
            let keycode = (event.keyCode ? event.keyCode : event.which);
            if (keycode == '13') {
                buttonSelector.click();
                return false;
            }
        });
    }

    module.keepSidebarDropdownOpen = () => {
        const url = window.location;
        const targetLinkSelector = $('ul.nav-treeview a').filter(function () {
            return this.href == url;
        });
        targetLinkSelector.addClass('active');
        targetLinkSelector.closest('ul').css('display', 'block');
        targetLinkSelector.closest('li.menu-dropdown').addClass('menu-dropdown menu-is-opening');
    }

    module.disableElementsInAjaxProgress = () => {
        $(document)
            .ajaxStart(function () {
                $('button').prop('disabled', true).css('cursor', 'wait');
                $('a').css('pointer-events', 'none').css('cursor', 'wait');
            })
            .ajaxStop(function () {
                $('button').prop('disabled', false).css('cursor', 'pointer');
                $('a').css('pointer-events', 'auto').css('cursor', 'pointer');
            });
    }

    return module;
})();

export const DatatableAttribute = (function () {
    const module = {};

    module.language = {
        info: resultCount,
        infoEmpty: noResult,
        lengthMenu: menuLength,
        paginate: {
            previous: `<i class="fa fa-angle-left"></i>`,
            next: `<i class="fa fa-angle-right"></i>`
        },
        aria: {
            paginate: {
                previous: 'Previous',
                next: 'Next'
            }
        },
        emptyTable: emptyTable
    }

    /* Create increment number for ordinal column of datatable */
    module.renderOrdinalColumn = (datatableSelector) => {
        datatableSelector.on('draw.dt', function () {
            const PageInfo = datatableSelector.page.info();
            datatableSelector.column(0, {page: 'current'}).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1 + PageInfo.start;
            });
        });
    }

    return module;
})();

export const ProgressingBar = (function () {
    const module = {};

    const configMode = () => {
        topbar.config({
            autoRun: true,
            barThickness: 6,
            barColors: {
                "0": "rgba(26,  188, 156, .9)",
                ".25": "rgba(52,  152, 219, .9)",
                ".50": "rgba(241, 196, 15,  .9)",
                ".75": "rgba(230, 126, 34,  .9)",
                "1.0": "rgba(211, 84,  0,   .9)",
            },
            shadowBlur: 10,
            shadowColor: "rgba(0,   0,   0,   .6)",
            className: null,
        });
    }

    module.init = () => {
        configMode();
        $(document)
            .ajaxStart(function () {
                topbar.show();
            })
            .ajaxStop(function () {
                topbar.hide();
            });
    }
    return module;
})();

export const LocalStorage = (function () {
    const module = {};

    module.setJsonItem = (name, value) => {
        localStorage.setItem(name, JSON.stringify(value));
    }

    module.getJsonItem = (name) => {
        return JSON.parse(localStorage.getItem(name));
    }

    module.removeItem = (name) => {
        localStorage.removeItem(name);
    }

    module.removeAll = () => {
        localStorage.clear();
    }

    return module;
})();