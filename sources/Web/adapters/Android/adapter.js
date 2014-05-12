cobalt.android_adapter={
	//
	//ANDROID ADAPTER
	//
	init:function(){
		cobalt.platform="Android";
	},
	// handle events sent by native side
    handleEvent:function(json){
		cobalt.log("received event", json.event)
        if (cobalt.userEvents && typeof cobalt.userEvents[json.event] === "function"){
			cobalt.userEvents[json.event](json.data,json.callback);
	    }else{
	        switch (json.event){
		        case "onBackButtonPressed":
				    cobalt.log('sending OK for a native back')
			        cobalt.sendCallback(json.callback,{value : true});
			    break;
                default :
                    cobalt.adapter.handleUnknown(json);
                break;
	        }
        }
    },
    //send native stuff
    send:function(obj){
        if (obj && !cobalt.debugInBrowser){
        	cobalt.divLog('sending',obj)
	        try{	        	
		        Android.handleMessageSentByJavaScript(JSON.stringify(obj));
	        }catch (e){
		        cobalt.log('ERROR : cant connect to native')
	        }

        }
    },
	//modal stuffs. really basic on ios, more complex on android.
	navigateToModal:function(page, controller){
		cobalt.send({ "type":"navigation", "action":"modal", data : { page :page, controller: controller }}, 'cobalt.adapter.storeModalInformations');
	},
	dismissFromModal:function(){
        var dismissInformations= cobalt.storage.getItem("dismissInformations","json");
        if (dismissInformations && dismissInformations.page && dismissInformations.controller){
            cobalt.send({ "type":"navigation", "action":"dismiss", data : { page : dismissInformations.page, controller:dismissInformations.controller }});
            cobalt.storage.removeItem("dismissInformations");
        }else{
            cobalt.log("WANRING : dismissInformations are not available in storage")
        }

	},
	storeModalInformations:function(params){
		cobalt.divLog("storing informations for the dismiss :", params)
		cobalt.storage.setItem("dismissInformations",params, "json")

	},
	//localStorage stuff
	initStorage:function(){
		//on android, try to bind window.localStorage to Android LocalStorage
		try{
			window.localStorage=LocalStorage;
		}catch(e){
			cobalt.log("LocalStorage WARNING : can't find android class LocalStorage. switching to raw localStorage")
		}
		return cobalt.storage.enable();
	},
	//default behaviours
    handleCallback : cobalt.defaultBehaviors.handleCallback,
    handleUnknown : cobalt.defaultBehaviors.handleUnknown
};
cobalt.adapter=cobalt.android_adapter;