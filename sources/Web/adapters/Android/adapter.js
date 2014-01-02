nativeBridge.android_adapter={
	//
	//ANDROID ADAPTER
	//
	init:function(){
		nativeBridge.platform="Android";
	},
	// handle events sent by native side
    handleEvent:function(event){
		nativeBridge.log("----received : "+JSON.stringify(event), false)
        if (nativeBridge.userEvents && typeof nativeBridge.userEvents[event.name] === "function"){
			nativeBridge.userEvents[event.name](event);
	    }else{
	        switch (event.name){
		        case "onBackButtonPressed":
				    nativeBridge.log('sending OK for a native back')
			        nativeBridge.sendCallback(event,true);
			    break;
	        }
        }
    },
    //send native stuff
    send:function(obj){
        if (obj && nativeBridge.sendingToNative){
        	nativeBridge.log('----sending :'+JSON.stringify(obj), false)
	        try{	        	
		        Android.handleMessageSentByJavaScript(JSON.stringify(obj));
	        }catch (e){
		        nativeBridge.log('cant connect to native', false)
	        }

        }
    },
	//modale stuffs. really basic on ios, more complex on android.
	navigateToModale:function(navigationPageName, navigationClassId){
		if ( nativeBridge.checkDependency('storage') ){
			nativeBridge.send({ "type":"typeNavigation", "navigationType":"modale", "navigationPageName":navigationPageName, 
								"navigationClassId": navigationClassId, "callbackID":"nativeBridge.adapter.storeModaleInformations"});
		}
	},
	dismissFromModale:function(){
		if ( nativeBridge.checkDependency('storage') ){
			var dismissInformations= utils.storage.getItem("dismissInformations","json");
			if (dismissInformations && dismissInformations.navigationPageName && dismissInformations.navigationClassName){
				nativeBridge.send({  "type":"typeNavigation","navigationType":"dismiss",
					                  navigationPageName : dismissInformations.navigationPageName,
					                  navigationClassName:dismissInformations.navigationClassName
				                  });
				utils.storage.removeItem("dismissInformations");
			}else{
				nativeBridge.log("dismissInformations are not available in storage")
			}
		}


	},
	storeModaleInformations:function(params){
		//nativeBridge.log("storing informations for the dismiss :", false)
		if ( nativeBridge.checkDependency('storage') ){
			nativeBridge.log(params, false)
			utils.storage.setItem("dismissInformations",params, "json")

		}
	},
	//localStorage stuff
	initStorage:function(){
		//on android, try to bind window.localStorage to Android LocalStorage
		try{
			window.localStorage=LocalStorage;
		}catch(e){
			nativeBridge.log("LocalStorage ERROR : can't find android class LocalStorage. switching to raw localStorage")
		}
		return utils.storage.enable();
	},
	//default behaviours
    handleCallback : nativeBridge.defaultBehaviors.handleCallback
};
nativeBridge.adapter=nativeBridge.android_adapter;
