/*
 * MyToast.cpp
 *
 *  Created on: Aug 8, 2013
 *      Author: sebastienfamel
 */
#include <FGraphics.h>
#include "MyToast.h"

using namespace Tizen::Ui;
using namespace Tizen::Ui::Controls;
using namespace Tizen::Graphics;

MyToast::MyToast()
	: __pPanel(null)
	, __pLabel(null)
{
}

MyToast::~MyToast() {
	// TODO Auto-generated destructor stub
}

bool
MyToast::Initialize(void) {
	Construct(FORM_STYLE_NORMAL);
	return true;
}

result
MyToast::OnInitializing(void) {
	result r = E_SUCCESS;

	__pPanel = new Panel();
	__pPanel->Construct(Rectangle(100, 200, 300, 300));
	__pPanel->SetBackgroundColor(Color(0x50, 0xFF, 0x38));


	__pLabel = new Label();
	__pLabel->SetText(L"Toasting");

	__pPanel->AddControl(__pLabel);

	AddControl(__pPanel);

	return r;
}

