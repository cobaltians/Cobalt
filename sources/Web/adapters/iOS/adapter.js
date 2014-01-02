nativeBridge.ios_adapter={
	//
	//IOS ADAPTER
	//
    pipeline:[], //array of sends waiting to go to native
    pipelineRunning:false,//bool to knwow if new sends should go to pipe or go to native

	init:function(){
		nativeBridge.platform="iOs";
	},
	// handle events sent by native side
    handleEvent:function(event){
		nativeBridge.log("<b>received</b> : "+JSON.stringify(event), false)
	    if (nativeBridge.userEvents && typeof nativeBridge.userEvents[event.name] === "function"){
			nativeBridge.userEvents[event.name](event);
	    }
    },
    // handle callbacks sent by native side
    handleCallback:function(callback){
        switch(callback.callbackID){
            case "callbackSimpleAcquitment":
                //nativeBridge.log("received callbackSimpleAcquitment", false)
                nativeBridge.adapter.unpipe();
                
                if (nativeBridge.adapter.pipeline.length==0){
                    nativeBridge.log('set pipe running=false', false)
                    nativeBridge.adapter.pipelineRunning=false;
                }
                
                break;
	        default:
			    nativeBridge.tryToCallCallback(callback)
		    break;
        }
    },
    //send native stuff
    send:function(obj){
	    nativeBridge.log('adding to pipe', false)
        nativeBridge.adapter.pipeline.push(obj);
        if (!nativeBridge.adapter.pipelineRunning){
            nativeBridge.adapter.unpipe()
        }
    },
    //unpipe elements when receiving a ACK from ios.
    unpipe:function(){
        nativeBridge.adapter.pipelineRunning=true;
        var objToSend=nativeBridge.adapter.pipeline.shift();
	    if (objToSend && nativeBridge.sendingToNative){
            nativeBridge.log('----sending : '+JSON.stringify(objToSend), false)
            document.location.href=encodeURIComponent("h@ploid#k&y"+JSON.stringify(objToSend));
        }
    },
	//default behaviours
	navigateToModale : nativeBridge.defaultBehaviors.navigateToModale,
	dismissFromModale : nativeBridge.defaultBehaviors.dismissFromModale,
	initStorage : nativeBridge.defaultBehaviors.initStorage

};
nativeBridge.adapter=nativeBridge.ios_adapter;