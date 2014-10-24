(function(cobalt){
    var plugin={
        name:"webservices",

        settings:{
            base:{
                url :"",
                params : {}
            },
            defaultParameters:{
                type : "GET",
                saveToStorage : false
            }
        },
        calls:{},

        init:function(options){
            cobalt.log('init webservices plugin with options', options)

            //create shortcuts
            cobalt.ws={
                call : this.call.bind(this),
                config : this.config.bind(this)
            }
            if (options){
                this.config(options);
            }

        },
        config:function(settings){
            if (settings){
                if (settings.base){
                    this.settings.base = cobalt.utils.extend(this.settings.base, settings.base);
                }
                if (settings.defaultParameters){
                    this.settings.defaultParameters = cobalt.utils.extend(this.settings.defaultParameters, settings.defaultParameters);
                }
            }
        },
        call:function(options){
            var self=this;
            var newCall = {
                url :           self.settings.base.url + options.url,
                params :        cobalt.utils.extend( this.settings.base.params, options.params ),
                type :          options.type || this.settings.defaultParameters.type,
                filterData :    options.filterData || this.settings.defaultParameters.filterData,
                storageKey :    ( typeof options.storageKey =="string" && options.storageKey.length ) ? options.storageKey : undefined,

                successCallback : (typeof options.successCallback =="function") ?  options.successCallback : self.settings.defaultParameters.successCallback || undefined,
                errorCallback : (typeof options.errorCallback =="function") ?  options.errorCallback : self.settings.defaultParameters.errorCallback || undefined
            }
            if (newCall.storageKey){
                newCall.saveToStorage = true;
                newCall.cacheCallback = (typeof options.cacheCallback =="function") ?  options.cacheCallback : self.settings.defaultParameters.cacheCallback || undefined;
            }

            self.send(newCall, function( data ){
                cobalt.log('WS call started with id = '+data.callId)
                newCall.callId = data.callId;
                self.calls[data.callId] = newCall;
            })


        },
        handleEvent:function(json){
            cobalt.log('received webservices plugin event', json)
            var data = ( json && json.data ) ? json.data : undefined;
            if (data && data.callId && this.calls[data.callId]){
                var concernedCall = this.calls[data.callId];
                switch (json.action){
                    case "onWSError":
                        cobalt.log("dddaata",data.data)
                        if (concernedCall.errorCallback){
                            concernedCall.errorCallback( data.data || data.text, concernedCall )
                        }else{
                            cobalt.log('WS error : No JS error callback for call ' + data.callId)
                        }
                        break;
                    case "onWSResult":
                        if (concernedCall.successCallback){
                            concernedCall.successCallback( data.data || data.text, concernedCall )
                        }else{
                            cobalt.log('WS success but no JS callback for call ' + data.callId)
                        }
                        break;
                    case "onStorageResult":
                        if (concernedCall.cacheCallback){
                            concernedCall.cacheCallback( data.data || data.text, concernedCall )
                        }else{
                            cobalt.log('Some data in storage but no JS callback for call '+ data.callId)
                        }
                        break;
                    default :
                        cobalt.log('WS unknown action received from native side for call '+ data.callId);

                }
            }else{
                cobalt.log('WS unknown call event for call ', json)
            }


            
        },
        send:function(data, callback){
            cobalt.send({ type : "plugin", name : "webservices", action : "call", data : data }, callback);
        }
    };
    cobalt.plugins.register(plugin);

})(cobalt || {});