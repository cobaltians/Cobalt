/*
 * CustomHybridForm.h
 *
 *  Created on: Oct 2, 2013
 *      Author: sebastienfamel
 */

#ifndef CUSTOMHYBRIDFORM_H_
#define CUSTOMHYBRIDFORM_H_

#include <NativeBridgeForm.h>
#include <FIo.h>
#include <FApp.h>

class CustomHybridForm
	: public NativeBridgeForm
	, public Tizen::Ui::IActionEventListener
{
public:
	CustomHybridForm();
	CustomHybridForm(Tizen::Base::String htmlPageToLoad);
	CustomHybridForm(Tizen::Base::String ressourcePath,Tizen::Base::String htmlPageToLoad);

	virtual void OnActionPerformed(const Tizen::Ui::Control& source, int actionId);

	bool Initialize();
	virtual ~CustomHybridForm();

private :
	static const int ID_BUTTON_CHARAC = 201;
};

#endif /* CUSTOMHYBRIDFORM_H_ */
