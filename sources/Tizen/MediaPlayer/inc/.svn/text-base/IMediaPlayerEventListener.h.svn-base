/*
 * IMediaPlayerEventListener.h
 *
 *  Created on: Oct 7, 2013
 *      Author: sebastien
 */

#ifndef IMEDIAPLAYEREVENTLISTENER_H_
#define IMEDIAPLAYEREVENTLISTENER_H_

#include "tizenx.h"

using namespace Tizen::Base;
using namespace Tizen::Media;

class IMediaPlayerEventListener: public Object
{
	public:

		virtual ~IMediaPlayerEventListener();

		virtual void OnPlayerStateChanged(	PlayerState state,
											result res)=0;
		virtual void OnLoadingMedia(const int percent)=0;
		virtual void OnPositionChanged(const long position)=0;
		virtual void OnMediaLoaded(void)=0;
		virtual void OnVolumeChanged(const int volume)=0;
};

#endif /* IMEDIAPLAYEREVENTLISTENER_H_ */
