import {App} from "./app.js";

export const FormHandler = (function () {
    const module = {};

    module.handleFileUploading = (fileSelector, requestUrl, redirectUrl) => {
        const fileUpload = fileSelector[0].files[0];

        if (fileUpload === undefined) {
            App.showErrorMessage(chooseFileMessage);
            return;
        }

        const formData = new FormData();
        formData.append('file', fileUpload);

        $.ajax({
            url: requestUrl,
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false
        })
            .done((response) => {
                window.location.href = redirectUrl;
            })
            .fail((jqXHR) => {
                App.handleResponseMessageByStatusCode(jqXHR);
            })
    }

    module.handleFileDownloading = (requestUrl) => {
        $.ajax({
            url: requestUrl,
            type: 'GET',
            xhrFields: {
                responseType: 'blob' // Set the response type to blob
            },
            success: function(response, status, xhr) {
                // Extract the filename from the Content-Disposition header
                let filename = '';
                const disposition = xhr.getResponseHeader('Content-Disposition');
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }
                }

                // Create a blob URL from the response data
                const blob = new Blob([response], { type: 'application/octet-stream' });
                const url = window.URL.createObjectURL(blob);

                // Create a temporary link element
                let a = document.createElement('a');
                a.href = url;
                a.download = filename || 'sample_file.csv'; // Set the desired filename
                document.body.appendChild(a);

                // Trigger a click event to download the file
                a.click();

                // Cleanup
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            },
            error: function(xhr, status, error) {
                // Handle any errors
                console.error('Error downloading file:', error);
            }
        });
    }

    module.handleServerValidationError = (formSelector, jqXHR) => {
        const {errors, message, redirect} = jqXHR.responseJSON;

        if (errors) {
            for (const key in errors) {
                const input = formSelector.find('[name="' + makeInputName(key) + '"]').first();
                const error = makeErrorMessage(errors[key]);
                const group = input.closest('.with-validation');

                if (group.length) {
                    group.append(error);
                    addHidingEventToErrorText(group);
                }
            }
        }
    }

    module.clearAllInputs = (formSelector) => {
        formSelector.find('input')
            .val('')
            .prop('checked', false)
            .prop('selected', false);
        formSelector.find('select').val('');
        formSelector.find('textarea').val('');
        $('.selectpicker').selectpicker('refresh');
    }

    module.clearAllErrors = (formSelector) => {
        formSelector.find('.error-text').remove();
    }

    const addHidingEventToErrorText = (validationGroupSelector) => {
        validationGroupSelector.find('input').on( "focus", function() {
            const errorTextSelector = validationGroupSelector.find('.error-text');
            errorTextSelector.css('visibility', 'hidden');
        });
    }

    /**
     * Make div element for showing error message
     *
     * @param error
     * @returns {HTMLDivElement}
     */
    const makeErrorMessage = (error) => {
        const errorSpan = document.createElement("span");
        errorSpan.classList.add('error-text');
        errorSpan.innerText = Array.isArray(error) ? error.join("\n") : error;

        return errorSpan;
    }

    /**
     * Convert validation attribute name to HTML input name
     *
     * @param validationName string, eg: 'supplier.0.supplier_id'
     * @returns {*}
     */
    const makeInputName = (validationName) => {
        const attributes = validationName.split(".");
        return attributes.map((el, i) => (i ? "[" + el + "]" : el)).join("");
    }

    return module;
})();




