cobalt.android_adapter={
	//
	//ANDROID ADAPTER
	//
	init:function(){
		cobalt.platform="Android";
	},
	// handle events sent by native side
    handleEvent:function(event){
		cobalt.log("----received : "+JSON.stringify(event), false)
        if (cobalt.userEvents && typeof cobalt.userEvents[event.name] === "function"){
			cobalt.userEvents[event.name](event);
	    }else{
	        switch (event.name){
		        case "onBackButtonPressed":
				    cobalt.log('sending OK for a native back')
			        cobalt.sendCallback(event,true);
			    break;
	        }
        }
    },
    //send native stuff
    send:function(obj){
        if (obj && cobalt.sendingToNative){
        	cobalt.log('----sending :'+JSON.stringify(obj), false)
	        try{	        	
		        Android.handleMessageSentByJavaScript(JSON.stringify(obj));
	        }catch (e){
		        cobalt.log('cant connect to native', false)
	        }

        }
    },
	//modale stuffs. really basic on ios, more complex on android.
	navigateToModale:function(page, controller){
		if ( cobalt.checkDependency('storage') ){
			cobalt.send({ "type":"navigation", "action":"modale", data : { page :page, controller: controller }}, 'cobalt.adapter.storeModaleInformations');
		}
	},
	dismissFromModale:function(){
		if ( cobalt.checkDependency('storage') ){
			var dismissInformations= utils.storage.getItem("dismissInformations","json");
			if (dismissInformations && dismissInformations.page && dismissInformations.controller){
				cobalt.send({ "type":"navigation", "action":"dismiss", data : { page : dismissInformations.page, controller:dismissInformations.controller }});
				utils.storage.removeItem("dismissInformations");
			}else{
				cobalt.log("dismissInformations are not available in storage")
			}
		}


	},
	storeModaleInformations:function(params){
		//cobalt.log("storing informations for the dismiss :", false)
		if ( cobalt.checkDependency('storage') ){
			cobalt.log(params, false)
			utils.storage.setItem("dismissInformations",params, "json")

		}
	},
	//localStorage stuff
	initStorage:function(){
		//on android, try to bind window.localStorage to Android LocalStorage
		try{
			window.localStorage=LocalStorage;
		}catch(e){
			cobalt.log("LocalStorage ERROR : can't find android class LocalStorage. switching to raw localStorage")
		}
		return utils.storage.enable();
	},
	//default behaviours
    handleCallback : cobalt.defaultBehaviors.handleCallback
};
cobalt.adapter=cobalt.android_adapter;