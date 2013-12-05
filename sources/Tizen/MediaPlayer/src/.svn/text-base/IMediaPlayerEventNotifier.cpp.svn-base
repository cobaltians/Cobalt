/*
 * IMediaPlayerEventNotifier.cpp
 *
 *  Created on: Oct 8, 2013
 *      Author: sebastien
 */

#include "MediaPlayer/IMediaPlayerEventNotifier.h"

IMediaPlayerEventNotifier::~IMediaPlayerEventNotifier()
{
	// TODO Auto-generated destructor stub
}

void IMediaPlayerEventNotifier::PlayerStateChanged(PlayerState state, result res)
{
	for (set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.begin(), end = mListeners.end(); listenersIterator != end; listenersIterator++)
	{
		(*listenersIterator)->OnPlayerStateChanged(state, res);
	}
}

void IMediaPlayerEventNotifier::LoadingMedia(const int percent)
{
	for (set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.begin(), end = mListeners.end(); listenersIterator != end; listenersIterator++)
	{
		(*listenersIterator)->OnLoadingMedia(percent);
	}
}

void IMediaPlayerEventNotifier::MediaLoaded(void)
{
	for (set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.begin(), end = mListeners.end(); listenersIterator != end; listenersIterator++)
	{
		(*listenersIterator)->OnMediaLoaded();
	}
}

void IMediaPlayerEventNotifier::PositionChanged(const long position)
{
	for (set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.begin(), end = mListeners.end(); listenersIterator != end; listenersIterator++)
	{
		(*listenersIterator)->OnPositionChanged(position);
	}
}

void IMediaPlayerEventNotifier::VolumeChanged(const int volume, IMediaPlayerEventListener *listener)
{
	/*
	for (set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.begin(), end = mListeners.end(); listenersIterator != end; listenersIterator++)
	{
		if (DEBUG) AppLogDebugTag("MEDIA PLAYER", "IMediaPlayerEventNotifier - VolumeChanged: notify listener?");
		if (listener != null &&
			! (*listenersIterator)->Equals(*listener))
		{
			if (DEBUG) AppLogDebugTag("MEDIA PLAYER", "IMediaPlayerEventNotifier - VolumeChanged: yes");
			(*listenersIterator)->OnVolumeChanged(volume);
		}
	}
	*/
}

void IMediaPlayerEventNotifier::RegisterListener(IMediaPlayerEventListener *listener)
{
	mListeners.insert(listener);
}

void IMediaPlayerEventNotifier::UnregisterListener(IMediaPlayerEventListener *listener)
{
	set<IMediaPlayerEventListener *>::const_iterator listenersIterator = mListeners.find(listener);
	if (listenersIterator != mListeners.end())
	{
		mListeners.erase(listenersIterator);
	}
	else
	{
		if (DEBUG) AppLogException("Could not unregister the specified listener object as it is not registered.");
	}
}
