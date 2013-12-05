/*
 * NativeBridgeFormFactory.h
 *
 *  Created on: Sep 24, 2013
 *      Author: sebastienfamel
 */

#ifndef NATIVEBRIDGEFORMFACTORY_H_
#define NATIVEBRIDGEFORMFACTORY_H_

#include "tizenx.h"

using namespace Tizen::Ui::Scenes;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Base;

class NativeBridgeFormFactory :
	public IFormFactory
{
public:
	NativeBridgeFormFactory();
	virtual ~NativeBridgeFormFactory();

	virtual Form* CreateFormN(const String& formId, const SceneId& sceneId);

public:

	static const String FORM_DEFAULT;
	static const String FORM_ZOOM_HYBRID;
	static const String FORM_CUSTOM_HYBRID;
	static const String FORM_MODAL_TEST;
	static const String FORM_TOAST_ALERT;
	static const String FORM_PULL_TO_REFRESH;
	static const String FORM_INFINITE_SCROLL;
	static const String FORM_PULL_TO_REFRESH_AND_INFINITE_SCROLL;
	static const String FORM_PULL_TO_REFRESH_NON_ACTIVE;
	static const String FORM_PULL_TO_REFRESH_CUSTOM;
	static const String FORM_INFINITE_SCROLL_NON_ACTIVE;

};


#endif /* NATIVEBRIDGEFORMFACTORY_H_ */
