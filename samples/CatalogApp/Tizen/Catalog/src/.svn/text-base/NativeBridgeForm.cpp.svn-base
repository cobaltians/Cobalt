/*
 * NativeBridgeForm.cpp
 *
 *  Created on: Sep 23, 2013
 *      Author: sebastienfamel
 */
#
#include "NativeBridgeForm.h"
#include "ZoomHybridForm.h"

using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Web;
using namespace Tizen::Web::Controls;
using namespace Tizen::Graphics;
using namespace Tizen::Base;
using namespace Tizen::Base::Collection;
using namespace Tizen::Web::Json;
using namespace Tizen::Ui::Scenes;
using namespace Tizen::App;
using namespace Tizen::Io;

const String NativeBridgeForm::kType = (L"type");
const String NativeBridgeForm::kTypeEvent = (L"typeEvent");
const String NativeBridgeForm::kTypeLog = (L"typeLog");
const String NativeBridgeForm::kTypeAlert = (L"typeAlert");
const String NativeBridgeForm::kTypeNavigation = (L"typeNavigation");
const String NativeBridgeForm::kTypeCallback = (L"typeCallback");
const String NativeBridgeForm::kTypePush = (L"push");
const String NativeBridgeForm::kTypePop = (L"pop");
const String NativeBridgeForm::kTypeModale = (L"modale");
const String NativeBridgeForm::kTypeDismiss = (L"dismiss");
const String NativeBridgeForm::kCallback = (L"callback");
const String NativeBridgeForm::kNameToast = (L"nameToast");

const String NativeBridgeForm::kTypeNativeBridgeReady = (L"nativeBridgeIsReady");
const String NativeBridgeForm::kValue = (L"value");
const String NativeBridgeForm::kName = (L"name");
const String NativeBridgeForm::kIndex = (L"index");
const String NativeBridgeForm::kJSAlertTitle = (L"alertTitle");
const String NativeBridgeForm::kJSAlertMessage = (L"alertMessage");
const String NativeBridgeForm::kJSAlertButtons = (L"alertButtons");
const String NativeBridgeForm::kJSAlertReceiver = (L"alertReceiver");
const String NativeBridgeForm::kJSAlertId = (L"alertId");
const String NativeBridgeForm::kJSCallbackID = (L"callbackID");
const String NativeBridgeForm::kJSNavigationType = (L"navigationType");
const String NativeBridgeForm::kJSNavigationClassId = (L"navigationClassId");
const String NativeBridgeForm::kJSNavigationPageName = (L"navigationPageName");
const String NativeBridgeForm::kJSNavigationPushId = (L"pushNumber");
const String NativeBridgeForm::kJSOnBackButtonPressed = (L"onBackButtonPressed");
const String NativeBridgeForm::kJSHandleBackButtonPressedResult = (L"HandleBackButtonPressedResult");
const String NativeBridgeForm::kJSParams = (L"params");
const String NativeBridgeForm::kJSNameSetZoom = (L"nameSetZoom");

String NativeBridgeForm::__ressourcePath = null;
String NativeBridgeForm::__htmlPageToLoad = null;

bool nativeBridgeIsReady;

NativeBridgeForm::NativeBridgeForm():	__pWeb(null),
										__pAlert(null)
{

}

NativeBridgeForm::NativeBridgeForm(String htmlPageToLoad):	__pWeb(null),
															__pAlert(null)
{
	__htmlPageToLoad = htmlPageToLoad;
}

NativeBridgeForm::NativeBridgeForm(String ressourcePath,String htmlPageToLoad):	__pWeb(null),
																				__pAlert(null)
{
	__htmlPageToLoad = htmlPageToLoad;
	__ressourcePath = ressourcePath;
}


NativeBridgeForm::~NativeBridgeForm()
{

}

