cobalt.bb10_adapter={
	//
	//BB10 ADAPTER
	//
	init:function(){
		//bind message from native handler
 		try{
 			navigator.cascades.onmessage = cobalt.execute
 		}catch(e){
 	        cobalt.log('cant bind JS to native')
 		}

		//fix ajax calls with a native patch
		if (!cobalt.debugInBrowser){
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
    handleEvent:function(json){
		cobalt.log("received event", json.event )
		if (cobalt.userEvents && typeof cobalt.userEvents[json.event] === "function"){
			cobalt.userEvents[json.event](json.data,json.callback);
	    }else{
	        switch (json.event){
		        case "onBackButtonPressed":
				    cobalt.log('sending OK for a native back')
			        cobalt.sendCallback(json.callback,{value : true});
			    break;
	        }
        }
    },
    //send native stuff
    send:function(obj){
        if (obj && !cobalt.debugInBrowser){
        	cobalt.divLog('sending', obj)
	        try{	        	
		        navigator.cascades.postMessage(encodeURIComponent(JSON.stringify(obj)));
	        }catch (e){
		        cobalt.log('cant connect to native')
	        }

        }
    },
	//default behaviours
    handleCallback : cobalt.defaultBehaviors.handleCallback,
    navigateToModal : cobalt.defaultBehaviors.navigateToModal,
	dismissFromModal : cobalt.defaultBehaviors.dismissFromModal,
	initStorage : cobalt.defaultBehaviors.initStorage
};
cobalt.adapter=cobalt.bb10_adapter;