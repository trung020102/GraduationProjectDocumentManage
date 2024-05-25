export const Url = (function () {
    const module = {};

    module.web = {
        loginPage: '/login',
        homePage: '/home',

        manageDocument: '/document/manage',
        createDocument: '/document/create',
        editDocument: '/document/edit',

        manageUser: '/users',
        createUser: '/users/create',
        
        manageCategory: '/categories',
    }

    return module;
})();