bool
NativeBridgeForm::Initialize()
{

	if(__ressourcePath == null)
	{
		__ressourcePath = L"file://" + Tizen::App::App::GetInstance()->GetAppResourcePath() + L"www_tv/";
	}

	__waitingJSCallList.Construct();

	AddOrientationEventListener(*this);
	SetOrientation(ORIENTATION_AUTOMATIC_FOUR_DIRECTION);
	SetFormBackEventListener(this);
	GetClientAreaBounds();

	SetBackgroundColor(Color (0xFF, 0xAA, 0xAA));
	Rectangle bound = GetClientAreaBounds();

	__pWeb = new (std::nothrow) Web();
	__pWeb->Construct(Rectangle(0, 0, bound.width, bound.height));
	AddControl(__pWeb);
	Rectangle r = __pWeb->GetBounds();

	__pWeb->SetFocus();

	nativeBridgeIsReady = false;
	__pWeb->AddJavaScriptBridge(*this);

	return true;
}

void
NativeBridgeForm::LoadUrl(const String ressourcePath, const String url)
{
	String urlToLoad;
	urlToLoad.Append(ressourcePath);
	urlToLoad.Append(url);
	__pWeb->LoadUrl(urlToLoad.GetPointer());
}

result
NativeBridgeForm::OnInitializing(void)
{
	if(__htmlPageToLoad != null && __ressourcePath !=null)
		__pWeb->LoadUrl(__ressourcePath +__htmlPageToLoad);
	return E_SUCCESS;
}

result
NativeBridgeForm::OnTerminating(void)
{
	return E_SUCCESS;
}

void
NativeBridgeForm::OnOrientationChanged(const Control& source, OrientationStatus orientationStatus)
{
	Rectangle bound = GetClientAreaBounds();

	__pWeb->SetBounds(Rectangle(0, 0, bound.width, bound.height));
	Invalidate(true);
}

void
NativeBridgeForm::OnFormBackRequested(Form& source)
{
	JsonObject* pJsonObj = new JsonObject();
	pJsonObj->Construct();

	JsonString* pKTypeEvent = new JsonString(kTypeEvent);
	JsonString* pKJSOnBackButtonPressed = new JsonString(kJSOnBackButtonPressed);
	JsonString* pCallBackId = new JsonString(kJSHandleBackButtonPressedResult);
	pJsonObj->Add(&kType, pKTypeEvent);
	pJsonObj->Add(&kName, pKJSOnBackButtonPressed);
	pJsonObj->Add(&kJSCallbackID, pCallBackId);

	char* pCompseBuf = new char[500];
	JsonWriter::Compose(pJsonObj, pCompseBuf, 500);
	String script(pCompseBuf);

	ExecuteScriptInWebView(&script);
}

void
NativeBridgeForm::HandleJavaScriptRequestN(IJsonValue* pArg)
{
	JsonObject* pJsonObject = static_cast<JsonObject *> (pArg);
	IJsonValue* pJsonData = null;

	String key(L"data");

	pJsonObject->GetValue(&key, pJsonData);

	TreatData(pJsonData);
}

