(function(cobalt){
    var plugin={
        name:"location",
        init:function(options){
            cobalt.log('initing location plugin with options', options)

            //create shortcuts
            cobalt.getLocation=this.getLocation;

        },
        getLocation:function(callback){
            cobalt.log('sending getLocation call')
            cobalt.send({ type : "plugin", name:"location", action : "getLocation"}, callback)

        },
        handleEvent:function(data){
            cobalt.log('received native location plugin call', data)
        }
    };
    cobalt.plugins.register(plugin);

})(cobalt || {});

