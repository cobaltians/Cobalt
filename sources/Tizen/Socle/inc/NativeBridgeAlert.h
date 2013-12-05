/*
 * NativeBridgeAlert.h
 *
 *  Created on: Sep 12, 2013
 *      Author: sebastienfamel
 */

#ifndef NATIVEBRIDGEALERT_H_
#define NATIVEBRIDGEALERT_H_

#include "NativeBridge/NativeBridgeForm.h"

#include "tizenx.h"

using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Web::Json;
using namespace Tizen::Base;

class NativeBridgeForm;

class NativeBridgeAlert
	: public Popup
	, public JsonObject
	, public IActionEventListener
{
public:
	NativeBridgeAlert();
	NativeBridgeAlert(JsonString title, JsonString message, JsonArray* pArrayButton);
	virtual ~NativeBridgeAlert();
	virtual result OnInitializing(void);

	void ShowPopup(void);
	void HidePopup(void);
	void SetNativeBridgeForm(NativeBridgeForm* form);
	void SetCallbackID(JsonString* callbackID);
	void SetAlertId(JsonNumber* alertID);

	virtual void OnActionPerformed(const Control& source, int actionId);

private:
	static const int ID_BUTTON_ONE = 501;
	static const int ID_BUTTON_TWO = 502;
	static const int ID_BUTTON_THREE = 503;

	JsonString __title;
	JsonString __message;
	JsonString* __pCallbackID;;
	JsonArray* __pArrayButton;
	JsonNumber* __pAlertID;
	NativeBridgeForm* __pForm;
	Integer __selectedButton;

	Popup* __pPopup;
};

#endif /* NATIVEBRIDGEALERT_H_ */
