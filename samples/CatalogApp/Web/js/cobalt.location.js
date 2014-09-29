(function(cobalt){
    var plugin={
        name:"location",
        onError:undefined,
        onSuccess:undefined,

        init:function(options){
            cobalt.log('init location plugin with options', options)

            //create shortcuts
            cobalt.getLocation=this.getLocation.bind(this);

            if (options && typeof options.onError == "function"){
                this.onError=options.onError;
            }

        },
        getLocation:function(callback){
            if (typeof callback== "function"){
                this.onSuccess = callback;
            }
            cobalt.log('sending getLocation call', this.onSuccess)
            cobalt.send({ type : "plugin", name:"location", action : "getLocation"})

        },
        handleEvent:function(json){
            cobalt.log('received native location plugin event', json)
            if (json && json.data){
                if (json.data.error){
                    if (this.onError){
                        this.onError(json.data.code, json.data.text)
                    }else{
                        cobalt.log('location plugin error', json.data.code, json.data.text)
                    }
                }else{
                    if (this.onSuccess){
                        this.onSuccess(json.data.value)
                    }else{
                        cobalt.log('location plugin success. but no callback defined. ', json.data.value, this)
                    }
                }
            }
        }
    };
    cobalt.plugins.register(plugin);

})(cobalt || {});