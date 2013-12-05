/*
 * MediaPlayer.cpp
 *
 *  Created on: Oct 7, 2013
 *      Author: sebastien
 */

#include "MediaPlayer/MediaPlayer.h"

using namespace Tizen::Base::Collection;
using namespace Tizen::Base::Utility;

int MediaPlayer::oVolume = 7;
int MediaPlayer::VOLUME_MAX = 15;
bool MediaPlayer::oReset = false;
bool MediaPlayer::oKeepInBackground = false;
bool MediaPlayer::oWasPlayingWhenSeekToOccured = false;
bool MediaPlayer::oWasPlayingWhenInterruptOccured = false;

Timer *MediaPlayer::oTimer = null;

MediaPlayer::MediaPlayer(bool keepInBackground) {
	// TODO Auto-generated constructor stub
	result res = E_SUCCESS;

	oKeepInBackground = keepInBackground;

	if ((oKeepInBackground && oMediaPlayer == null)
		|| ! oKeepInBackground) {
		oMediaPlayer.reset(new (nothrow) Player());
		res = oMediaPlayer->Construct(*this);
		TryCatch(! IsFailed(res), , null);

		oTimer = new Timer();
		res = oTimer->Construct(*this);
		TryCatch(! IsFailed(res), , null);
	}
	res = SettingInfo::GetValue(L"http://tizen.org/setting/sound.media.volume", oVolume);
	TryCatch(! IsFailed(res), , null);

	res = SystemInfo::GetValue(L"http://tizen.org/system/sound.media.volume.resolution.max", VOLUME_MAX);
	TryCatch(! IsFailed(res), , null);

	res = SettingInfo::AddSettingEventListener(*this);
	TryCatch(! IsFailed(res), , null);

	CATCH:
		SetLastResult(res);
}

MediaPlayer::MediaPlayer(BufferInfo *bufferInfo, bool keepInBackground) {
	// TODO Auto-generated constructor stub
	result res = E_SUCCESS;

	oKeepInBackground = keepInBackground;

	if ((oKeepInBackground && oMediaPlayer == null)
		|| ! oKeepInBackground) {
		oMediaPlayer.reset(new (nothrow) Player());
		res = oMediaPlayer->Construct(*this, bufferInfo);
		TryCatch(! IsFailed(res), , null);

		oTimer = new Timer();
		res = oTimer->Construct(*this);
		TryCatch(! IsFailed(res), , null);
	}
	res = SettingInfo::GetValue(L"http://tizen.org/setting/sound.media.volume", oVolume);
	TryCatch(! IsFailed(res), , null);

	res = SystemInfo::GetValue(L"http://tizen.org/system/sound.media.volume.resolution.max", VOLUME_MAX);
	TryCatch(! IsFailed(res), , null);

	res = SettingInfo::AddSettingEventListener(*this);
	TryCatch(! IsFailed(res), , null);

	CATCH:
		SetLastResult(res);
}

MediaPlayer::~MediaPlayer() {
	// TODO Auto-generated destructor stub
	result res = E_SUCCESS;

	if (! oKeepInBackground) {
		oTimer->Cancel();
		delete oTimer;

		PlayerState state = GetState();
		if (state == PLAYER_STATE_OPENED
			|| state == PLAYER_STATE_PAUSED
			|| state == PLAYER_STATE_PLAYING
			|| state == PLAYER_STATE_ENDOFCLIP
			|| state == PLAYER_STATE_STOPPED)
		{
			res = oMediaPlayer->Close();
			TryCatch(! IsFailed(res), , null);
			PlayerStateChanged(PLAYER_STATE_CLOSED);
			oMediaPlayer.reset(new (nothrow) Player());
		}

		res = SettingInfo::RemoveSettingEventListener(*this);
		TryCatch(! IsFailed(res), , null);
	}

	CATCH:
		SetLastResult(res);
}

result MediaPlayer::Prepare(String mediaUrl)
{
	 result res = E_SUCCESS;

	 Uri mediaUri;
	 mediaUri.SetUri(mediaUrl);
	 // true: async, sync otherwise
	 res = oMediaPlayer->OpenUrl(mediaUri,
			 	 	 	 	 	 true);
	 return res;
}

