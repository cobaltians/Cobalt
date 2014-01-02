/* helper object to communicate with native */
var nativeBridge={
    
    userEvents:{}, //objects of events defined by the user
	debug:false, //disable stuff for all nativeBridge. dont send logs to native
	sendingToNative:true, //enable or disable native calls

	callbacks:{},//array of all callbacks by callbackID
	lastCallbackId:0,

	/*	nativeBridge.init(options)
		see doc for options
	*/
	init:function(options){
    	if (options){
			if (options.sendingToNative===false){
				this.sendingToNative=false;
			}
			if (options.debug===true){
				this.debug=true;
				this.createLogDiv();
			}
		    if (options.debugAjax===true){
			    this.debugAjax=true;
		    }
			if (options.events){
		        this.userEvents=options.events
	        }
		    if (options.storage===true){
			    nativeBridge.initStorage();
		    }
		}
		if (nativeBridge.adapter.init){
			nativeBridge.adapter.init();
		}
		//send nativeBridge is ready event to native
		nativeBridge.send({'type':'nativeBridgeIsReady'})
    },


	/*	nativeBridge.log(stuff,sendLogToNative)
		stuff : a string or an object. object will be json-ised
		sendLogToNative : boolean to call native log function. default to true.
			be carefull of looping calls !
	*/
	log:function( stuff, sendLogToNative ){
	    if ( nativeBridge.debug ){
		    if (sendLogToNative === undefined) sendLogToNative=true;
		 	stuff=nativeBridge.toString(stuff)
		    var logdiv=$('#nativeBridge_logdiv')
		   	if (logdiv.length){
				try{
                    logdiv.append("<br/>"+nativeBridge.HTMLEncode(stuff));
                }catch(e){
                    logdiv.append("<br/><b>nativeBridge.log failed on something.</b>");
                }
			}
		    if( sendLogToNative ){
			    nativeBridge.send({"type":"typeLog","value":stuff})
	        }
	    }
    },
	/* internal, create log div if needed */
	createLogDiv:function(){
		if ($('#nativeBridge_logdiv').length==0){
			//create usefull log div:
			$('body').append('<div id="nativeBridge_logdiv" style="position: absolute; top:10px; right: 10px; padding:10px; width:10px; height: 10px; border:1px solid blue; overflow: hidden; "></div>')
			$('#nativeBridge_logdiv').on('tap',nativeBridge.toggleLogDiv).on('click',nativeBridge.toggleLogDiv);
		}
	},
	/* internal, toggle visibility of log div if log div was created by the lib */
	toggleLogDiv:function(){
		if ($(this).css('width')!="250px"){
			$(this).css({ width : '250px', height:'300px', overflow:"scroll"});
		}else{
			$(this).css({ width : '10px', height:'10px', overflow:"hidden"});
		}
	},
	//Sends an object to native side.
	//See doc for guidelines.
	send:function(obj, callback){
		if (callback){
			if (typeof callback==="function"){
				obj.callbackID = nativeBridge.lastCallbackId++;
				nativeBridge.callbacks[obj.callbackID]=callback;
			}else if (typeof callback==="string"){
				obj.callbackID = ""+callback;
			}
	    }
		nativeBridge.adapter.send(obj, callback)
	},
	//Sends an event to native side.
	//See doc for guidelines.
    sendEvent:function(eventName, params, callback){
	    if (eventName){
		    var obj = params || {};
		    obj.type = "typeEvent";
		    obj.name = eventName;
		    nativeBridge.send(obj, callback);
	    }
	},
	//Sends a callback to native side.
	//See doc for guidelines.
    sendCallback:function(originalEvent, params){
		var event=originalEvent;
		if (typeof event.callbackID ==="string" && event.callbackID.length > 0){
			//nativeBridge.log("calling callback with "+JSON.stringify({type:"typeCallback", callbackID:event.callbackID, params: params}))
			nativeBridge.send({type:"typeCallback", callbackID:event.callbackID, params: params})
	    }
	},
	//Navigate to an other page or do some special navigation actions
	//See doc for guidelines.
	navigate:function(navigationType, navigationPageName, navigationClassId){
		switch (navigationType){
			case "push":
				if (navigationPageName){
					nativeBridge.send({ "type":"typeNavigation", "navigationType":"push", "navigationPageName":navigationPageName, "navigationClassId": navigationClassId});
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
	/* sends a toast request to native */
	toast:function(text){
		nativeBridge.sendEvent("nameToast", { "value" : nativeBridge.toString(text) });
	},

	/*  Raise a native alert with options
		See doc for guidelines.

		//full web
		nativeBridge.alert("Texte");
		nativeBridge.alert("Title", "Texte", ["Ok"], { callback:function(index){console.log('popup dismissed') }});
		nativeBridge.alert("Title", "Texte", ["Ok"], { callback:"app.popupDismissed", alertId:12 });

		//native callbacks
		nativeBridge.alert("Title", "Texte", ["Ok"], { callbackType:"native", alertId:12 });

	 */
	alert:function(title, text, buttons, options){
		if (title || text){
			var obj={ type:"typeAlert"}
			var callback;
			if (text) obj.alertMessage = text;
			if (title) obj.alertTitle = title;

			//Add buttons if any
			if (buttons && nativeBridge.isArray(buttons) && buttons.length){
				obj.alertButtons=buttons;
			}
			//check options
			if ( options ){
				//add web callback if any
				if (typeof options.callback === "string" || typeof options.callback === "function"){
					obj.alertReceiver="web";
					callback=options.callback;
				}
				//add alertIdentifier
				obj.alertId=parseInt(options.alertId);
				//set callbacks as native if asked
				if (options.callbackType === "native"){
					obj.alertReceiver="native";
					if (callback){
						nativeBridge.log("warning : alert callback has been set to native, web callback won't be called.");
					}
					callback=undefined;
				}
				if ( options.mandatory === true ){
					obj.alertIsCancelable=false;
				}
			}
			//enforce alertId presence :
			if (!obj.alertId || !nativeBridge.isNumber(obj.alertId)){
				obj.alertId=0;
			}
			nativeBridge.send(obj, callback);
		}
	},
	/*
		show a web page as an alert.
		//see doc for guidelines.
		//nativeBridge.webAlert("show","tests_12_webAlertContent.html",1.2);
		//nativeBridge.webAlert("dismiss");
	 */
	webAlert:function(action, pageName, fadeDuration){
		switch (action){
			case "dismiss":
				nativeBridge.send({type:"typeWebAlert", name:"dismiss"});
			break;
			case "show":
				if (pageName){
					nativeBridge.send({type:"typeWebAlert", name:"show", pageName:pageName, fadeDuration:fadeDuration})
				}
			break;
		}
	},
    /* internal, called from native */
    execute:function(data){
    	//nativeBridge.log(data,false)
        /*test if data.type exists, otherwise parse data or die silently */
        if (data && ! data.type){
        	try{
                data = JSON.parse(data);
            }catch(e){
                data = {};
            }
        }
        try{
	        switch (data.type){
	        	case "typeEvent":
                    nativeBridge.adapter.handleEvent(data)
	            break
	            case "typeCallback":
                    nativeBridge.adapter.handleCallback(data)
                    break;
                case "typeLog":
                    nativeBridge.log('LOG '+decodeURIComponent(data.value), data.logBack)
                break
	        	default:
	        		nativeBridge.log('received unhandled data type : '+data.type)        		
	        }
	    }catch(e){
            nativeBridge.log('nativeBridge.execute failed : '+e)
        }
    },
	//internal function to try calling callbackID if it's representing a string or a function.
	tryToCallCallback:function(callback){
		//nativeBridge.log('trying to call web callback')
		var callbackfunction=null;
        if (nativeBridge.isNumber(callback.callbackID) && typeof nativeBridge.callbacks[callback.callbackID]==="function"){
	        //if it's a number, a real JS callback should exist in nativeBridge.callbacks
	        callbackfunction=nativeBridge.callbacks[callback.callbackID]

		}else if (typeof callback.callbackID === "string"){
	        //if it's a string, check if function exists
	        callbackfunction=eval(callback.callbackID)
		}
		if (typeof callbackfunction === "function"){
	        try{
		        callbackfunction(callback.params)
	        }catch(e){
		        nativeBridge.log('Failed calling callback #'+callback.callbackID+'.')
	        }
        }
	},
	//internal, call adapter.initStorage.
	initStorage:function(){
		//only enable once if ok.
		if (! nativeBridge.localStorageEnabled){
			//init from adapter
			nativeBridge.localStorageEnabled=nativeBridge.adapter.initStorage();
			//if wrong state
			if (! nativeBridge.localStorageEnabled){
				nativeBridge.log("LocalStorage ERROR : localStorage not available !")
			}
		}
		return nativeBridge.localStorageEnabled;
	},

	// usefull functions
	isNumber : function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
	},
	isArray:function(arr){
		return ( Object.prototype.toString.call( arr ) === '[object Array]' );
	},
	toString: function(stuff){
		if (typeof stuff != "string"){
			try{
				stuff=JSON.stringify(stuff)
			}catch (e){
				stuff = ""+stuff;
			}
		}
		return stuff;
	},
	HTMLEncode:function(value){
        return $('<div/>').text(value || '').html()
    },
    HTMLDecode:function(value){
        return $('<div/>').html(value || '').text();
    },
	checkDependency:function(dependency){
		switch(dependency){
			case "storage":
				if ( ! window.utils || ! window.utils.storage){
					nativeBridge.log('WARNING : window.utils.storage is not set. it is required for some navigate calls')
					return false;

				}
				return nativeBridge.initStorage();
			break;
		}
	},

	defaultBehaviors:{
		handleCallback:function(callback){
	        switch(callback.callbackID){
	            default:
				    nativeBridge.tryToCallCallback(callback)
			    break;
	        }
	    },
		navigateToModale:function(navigationPageName, navigationClassId){
			nativeBridge.send({ "type":"typeNavigation", "navigationType":"modale", "navigationPageName":navigationPageName, "navigationClassId": navigationClassId});
		},
		dismissFromModale:function(){
			nativeBridge.send({ "type":"typeNavigation", "navigationType":"dismiss"});
		},
		initStorage:function(){
			if (window.utils && utils.storage){
				return utils.storage.enable();
			}else{
				nativeBridge.log('WARNING : you should include utils.storage to use storage')
			}
			return false;
		}
	}
};
nativeBridge.android_adapter={
	//
	//ANDROID ADAPTER
	//
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
