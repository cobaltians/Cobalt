cobalt.ios_adapter={
    //
    //IOS ADAPTER
    //
    pipeline:[], //array of sends waiting to go to native
    pipelineRunning:false,//bool to knwow if new sends should go to pipe or go to native

    init:function(){
        cobalt.platform="iOs";
    },
    // handle callbacks sent by native side
    handleCallback:function(json){
        switch(json.callback){
            case "callbackSimpleAcquitment":
                cobalt.divLog("received message acquitment")
                cobalt.adapter.unpipe();
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
    //send native stuff
    send:function(obj){
        cobalt.divLog('adding to ios message stack', obj)
        cobalt.adapter.pipeline.push(obj);
        if (!cobalt.adapter.pipelineRunning){
            cobalt.adapter.unpipe()
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
    },
    //datePicker stuff
    datePicker:{
        init:function(inputs){
            inputs.each(function(){
                var input=$(this);
                var id=input.attr('id');

                var placeholder=input.attr('placeholder');
                if (placeholder){
                    $('head').append('<style> #'+id+':before{ content:"'+placeholder+'"; '+cobalt.datePicker.placeholderStyles+' } #'+id+':focus:before,#'+id+'.not_empty:before{ content:none }</style>');
                }
                input.on('change keyup',cobalt.datePicker.updateFromValue);
            });
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