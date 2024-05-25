import {App, ProgressingBar} from "./app.js";

/* Init global events */
(function () {
    App.keepSidebarDropdownOpen();
    App.clearAllModalInputsAfterClosing();
    App.disableElementsInAjaxProgress();
    ProgressingBar.init();
})();