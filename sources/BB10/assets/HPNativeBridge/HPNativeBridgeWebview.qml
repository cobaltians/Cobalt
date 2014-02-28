/*
 * Ce fichier contient la WebView customisée du socle nativeBridge. 
 * C'est ici que se trouve la plupart du code de nativeBridge ainsi que dans le fichier JavaScript correspondant.
 */

import bb.cascades 1.0
import bb.system 1.0

import "HPNativeBridgeWebView.js" as JS

ScrollView {
    id : scrollView
    
    property string baseUrl : "local:///assets/www/"
    property bool isModal: false
    property bool isInfiniteScrollActive: false
    property bool isPullToRefreshActive: false
    property bool nativeBridgeIsReady: false    
    property bool isInfiniteScrollRefreshing: false
    property variant waitingsJavaScriptCalls : []
    property string pullToRefreshHeaderFileName : "HPNativeBridge/HPNativeBridgePullToRefreshHeader.qml"

    signal messageSentByJavascript(variant jsonObj)

    content: Container {
        id : scrollViewContent

        property variant refreshHeader: refreshHeader
        
        layout: StackLayout {
            // PullToRefresh header added dynamically after the webview... -> it must be added above the WebView!
            orientation: LayoutOrientation.BottomToTop        
        }

        verticalAlignment: VerticalAlignment.Fill
        horizontalAlignment: HorizontalAlignment.Fill
        
        WebView {
            id: webView

            minHeight: 300.0
            settings.background: Color.Transparent
            horizontalAlignment: HorizontalAlignment.Center
            
            onMessageReceived: {
                try {
                    if (message && message.data) {
                        var jsonObj = JSON.parse(decodeURIComponent(message.data));
                        JS.handleMessageSentByJavaScript(jsonObj);
                        messageSentByJavascript(jsonObj)
                        //console.log('WebView Received: ' + JSON.stringify(jsonObj));
                    }
                } catch (e) {
                    console.log('ERROR in received message: ' + e + ' || ' + decodeURIComponent(message.data));
                }
            }
            
            onLoadingChanged: {
                console.log("onLoadingChanged");
                if (loadRequest.status == WebLoadStatus.Started) {
                    //console.log("Load started")
                } else if (loadRequest.status == WebLoadStatus.Succeeded) {
                    //console.log("Load finished")
                    if (isPullToRefreshActive && ! scrollViewContent.refreshHeader) {
                        //pullToRefreshHeader should only be set once !
                        JS.setPullToRefreshHeader(pullToRefreshHeaderFileName);
                    } else 
                    	console.log("WebView loaded but already PullToRefresh header");
                    JS.handleScrolling();
                } else if (loadRequest.status == WebLoadStatus.Failed) {
                    //console.log("Load failed.");
                }
            }
            
            attachedObjects: [
                SystemToast {
                    id: myQmlToast
                    
                    body: ""
                },
                SystemDialog {
                    id: myQmlDialog

                    property int alertID: 0
                    property string callbackID: ""
                    
                    title: ""
                    body: ""
                    
                    onFinished: {
                        JS.alertDialogClickedButton(myQmlDialog.result);
                    }
                },
                ComponentDefinition {
                    id: secondPageDefinition
                    
                    source: ""
                },
                Sheet {
                    id: modalSheet
                },
                LayoutUpdateHandler {
                    id: webViewHandler
                }
            ]
        }

        attachedObjects: [
            LayoutUpdateHandler {
                id: containerhandler
                
                property variant formerFrame: layoutFrame
                
                onLayoutFrameChanged: {
                    // If the height of the frame changed (content has been added in WebView) -> do not update scrolling for pullToRefreshHeader!
                    if (layoutFrame.height == formerFrame.height)
                    	JS.handleScrolling();
                    else
                    	JS.showOrHideRefreshHeader();
                    formerFrame = layoutFrame;
                }
            }
        ]
    }

    attachedObjects: [
        LayoutUpdateHandler {
            id: scrollhandler
        }
    ]

    /**
     * WebView
     */
    function loadUrl(mUrl) {
        //console.log("Load url: "+mUrl);
        try {
            webView.url = mUrl;
        } catch (e) {
            console.log(e);
        }
    }

    function loadFileContentFromAssets(filePath, fileName) {
        JS.loadFileContentFromAssets(filePath, fileName);
    }

    function setMinHeight(minHeight) {
        if (minHeight > 0) webView.minHeight = minHeight
    }

    function executeScriptInWebview(object) {
        JS.executeScriptInWebview(object);
    }
    
    function sendCallbackResponse(objectToSend, callbackID) {
        JS.sendCallbackResponse(objectToSend, callbackID);
    }
    
    /**
     * InfiniteScroll
     */
    function cancelInfiniteScroll() {
        JS.cancelInfiniteScroll();
    }

    function setInfiniteScrollEnabled(enabled) {
        JS.setInfiniteScrollEnabled(enabled);
    }

    /**
     * PullToRefresh
     */
    function cancelPullToRefresh() {
        JS.cancelPullToRefresh();
    }

    function setPullToRefreshEnabled(enabled) {
        JS.setPullToRefreshEnabled(enabled);
    }

	/**
	 * Events
	 */
    onCreationCompleted: {
        //console.log("onCreationCompleted: "+pullToRefreshHeaderFileName+" "+isPullToRefreshActive);
        //JS.setPullToRefreshHeader(pullToRefreshHeaderFileName);
    }

    onIsPullToRefreshActiveChanged: {
        JS.showOrHideRefreshHeader();
    }
    
    onTouch: {
        try {
            // If there is a refresh header, handle the touch of the webview to release it when necessary
            scrollViewContent.refreshHeader.onWebviewTouch(event);
        } catch (e) {

        }
    }
}