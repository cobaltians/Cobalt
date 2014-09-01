(function(cobalt){
    var plugin={
        name:"location",
        onError:undefined,

        init:function(options){
            cobalt.log('initing location plugin with options', options)

            //create shortcuts
            cobalt.getLocation=this.getLocation;

            if (options && typeof options.onError == "function"){
                this.onError=options.onError;
            }

        },
        getLocation:function(callback){
            cobalt.log('sending getLocation call')
            cobalt.send({ type : "plugin", name:"location", action : "getLocation"}, callback)

        },
        handleEvent:function(json){
            cobalt.log('received native location plugin call', json)
            if (json && json.data && json.data.error){
                if (this.onError){
                    this.onError(json.data.code, json.data.text)
                }else{
                    cobalt.log('location plugin error', json.data.code, json.data.text)
                }
            }
        }
    };
    cobalt.plugins.register(plugin);

})(cobalt || {});

