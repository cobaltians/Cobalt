cobalt.ios_adapter={
    //
    //IOS ADAPTER
    //
    pipeline:[], //array of sends waiting to go to native
    pipelineRunning:false,//bool to know if new sends should go to pipe or go to native

    isBelowIOS7:false,

    init:function(){
        cobalt.platform="iOs";

        if (typeof CobaltWebCommunicationClass === "undefined"){
            cobalt.log('Warning : CobaltWebCommunicationClass undefined. We probably are below ios7.')
            cobalt.adapter.isBelowIOS7 = true;
        }else{
            cobalt.adapter.isBelowIOS7 = false;
        }
    },
    // handle callbacks sent by native side
    handleCallback:function(json){
        if (cobalt.adapter.isBelowIOS7){
            cobalt.adapter.ios6.handleCallback(json);
        }else{
            cobalt.defaultBehaviors.handleCallback(json);
        }
    },
    //send native stuff
    send:function(obj){
        if (cobalt.adapter.isBelowIOS7){
            cobalt.adapter.ios6.send(obj);
        }else{
            if (obj && !cobalt.debugInBrowser){
                cobalt.divLog('sending',obj)
                try{
                    CobaltWebCommunicationClass.handleMessageSentByJavaScript(JSON.stringify(obj));
                }catch (e){
                    cobalt.log('ERROR : cant connect to native.')
                }

            }
        }
    },
    //datePicker stuff
    datePicker:{
        init:function(inputs){
			cobalt.utils.each(inputs, function(){
                var input=this;
                var id=cobalt.utils.attr(input, 'id');

                var placeholder=cobalt.utils.attr(input, 'placeholder');
                if (placeholder){
                    cobalt.utils.append(document.head, '<style> #'+id+':before{ content:"'+placeholder+'"; '+cobalt.datePicker.placeholderStyles+' } #'+id+':focus:before,#'+id+'.not_empty:before{ content:none }</style>')
                }
				
                input.addEventListener('change',cobalt.datePicker.updateFromValue, false);
				input.addEventListener('keyup',cobalt.datePicker.updateFromValue, false);
            });
        }
    },

    ios6:{
        // iOS < 7 is using an old-school url change hack to send messages from web to native.
        // Because of the url change, only one message can be sent to the native at a time.
        // The acquitement sent by native once each event has been received ensure this behavior.
        // Messages are queued and sent one after the other as soon as the acq is received.
        handleCallback:function(json){
            switch(json.callback){
                case "callbackSimpleAcquitment":
                    cobalt.divLog("received message acquitement")
                    cobalt.adapter.ios6.unpipe();
                    if (cobalt.adapter.pipeline.length==0){
                        cobalt.divLog('end of ios message stack')
                        cobalt.adapter.pipelineRunning=false;
                    }
                    break;
                default:
                    cobalt.tryToCallCallback(json)
                    break;
            }
        },
        send:function(obj){
            cobalt.divLog('adding to ios message stack', obj)
            cobalt.adapter.pipeline.push(obj);
            if (!cobalt.adapter.pipelineRunning){
                cobalt.adapter.ios6.unpipe()
            }
        },
        //unpipe elements when receiving a ACK from ios.
        unpipe:function(){
            cobalt.adapter.pipelineRunning=true;
            var objToSend=cobalt.adapter.pipeline.shift();
            if (objToSend && !cobalt.debugInBrowser){
                cobalt.divLog('sending',objToSend)
                document.location.href=encodeURIComponent("cob@l7#k&y"+JSON.stringify(objToSend));
            }
        }
    },

    //default behaviours
    handleEvent : cobalt.defaultBehaviors.handleEvent,
    handleUnknown : cobalt.defaultBehaviors.handleUnknown,
    navigateToModal : cobalt.defaultBehaviors.navigateToModal,
    dismissFromModal : cobalt.defaultBehaviors.dismissFromModal,
    initStorage : cobalt.defaultBehaviors.initStorage

};
cobalt.adapter=cobalt.ios_adapter;