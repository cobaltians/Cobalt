var app={
    debug:true,
    //debugInBrowser:true,
    debugInDiv:true,

    /* This code below, combined with the touch module of zepto helps in resolving issues
       with fastclick on android devices : some devices receive "tap" event before "click" and
       some don't. ensure only one event is fired in all cases.
    */
    touchTimer:null,
    touch:function(selector, touchHandler, allowDefault){
        var preventDefault = allowDefault ? false : true;

        $(selector).each(function(i,elem){
            var elem= $(elem);
            var touchup=function(){
                elem.removeClass('touched');
            };
            var touching=function(e){
                if (!elem.hasClass('touched')){
                    elem.addClass('touched');
                    clearTimeout(app.touchTimer);
                    app.touchTimer = setTimeout(touchup,1000);
                    touchHandler.apply([this, e ]);

                }
                if (preventDefault)
                    return false;
            }
            elem.unbind('tap').on('tap',touching);
            elem.unbind('click').on('click',touching);
        });
    },

    /*
        initPage binds some common links like push and pop and send the page title to the native side
     */
    initPage:function(title){

        app.touch('a.push',function(){
            if ( ! $(this).hasClass('disabled') ){
                cobalt.navigate('push',$(this).attr('data-href'),$(this).attr('data-classid'))
            }
        });
        app.touch('a.pop',function(){
            cobalt.navigate('pop',$(this).attr('data-href'),$(this).attr('data-classid'));
        });
        app.touch('a.dismiss',function(){
            cobalt.navigate('dismiss');
        });
        app.touch('a.modal',function(){
            cobalt.navigate('modal',$(this).attr('data-href'));
        });

        if (title){
            cobalt.sendEvent('setTexts',{
                title : title
            });
        }

    },


    /* change font size on body. used in events demo page */
    setZoom : function(zoomLevel){
        try{
            document.querySelectorAll('body')[0].style.fontSize=zoomLevel+"px"

        }catch(e){
            cobalt.log(e);
        }
    },

    /* a small assert function used in localStorage and callbacks pages */
    assertEqual : function(testID,func_or_result,expectedResult){
        try{
            var result= ( typeof func_or_result =="function") ? func_or_result() : func_or_result;
            cobalt.log('testing', result, 'vs', expectedResult);
            if (result === expectedResult){
                cobalt.log('test #'+testID+" success! ");
                return true;
            }else{
                cobalt.log('test #'+testID+" failed! ");
                cobalt.log(result, " != ", expectedResult);
            }
        }catch(e){
            cobalt.log('test #'+testID+" failed! ", e)
        }
        return false;
    }

}