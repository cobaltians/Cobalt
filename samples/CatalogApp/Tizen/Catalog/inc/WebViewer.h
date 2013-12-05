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

#ifndef _WEB_VIEWER_H_
#define _WEB_VIEWER_H_

#include <FApp.h>
#include <FBase.h>
#include <FSystem.h>
#include <FUi.h>
#include <FWeb.h>

#include "NativeBridgeForm.h"

class NativeBridgeAlert;

class WebViewer
	: public Tizen::App::Application
	, public Tizen::Ui::Controls::IFormBackEventListener

{
public:
	WebViewer(void);
	~WebViewer(void);

	// The application must have a factory method that creates an instance of the application.
	static Tizen::App::Application* CreateInstance(void);

	// This method is called when the application is on initializing.
	virtual bool OnAppInitializing(Tizen::App::AppRegistry& appRegistry);

	// This method is called when the application is on terminating.
	virtual bool OnAppTerminating(Tizen::App::AppRegistry& appRegistry, bool forcedTermination = false);

	// Thie method is called when the application is brought to the foreground
	virtual void OnForeground(void);

	// This method is called when the application is sent to the background.
	virtual void OnBackground(void);

	// This method is called when the application has little available memory.
	virtual void OnLowMemory(void);

	// This method is called when the device's battery level is changed.
	virtual void OnBatteryLevelChanged(Tizen::System::BatteryLevel batteryLevel);

	// Called when the screen turns on.
	virtual void OnScreenOn(void);

	// Called when the screen turns off.
	virtual void OnScreenOff(void);

	virtual void OnFormBackRequested(Tizen::Ui::Controls::Form& source);

private:
	result CreateWebForm(void);


private:

	NativeBridgeForm* __pMainForm;
	Tizen::Base::String __path;

};

#endif //_WEB_VIEWER_H_

