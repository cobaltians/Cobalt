/*
 * IMediaPlayerEventNotifier.h
 *
 *  Created on: Oct 8, 2013
 *      Author: sebastien
 */

#ifndef IMEDIAPLAYEREVENTNOTIFIER_H_
#define IMEDIAPLAYEREVENTNOTIFIER_H_

#include <set>

#include "IMediaPlayerEventListener.h"
#include "tizenx.h"

using namespace Tizen::Media;

using namespace std;

class IMediaPlayerEventNotifier
{
	protected:

		virtual void PlayerStateChanged(PlayerState state,
										result res = E_SUCCESS);
		virtual void LoadingMedia(const int percent);
		virtual void MediaLoaded(void);
		virtual void PositionChanged(const long position);
		virtual void VolumeChanged(	const int volume,
									IMediaPlayerEventListener *listener = null);

	public:

		virtual ~IMediaPlayerEventNotifier();

		virtual void RegisterListener(IMediaPlayerEventListener *listener);
		virtual void UnregisterListener(IMediaPlayerEventListener *listener);

	private:

		set<IMediaPlayerEventListener*> mListeners;
};

#endif /* IMEDIAPLAYEREVENTNOTIFIER_H_ */