void
NativeBridgeForm::TreatData(IJsonValue* pJSData)
{
	JsonObject* pJSObjectData = static_cast<JsonObject *> (pJSData);
	IJsonValue* pJSValueType = null;
	IJsonValue* pJSValueValue = null;
	IJsonValue* pJSValueName = null;
	IJsonValue* pJSValueAlertTitle = null;
	IJsonValue* pJSValueAlertMessage = null;
	IJsonValue* pJSValueAlertButtons = null;
	IJsonValue* pJSValueAlertId = null;
	IJsonValue* pJSValueCallbackID = null;
	IJsonValue* pJSValueAlertReceiver = null;
	IJsonValue* pJSValueNavigationType = null;
	IJsonValue* pJSValueActivityId = null;
	IJsonValue* pJSValuePageNamed = null;
	IJsonValue* pJSValueNavigationPushId = null;
	IJsonValue* pJSValueParams = null;
	JsonString* pJSStringNavigationType = null;
	JsonString* pJSStringActivityId = new JsonString(L"");
	JsonString* pJSStringPageNamed = null;
	JsonString* pJSStringType = null;
	JsonString* pJSStringValue = null;
	JsonString* pJSStringName = null;
	JsonString* pJSStringAlertTitle = null;
	JsonString* pJSStringAlertMessage = null;
	JsonNumber* pJSNumberAlertId = null;
	JsonString* pJSStringCallbackID = null;
	JsonString* pJSStringAlertReceiver = null;
	JsonArray* pJSArrayAlertButtons = null;

	pJSObjectData->GetValue(&kType, pJSValueType);
	pJSStringType = static_cast<JsonString*> (pJSValueType);

	//type nativeBridgeIsReady
	if (pJSStringType->CompareTo(kTypeNativeBridgeReady) == 0)
	{
		nativeBridgeIsReady = true;
		ExecuteWaitingJSCallList();
	}

	//type log
	else if (pJSStringType->CompareTo(kTypeLog) == 0)
	{
		pJSObjectData->GetValue(&kValue, pJSValueValue);
		pJSStringValue = static_cast<JsonString*> (pJSValueValue);
	}

	//type event
	else if (pJSStringType->CompareTo(kTypeEvent) == 0)
	{
		pJSObjectData->GetValue(&kName, pJSValueName);
		pJSStringName = static_cast<JsonString*> (pJSValueName);

		if (pJSStringName->CompareTo(kNameToast) == 0)
		{
			pJSObjectData->GetValue(&kValue, pJSValueValue);
			pJSStringValue = static_cast<JsonString*> (pJSValueValue);

			// TODO
			/* __pMyToast = new MyToast();
			__pMainForm->AddControl(__pMyToast);*/
		}
	}

	//type alert
	else if (pJSStringType->CompareTo(kTypeAlert) == 0)
	{

		pJSObjectData->GetValue(&kJSAlertTitle, pJSValueAlertTitle);
		if (pJSValueAlertTitle != null)
		{
			pJSStringAlertTitle = static_cast<JsonString *> (pJSValueAlertTitle);
		}

		pJSObjectData->GetValue(&kJSAlertMessage, pJSValueAlertMessage);
		if (pJSValueAlertMessage != null)
		{
			pJSStringAlertMessage = static_cast<JsonString *> (pJSValueAlertMessage);
		}

		pJSObjectData->GetValue(&kJSAlertButtons, pJSValueAlertButtons);
		if (pJSValueAlertButtons != null)
		{
			pJSArrayAlertButtons = static_cast<JsonArray *> (pJSValueAlertButtons);
		}

		pJSObjectData->GetValue(&kJSAlertId, pJSValueAlertId);
		if (pJSValueAlertId != null)
		{
			pJSNumberAlertId = static_cast<JsonNumber *> (pJSValueAlertId);
		}

		pJSObjectData->GetValue(&kJSCallbackID, pJSValueCallbackID);
		if (pJSValueCallbackID != null)
		{
			if (pJSValueCallbackID->GetType() == JSON_TYPE_STRING)
			{
				pJSStringCallbackID = static_cast<JsonString *> (pJSValueCallbackID);
			}
			else if (pJSValueCallbackID->GetType() == JSON_TYPE_NUMBER)
			{
				JsonNumber* pJSNumberTemp = static_cast <JsonNumber *> (pJSValueCallbackID);
				Integer pInt = pJSNumberTemp->value;
				pJSStringCallbackID = new JsonString(L"");
				pJSStringCallbackID->Append(pInt.ToInt());
			}
		}

		pJSObjectData->GetValue(&kJSAlertReceiver, pJSValueAlertReceiver);
		if (pJSValueAlertReceiver != null)
		{
			pJSStringAlertReceiver = static_cast<JsonString *> (pJSValueAlertReceiver);
		}

		CreatePopup(pJSStringAlertTitle, pJSStringAlertMessage, pJSArrayAlertButtons, pJSNumberAlertId, pJSStringCallbackID);
	}

	//type navigation
	else if (pJSStringType->CompareTo(kTypeNavigation) == 0)
	{
		pJSObjectData->GetValue(&kJSNavigationType, pJSValueNavigationType);
		pJSStringNavigationType = static_cast<JsonString *> (pJSValueNavigationType);

		if (pJSStringNavigationType->CompareTo(kTypePush) == 0)
		{
			//get navigationClassId
			bool b = true;
			pJSObjectData->ContainsKey(&kJSNavigationClassId, b);
			if(b == true)
			{
				pJSObjectData->GetValue(&kJSNavigationClassId, pJSValueActivityId);
				if(pJSValueActivityId != null && pJSValueActivityId->GetType() != JSON_TYPE_NULL)
				{
					pJSStringActivityId = static_cast<JsonString *> (pJSValueActivityId);
				}
				else
				{
					pJSStringActivityId->Append(L"default");
				}
			}
			else
			{
				pJSStringActivityId->Append(L"default");
			}
			//@§@ is a separator between the "real" formId to generate and the identifier (to be able to register and push the scene)
			pJSStringActivityId->Append(L"@§@");
			//create the new scene activity name
			pJSObjectData->GetValue(&kJSNavigationPushId, pJSValueNavigationPushId);
			JsonNumber* pJSNavPushId = static_cast <JsonNumber *> (pJSValueNavigationPushId);
			Double* pPushId = static_cast<Double *> (pJSNavPushId);
			pJSStringActivityId->Append(pPushId->ToDouble());

			//get HTMLPageName
			pJSObjectData->GetValue(&kJSNavigationPageName, pJSValuePageNamed);
			pJSStringPageNamed = static_cast<JsonString *> (pJSValuePageNamed);

			//Pushwebview with arguments
			PushWebView(pJSStringActivityId, pJSStringPageNamed);
		}
		else if (pJSStringNavigationType->CompareTo(kTypePop) == 0)
		{
			PopWebViewActivity();
		}
		else if (pJSStringNavigationType->CompareTo(kTypeModale) == 0)
		{

		}
		else if (pJSStringNavigationType->CompareTo(kTypeDismiss) == 0)
		{

		}
		else AppLogDebug("Erreur de lecture de type");
	}

	//type callback
	else if (pJSStringType->CompareTo(kTypeCallback) == 0)
	{
		pJSObjectData->GetValue(&kJSCallbackID, pJSValueCallbackID);
		if (pJSValueCallbackID != null) {
			pJSStringCallbackID = static_cast<JsonString *> (pJSValueCallbackID);

			if (pJSStringCallbackID->CompareTo(kJSHandleBackButtonPressedResult) == 0)
			{
				pJSObjectData->GetValue(&kJSParams, pJSValueParams);
				JsonBool* pJSBoolAllowedToGoBack = static_cast<JsonBool *> (pJSValueParams);
				HandleBackButtonPressedResult(pJSBoolAllowedToGoBack);
			}
		}
		else
		{
			//catch params and if true close app
			pJSObjectData->GetValue(&kJSParams, pJSValueParams);
			JsonBool* pAllowedToGoBack = static_cast<JsonBool *> (pJSValueParams);
			HandleBackButtonPressedResult(pAllowedToGoBack);
		}
	}
	else
	{
		AppLogDebug("Unhandled nativeBridge type");
	}
}