result MediaPlayer::Play()
{
	 result res = E_SUCCESS;

	 PlayerState state = GetState();
	 if (state == PLAYER_STATE_PAUSED
		 || state == PLAYER_STATE_OPENED
		 || state == PLAYER_STATE_ENDOFCLIP
		 || state == PLAYER_STATE_STOPPED)
	 {
		 res = oMediaPlayer->Play();
		 TryReturn(! IsFailed(res), res, null);
		 PlayerStateChanged(PLAYER_STATE_PLAYING);

		 res = oTimer->StartAsRepeatable(1000);
		 if (IsFailed(res))
		 {
			 oTimer->Cancel();
			 res = oTimer->StartAsRepeatable(1000);
			 TryReturn(! IsFailed(res), res, null);
		 }

		 return E_SUCCESS;
	 }
	 else
	 {
		 return E_INVALID_STATE;
	 }
}

result MediaPlayer::Pause(void)
{
	result res = E_SUCCESS;

	if (GetState() == PLAYER_STATE_PLAYING)
	{
		res = oMediaPlayer->Pause();
		TryReturn(! IsFailed(res), res, null);
		PlayerStateChanged(PLAYER_STATE_PAUSED);

		res = oTimer->Cancel();
		TryReturn(! IsFailed(res), res, null);

		return E_SUCCESS;
	}
	else
	{
		return E_INVALID_STATE;
	}
}

result MediaPlayer::Stop(void)
{
	result res = E_SUCCESS;

	PlayerState state = GetState();
	if (state == PLAYER_STATE_PLAYING
		|| state == PLAYER_STATE_PAUSED)
	{
		res = oMediaPlayer->Stop();
		TryReturn(! IsFailed(res), res, null);
		PlayerStateChanged(PLAYER_STATE_STOPPED);

		res = oTimer->Cancel();
		TryReturn(! IsFailed(res), res, null);

		return E_SUCCESS;
	}
	else
	{
		return E_INVALID_STATE;
	}
}

result MediaPlayer::SeekTo(long position)
{
	result res = E_SUCCESS;

	PlayerState state = GetState();
	if (state == PLAYER_STATE_OPENED
		|| state == PLAYER_STATE_PLAYING
		|| state == PLAYER_STATE_PAUSED)
	{
		if (state == PLAYER_STATE_PLAYING)
		{
			res = oMediaPlayer->Pause();
			TryReturn(! IsFailed(res), res, null);
			PlayerStateChanged(PLAYER_STATE_PAUSED);

			oWasPlayingWhenSeekToOccured = true;

			res = oTimer->Cancel();
			TryReturn(! IsFailed(res), res, null);
		}

		res = oMediaPlayer->SeekTo(position);
		TryReturn(! IsFailed(res), res, null);

		return E_SUCCESS;
	}
	else
	{
		return E_INVALID_STATE;
	}
}

int MediaPlayer::GetVolume(void)
{
	return oVolume;
}

int MediaPlayer::GetMaxVolume(void)
{
	return VOLUME_MAX;
}

result MediaPlayer::SetVolume(IMediaPlayerEventListener* listener = null, int volume = oVolume)
{
	result res = E_SUCCESS;

	oVolume = volume;
	res = SettingInfo::SetValue(L"http://tizen.org/setting/sound.media.volume", oVolume);
	TryReturn(! IsFailed(res), res, null);

	VolumeChanged(oVolume, listener);

	return E_SUCCESS;
}

long MediaPlayer::GetPosition(void)
{
	result res = E_SUCCESS;
	long position = oMediaPlayer->GetPosition();
	res = GetLastResult();

	if (IsFailed(res))
	{
		position = 0;
	}

	return position;
}

long MediaPlayer::GetDuration(void)
{
	result res = E_SUCCESS;

	long duration = 0;

	PlayerState state = GetState();
	if (state == PLAYER_STATE_OPENED
		|| state == PLAYER_STATE_PLAYING
		|| state == PLAYER_STATE_PAUSED
		|| state == PLAYER_STATE_STOPPED)
	{
		duration = oMediaPlayer->GetDuration();
		res = GetLastResult();

		if (IsFailed(res))
		{
			duration = 0;
		}
	}

	return duration;
}

