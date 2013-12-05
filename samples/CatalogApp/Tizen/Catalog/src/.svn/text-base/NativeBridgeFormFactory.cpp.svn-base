/*
 * NativeBridgeFormFactory.cpp
 *
 *  Created on: Sep 24, 2013
 *      Author: sebastienfamel
 */

#include "NativeBridgeFormFactory.h"
#include "NativeBridgeForm.h"
#include "ZoomHybridForm.h"
#include "CustomHybridForm.h"

using namespace Tizen::Ui::Scenes;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Base;
using namespace Tizen::App;

const String NativeBridgeFormFactory::FORM_DEFAULT = L"default";
const String NativeBridgeFormFactory::FORM_ZOOM_HYBRID = L"zoomHybrid";
const String NativeBridgeFormFactory::FORM_CUSTOM_HYBRID = L"customHybrid";
const String NativeBridgeFormFactory::FORM_MODAL_TEST = L"modalTest";
const String NativeBridgeFormFactory::FORM_TOAST_ALERT = L"toastAlert";
const String NativeBridgeFormFactory::FORM_PULL_TO_REFRESH = L"pullToRefresh";
const String NativeBridgeFormFactory::FORM_INFINITE_SCROLL = L"infiniteScroll";
const String NativeBridgeFormFactory::FORM_PULL_TO_REFRESH_AND_INFINITE_SCROLL = L"PTR_and_IfiniteScroll";
const String NativeBridgeFormFactory::FORM_PULL_TO_REFRESH_NON_ACTIVE = L"pullToRefreshNonActive";
const String NativeBridgeFormFactory::FORM_PULL_TO_REFRESH_CUSTOM = L"pullToRefreshCustom";
const String NativeBridgeFormFactory::FORM_INFINITE_SCROLL_NON_ACTIVE = L"infiniteScrollNonActive";


NativeBridgeFormFactory::NativeBridgeFormFactory()
{

}

NativeBridgeFormFactory::~NativeBridgeFormFactory()
{
}

Form*
NativeBridgeFormFactory::CreateFormN(const String& formId, const SceneId& sceneId)
{
	String formToUse;

	int indexOfString = 0;
	String stringToFind = L"@§@";
	result r = formId.IndexOf(stringToFind, 0, indexOfString);

	if (r == E_SUCCESS)
	{
		formId.SubString(0, indexOfString, formToUse);
	}
	else
	{
		formToUse = formId;
	}

	String htmlPageToLoad = NativeBridgeForm::GetHtmlPageNameToLoad();
	//AppLogDebug("LOAD %ls", htmlPageToLoad.GetPointer());
	//AppLogDebug("FormToUse : %ls", formToUse.GetPointer());

	NativeBridgeForm* pForm;
	if (formToUse.Equals(FORM_DEFAULT, true))
	{
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();

	}
	else if (formToUse.Equals(FORM_ZOOM_HYBRID, true))
	{
		ZoomHybridForm* pFormCustom = new (std::nothrow) ZoomHybridForm(htmlPageToLoad);
		pFormCustom->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE | FORM_STYLE_FOOTER);
		pFormCustom->Initialize();
		pForm = pFormCustom;
	}
	else if (formToUse.Equals(FORM_CUSTOM_HYBRID, true))
	{
		CustomHybridForm* pFormCustom = new (std::nothrow) CustomHybridForm(htmlPageToLoad);
		pFormCustom->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE | FORM_STYLE_FOOTER);
		pFormCustom->Initialize();
		pForm = pFormCustom;
	}
	else if (formToUse.Equals(FORM_MODAL_TEST, true))
	{
		//TODO construct modalTestForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_TOAST_ALERT, true))
	{
		//TODO construct toastAlertForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_PULL_TO_REFRESH, true))
	{
		//TODO construct pullToRefreshForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_INFINITE_SCROLL, true))
	{
		//TODO construct infiniteScrollForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_PULL_TO_REFRESH_AND_INFINITE_SCROLL, true))
	{
		//TODO construct PTR_and_ifiniteScrollForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_PULL_TO_REFRESH_NON_ACTIVE, true))
	{
		//TODO construct pullToRefreshNonActiveForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_PULL_TO_REFRESH_CUSTOM, true)) {
		//TODO construct pullToRefreshCustomForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else if (formToUse.Equals(FORM_INFINITE_SCROLL_NON_ACTIVE, true))
	{
		//TODO construct infiniteScrollNonActiveForm
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	else
	{
		AppLogDebug("ERROR : Invalid formId : %ls" , formToUse.GetPointer());
		pForm = new (std::nothrow) NativeBridgeForm(htmlPageToLoad);
		pForm->Construct(FORM_STYLE_NORMAL |  FORM_STYLE_INDICATOR_AUTO_HIDE );
		pForm->Initialize();
	}
	return pForm;
}