void
NativeBridgeForm::PopWebViewActivity()
{
	//nativeBridgeIsReady = false;
	SceneManager* pSceneManager = SceneManager::GetInstance();
	AppAssert(pSceneManager);

	pSceneManager->GoBackward(BackwardSceneTransition(SCENE_TRANSITION_ANIMATION_TYPE_RIGHT));
}

void
NativeBridgeForm::HandleBackButtonPressedResult(bool allowedToGoBack)
{
	if (allowedToGoBack)
	{
		SceneManager* pSceneManager = SceneManager::GetInstance();
		AppAssert(pSceneManager);

		result r = pSceneManager->GoBackward(BackwardSceneTransition(SCENE_TRANSITION_ANIMATION_TYPE_RIGHT));

		if (r == E_UNDERFLOW)
		{
			Tizen::App::App* pApp = Tizen::App::App::GetInstance();
			AppAssert(pApp);
			pApp->Terminate();
		}
	}
}

void
NativeBridgeForm::PushWebView(JsonString* pJSStringActivityId, JsonString* pJSStringPageNamed)
{
	nativeBridgeIsReady = false;

	SceneManager* pSceneManager = SceneManager::GetInstance();
	AppAssert(pSceneManager);

	//save the url to load in new page
	String* pageNamed = static_cast<String *> (pJSStringPageNamed);
	SetHtmlPageNameToLoad(pageNamed->GetPointer());

	//push new scene
	//a new scene is registered each time we want to push a new view... not that clean... something better may be found later ;)
	//pActivityId is something like "zoomHybrid@@@7" with zoomHybrid defining the formId we want to create in formFactory and @@@+number which is a unique identifier
	//to be able to register a new scene.
	//Because problem is : impossible to push the same scene twice (pas deux fois d'affilée !)
	pSceneManager->RegisterScene(pJSStringActivityId->GetPointer(),pJSStringActivityId->GetPointer(), L"");
	pSceneManager->GoForward(ForwardSceneTransition(pJSStringActivityId->GetPointer(), SCENE_TRANSITION_ANIMATION_TYPE_LEFT, SCENE_HISTORY_OPTION_ADD_HISTORY));

}

