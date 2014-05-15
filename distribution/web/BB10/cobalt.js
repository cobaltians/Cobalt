/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

var cobalt={
    
    userEvents:{}, //objects of events defined by the user
	debug:false,
	debugInBrowser:false,
	debugInDiv:false,

	callbacks:{},//array of all callbacks by callbackID
	lastCallbackId:0,

	/*	cobalt.init(options)
		see doc for options
	*/
	init:function(options){
    	if (options){
            this.debug = ( options.debug === true );
            this.debugInBrowser = ( options.debugInBrowser === true );
            this.debugInDiv = ( options.debugInDiv === true );

            if (cobalt.debugInDiv){
                this.createLogDiv();
            }
		    if (options.events){
		        this.userEvents=options.events
	        }
            cobalt.storage.enable();

            $.extend(cobalt.datePicker, options.datePicker);
            if (cobalt.datePicker.enabled){
                cobalt.datePicker.init();
            }


		}else{
            cobalt.storage.enable();
        }


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
	/*	cobalt.log(stuff,...)
		all arguments can be a string or an object. object will be json-ised and separated with a space.
		cobalt.log('toto')
		cobalt.log('a',5,{"hip":"hop"})
	*/
	log:function(){
        var logString=cobalt.argumentsToString(arguments);
        if ( cobalt.debug ){
            if (cobalt.debugInBrowser && window.console){
                console.log(logString);
            }else{
                cobalt.send({ type : "log", value : logString })
            }
            cobalt.divLog(logString)
        }
    },
    //TODO change all dependencies
    divLog:function(){
        //TODO document this
        if (cobalt.debugInDiv){
	        cobalt.createLogDiv();
            var logdiv=$('#cobalt_logdiv')
            if (logdiv.length){
                var logString="<br/>"+cobalt.argumentsToString(arguments);
                try{
                    logdiv.append(logString);
                }catch(e){
                    logdiv.append("<b>cobalt.log failed on something.</b>");
                }
            }
        }
    },
    argumentsToString:function(){
        var stringItems=[];
        //ensure arguments[0] exists?
        $.each(arguments[0],function(i,elem){
            stringItems.push(cobalt.toString(elem))
        })
        return stringItems.join(' ');
    },
    //TODO change all dependencies, enhance
	/* internal, create log div if needed */
	createLogDiv:function(){
		if ($('#cobalt_logdiv').length==0){
			//create usefull log div:
			$('body').append('<div id="cobalt_logdiv" style="width:100%; text-align: left; height: 100px; border:1px solid blue; overflow: scroll; background:#eee;"></div>')
		}
	},
	//TODO change all dependencies, enhance
	/* internal, toggle visibility of log div if log div was created by the lib */
	toggleLogDiv:function(){
        //TODO remove $() dependencie
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
        if (cobalt.debugInBrowser){
            cobalt.log('sending', obj)
        }
		cobalt.adapter.send(obj, callback)
	},
	//Sends an event to native side.
	//See doc for guidelines.
    sendEvent:function(eventName, params, callback){
	    if (eventName){
		    var obj = {
			    type : "event",
			    event : eventName,
			    data : params || {}
		    };
		    cobalt.send(obj, callback);
	    }
	},
	//Sends a callback to native side.
	//See doc for guidelines.
    sendCallback:function(callback, data){
		if (typeof callback ==="string" && callback.length > 0){
			cobalt.divLog("calling callback with callback id = ",callback)
			cobalt.send({type:"callback", callback : callback, data: data})
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
			case "modal":
				if (page){
					cobalt.adapter.navigateToModal(page, controller);
				}
			break;
			case "dismiss":
				cobalt.adapter.dismissFromModal();
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
		show a web page as an layer.
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
				if (data){
					cobalt.send({type:"webLayer", action:"show", data :{ page:data, fadeDuration:fadeDuration }})
				}
			break;
		}
	},
	/*
		open an url in the device browser.
		//cobalt.openExternalUrl("http://cobaltians.com")
	*/
	openExternalUrl:function(url){
		if (url){
			cobalt.send({
				type: "intent",
				action: "openExternalUrl",
				data: {
					url: url
	    		}
			});
		}
	},
    /* internal, called from native */
    execute:function(json){
    	cobalt.divLog("received", json)
        /*parse data if string, die silently if parsing error */
		if (json && typeof json == "string"){
        	try{
                json = JSON.parse(json);
            }catch(e){
				cobalt.divLog("can't parse string to JSON");
            }
        }
        try{
	        switch (json && json.type){
	        	case "event":
                    cobalt.adapter.handleEvent(json)
	                break;
	            case "callback":
                    cobalt.adapter.handleCallback(json)
                    break;
		        default:
                    cobalt.adapter.handleUnknown(json)
	        }
	    }catch(e){
            cobalt.log('cobalt.execute failed : '+e)
        }
    },
	//internal function to try calling callbackID if it's representing a string or a function.
	tryToCallCallback:function(json){
		cobalt.divLog('trying to call web callback')
		var callbackfunction=null;
        if (cobalt.isNumber(json.callback) && typeof cobalt.callbacks[json.callback]==="function"){
	        //if it's a number, a real JS callback should exist in cobalt.callbacks
	        callbackfunction=cobalt.callbacks[json.callback]

		}else if (typeof json.callback === "string"){
	        //if it's a string, check if function exists
	        callbackfunction=eval(json.callback);
		}
		if (typeof callbackfunction === "function"){
	        try{
		        callbackfunction(json.data)
	        }catch(e){
		        cobalt.log('Failed calling callback : ' + e)
	        }
        }else{
            cobalt.adapter.handleUnknown(json);
        }
	},
	// usefull functions
	isNumber : function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
	},
	isArray:function(arr){
		return ( Object.prototype.toString.call( arr ) === '[object Array]' );
	},
	toString: function(stuff){
        switch (typeof  stuff){
            case "string":
                break;
            case "function":
                stuff = (""+stuff.call).replace('native','web')//to avoid panic ;)
                break;            
            default:
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
    defaultBehaviors:{
		handleEvent:function(json){
			cobalt.log("received event", json.event)
		    if (cobalt.userEvents && typeof cobalt.userEvents[json.event] === "function"){
				cobalt.userEvents[json.event](json.data,json.callback);
		    }else{
                cobalt.adapter.handleUnknown(json)
            }
	    },
		handleCallback:function(json){
	        switch(json.callback){
	            default:
				    cobalt.tryToCallCallback(json)
			    break;
	        }
	    },
        handleUnknown:function(json){
			cobalt.log('received unhandled message ', json );
	    },
		navigateToModal:function(page, controller){
			cobalt.send({ "type":"navigation", "action":"modal", data : { page :page, controller: controller }});
		},
		dismissFromModal:function(){
			cobalt.send({ "type":"navigation", "action":"dismiss"});
		},
		initStorage:function(){
			return cobalt.storage.enable()
		}
	},

    datePicker:{
        //USER OPTIONS
        enabled : true,
        texts:{
            validate : "Ok",
            cancel : "Cancel",
            delete : "Clear"
        },
        //Default format function. used by some adapters to format input value if needed
        //user can override this. default format is "yyyy-mm-dd".
        format:function(value){
            return value;
        },
        //internal
        init:function(){
            var inputs=$('input[type=date]')

            inputs.each(function(){
                var input=$(this);
                var id=input.attr('id');
                if (!id){
                    id='CobaltGeneratedId_'+Math.random().toString(36).substring(7);
                    input.attr('id',id);
                }
                cobalt.datePicker.updateFromValue.apply(input);
            });

            if (cobalt.adapter.datePicker && cobalt.adapter.datePicker.init){
                cobalt.adapter.datePicker.init(inputs);
            }
        },
        updateFromValue : function(){
            var id=$(this).attr('id');
            cobalt.log("updating storage value of date #",id)
            if ($(this).val()){
                $(this).addClass('not_empty')
            }else{
                $(this).removeClass('not_empty')
            }
            cobalt.log('current value is', $(this).val())
            var values=$(this).val().split('-')
            if (values.length==3){
                var d={
                    year: parseInt(values[0],10),
                    month : parseInt(values[1],10),
                    day : parseInt(values[2],10)
                }
                cobalt.log('setting storage date ', 'CobaltDatePickerValue_'+id, d);
                cobalt.storage.setItem('CobaltDatePickerValue_'+id, d ,'json')

            }else{
                cobalt.log('removing date');
                cobalt.storage.removeItem('CobaltDatePickerValue_'+id)
            }
            return false;
        },
        enhanceFieldValue:function(){
            //cobalt.log('updating date format')
            var date = cobalt.storage.getItem('CobaltDatePickerValue_'+$(this).attr('id'), 'json')
            if (date){
                cobalt.log('format date=',date)
                $(this).val(cobalt.datePicker.format(date.year+'-'+cobalt.datePicker.zerofill(date.month,2)+'-'+cobalt.datePicker.zerofill(date.day,2)))
            }
        },
        zerofill:function(number, padding){
            return new String( new Array(padding + 1).join("0") + number ).slice(-padding)
        },
        val:function(input){
            if (cobalt.adapter.datePicker && cobalt.adapter.datePicker.val){
                cobalt.log('returning cobalt adapter datePicker value')
                return cobalt.adapter.datePicker.val(input);
            }else{
                cobalt.log('returning default datePicker value')
                var values=( $(input).val()||"" ).split('-');
                if (values.length==3){
                    return {
                        year: parseInt(values[0], 10),
                        month : parseInt(values[1], 10),
                        day : parseInt(values[2], 10)
                    }
                }
                return undefined;
            }
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
				if (val){
					switch(type){
						case undefined :return val;
						case "int":     return parseInt(val);
						case "float":   return parseFloat(val);
						case "date":    return new Date(val);
						case "json":    return JSON.parse(val)
					}
					return val;						
				}
				return undefined;
			}
		},
		setItem:function(uid, value, type){
            //TODO Fix BUG when using setItem undefined and type=json
			if (this.storage){
				switch ( type ){
					case 'json' :   return this.storage.setItem(uid, JSON.stringify(value));
					default : 		return this.storage.setItem(uid,""+value);
				}
			}
		},
		removeItem:function(uid){
			if (this.storage){
				return this.storage.removeItem(uid)
			}
		}
	}

};cobalt.bb10_adapter={
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
                default :
                    cobalt.adapter.handleUnknown(json);
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
	handleUnknown : cobalt.defaultBehaviors.handleUnknown,
    navigateToModal : cobalt.defaultBehaviors.navigateToModal,
	dismissFromModal : cobalt.defaultBehaviors.dismissFromModal,
	initStorage : cobalt.defaultBehaviors.initStorage
};
cobalt.adapter=cobalt.bb10_adapter;