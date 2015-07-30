cobalt.android_adapter = {
    //
    //ANDROID ADAPTER
    //
    init: function () {
        cobalt.platform = { is : "Android" };
    },
    // handle events sent by native side
    handleEvent: function (json) {
        cobalt.log("received event", json.event);
        if (cobalt.events && typeof cobalt.events[json.event] === "function") {
            cobalt.events[json.event](json.data, json.callback);
        } else {
            switch (json.event) {
                case "onBackButtonPressed":
                    cobalt.log('sending OK for a native back');
                    cobalt.sendCallback(json.callback, {value: true});
                    break;
                default :
                    cobalt.adapter.handleUnknown(json);
                    break;
            }
        }
    },
    //send native stuff
    send: function (obj) {
        if (obj && !cobalt.debugInBrowser) {
            cobalt.divLog('sending', obj);
            try {
                Android.onCobaltMessage(JSON.stringify(obj));
            } catch (e) {
                cobalt.log('ERROR : cant connect to native')
            }

        }
    },
    //modal stuffs. really basic on ios, more complex on android.
    navigateToModal: function (options) {
        cobalt.send({
            "type": "navigation",
            "action": "modal",
            data: {
				page: options.page, 
				controller: options.controller,
				data : options.data
			}
        }, 'cobalt.adapter.storeModalInformations');
    },
    dismissFromModal: function (data) {
        var dismissInformations = cobalt.storage.getItem("dismissInformations", "json");
        if (dismissInformations && dismissInformations.page && dismissInformations.controller) {
            cobalt.send({
                "type": "navigation",
                "action": "dismiss",
                data: {
					page: dismissInformations.page, 
					controller: dismissInformations.controller,
					data : data
				}
            });
            cobalt.storage.removeItem("dismissInformations");
        } else {
            cobalt.log("WANRING : dismissInformations are not available in storage")
        }

    },
    storeModalInformations: function (params) {
        cobalt.divLog("storing informations for the dismiss :", params);
        cobalt.storage.setItem("dismissInformations", params, "json");

    },
    //localStorage stuff
    initStorage: function () {
        //on android, try to bind window.localStorage to Android LocalStorage
        try {
            window.localStorage = LocalStorage;
        } catch (e) {
            cobalt.log("LocalStorage WARNING : can't find android class LocalStorage. switching to raw localStorage")
        }
        return cobalt.storage.enable();
    },
    //datePicker stuff
    datePicker: {
        init: function (inputs) {

            cobalt.utils.each(inputs, function () {
                var input = this;
                var id = cobalt.utils.attr(input, 'id');

                cobalt.log('datePicker setted with value=' + input.value);
                cobalt.utils.attr(input, 'type', 'text');
                cobalt.datePicker.enhanceFieldValue.apply(input);

                input.addEventListener('focus', function () {
                    cobalt.log('show formPicker date for date #', id);
                    input.blur();
                    var previousDate = cobalt.storage.getItem('CobaltDatePickerValue_' + id, 'json');
                    if (!previousDate) {
                        var d = new Date();
                        previousDate = {
                            year: d.getFullYear(),
                            day: d.getDate(),
                            month: d.getMonth() + 1
                        }
                    }
                    cobalt.send({
                        type: "ui", control: "picker", data: {
                            type: "date", date: previousDate,
                            texts: cobalt.datePicker.texts
                        }
                    }, function (newDate) {
                        if (newDate && newDate.year) {
                            input.value = newDate.year + '-' + newDate.month + '-' + newDate.day;
                            cobalt.log('setting storage date ', newDate);
                            cobalt.storage.setItem('CobaltDatePickerValue_' + id, newDate, 'json');
                            cobalt.datePicker.enhanceFieldValue.apply(input);
                        } else {
                            cobalt.log('removing storage date');
                            input.value = "";
                            cobalt.storage.removeItem('CobaltDatePickerValue_' + id)
                        }
                    });
                    return false;

                }, false);

            });
        },
        val: function (input) {
            var date = cobalt.storage.getItem('CobaltDatePickerValue_' + cobalt.utils.attr(input, 'id'), 'json');
            if (date) {
                var str_date = cobalt.datePicker.stringifyDate(date);
                cobalt.log('returning storage date ', str_date);
                return str_date;
            }
            return undefined;
        }
    },

    //default behaviours
    handleCallback: cobalt.defaultBehaviors.handleCallback,
    handleUnknown: cobalt.defaultBehaviors.handleUnknown
};
cobalt.adapter = cobalt.android_adapter;