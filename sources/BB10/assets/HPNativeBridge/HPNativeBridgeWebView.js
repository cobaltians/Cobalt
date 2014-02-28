
/*
 * HANDLE WEBVIEWS MESSAGES
 */

function handleMessageSentByJavaScript(jsonObj)
{
	switch(jsonObj.type){
	case "typeNavigation":
		switch(jsonObj.navigationType){
		case "push" :	push(jsonObj);	break;
		case "pop" : 		pop(); 			break;
		case "modal" :		modal(jsonObj); break;
		case "dismiss" :	dismiss(); 		break;
		} 
		break;
	case "typeEvent":
		switch (jsonObj.name){
		case "nameToast":
			var text = jsonObj.value;
			myQmlToast.body = text;
			myQmlToast.show();
			break;
		case "ajax":
			handleAjaxCall(jsonObj.options, function(params){
				sendCallbackResponse(params,jsonObj.callbackID);
			});
			break;
		default :
			//todo automatic call or subclass handler call?	
		}
		break;
	case "typeLog":
		console.log("JS Log : "+jsonObj.value);
		break;
	case "typeAlert":
		createAlertDialog(jsonObj);
		break;
	case "nativeBridgeIsReady":
		nativeBridgeIsReady = true;
		executeWaitingCallsInWebview();
		break;
	case "typeCallback":
		switch (jsonObj.callbackID){
		case "infiniteScrollDidRefresh":
			infiniteScrollDidRefresh();
			break;
		case "pullToRefreshDidRefresh":
			pullToRefreshDidRefresh();
		default :
			//todo automatic call or subclass handler call?	
		}
		break;
	default:
		console.log("UNKNOWN TYPE = "+jsonObj.type);
	}
}

/*
 * SEND MESSAGES TO WEBVIEW METHODS
 */

function executeScriptInWebview(object)
{
	if(nativeBridgeIsReady)
	{
		webView.postMessage(JSON.stringify(object));
	}
	else
	{
		var tmpArray = waitingsJavaScriptCalls;
		tmpArray.push(object);
		waitingsJavaScriptCalls = tmpArray;
		//waitingsJavaScriptCalls.push(object);
	}
}

function sendCallbackResponse(objectToSend,callbackID)
{
	if(callbackID!=undefined && (""+callbackID).length > 0)
	{
		var object = {};
		object.type = "typeCallback";
		object.callbackID = ""+callbackID; //force string
		object.params = objectToSend;
		executeScriptInWebview(object);
	}
}

function executeWaitingCallsInWebview()
{
	var tmpArray = waitingsJavaScriptCalls;
	while (tmpArray.length)
	{	
		executeScriptInWebview(tmpArray.shift());
	}
	waitingsJavaScriptCalls = tmpArray;
}

/*
 * FIX ajax method
 */
function handleAjaxCall(options,callback){
	var url= options.url;
	var type= (options.type in [ 'POST', 'GET' ]) ? options.type : "GET"; //default to GET
	var data = options.data ? options.data : {}; //default to empty params
	//add params to url if sending data through GET
	if (type=="GET"){
		url+="?"+serializeAjaxParams(options.data);
	}

	var request = new XMLHttpRequest();
	request.onreadystatechange=function() {
		if(request.readyState === XMLHttpRequest.DONE) {
			if (request.status === 200) {
				try{
					var response = JSON.parse(request.responseText);
				}catch(e){
					var response = request.responseText;
				}
				var params={
						success : true,
						data : response
				}
				callback(params);
			}
			else {
				// This is very handy for finding out why your web service won't talk to you
				console.log("Native Ajax Error " + request.status + ", " + request.statusText);
				var params = {
						error: true,
						status : request.status,
						statusText : request.statusText
				}
				callback(params);
			}
		}
	}
	var encodedString = encodeURIComponent(data);
	request.open(type, url, true); // only async supported
	// You might not need an auth header, or might need to modify - check web service docs
	//request.setRequestHeader("Authorization", "Bearer " + yourAccessToken);
	// Post types other than forms should work fine too but I've not tried
	request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	// Form data is web service dependent - check parameter format
	var requestString = "text=" + encodedString;
	request.send(requestString);
}
//returns the querystring encoding of a javascript object
//also converts recursive objects (using php "array" notation for the query string)
function serializeAjaxParams(obj, prefix) {
    var str = [];
    for(var p in obj) {
        var k = prefix ? prefix + "[" + p + "]" : p, v = obj[p];
        str.push(typeof v == "object" ? 
        		serializeAjaxParams(v, k) :
            encodeURIComponent(k) + "=" + encodeURIComponent(v));
    }
    return str.join("&");
}