void
NativeBridgeForm::CreatePopup(	JsonString* pJSStringAlertTitle, JsonString* pJSStringAlertMessage,
								JsonArray* pJSArrayAlertButtons, JsonNumber* pJSNumberAlertId,
								JsonString* pJSStringCallbackID)
{
	JsonArray *pJSArrayAlert = new JsonArray();
	pJSArrayAlert->Construct();

	if (pJSArrayAlertButtons == null)
	{
		JsonString *pJSStringClose = new JsonString(L"Fermer");
		pJSArrayAlert->Add(pJSStringClose);
	}
	else
	{
		pJSArrayAlert = pJSArrayAlertButtons;
	}

	if (pJSStringAlertTitle == null || pJSStringAlertMessage == null)
	{
		if (pJSStringAlertTitle == null)
		{
			__pAlert = new NativeBridgeAlert(L"", pJSStringAlertMessage->GetPointer(), pJSArrayAlert);
			__pAlert->Tizen::Ui::Controls::Popup::Construct(false, FloatDimension(630.0f, 750.0f));
		}
		else
		{
			__pAlert = new NativeBridgeAlert(pJSStringAlertTitle->GetPointer(), L"", pJSArrayAlert);
			__pAlert->Tizen::Ui::Controls::Popup::Construct(true, FloatDimension(630.0f, 230.0f));
		}
	}
	else
	{
		__pAlert = new NativeBridgeAlert(pJSStringAlertTitle->GetPointer(), pJSStringAlertMessage->GetPointer(), pJSArrayAlert);
		__pAlert->Tizen::Ui::Controls::Popup::Construct(true, FloatDimension(630.0f, 750.0f));
	}

	if (pJSStringCallbackID != null)
	{
		__pAlert->SetCallbackID(pJSStringCallbackID);
	}

	if (pJSNumberAlertId != null)
	{
		__pAlert->SetAlertId(pJSNumberAlertId);
	}

	__pAlert->SetNativeBridgeForm(this);
	__pAlert->ShowPopup();
}

String
NativeBridgeForm::GetName()
{
	return String(L"HPNativeBridge");
}

void
NativeBridgeForm::PopupHasHidden(Integer* pButtonIndex, JsonString* pJSStringCallbackID, JsonNumber* pJSNumberAlertID)
{

	if (pJSStringCallbackID != null)
	{
		JsonObject* pJSObjectParams = new JsonObject();
		pJSObjectParams->Construct();
		JsonNumber* pKJSIndex = new JsonNumber(pButtonIndex->ToInt());
		pJSObjectParams->Add(&kIndex, pKJSIndex);
		if (pJSNumberAlertID != null)
		{
			pJSObjectParams->Add(&kJSAlertId, pJSNumberAlertID);
		}

		// for use SenCallbackReponse with a String for params and not an object
		/*char* pComposeBuf = new char[50];
		JsonWriter::Compose(pJSObjectParams, pComposeBuf, 50);
		String paramsToSend(pComposeBuf);
		SendCallbackResponse(pJSStringCallbackID, &paramsToSend);
		*/

		SendCallbackResponse(pJSStringCallbackID, pJSObjectParams);
	}
}

