/*
 * NativeBridgeForm.h
 *
 *  Created on: Sep 23, 2013
 *      Author: sebastienfamel
 */

#ifndef NATIVEBRIDGEFORM_H_
#define NATIVEBRIDGEFORM_H_

#include "NativeBridge/NativeBridgeAlert.h"

#include "tizenx.h"

using namespace Tizen::Base;
using namespace Tizen::Base::Collection;
using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Ui::Scenes;
using namespace Tizen::Web::Controls;
using namespace Tizen::Web::Json;

class NativeBridgeAlert;

class NativeBridgeForm
	: public Form
	//, public IOrientationEventListener
	, public IFormBackEventListener
	, public IJavaScriptBridge

{
public:
	NativeBridgeForm(void);
	NativeBridgeForm(String htmlPageToLoad);
	NativeBridgeForm(	String ressourcePath,
						String htmlPageToLoad);
	virtual ~NativeBridgeForm();
	void Init(Web *web);
	virtual bool Initialize()=0;

	virtual result OnInitializing(void);
	virtual result OnTerminating(void);

	/*
	virtual void OnOrientationChanged(	const Control& source,
										OrientationStatus orientationStatus);
	*/

	virtual void OnFormBackRequested(Form& source);
	virtual void HandleJavaScriptRequestN(IJsonValue* pArg);
	virtual Tizen::Base::String GetName(void);

	void LoadUrl(	String ressourcePath,
					String url);

	void PopupHasHidden(Integer* buttonIndex,
						JsonString* callbackID,
						JsonNumber* alertID);

	static void SetHtmlPageNameToLoad(String htmlToLoad);
	static String GetHtmlPageNameToLoad();


private:

	void CreatePopup(	JsonString* pAlertTitle,
						JsonString* pAlertMessage,
						JsonArray* pAlertButtons,
						JsonNumber* pJSNumberAlertId,
						JsonString* pJSStringCallbackID);

	void PopWebViewActivity();
	void HandleBackButtonPressedResult(bool allowedToGoBack);

protected :
	virtual bool TreatData(IJsonValue* pJSData);
	void PushWebView(	JsonString* pActivityId,
						JsonString* pPageNamed,
						bool isNativeBridgeView = true,
						SceneTransitionAnimationType animation = SCENE_TRANSITION_ANIMATION_TYPE_LEFT,
						SceneHistoryOption history = SCENE_HISTORY_OPTION_ADD_HISTORY);
	void ExecuteScriptInWebView(String* JSScript);
	void ExecuteWaitingJSCallList();
	void SendCallbackResponse(	JsonString* callbackId,
									String* stringToSend);
	void SendCallbackResponse(	JsonString* callbackId,
								JsonObject* subObject);
	void SendEvent(	JsonString* name,
					JsonObject* object);

public:
	const static String kType ;
	const static String kTypeEvent;
	const static String kTypeLog;
	const static String kTypeAlert;
	const static String kTypeNavigation;
	const static String kTypeCallback;
	const static String kTypePush;
	const static String kTypePop;
	const static String kTypeModal;
	const static String kTypeDismiss;
	const static String kCallback;
	const static String kNameToast;
	const static String kOpenExternalUrl;

	const static String kTypeNativeBridgeReady;
	const static String kValue;
	const static String kName;
	const static String kIndex;
	const static String kUrl;
	const static String kJSAlertTitle;
	const static String kJSAlertMessage;
	const static String kJSAlertButtons;
	const static String kJSAlertReceiver;
	const static String kJSAlertId;
	const static String kJSCallbackID;
	const static String kJSNavigationType;
	const static String kJSNavigationClassId;
	const static String kJSNavigationPageName;
	const static String kJSNavigationPushId;
	const static String kJSOnBackButtonPressed;
	const static String kJSHandleBackButtonPressedResult;
	const static String kJSParams;
	const static String kJSNameSetZoom;

private:

	static String __ressourcePath;
	static String __htmlPageToLoad;


	Web* __pWeb;
	NativeBridgeAlert* __pAlert;
	bool __nativeBridgeIsReady;
	ArrayListT <String> __waitingJSCallList;
};

#endif /* NATIVEBRIDGEFORM_H_ */