/*
 * ALERT DIALOG METHODS
 */

function createAlertDialog(jsonObj) {
	if(jsonObj)
	{
		myQmlDialog.title = jsonObj.alertTitle;
		myQmlDialog.body = jsonObj.alertMessage;
		if(! jsonObj.alertButtons)
			jsonObj.alertButtons=[];

		switch(jsonObj.alertButtons.length)
		{
		case 0:
			myQmlDialog.customButton.label = "OK";
			myQmlDialog.confirmButton.label = undefined;
			myQmlDialog.cancelButton.label = undefined;
			break;
		case 1:
			myQmlDialog.customButton.label = jsonObj.alertButtons[0];
			myQmlDialog.confirmButton.label = undefined;
			myQmlDialog.cancelButton.label = undefined;
			break;
		case 2:
			myQmlDialog.customButton.label = jsonObj.alertButtons[0];
			myQmlDialog.confirmButton.label = jsonObj.alertButtons[1];
			myQmlDialog.cancelButton.label = undefined;

			break;
		default :
			myQmlDialog.customButton.label = jsonObj.alertButtons[0];
		myQmlDialog.confirmButton.label = jsonObj.alertButtons[1];
		myQmlDialog.cancelButton.label = jsonObj.alertButtons[2];
		break;
		}

		myQmlDialog.defaultButton = null;
		myQmlDialog.alertID = jsonObj.alertId;

		if(!(jsonObj.alertReceiver && jsonObj.alertReceiver == "native") && jsonObj.callbackID != undefined)
		{
			myQmlDialog.callbackID = ""+jsonObj.callbackID;
		}
		else console.log("callbackID = "+jsonObj.callbackID);


		myQmlDialog.show();
	}
} 

function alertDialogClickedButton(indexOfButton)
{
	if(myQmlDialog.callbackID != undefined)
	{
		var objectToSend = {};
		objectToSend.alertId = myQmlDialog.alertID;
		if(indexOfButton == SystemUiResult.CustomButtonSelection)
			objectToSend.index = 0;
		else 
			objectToSend.index = indexOfButton-1;

		sendCallbackResponse(objectToSend, myQmlDialog.callbackID);
		console.log("button clicked "+indexOfButton+" callbackID = "+myQmlDialog.callbackID+" alertID = "+myQmlDialog.alertID);
	}

	myQmlDialog.callbackID = "";
	myQmlDialog.alertID = 0;
}

/*
 * Handle scroll
 */