void
NativeBridgeForm::ExecuteScriptInWebView(String* pScriptToJs)
{
	String debut = L"nativeBridge.execute(";
	String fin = L")";
	String script = debut + pScriptToJs->GetPointer() + fin;

	if (nativeBridgeIsReady)
	{
		__pWeb->EvaluateJavascriptN(script);
	}
	else
	{
		__waitingJSCallList.Add(script);
	}
}

void
NativeBridgeForm::ExecuteWaitingJSCallList()
{
	IEnumeratorT< String >* pEnum = __waitingJSCallList.GetEnumeratorN();

	while (pEnum->MoveNext() == E_SUCCESS)
	{
		String script;
		pEnum->GetCurrent(script);
		ExecuteScriptInWebView(&script);
	}
	__waitingJSCallList.RemoveAll();
}

void
NativeBridgeForm::SendCallbackResponse(JsonString* pJSStringCallbackId, String* pParamsToSend)
{
	char* pComposeBuf = new char[1000];
	JsonObject* pJSObjectScriptToSend = new JsonObject();
	pJSObjectScriptToSend->Construct();
	JsonString* pJSStringToSend = new JsonString(pParamsToSend->GetPointer());

	if(pJSStringCallbackId != null && pJSStringCallbackId->GetLength()  > 0)
	{
		JsonString* pKJSTypeCallback = new JsonString(kTypeCallback);

		pJSObjectScriptToSend->Add(&kType, pKJSTypeCallback);
		pJSObjectScriptToSend->Add(&kJSCallbackID, pJSStringCallbackId);
	}
	pJSObjectScriptToSend->Add(&kJSParams, pJSStringToSend);

	result r = JsonWriter::Compose(pJSObjectScriptToSend, pComposeBuf, 1000);
	if (r ==  E_SUCCESS)
	{
		String scriptToSend(pComposeBuf);
		ExecuteScriptInWebView(&scriptToSend);
	}
	else if (r == E_INVALID_ARG)
	{
		AppLogDebug("E_INVALID_ARG in JsonWriter ");
	}
	else if (r == E_INVALID_DATA)
	{
		AppLogDebug("E_INVALID_DATA in JsonWriter");
	}
}

void
NativeBridgeForm::SendCallbackResponse(JsonString* pJSStringCallbackId, JsonObject* pJSObjectParams)
{
	char* pComposeBuf = new char[1000];
	JsonObject* pJSObjectScriptToSend = new JsonObject();
	pJSObjectScriptToSend->Construct();

	if(pJSStringCallbackId != null && pJSStringCallbackId->GetLength()  > 0)
	{
		JsonString* pKJSTypeCallback = new JsonString(kTypeCallback);

		pJSObjectScriptToSend->Add(&kType, pKJSTypeCallback);
		pJSObjectScriptToSend->Add(&kJSCallbackID, pJSStringCallbackId);
	}

	pJSObjectScriptToSend->Add(&kJSParams, pJSObjectParams);

	result r = JsonWriter::Compose(pJSObjectScriptToSend, pComposeBuf, 1000);
	if (r ==  E_SUCCESS)
	{
		String scriptToSend(pComposeBuf);
		ExecuteScriptInWebView(&scriptToSend);
	}
	else if (r == E_INVALID_ARG)
	{
		AppLogDebug("E_INVALID_ARG in JsonWriter ");
	}
	else if (r == E_INVALID_DATA)
	{
		AppLogDebug("E_INVALID_DATA in JsonWriter");
	}

}

void
NativeBridgeForm::SetHtmlPageNameToLoad(String htmlToLoad)
{
	__htmlPageToLoad = htmlToLoad;
}

String
NativeBridgeForm::GetHtmlPageNameToLoad()
{
	return __htmlPageToLoad;
}
