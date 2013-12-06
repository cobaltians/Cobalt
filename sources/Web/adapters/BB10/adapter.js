nativeBridge.bb10_adapter={
	//
	//BB10 ADAPTER
	//
	init:function(){
		//bind message from native handler
 		try{
 			navigator.cascades.onmessage = nativeBridge.execute
 		}catch(e){
 	        nativeBridge.log('cant bind JS to native', false)
 		}

		//fix ajax calls with a native patch
		if (!nativeBridge.debugAjax){
			var lib=window.Zepto || window.jQuery;
			lib.ajax=function(options){
				nativeBridge.sendEvent('ajax',{options : options},function(params){
	                if (params.success && options.success){
	                    options.success(params.data);
	                }else if (params.error){
	                    try{
				       		options.error({ status:params.status, statusText:params.statusText });
			            }catch(e){
				    		nativeBridge.log('cant call ajax error callback : '+ e)
			            }
	                }
	            });
			}
		}
	},
	
	// handle events sent by native side
    handleEvent:function(event){
		nativeBridge.log("----received : "+JSON.stringify(event), false)
        if (nativeBridge.userEvents && typeof nativeBridge.userEvents[event.name] === "function"){
			nativeBridge.userEvents[event.name](event);
	    }
    },
    //send native stuff
    send:function(obj){
        if (obj && nativeBridge.sendingToNative){
        	nativeBridge.log('----sending :'+JSON.stringify(obj), false)
	        try{	        	
		        navigator.cascades.postMessage(encodeURIComponent(JSON.stringify(obj)));
	        }catch (e){
		        nativeBridge.log('cant connect to native', false)
	        }

        }
    },
	//default behaviours
    handleCallback : nativeBridge.defaultBehaviors.handleCallback,
    navigateToModale : nativeBridge.defaultBehaviors.navigateToModale,
	dismissFromModale : nativeBridge.defaultBehaviors.dismissFromModale,
	initStorage : nativeBridge.defaultBehaviors.initStorage
};
nativeBridge.adapter=nativeBridge.bb10_adapter;