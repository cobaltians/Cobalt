/*
 * Container par défaut d'HPNativeBridge.
 * Incluez celui-ci dans vos templates QML
 * le signal onMessageSentByJavascript est appelé lorsque le JavaScript envoie un message au natif.
 */

import bb.cascades 1.0

Container {
    id: thisContainer
    
    property alias nativeBridgeWV: nativeBridgeWV
    property alias baseUrl: nativeBridgeWV.baseUrl
    property alias isInfiniteScrollActive: nativeBridgeWV.isInfiniteScrollActive
    property alias isPullToRefreshActive: nativeBridgeWV.isPullToRefreshActive
    property alias nativeBridgeIsReady: nativeBridgeWV.nativeBridgeIsReady
    
    signal messageSentByJavascript(variant jsonObj)
    signal modalDismissed
    
    background: Color.Transparent
    verticalAlignment: VerticalAlignment.Fill
    horizontalAlignment: HorizontalAlignment.Fill
    
    HPNativeBridgeWebview {
        id: nativeBridgeWV
        
        verticalAlignment: VerticalAlignment.Fill
        horizontalAlignment: HorizontalAlignment.Fill
        
        onMessageSentByJavascript: {
            thisContainer.messageSentByJavascript(jsonObj)
        }
    }

    /**
     * WebView
     */
    function loadUrl(mUrl) {
        nativeBridgeWV.loadUrl(mUrl);
    }
    
    function executeScriptInWebview(object) {
        nativeBridgeWV.executeScriptInWebview(object);
    }

    function sendCallbackResponse(objectToSend, callbackID) {
        nativeBridgeWV.sendCallbackResponse(objectToSend, callbackID);
    }

	/**
	 * Modal
	 */
    function closeSheet() {
        modalSheet.close();
        modalDismissed();
        var script = {
            "type": "typeEvent",
            "name": "onModalDismissed"
        };
        executeScriptInWebview(script);
    }
    
    /**
     * Infinite Scroll
     */
    function setInfiniteScrollEnabled(enabled) {
        nativeBridgeWV.setInfiniteScrollEnabled(enabled);
    }

    function cancelInfiniteScroll() {
        nativeBridgeWV.cancelInfiniteScroll();
    }

    /**
     * PullToRefresh
     */
    function setPullToRefreshEnabled(enabled) {
        nativeBridgeWV.setPullToRefreshEnabled(enabled);
    }

    function cancelPullToRefresh() {
        nativeBridgeWV.cancelPullToRefresh();
    }
}