function handleScrolling()
{
	//console.log('--handleScrolling', nativeBridgeIsReady, isPullToRefreshActive, scrollViewContent.refreshHeader.refreshing, -containerhandler.layoutFrame.y, scrollViewContent.refreshHeader.refreshHandler.layoutFrame.height)

	//InfiniteScrollRefresh is called when bottom of the page is reached (and infiniteScroll is active !)
	var offset = containerhandler.layoutFrame.height + containerhandler.layoutFrame.y - scrollhandler.layoutFrame.height;
	if(nativeBridgeIsReady && isInfiniteScrollActive && !isInfiniteScrollRefreshing  && containerhandler.layoutFrame.height > 0 && containerhandler.layoutFrame.y < 0 && offset < 0)
	{
		JS.infiniteScrollRefresh();
	}

	//ask for refreshing... if pullToRefresh is active
	if(isPullToRefreshActive)
	{
		//scroll webview to hide pullToRefreshHeader when it is not refreshing !
		scrollViewContent.refreshHeader.resetScrollIfNeeded(ScrollAnimation.Smooth);
		//showOrHideRefreshHeader();
		
		if (!scrollViewContent.refreshHeader.refreshing) {
			scrollViewContent.refreshHeader.readyForRefresh = false;

			if (-containerhandler.layoutFrame.y < 0) {
				scrollViewContent.refreshHeader.refreshImage.rotationZ = (-containerhandler.layoutFrame.y*10 >= -180) ? -containerhandler.layoutFrame.y*10 : -180;
				if (! scrollViewContent.refreshHeader.refreshing) {
					scrollViewContent.refreshHeader.readyForRefresh = true;
				}
				//RELACHER POUR RAFRAICHIR
				scrollViewContent.refreshHeader.refreshStatusLabel.text = scrollViewContent.refreshHeader.releaseToRefreshText;
			} else if (-containerhandler.layoutFrame.y < scrollViewContent.refreshHeader.refreshHandler.layoutFrame.height) {
				//if (refreshHeader.refreshedAt == "") {
				scrollViewContent.refreshHeader.refreshStatusLabel.text = scrollViewContent.refreshHeader.pullToRefreshText;
				/*} else {
					refreshHeader.refreshStatus.text = qsTr("Pull to refresh. Last refreshed ")// + refreshHeader.timeSince(refreshHeader.refreshedAt);
				}*/
				scrollViewContent.refreshHeader.refreshImage.rotationZ = 0;
			} else {
				//don't refresh
				scrollViewContent.refreshHeader.refreshImage.rotationZ = 0;
			}
		}
		else
		{
			scrollViewContent.refreshHeader.refreshStatusLabel.text = scrollViewContent.refreshHeader.refreshingText;
		}
	}
}

/*
 * Infinite Scroll methods 
 */

function setInfiniteScrollEnabled(enabled)
{
	isInfiniteScrollActive = enabled;
}

function infiniteScrollRefresh()
{
	//console.log("InfiniteScroll");
	isInfiniteScrollRefreshing = true;
	var object = {"type":"typeEvent","name":"infiniteScrollRefresh","callbackID":"infiniteScrollDidRefresh"};
	executeScriptInWebview(object);
}

function infiniteScrollDidRefresh()
{
	//console.log("InfiniteScroll Did Refresh");

	isInfiniteScrollRefreshing = false;
}

function cancelInfiniteScroll()
{
	if(isInfiniteScrollRefreshing && isInfiniteScrollActive)
	{
		var object = {"type":"typeEvent","name":"infiniteScrollCancelled"};
		executeScriptInWebview(object);
		isInfiniteScrollRefreshing = false;
		//handleScrolling();
	}
}

/*
 * Pull To Refresh methods 
 */

function setPullToRefreshEnabled(enabled)
{
	isPullToRefreshActive = enabled;
}

function pullToRefreshRefresh()
{
	var object = {"type":"typeEvent","name":"pullToRefreshRefresh","callbackID":"pullToRefreshDidRefresh"};
	executeScriptInWebview(object);
}

function pullToRefreshDidRefresh()
{
	scrollViewContent.refreshHeader.refreshing = false;
	scrollView.scrollToPoint(0,scrollViewContent.refreshHeader.refreshHandler.layoutFrame.height,ScrollAnimation.Smooth);
}

function cancelPullToRefresh()
{
	if(isPullToRefreshActive && scrollViewContent.refreshHeader.refreshing)
	{
		//console.log('---cancelPullToRefresh');
		var object = {"type":"typeEvent","name":"pullToRefreshCancelled"};
		executeScriptInWebview(object);
		scrollViewContent.refreshHeader.refreshing = false;
		//handleScrolling();
	}
}

function showOrHideRefreshHeader()
{
	if (!isPullToRefreshActive && scrollViewContent.refreshHeader) {
		scrollViewContent.refreshHeader.visible = false;
	} else if(scrollViewContent.refreshHeader){
		scrollViewContent.refreshHeader.visible = true;
	}
}

function setPullToRefreshHeader(newRefreshHeaderQMLName) {
    secondPageDefinition.source = "asset:///" + newRefreshHeaderQMLName;
    var nRefreshHeader = secondPageDefinition.createObject();
    if(nRefreshHeader)
    {
        //nRefreshHeader.visible = false;
        if(scrollViewContent.refreshHeader && scrollViewContent.indexOf(refreshHeader) > -1)
        {
            scrollViewContent.replace(scrollViewContent.indexOf(refreshHeader), nRefreshHeader);
        }
        else
        {
            scrollViewContent.add(nRefreshHeader);
        }
        scrollViewContent.refreshHeader = nRefreshHeader;
        console.log("setPullToRefreshHeader refreshHeight : " + nRefreshHeader.refreshHandler.layoutFrame.height);
    }
}

