cobalt.tizen_adapter={
	//
	//TIZEN ADAPTER
	//
	init:function(){
		//add the object that will talk to tizen to the current webpage
		$('body').prepend('<object id="Tizen" type="application/x-tizen-jsbridge" width="0" height="0" style="position:absolute;"></object>');

		cobalt.navigate=this.navigate;
		cobalt.defaultBehaviors.initStorage(); //cause we need it for push !

		cobalt.platform="Tizen";
	},
	//Navigate to an other page or do some special navigation actions
	//See doc for guidelines.
	navigate:function(navigationType, navigationPageName, navigationClassId){
		switch (navigationType){
			case "push":
				if (navigationPageName){
					if ( cobalt.checkDependency('storage') ){
						var pushNumber= utils.storage.getItem('cobalt_pushNumber','int') || 0;
						pushNumber++;
						utils.storage.setItem('cobalt_pushNumber',pushNumber)
						cobalt.send({ "type":"typeNavigation", "navigationType":"push", "navigationPageName":navigationPageName, "navigationClassId": navigationClassId, 'pushNumber':pushNumber});
					}
				}
			break;
			case "pop":
				cobalt.send({ "type":"typeNavigation", "navigationType":"pop"});
			break;
			case "modale":
				if (navigationPageName){
					cobalt.adapter.navigateToModale(navigationPageName, navigationClassId);
				}
			break;
			case "dismiss":
				cobalt.adapter.dismissFromModale();
			break;
		}
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
				var jsondata = {name:"HPNativeBridge", data:obj};
				Tizen.requestToNative(JSON.stringify(jsondata));        
			}catch (e){
		        cobalt.log('cant connect to native : '+e, false)
	        }
        }
    },
	//default behaviours
    handleCallback : cobalt.defaultBehaviors.handleCallback,
    navigateToModale : cobalt.defaultBehaviors.navigateToModale,
	dismissFromModale : cobalt.defaultBehaviors.dismissFromModale,
	initStorage : cobalt.defaultBehaviors.initStorage
};
cobalt.adapter=cobalt.tizen_adapter;