PlayerState MediaPlayer::GetState(void)
{
	return oMediaPlayer->GetState();
}

Dimension MediaPlayer::GetVideoDimensions()
{
	result res = E_SUCCESS;
	MediaStreamInfo *mediaStreamInfos = null;
	const IList *videoStreamInfoList;
	int videoStreamInfoListCount = 0;

	mediaStreamInfos = oMediaPlayer->GetCurrentMediaStreamInfoN();
	res = GetLastResult();
	TryCatch(! IsFailed(res), , null);

	videoStreamInfoList = mediaStreamInfos->GetVideoStreamInfoList();
	res = GetLastResult();
	TryCatch(! IsFailed(res), , null);

	videoStreamInfoListCount = videoStreamInfoList->GetCount();
	if (videoStreamInfoListCount > 0)
	{
		VideoStreamInfo *videoStreamInfo = (VideoStreamInfo*) videoStreamInfoList->GetAt(0);
		return Dimension(	videoStreamInfo->GetWidth(),
							videoStreamInfo->GetHeight());
	}
	else
	{
		return Dimension(0, 0);
	}

	CATCH:
		SetLastResult(res);
		return Dimension(0, 0);
}

void MediaPlayer::OnSettingChanged(String &key)
{
	result res = E_SUCCESS;

	if (key.Equals(L"http://tizen.org/setting/sound.media.volume", true))
	{
		res = SettingInfo::GetValue(L"http://tizen.org/setting/sound.media.volume", oVolume);
		TryCatch(! IsFailed(res), , null);
		VolumeChanged(oVolume);
	}

	CATCH:
		SetLastResult(res);
}

void MediaPlayer::OnTimerExpired(Timer &timer)
{
	result res = E_SUCCESS;

	if (oReset)
	{
		oReset = false;

		res = oMediaPlayer->SetLooping(false);
		TryCatch(! IsFailed(res), , null);
	}

	PositionChanged(GetPosition());

	CATCH:
		SetLastResult(res);
}

void MediaPlayer::OnPlayerAudioFocusChanged(void)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerAudioFocusChanged");

	PlayerStateChanged(GetState());
}

void MediaPlayer::OnPlayerOpened(result res)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerOpened");
	PlayerStateChanged(PLAYER_STATE_OPENED);
	Play();
}

void MediaPlayer::OnPlayerBuffering(int percent)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerBuffering: %i%%", percent);

	if (percent >= 100)
	{
		MediaLoaded();
	}
	else
	{
		LoadingMedia(percent);
	}
}

void MediaPlayer::OnPlayerEndOfClip(void)
{
	result res = E_SUCCESS;

	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerEndOfClip");

	res = oTimer->Cancel();
	TryCatch(! IsFailed(res), , null);

	res = oMediaPlayer->SetLooping(true);
	TryCatch(! IsFailed(res), , null);

	oReset = true;

	PlayerStateChanged(PLAYER_STATE_ENDOFCLIP);

	CATCH:
		SetLastResult(res);
}

void MediaPlayer::OnPlayerErrorOccurred(PlayerErrorReason res)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerErrorOccurred: %s", GetErrorMessage(res));
	PlayerStateChanged(PLAYER_STATE_ERROR, res);
}

void MediaPlayer::OnPlayerInterrupted(void)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerInterrupted");

	result res = Pause();
	if (! IsFailed(res))
	{
		oWasPlayingWhenInterruptOccured = true;
	}
}

void MediaPlayer::OnPlayerReleased(void)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerOpened");

	if (oWasPlayingWhenInterruptOccured
		&& GetState() == PLAYER_STATE_PAUSED)
	{
		Play();
	}

	oWasPlayingWhenInterruptOccured = false;
}

void MediaPlayer::OnPlayerSeekCompleted(result res)
{
	if (DEBUG) AppLog("MEDIA PLAYER - OnPlayerSeekCompleted");

	if (oWasPlayingWhenSeekToOccured
		&& GetState() == PLAYER_STATE_PAUSED)
	{
		Play();
	}

	oWasPlayingWhenSeekToOccured = false;
}

