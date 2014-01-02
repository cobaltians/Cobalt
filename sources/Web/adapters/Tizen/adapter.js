nativeBridge.tizen_adapter={
	//
	//TIZEN ADAPTER
	//
	init:function(){
		//add the object that will talk to tizen to the current webpage
		$('body').prepend('<object id="Tizen" type="application/x-tizen-jsbridge" width="0" height="0" style="position:absolute;"></object>');

		nativeBridge.navigate=this.navigate;
		nativeBridge.defaultBehaviors.initStorage(); //cause we need it for push !

		nativeBridge.platform="Tizen";
	},
	//Navigate to an other page or do some special navigation actions
	//See doc for guidelines.
	navigate:function(navigationType, navigationPageName, navigationClassId){
		switch (navigationType){
			case "push":
				if (navigationPageName){
					if ( nativeBridge.checkDependency('storage') ){
						var pushNumber= utils.storage.getItem('nativeBridge_pushNumber','int') || 0;
						pushNumber++;
						utils.storage.setItem('nativeBridge_pushNumber',pushNumber)
						nativeBridge.send({ "type":"typeNavigation", "navigationType":"push", "navigationPageName":navigationPageName, "navigationClassId": navigationClassId, 'pushNumber':pushNumber});
					}
				}
			break;
			case "pop":
				nativeBridge.send({ "type":"typeNavigation", "navigationType":"pop"});
			break;
			case "modale":
				if (navigationPageName){
					nativeBridge.adapter.navigateToModale(navigationPageName, navigationClassId);
				}
			break;
			case "dismiss":
				nativeBridge.adapter.dismissFromModale();
			break;
		}
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
				var jsondata = {name:"HPNativeBridge", data:obj};
				Tizen.requestToNative(JSON.stringify(jsondata));        
			}catch (e){
		        nativeBridge.log('cant connect to native : '+e, false)
	        }
        }
    },
	//default behaviours
    handleCallback : nativeBridge.defaultBehaviors.handleCallback,
    navigateToModale : nativeBridge.defaultBehaviors.navigateToModale,
	dismissFromModale : nativeBridge.defaultBehaviors.dismissFromModale,
	initStorage : nativeBridge.defaultBehaviors.initStorage
};
nativeBridge.adapter=nativeBridge.tizen_adapter;