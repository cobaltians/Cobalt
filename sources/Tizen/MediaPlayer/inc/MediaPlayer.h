/*
 * MediaPlayer.h
 *
 *  Created on: Oct 7, 2013
 *      Author: sebastien
 */

#ifndef MEDIAPLAYER_H_
#define MEDIAPLAYER_H_

#include "IMediaPlayerEventNotifier.h"
#include "tizenx.h"

using namespace Tizen::Base;
using namespace Tizen::Base::Runtime;
using namespace Tizen::Graphics;
using namespace Tizen::Media;
using namespace Tizen::System;

using namespace std;

class MediaPlayer: 	public Object,
					public IMediaPlayerEventNotifier,
					public IPlayerEventListener,
					public ITimerEventListener,
					public ISettingEventListener {

	public:

		MediaPlayer(bool keepInBackground = false);
		MediaPlayer(BufferInfo *bufferInfo,
					bool keepInBackground = false);
		virtual ~MediaPlayer();

		result Prepare(String mediaUrl);
		result Play(void);
		result Pause(void);
		result Stop(void);
		result SeekTo(long position);
		int GetVolume(void);
		int GetMaxVolume(void);
		result SetVolume(IMediaPlayerEventListener* listener, int volume);
		long GetDuration(void);
		long GetPosition(void);
		PlayerState GetState(void);
		Dimension GetVideoDimensions();

	// Listeners
	public:

		//Settings
		virtual void OnSettingChanged(String &key);

		//Timer
		virtual void OnTimerExpired(Timer &timer);

		//Player
		virtual void OnPlayerAudioFocusChanged(void);
		virtual void OnPlayerBuffering(int percent);
		virtual void OnPlayerEndOfClip(void);
		virtual void OnPlayerErrorOccurred(Tizen::Media::PlayerErrorReason res);
		virtual void OnPlayerInterrupted(void);
		virtual void OnPlayerOpened(result res);
		virtual void OnPlayerReleased(void);
		virtual void OnPlayerSeekCompleted(result res);

	protected:

		//static Player *oMediaPlayer;
		unique_ptr<Player> oMediaPlayer;
		static int oVolume;
		static int VOLUME_MAX;
		static bool oReset;

		static Timer *oTimer;
		static bool oKeepInBackground;
		static bool oWasPlayingWhenSeekToOccured;
		static bool oWasPlayingWhenInterruptOccured;
};

#endif /* MEDIAPLAYER_H_ */
