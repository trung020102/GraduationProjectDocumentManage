export const TimeHelper = (function () {
    const module = {
    };

    module.pattern = {
        monthDayYearWithSlash: 'MM/DD/YYYY',
        yearMonthDayWithHyphen: 'YYYY-MM-DD',
        dayMonthYearWithSlash: 'DD/MM/YYYY',
    }

    module.convertDateTimeToPattern = (dateTime, pattern) => {
        return moment(dateTime).format(pattern);
    }

    module.convertDateTimeStringToPattern = (dateTimeString, stringPattern, toPattern) => {
        return  moment(dateTimeString, stringPattern).format(toPattern);
    }

    module.timeNowToPattern = (pattern) => {
        return moment().format(pattern);
    }

    return module;
})();