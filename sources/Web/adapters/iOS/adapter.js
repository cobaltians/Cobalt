cobalt.ios_adapter={
	//
	//IOS ADAPTER
	//
    pipeline:[], //array of sends waiting to go to native
    pipelineRunning:false,//bool to knwow if new sends should go to pipe or go to native

	init:function(){
		cobalt.platform="iOs";
	},
	// handle events sent by native side
    handleEvent:function(event){
		cobalt.log("<b>received</b> : "+JSON.stringify(event), false)
	    if (cobalt.userEvents && typeof cobalt.userEvents[event.name] === "function"){
			cobalt.userEvents[event.name](event);
	    }
    },
    // handle callbacks sent by native side
    handleCallback:function(callback){
        switch(callback.callbackID){
            case "callbackSimpleAcquitment":
                //cobalt.log("received callbackSimpleAcquitment", false)
                cobalt.adapter.unpipe();
                
                if (cobalt.adapter.pipeline.length==0){
                    cobalt.log('set pipe running=false', false)
                    cobalt.adapter.pipelineRunning=false;
                }
                
                break;
	        default:
			    cobalt.tryToCallCallback(callback)
		    break;
        }
    },
    //send native stuff
    send:function(obj){
	    cobalt.log('adding to pipe', false)
        cobalt.adapter.pipeline.push(obj);
        if (!cobalt.adapter.pipelineRunning){
            cobalt.adapter.unpipe()
        }
    },
    //unpipe elements when receiving a ACK from ios.
    unpipe:function(){
        cobalt.adapter.pipelineRunning=true;
        var objToSend=cobalt.adapter.pipeline.shift();
	    if (objToSend && cobalt.sendingToNative){
            cobalt.log('----sending : '+JSON.stringify(objToSend), false)
            document.location.href=encodeURIComponent("h@ploid#k&y"+JSON.stringify(objToSend));
        }
    },
	//default behaviours
	navigateToModale : cobalt.defaultBehaviors.navigateToModale,
	dismissFromModale : cobalt.defaultBehaviors.dismissFromModale,
	initStorage : cobalt.defaultBehaviors.initStorage

};
cobalt.adapter=cobalt.ios_adapter;