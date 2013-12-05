/*
 * NativeBridgeAlert.cpp
 *
 *  Created on: Sep 12, 2013
 *      Author: sebastienfamel
 */

#include "NativeBridgeAlert.h"
#include "WebViewer.h"

using namespace Tizen::Graphics;
using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Web::Controls;
using namespace Tizen::Web::Json;
using namespace Tizen::Base;
using namespace Tizen::Base::Collection;

NativeBridgeAlert::NativeBridgeAlert() :	__title(""),
											__message(""),
											__pCallbackID(null),
											__pArrayButton(null),
											__pAlertID(null),
											__pForm(null),
											__selectedButton(1),
											__pPopup(null)
{

}

NativeBridgeAlert::NativeBridgeAlert(JsonString title, JsonString message, JsonArray *pArrayButton) :	__title(title),
																										__message(message),
																										__pCallbackID(null),
																										__pArrayButton(pArrayButton),
																										__pAlertID(null),
																										__selectedButton(1),
																										__pPopup(null)
{

}

NativeBridgeAlert::~NativeBridgeAlert()
{

}

result NativeBridgeAlert::OnInitializing(void)
{
	result r = E_SUCCESS;

	SetTitleText(__title);

	FloatRectangle rect;
	rect = GetClientAreaBoundsF();

	Label* pLabel = new (std::nothrow) Label();
	pLabel->Construct(
			FloatRectangle(0.0f, 32.0f, rect.width, rect.height - 150.0f),
			__message);
	pLabel->SetTextHorizontalAlignment(ALIGNMENT_CENTER);
	AddControl(pLabel);

	if (__pArrayButton->GetCount() == 2)
	{
		IJsonValue* pJsonValue = null;
		JsonString* pJsonString = null;

		__pArrayButton->GetAt(0, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonOne = new (std::nothrow) Button;
		pButtonOne->Construct(
				FloatRectangle(rect.width / 10, rect.height - 98.0f,
						rect.width / 3, 74.0f), pJsonString->GetPointer());
		pButtonOne->SetActionId(ID_BUTTON_ONE);
		pButtonOne->AddActionEventListener(*this);
		AddControl(pButtonOne);

		__pArrayButton->GetAt(1, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonTwo = new (std::nothrow) Button;
		pButtonTwo->Construct(
				FloatRectangle(((rect.width/2)+(rect.width / 10)), rect.height - 98.0f,
						rect.width / 3, 74.0f), pJsonString->GetPointer());
		pButtonTwo->SetActionId(ID_BUTTON_TWO);
		pButtonTwo->AddActionEventListener(*this);
		AddControl(pButtonTwo);
	}

	else if (__pArrayButton->GetCount() >= 2)
	{
		IJsonValue* pJsonValue = null;
		JsonString *pJsonString = null;

		__pArrayButton->GetAt(0, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonOne = new (std::nothrow) Button;
		pButtonOne->Construct(
				FloatRectangle(rect.width / 20, rect.height - 98.0f,
						rect.width / 4, 74.0f), pJsonString->GetPointer());
		pButtonOne->SetActionId(ID_BUTTON_ONE);
		pButtonOne->AddActionEventListener(*this);
		AddControl(pButtonOne);

		__pArrayButton->GetAt(1, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonTwo = new (std::nothrow) Button;
		pButtonTwo->Construct(
				FloatRectangle(((rect.width/3)+(rect.width / 20)), rect.height - 98.0f,
						rect.width / 4, 74.0f), pJsonString->GetPointer());
		pButtonTwo->SetActionId(ID_BUTTON_TWO);
		pButtonTwo->AddActionEventListener(*this);
		AddControl(pButtonTwo);

		__pArrayButton->GetAt(2, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonThree = new (std::nothrow) Button;
		pButtonThree->Construct(
				FloatRectangle(((2*(rect.width/3))+(rect.width / 20)), rect.height - 98.0f,
						rect.width / 4, 74.0f), pJsonString->GetPointer());
		pButtonThree->SetActionId(ID_BUTTON_THREE);
		pButtonThree->AddActionEventListener(*this);
		AddControl(pButtonThree);
	}

	else
	{
		IJsonValue* pJsonValue = null;
		JsonString *pJsonString = null;

		__pArrayButton->GetAt(0, pJsonValue);
		pJsonString = static_cast<JsonString*>(pJsonValue);

		Button* pButtonClose = new (std::nothrow) Button;
		pButtonClose->Construct(
				FloatRectangle(rect.width / 4, rect.height - 98.0f,
				rect.width / 2, 74.0f), pJsonString->GetPointer());
		pButtonClose->SetActionId(ID_BUTTON_ONE);
		pButtonClose->AddActionEventListener(*this);
		AddControl(pButtonClose);
	}
	return r;
}

void NativeBridgeAlert::ShowPopup(void)
{
	SetShowState(true);
	Show();
}

void NativeBridgeAlert::HidePopup(void)
{
	SetShowState(false);
	if (__pForm != null)
	{
		__pForm->PopupHasHidden(&__selectedButton, __pCallbackID, __pAlertID);
	}
	Invalidate(true);
}

void NativeBridgeAlert::SetNativeBridgeForm(NativeBridgeForm* pForm)
{
	__pForm = pForm;
}

void NativeBridgeAlert::SetCallbackID(JsonString* pCallbackID)
{
	__pCallbackID = pCallbackID;
}

void NativeBridgeAlert::SetAlertId(JsonNumber* pAlertID)
{
	__pAlertID = pAlertID;
}

void NativeBridgeAlert::OnActionPerformed(const Control& source, int actionId)
{
	switch (actionId)
	{
	case ID_BUTTON_ONE:
		__selectedButton = 0;
		HidePopup();
		break;
	case ID_BUTTON_TWO:
		__selectedButton = 1;
		HidePopup();
		break;
	case ID_BUTTON_THREE:
		__selectedButton = 2;
		HidePopup();
		break;
	default:
		break;
	}
}

