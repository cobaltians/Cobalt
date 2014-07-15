//move this to core
cobalt.plugins={
	enabledPlugins:[],
	init:function(){		
		for (var i=0; i<cobalt.plugins.enabledPlugins.length;i++){
			cobalt.plugins.enabledPlugins[i].init();
		}
		
	}

}


//(function(cobalt){
	var plugin={
		name:"geoloc",
		init:function(){
			cobalt.log('initing geoloc plugin')			
			
			//create shortcuts
			cobalt.getGeoloc=this.sendEvent;
			
		},
		sendEvent:function(callback){
			this.callback=callback;
			cobalt.log('sending getGeoloc call')
			cobalt.send({ type : "plugin", name:"geoloc"}, callback)
			
		},
		handleEvent:function(data){
			cobalt.log('received native getGeoloc call', data)			
		}
	
	}

	//keep this in geoloc plugin
	cobalt.plugins.enabledPlugins.push(plugin);
	
	//})(cobalt || {});
cobalt.plugins.init();
