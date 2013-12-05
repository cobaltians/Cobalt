/*
 * Header du PullToRefresh
 */

import bb.cascades 1.0

import "HPNativeBridgeWebView.js" as JS

Container {
    
    id: refreshContainer

    property alias refreshHandler: refreshHandler
    property alias refreshImage: refreshImage
    property alias refreshStatusLabel: refreshStatusLabel
    
    property string pullToRefreshText: "Tirer pour rafraîchir"
    property string releaseToRefreshText: "Relâcher pour rafraîchir"
    property string refreshingText: "Chargement"
    property int refresh_threshold: 20 // How the user needs to pull to trigger before release to refresh
    property bool readyForRefresh: false
    property bool refreshing: false // true if currently refreshing the list
    //property string refreshedAt : ""
    property date lastUpdateDate

    signal refreshTriggered

    background: Color.Transparent
    topPadding: 10.0
    bottomPadding: 10.0
    horizontalAlignment: HorizontalAlignment.Fill
    
    layout: StackLayout {
        orientation: LayoutOrientation.LeftToRight
    }
    
    Container {
        id: leftContainer
        
        rightMargin: 20
        preferredWidth: 150
        
        ImageView {
            id: refreshImage
            
            imageSource: "asset:///HPNativeBridge/images/pullToRefreshArrow.png"
            preferredWidth: 36.0
            preferredHeight: 80.0
            verticalAlignment: VerticalAlignment.Center
            horizontalAlignment: HorizontalAlignment.Center
        }
        
        ActivityIndicator {
            id: loadingIndicator

            opacity: 0.5
            visible: false
            preferredWidth: 100
            preferredHeight: 100
            verticalAlignment: VerticalAlignment.Center
            horizontalAlignment: HorizontalAlignment.Center
        }
    }

    Container {
        id: refreshStatusContainer
        
        horizontalAlignment: HorizontalAlignment.Fill
        
        layout: AbsoluteLayout {
            
        }
        
        Label {
            id: refreshStatusLabel

            textStyle.fontFamily: "SlatePro"
            textStyle.textAlign: TextAlign.Left
            textStyle.color: Color.Black
            horizontalAlignment: HorizontalAlignment.Fill
        }
        Label {
            
            id: lastUpdateStatus
            
            text: formatDate(lastUpdateDate)
            textStyle.fontFamily: "SlatePro"
            textStyle.fontSize: FontSize.XSmall
            textStyle.textAlign: TextAlign.Left
            textStyle.color: Color.Gray
            horizontalAlignment: HorizontalAlignment.Center
            
            layoutProperties: AbsoluteLayoutProperties {
                positionY: 45
            }
        }
    }
    
    attachedObjects: [
        LayoutUpdateHandler {
            id: refreshHandler
            
            property int formerFrameHeight : layoutFrame.height
            
            onLayoutFrameChanged: {
                // If refresh header has just been added to screen
                if (formerFrameHeight == 0 && layoutFrame.height > 0)
                    resetScrollIfNeeded(ScrollAnimation.None);
                formerFrameHeight = layoutFrame.height;
            }
        }
    ]
    
	/**
     * Events
     */
    onCreationCompleted: {
        lastUpdateDate = new Date();
    }

    onRefreshTriggered: {
        JS.pullToRefreshRefresh();
    }

    onRefreshingChanged: {
        if (refreshing) {
            var now = new Date();
            //refreshContainer.refreshedAt = now.getTime() / 1000;
            lastUpdateDate = now;
            loadingIndicator.visible = true;
            loadingIndicator.running = true;
            //lastUpdateStatus.visible = true;
            refreshImage.visible = false;
        } else {
            loadingIndicator.running = false;
            loadingIndicator.visible = false;
            refreshImage.visible = true;
            //lastUpdateStatus.visible = false;
        }
    }
    
    onVisibleChanged: {
        console.log("onVisibleChanged refreshHeight: " + refreshHandler.layoutFrame.height);
    }

    function onWebviewTouch(event) {
        refreshContainer.resetPreferredHeight();
        // Pulled and released
        if (event.touchType == TouchType.Up) released();
    }

    /*
     * Scroll handlers
     */
    function released() {
        if (readyForRefresh) {
            readyForRefresh = false;
            refreshing = true;
            refreshTriggered();
        } else {
            resetScrollIfNeeded(ScrollAnimation.Smooth);
        }
    }

    function resetScrollIfNeeded(scrollAnimation) {
        if (! scrollAnimation) scrollAnimation = ScrollAnimation.None;
        // Scroll to bottom of the refresh header if the user released the WebView and the refresh header is still visible
        if (! refreshing && - containerhandler.layoutFrame.y < refreshHandler.layoutFrame.height) scrollView.scrollToPoint(0, refreshHandler.layoutFrame.height+2, scrollAnimation);
    }

    /**
     * Date format
     */
    function timeSince(date) {
        var seconds = Math.floor(((new Date().getTime() / 1000) - date));
        var interval = Math.floor(seconds / 31536000);
        if (interval > 1) return qsTr("%L1y ago").arg(interval);
        interval = Math.floor(seconds / 2592000);
        if (interval > 1) return qsTr("%L1m ago").arg(interval)
        interval = Math.floor(seconds / 86400);
        if (interval >= 1) return qsTr("%L1d ago").arg(interval)
        interval = Math.floor(seconds / 3600);
        if (interval >= 1) return qsTr("%L1h ago").arg(interval)
        interval = Math.floor(seconds / 60);
        if (interval > 1) return qsTr("%L1m ago").arg(interval)
        return qsTr("just now");
    }

    function formatDate(mDate) {
        return qsTr("Dernière mise à jour le " + addZero(mDate.getDate()) + "/" + addZero(mDate.getMonth() + 1) + " à " + addZero(mDate.getHours()) + ":" + addZero(mDate.getMinutes()));
    }

    // Display 06 instead of 6.
    function addZero(myNumber) {
        return (myNumber < 10 ? "0" + myNumber : myNumber);
    }
}
