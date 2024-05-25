import {FormHandler} from "./form.js";
import {App} from "./app.js";

// TODO Refactor form submission handler
const LogIn = (function () {
    const module = {
        logInUrl: '/api/login',
        logInButtonSelector: $('#log-in-button'),
        usernameInputSelector: $('#username'),
        passwordInputSelector: $('#password'),
        formLoginSelector: $('.hitec-form-signin')
    };

    module.init = () => {
        handleLogInSubmitButton();
        App.enableEnterKeyboard(module.formLoginSelector, module.logInButtonSelector);
    }

    const handleLogInSubmitButton = () => {
        module.logInButtonSelector.on('click', function() {
            const loginParam = {
                username: module.usernameInputSelector.val().trim(),
                password: module.passwordInputSelector.val().trim(),
            }

            logInByAjax(loginParam);
        })
    }

    const logInByAjax = (loginParam) => {
        $.ajax({
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            },
            type: 'POST',
            url: module.logInUrl,
            data: JSON.stringify(loginParam),
            beforeSend:  function () {
                module.formLoginSelector.addClass("loading");
                module.formLoginSelector.find(".error-text").remove();
                module.formLoginSelector.find("button").prop('disabled', true);
            },
        })
            .done((response) => {
                App.showSuccessMessage(response.message);
                window.location.href = response.redirectUrl;
            })
            .fail((jqXHR) => {
                FormHandler.handleServerValidationError(module.formLoginSelector, jqXHR)
                App.handleResponseMessageByStatusCode(jqXHR);
            })
            .always(() => {
                module.formLoginSelector.find("button").prop('disabled', false);
                module.formLoginSelector.removeClass("loading");
            });
    }

    return module;
})();

(function () {
    LogIn.init();
})();
