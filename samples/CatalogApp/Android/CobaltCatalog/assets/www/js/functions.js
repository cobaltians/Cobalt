
/* change font size on body */
function setZoom(zoomLevel){
    try{
        document.querySelectorAll('body')[0].style.fontSize=zoomLevel+"px"
        
    }catch(e){
        cobalt.log(e);
    }
}





