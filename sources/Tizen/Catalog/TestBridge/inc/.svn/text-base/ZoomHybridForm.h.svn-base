/*
 * ZoomHybridForm.h
 *
 *  Created on: Sep 27, 2013
 *      Author: sebastienfamel
 */

#ifndef ZOOMHYBRIDFORM_H_
#define ZOOMHYBRIDFORM_H_

#include <NativeBridgeForm.h>

#include "tizenx.h"

using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Base;


class ZoomHybridForm : 	public NativeBridgeForm,
						public IActionEventListener
{
public:
	ZoomHybridForm();
	ZoomHybridForm(String htmlPageToLoad);
	ZoomHybridForm(String ressourcePath,String htmlPageToLoad);
	virtual ~ZoomHybridForm();

	virtual void OnActionPerformed(const Control& source, int actionId);

	bool Initialize();
	static void SetZoomLevelToLoad(int zoomLevel);
	static int GetZoomLevelToLoad();

	void SetZoomLevelInWebView(int zoomLevel);

private:


private :
	static const int ID_BUTTON_MORE = 101;
	static const int ID_BUTTON_LESS = 102;

	ButtonItem __buttonMore;
	ButtonItem __buttonLess;
};

#endif /* ZOOMHYBRIDFORM_H_ */
