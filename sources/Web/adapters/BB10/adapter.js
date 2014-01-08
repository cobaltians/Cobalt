cobalt.bb10_adapter={
	//
	//BB10 ADAPTER
	//
	init:function(){
		//bind message from native handler
 		try{
 			navigator.cascades.onmessage = cobalt.execute
 		}catch(e){
 	        cobalt.log('cant bind JS to native', false)
 		}

		//fix ajax calls with a native patch
		if (!cobalt.debugAjax){
			var lib=window.Zepto || window.jQuery;
			lib.ajax=function(options){
				cobalt.sendEvent('ajax',{options : options},function(params){
	                if (params.success && options.success){
	                    options.success(params.data);
	                }else if (params.error){
	                    try{
				       		options.error({ status:params.status, statusText:params.statusText });
			            }catch(e){
				    		cobalt.log('cant call ajax error callback : '+ e)
			            }
	                }
	            });
			}
		}
		
		cobalt.platform="BB10";
	},
	
	// handle events sent by native side
    handleEvent:function(event){
		cobalt.log("----received : "+JSON.stringify(event), false)
        if (cobalt.userEvents && typeof cobalt.userEvents[event.name] === "function"){
			cobalt.userEvents[event.name](event);
	    }
    },
    //send native stuff
    send:function(obj){
        if (obj && cobalt.sendingToNative){
        	cobalt.log('----sending :'+JSON.stringify(obj), false)
	        try{	        	
		        navigator.cascades.postMessage(encodeURIComponent(JSON.stringify(obj)));
	        }catch (e){
		        cobalt.log('cant connect to native', false)
	        }

        }
    },
	//default behaviours
    handleCallback : cobalt.defaultBehaviors.handleCallback,
    navigateToModale : cobalt.defaultBehaviors.navigateToModale,
	dismissFromModale : cobalt.defaultBehaviors.dismissFromModale,
	initStorage : cobalt.defaultBehaviors.initStorage
};
cobalt.adapter=cobalt.bb10_adapter;