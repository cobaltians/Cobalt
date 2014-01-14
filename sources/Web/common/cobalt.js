/* helper object to communicate with native */
var cobalt={
    
    userEvents:{}, //objects of events defined by the user
	debug:false, //disable stuff for all cobalt. dont send logs to native
	sendingToNative:true, //enable or disable native calls

	callbacks:{},//array of all callbacks by callbackID
	lastCallbackId:0,

	/*	cobalt.init(options)
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
		}
		cobalt.storage.enable();

		if (cobalt.adapter.init){
			cobalt.adapter.init();
		}
		//send cobalt is ready event to native
		cobalt.send({'type':'cobaltIsReady'})
    },
	addEventListener:function(eventName, handlerFunction){
		if (typeof eventName === "string" && typeof handlerFunction === "function"){
			this.userEvents[eventName] = handlerFunction;
		}
	},
	removeEventListener:function(eventName){
		if (typeof eventName === "string" && this.userEvents[eventName] ){
			this.userEvents[eventName] = undefined;
		}
	},
	/*	cobalt.log(stuff,sendLogToNative)
		stuff : a string or an object. object will be json-ised
		sendLogToNative : boolean to call native log function. default to true.
			be carefull of looping calls !
	*/
	log:function( stuff, sendLogToNative ){
	    if ( cobalt.debug ){
		    if (sendLogToNative === undefined) sendLogToNative=true;
		 	stuff=cobalt.toString(stuff)
		    var logdiv=$('#cobalt_logdiv')
		   	if (logdiv.length){
				try{
                    logdiv.append("<br/>"+cobalt.HTMLEncode(stuff));
                }catch(e){
                    logdiv.append("<br/><b>cobalt.log failed on something.</b>");
                }
			}
		    if( sendLogToNative ){
			    cobalt.send({ type : "log", value : stuff })
	        }
	    }
    },
	/* internal, create log div if needed */
	createLogDiv:function(){
		if ($('#cobalt_logdiv').length==0){
			//create usefull log div:
			$('body').append('<div id="cobalt_logdiv" style="position: absolute; top:10px; right: 10px; padding:10px; width:10px; height: 10px; border:1px solid blue; overflow: hidden; "></div>')
			$('#cobalt_logdiv').on('tap',cobalt.toggleLogDiv).on('click',cobalt.toggleLogDiv);
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
				obj.callback = ""+(cobalt.lastCallbackId++);
				cobalt.callbacks[obj.callback]=callback;
			}else if (typeof callback==="string"){
				obj.callback = ""+callback;
			}
	    }
		cobalt.adapter.send(obj, callback)
	},
	//Sends an event to native side.
	//See doc for guidelines.
    sendEvent:function(eventName, params, callback){
	    if (eventName){
		    var obj = {
			    type : "event",
			    name : eventName,
			    data : params || {}
		    };
		    cobalt.send(obj, callback);
	    }
	},
	//Sends a callback to native side.
	//See doc for guidelines.
    sendCallback:function(originalEvent, data){
		var event=originalEvent;
		if (typeof event.callback ==="string" && event.callback.length > 0){
			//cobalt.log("calling callback with "+JSON.stringify({type:"typeCallback", callbackID:event.callbackID, data: data}))
			cobalt.send({type:"callback", callback :event.callback, data: data})
	    }
	},
	//Navigate to an other page or do some special navigation actions
	//See doc for guidelines.
	navigate:function(navigationType, page, controller){
		switch (navigationType){
			case "push":
				if (page){
					cobalt.send({ "type":"navigation", "action":"push", data : { page :page, controller: controller }});
				}
			break;
			case "pop":
				cobalt.send({ "type":"navigation", "action":"pop"});
			break;
			case "modale":
				if (page){
					cobalt.adapter.navigateToModale(page, controller);
				}
			break;
			case "dismiss":
				cobalt.adapter.dismissFromModale();
			break;
		}
	},
	/* sends a toast request to native */
	toast:function(text){
		cobalt.send({ type : "ui", control : "toast", data : { message : cobalt.toString(text) } } );
	},

	/*  Raise a native alert with options
		See doc for guidelines.

		//full web
		cobalt.alert("Texte");
		cobalt.alert("Title", "Texte", ["Ok"], { callback:function(data){cobalt.log('popup dismissed '+data.index) }});
		cobalt.alert("Title", "Texte", ["Ok"], { callback:"app.popupDismissed", cancelable : true });

	 */
	alert:function(title, text, buttons, options){
		if (title || text){
			var obj={ type : "ui", control : "alert", data : {
				message : text,
				title : title
			}};
			var callback;

			if (buttons && cobalt.isArray(buttons) && buttons.length){
				obj.data.buttons=buttons;
			}

			//check options
			if ( options ){
				//add web callback if any
				if (typeof options.callback === "string" || typeof options.callback === "function"){
					callback=options.callback;
				}
				if ( options.cancelable ){
					obj.data.cancelable=true;
				}
			}

			//enforce alertId presence :
			if (!obj.data.id || !cobalt.isNumber(obj.data.id)){
				obj.data.id=0;
			}
			cobalt.send(obj, callback);
		}
	},
	/*
		show a web page as an alert.
		//see doc for guidelines.
		//cobalt.webLayer("show","tests_12_webAlertContent.html",1.2);
		//cobalt.webLayer("dismiss");
		//in next example, foobar object will be sent in onWebLayerDismissed :
		//cobalt.webLayer("dismiss",{ foo : "bar"});
	 */
	webLayer:function(action, data, fadeDuration){
		switch (action){
			case "dismiss":
				cobalt.send({type:"webLayer", action:"dismiss", data: data});
			break;
			case "show":
				if (page){
					cobalt.send({type:"webLayer", action:"show", data :{ page:data, fadeDuration:fadeDuration }})
				}
			break;
		}
	},
    /* internal, called from native */
    execute:function(data){
    	//cobalt.log(data,false)
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
	        	case "event":
                    cobalt.adapter.handleEvent(data)
	                break;
	            case "callback":
                    cobalt.adapter.handleCallback(data)
                    break;
		        case "log": //TODO : is it needed?
                    cobalt.log('LOG '+decodeURIComponent(data.value), data.logBack)
                    break;
	        	default:
	        		cobalt.log('received unhandled data type : '+data.type)        		
	        }
	    }catch(e){
            cobalt.log('cobalt.execute failed : '+e)
        }
    },
	//internal function to try calling callbackID if it's representing a string or a function.
	tryToCallCallback:function(callback){
		//cobalt.log('trying to call web callback')
		var callbackfunction=null;
        if (cobalt.isNumber(callback.callback) && typeof cobalt.callbacks[callback.callback]==="function"){
	        //if it's a number, a real JS callback should exist in cobalt.callbacks
	        callbackfunction=cobalt.callbacks[callback.callback]

		}else if (typeof callback.callback === "string"){
	        //if it's a string, check if function exists
	        callbackfunction=eval(callback.callback)
		}
		if (typeof callbackfunction === "function"){
	        try{
		        callbackfunction(callback.data)
	        }catch(e){
		        cobalt.log('Failed calling callback : ' + e)
	        }
        }
	},
	//internal, call adapter.initStorage.
	initStorage:function(){
		//only enable once if ok.
		if (! cobalt.localStorageEnabled){
			//init from adapter
			cobalt.localStorageEnabled=cobalt.adapter.initStorage();
			//if wrong state
			if (! cobalt.localStorageEnabled){
				cobalt.log("LocalStorage ERROR : localStorage not available !")
			}
		}
		return cobalt.localStorageEnabled;
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
				return cobalt.storage.enable()
			break;
		}
	},

	defaultBehaviors:{
		handleCallback:function(callback){
	        switch(callback.callback){
	            default:
				    cobalt.tryToCallCallback(callback)
			    break;
	        }
	    },
		navigateToModale:function(page, controller){
			cobalt.send({ "type":"navigation", "action":"modale", data : { page :page, controller: controller }});
		},
		dismissFromModale:function(){
			cobalt.send({ "type":"typeNavigation", "action":"dismiss"});
		},
		initStorage:function(){
			return cobalt.storage.enable()
		}
	},


	storage : {
		/*	localStorage helper

			cobalt.storage.setItem('town','Lannion');
			cobalt.storage.getItem('town');
				//returns 'Lannion'

			cobalt.storage.setItem('age',12);
			cobalt.storage.getItem('age');
				//returns '12' (string)
			cobalt.storage.getItem('age','int'); //there is also float, date, json
				//returns 12 (number)

			//experimental :
			cobalt.storage.setItem('user',{name:'toto',age:6},'json');
			cobalt.storage.getItem('user','json');
				//returns {name:'toto',age:6} (object)

		 */
		storage:false,
		enable:function(){
			var storage,
					fail,
					uid;
			try {
				uid = new Date;
				(storage = window.localStorage).setItem(uid, uid);
				fail = storage.getItem(uid) != uid;
				storage.removeItem(uid);
				fail && (storage = false);
			} catch(e) {}

			if (!storage){
				return false;
			}else{
				this.storage=storage;
				return true;
			}
		},
		clear:function(){
			if (this.storage){
				this.storage.clear();
			}
		},
		getItem:function(uid, type){
			if (this.storage){
				var val=this.storage.getItem(uid);
				switch(type){
					case undefined :return val;
					case "int":     return parseInt(val);
					case "float":   return parseFloat(val);
					case "date":    return new Date(val);
					case "json":    return JSON.parse(val)
				}
				return val;
			}
		},
		setItem:function(uid, value, type){
			if (this.storage){
				switch ( type ){
					case undefined :return this.storage.setItem(uid,""+value);
					case 'json' :   return this.storage.setItem(uid, JSON.stringify(value));
				}
			}
		},
		removeItem:function(uid){
			if (this.storage){
				return this.storage.removeItem(uid)
			}
		}
	}



};