/*
 * NAVIGATION FUNCTIONS
 */
function getPropertiesForId(jsonObj)
{
	var classId = jsonObj.navigationClassId;

	var confs = JSON.parse(loadFileContentFromAssets("www/", "nativeBridge.conf"));
	var object = {};

	if(classId && confs[classId])
	{
		object.pageName = confs[classId].bbQmlName;
		object.isInfiniteScrollActive = confs[classId].infiniteScroll ? confs[classId].infiniteScroll : false;
		object.isPullToRefreshActive = confs[classId].pullToRefresh ? confs[classId].pullToRefresh : false;
	}

	if(!object.pageName || (object.pageName && object.pageName.length == 0))
	{
		console.log("Warning : qml name for classID "+ (classId ? classId : "null")+" not found. Looking for default classID");
		if(confs["default"])
		{
			object.pageName = confs["default"].bbQmlName;
			object.isInfiniteScrollActive = confs["default"].infiniteScroll ? confs["default"].infiniteScroll : false;
			object.isPullToRefreshActive = confs["default"].pullToRefresh ? confs["default"].pullToRefresh : false;
		}
		else console.log("Warning : No default key in conf file...");
	}

	object.navigationPageName = jsonObj.navigationPageName;

	return object;
}

// Inflate page to push or present
function inflatePage(properties, parent) {
	
	if (properties.pageName 
		&& properties.pageName.length > 0) {
		
		secondPageDefinition.source = "asset:///"+properties.pageName;
		var page = secondPageDefinition.createObject(parent);
		
		try {

			var mUrl = baseUrl + properties.navigationPageName;
			
			var json = {
				htmlPage: mUrl
			};
			if(properties.isInfiniteScrollActive)
				json.isInfiniteScrollActive = properties.isInfiniteScrollActive;
			if(properties.isPullToRefreshActive)
				json.isPullToRefreshActive = properties.isPullToRefreshActive;
			page.setProperties(json);
			
			/*
			page.nativeBridgeContainer.loadUrl(mUrl);
			
			if(properties.isInfiniteScrollActive)
				page.nativeBridgeContainer.nativeBridgeWV.isInfiniteScrollActive = properties.isInfiniteScrollActive;
			if(properties.isPullToRefreshActive)
				page.nativeBridgeContainer.nativeBridgeWV.isPullToRefreshActive = properties.isPullToRefreshActive;
			*/

		}
		catch(e) {

			console.log("ERROR : "+e);
		} 

		return page;
	}

	console.log("ERROR : impossible to create page for QML FileName : "+properties.pageName);
	return null;
}

function push(jsonObj)
{
	if (!nativeBridgeContainer.nativeBridgeWV.isModal){
		cancelInfiniteScroll();
		cancelPullToRefresh();
		var properties = getPropertiesForId(jsonObj);
		var page = inflatePage(properties,null);
		navigationPane.push(page);
	}else{
		console.log('WARNING skipped push : push into modal not available on BB10 for now.');
	}

}

function modal(jsonObj)
{
	cancelInfiniteScroll();
	cancelPullToRefresh();
	var properties = getPropertiesForId(jsonObj);
	var page = inflatePage(properties,null);
	page.nativeBridgeContainer.nativeBridgeWV.isModal=true;
	modalSheet.content = page;
	modalSheet.open();
}

function pop()
{
	if (!nativeBridgeContainer.nativeBridgeWV.isModal){
		if (navigationPane.count() > 1) 
			navigationPane.pop();
		else console.log("nothing in NavigationPane to pop ");
	}else{
		console.log('WARNING skipped pop : pop into modal not available on BB10 for now.');
	}	
}

function dismiss()
{
	closeSheet();
}

/*
 * Load File Content
 */
function loadFileContentFromAssets(filePath,fileName)
{
	return NativeBridgeHelper.getAssetsFileContent(filePath+fileName);
}
