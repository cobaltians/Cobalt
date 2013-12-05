//
// Tizen C++ SDK
// Copyright (c) 2012 Samsung Electronics Co., Ltd.
//
// Licensed under the Flora License, Version 1.1 (the License);
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://floralicense.org/license
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an AS IS BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

#include <FSysPowerManager.h>
#include "WebViewer.h"
#include "NativeBridgeForm.h"
#include "NativeBridgeFormFactory.h"

using namespace Tizen::Base;
using namespace Tizen::System;
using namespace Tizen::App;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Ui::Scenes;

const wchar_t *SCENE_DEFAULT = L"default";

WebViewer::WebViewer(void)
: __pMainForm(null)
, __path("")
{
}

WebViewer::~WebViewer(void)
{
}

Application*
WebViewer::CreateInstance(void)
{
	// You can create the instance through another constructor.
	return new WebViewer();
}


bool
WebViewer::OnAppInitializing(AppRegistry& appRegistry)
{
	static const wchar_t* PANEL_BLANK = L"";

	static NativeBridgeFormFactory formFactory;

	SceneManager* pSceneManager = SceneManager::GetInstance();
	AppAssert(pSceneManager);
	pSceneManager->RegisterFormFactory(formFactory);

	pSceneManager->RegisterScene(SCENE_DEFAULT, SCENE_DEFAULT, PANEL_BLANK);

	__path = L"file://" + GetAppResourcePath() + L"www_tv/";

	Frame* pAppFrame = new Frame();
	pAppFrame->Construct();
	AddFrame(*pAppFrame);

	result r = E_SUCCESS;

	r = CreateWebForm();
	TryCatch(r == E_SUCCESS, , "CreateWebForm() has failed.\n");
	return true;

	CATCH:
	return false;
}

bool
WebViewer::OnAppTerminating(AppRegistry& appRegistry, bool forcedTermination)
{
	return true;
}


void
WebViewer::OnForeground(void)
{
	//__pWeb->Resume();
}


void
WebViewer::OnBackground(void)
{
	//__pWeb->Pause();
}


void
WebViewer::OnLowMemory(void)
{
}


void
WebViewer::OnBatteryLevelChanged(BatteryLevel batteryLevel)
{
}

result
WebViewer::CreateWebForm(void)
{
	result r = E_SUCCESS;

	String pageNamed = L"app/html/home.html";
	NativeBridgeForm::SetHtmlPageNameToLoad(pageNamed.GetPointer());

	SceneManager* pSceneManager = SceneManager::GetInstance();
	AppAssert(pSceneManager);
	pSceneManager->GoForward(ForwardSceneTransition(SCENE_DEFAULT));

	return r;
}

void
WebViewer::OnScreenOn(void)
{
}

void
WebViewer::OnScreenOff(void)
{
}

void
WebViewer::OnFormBackRequested(Tizen::Ui::Controls::Form& source)
{
	AppLog("OnFormBackRequested.");
	Terminate();
}



