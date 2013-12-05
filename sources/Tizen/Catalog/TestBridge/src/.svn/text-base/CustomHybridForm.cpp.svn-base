/*
 * CustomHybridForm.cpp
 *
 *  Created on: Oct 2, 2013
 *      Author: sebastienfamel
 */

#include "CustomHybridForm.h"

using namespace Tizen::Base;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Web::Json;
using namespace Tizen::Io;
using namespace Tizen::App;

CustomHybridForm::CustomHybridForm()
{

}

CustomHybridForm::~CustomHybridForm()
{

}

CustomHybridForm::CustomHybridForm(String htmlPageToLoad) : NativeBridgeForm(htmlPageToLoad)
{

}

CustomHybridForm::CustomHybridForm(String ressourcePath,String htmlPageToLoad)
{

}

bool
CustomHybridForm::Initialize()
{
	NativeBridgeForm::Initialize();

	unsigned long style = GetFormStyle();
	Footer* pFooter;

	if (style & FORM_STYLE_FOOTER)
	{
		pFooter = GetFooter();
		pFooter->SetStyle(FOOTER_STYLE_BUTTON_TEXT);
		ButtonItem btnCharac;
		btnCharac.Construct(BUTTON_ITEM_STYLE_TEXT, ID_BUTTON_CHARAC);
		btnCharac.SetText("envoi au JS des caractères spéciaux");
		pFooter->SetButton(BUTTON_POSITION_RIGHT, btnCharac);
		pFooter->AddActionEventListener(*this);
	}

	return true;
}

void
CustomHybridForm::OnActionPerformed(const Tizen::Ui::Control& source, int actionId)
{
	switch (actionId)
	{
	case ID_BUTTON_CHARAC :
		JsonObject* pJsonObj = new JsonObject();
		pJsonObj->Construct();

		JsonString* pKTypeEvent = new JsonString(kTypeEvent);
		JsonString* pKJSNameTestCallback = new JsonString(L"nameTestCallback");
		JsonString* ptestCharac = new JsonString(L"@#&é/'(§è!çà)-_$*^¨`£ù%\"\\");
		pJsonObj->Add(&kType, pKTypeEvent);
		pJsonObj->Add(&kName, pKJSNameTestCallback);
		pJsonObj->Add(&kValue, ptestCharac);

		char* pCompseBuf = new char[500];
		JsonWriter::Compose(pJsonObj, pCompseBuf, 500);
		String script(pCompseBuf);

		NativeBridgeForm::ExecuteScriptInWebView(&script);
		break;
	}
}
