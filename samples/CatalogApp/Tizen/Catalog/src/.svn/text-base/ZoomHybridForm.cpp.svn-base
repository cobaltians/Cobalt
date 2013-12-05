/*
 * ZooHybridForm.cpp
 *
 *  Created on: Sep 27, 2013
 *      Author: sebastienfamel
 */

#include "ZoomHybridForm.h"

#include "AppResourceId.h"

using namespace Tizen::Base;
using namespace Tizen::Graphics;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Web::Json;
using namespace Tizen::Io;
using namespace Tizen::App;

/*int zoomLevel;
String fileName(L"zoomLevelToLoad.txt");
ButtonItem buttonMore;
ButtonItem buttonLess;*/

ZoomHybridForm::ZoomHybridForm()
{

}

ZoomHybridForm::ZoomHybridForm(String htmlPageToLoad) : NativeBridgeForm(htmlPageToLoad)
{

}

ZoomHybridForm::~ZoomHybridForm()
{

}

bool
ZoomHybridForm::Initialize()
{

	NativeBridgeForm::Initialize();

	int zoomLevel = GetZoomLevelToLoad();
	SetZoomLevelInWebView(zoomLevel);

	Footer* pFooter = GetFooter();
	pFooter->SetStyle(FOOTER_STYLE_BUTTON_TEXT);
	pFooter->SetColor(Color (0xFF, 0xAA, 0xAA));

	__buttonMore.Construct(BUTTON_ITEM_STYLE_TEXT, ID_BUTTON_MORE);
	__buttonMore.SetText(" + ");

	pFooter->SetButton(BUTTON_POSITION_RIGHT, __buttonMore);
	if (zoomLevel >= 20)
	{
		pFooter->SetButtonEnabled(BUTTON_POSITION_RIGHT, false);
	}
	__buttonLess.Construct(BUTTON_ITEM_STYLE_TEXT, ID_BUTTON_LESS);
	__buttonLess.SetText(" - ");

	pFooter->SetButton(BUTTON_POSITION_LEFT, __buttonLess);
	if (zoomLevel <= 5) {
		pFooter->SetButtonEnabled(BUTTON_POSITION_LEFT, false);
	}
	pFooter->AddActionEventListener(*this);

	return true;
}

void
ZoomHybridForm::OnActionPerformed(const Control& source, int actionId)
{
	int zoomLevel = GetZoomLevelToLoad();

	Footer* pFoot = GetFooter();

	switch (actionId) {
	case ID_BUTTON_MORE :
		zoomLevel++;
		pFoot->SetButtonEnabled(BUTTON_POSITION_LEFT, true);

		if (zoomLevel >= 20)
		{
			pFoot->SetButtonEnabled(BUTTON_POSITION_RIGHT, false);
		}
		pFoot->Invalidate(true);

		SetZoomLevelInWebView(zoomLevel);
		SetZoomLevelToLoad(zoomLevel);
		break;
	case ID_BUTTON_LESS :
		zoomLevel--;

		pFoot->SetButtonEnabled(BUTTON_POSITION_RIGHT, true);

		if (zoomLevel <= 5)
		{
			pFoot->SetButtonEnabled(BUTTON_POSITION_LEFT, false);
		}
		pFoot->Invalidate(true);

		SetZoomLevelInWebView(zoomLevel);
		SetZoomLevelToLoad(zoomLevel);
		break;
	default :
		break;
	}
}

void
ZoomHybridForm::SetZoomLevelInWebView(int zoomLevel)
{
	JsonObject* pJsonObj = new JsonObject();
	pJsonObj->Construct();

	JsonString* pKTypeEvent = new JsonString(kTypeEvent);
	JsonString* pKJSNameSetZoom = new JsonString(kJSNameSetZoom);
	JsonNumber* pZoomLevel = new JsonNumber(zoomLevel);
	pJsonObj->Add(&kType, pKTypeEvent);
	pJsonObj->Add(&kName, pKJSNameSetZoom);
	pJsonObj->Add(&kValue, pZoomLevel);

	char* pCompseBuf = new char[500];
	JsonWriter::Compose(pJsonObj, pCompseBuf, 500);
	String script(pCompseBuf);

	NativeBridgeForm::ExecuteScriptInWebView(&script);
}

void
ZoomHybridForm::SetZoomLevelToLoad(int zoomLevel)
{
	AppSetting *pAppSetting = AppSetting::GetInstance();
	AppAssert(pAppSetting);
	AppAssert(pAppSetting->SetValue(IDI_ZOOMLEVEL, zoomLevel) == E_SUCCESS);
}

int
ZoomHybridForm::GetZoomLevelToLoad()
{
	int zoomLevel = 10;

	AppSetting *pAppSetting = AppSetting::GetInstance();
	AppAssert(pAppSetting);
	AppAssert(pAppSetting->GetValue(IDI_ZOOMLEVEL, zoomLevel) == E_SUCCESS);

	return zoomLevel;
}

