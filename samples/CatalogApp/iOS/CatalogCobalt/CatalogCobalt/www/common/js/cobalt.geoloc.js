(function(cobalt){
	var plugin={
		name:"geoloc",
		init:function(options){
			cobalt.log('initing geoloc plugin with options', options)
			
			//create shortcuts
			cobalt.getGeoloc=this.getGeoloc;
			
		},
        getGeoloc:function(callback){
			this.callback=callback;
			cobalt.log('sending getGeoloc call')
			cobalt.send({ type : "plugin", name:"geoloc"}, callback)
			
		},
		handleEvent:function(data){
			cobalt.log('received native getGeoloc call', data)			
		}
	};
	cobalt.plugins.register(plugin);
	
})(cobalt || {});

