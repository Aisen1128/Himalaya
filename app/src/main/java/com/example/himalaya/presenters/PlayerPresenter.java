package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private List<IPlayerCallback> mPlayerCallbacks=new ArrayList<>();
    private static final String TAG = "PlayerPresenter";
    private static volatile PlayerPresenter sPlayerPresenter;
    private final XmPlayerManager mPlayerManager;
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModSp;

    public static final int PLAY_MODEL_LIST_INT=0;
    public static final int PLAY_MODEL_LIST_LOOP_INT=1;
    public static final int PLAY_MODEL_RANDOM_INT=2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT=3;

    public static final String PLAY_MODE_SP_NAME="playMod";
    public static final String PLAY_MODE_SP_KEY="currentPlayMode";

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告物料相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences("PlayMod", Context.MODE_PRIVATE);
    }

    public static PlayerPresenter getPlayerPresenter(){
        if(sPlayerPresenter ==null){
            synchronized (PlayerPresenter.class){
                if(sPlayerPresenter ==null){
                    sPlayerPresenter =new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet=false;
    public void setPlayList(List<Track> list, int playIndex){
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet=true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        }else{
            Log.d(TAG,"mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if(isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if(mPlayerManager!=null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);
            //通知UI更新
            for (IPlayerCallback playerCallback : mPlayerCallbacks) {
                playerCallback.onPlayModeChange(mode);
            }
            SharedPreferences.Editor editor = mPlayModSp.edit();
            editor.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            editor.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_SINGLE:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;

        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index){
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;

        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback playerCallback : mPlayerCallbacks) {
                playerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第index的位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {

    }

    @Override
    public void playByAlbumId(long id) {

    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        //从sp里面拿
        int modeIndex = mPlayModSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        iPlayerCallback.onPlayModeChange(getModeByInt(modeIndex));
        if(!mPlayerCallbacks.contains(iPlayerCallback)){
            mPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mPlayerCallbacks.remove(iPlayerCallback);
    }

    //=================广告相关的回调=============

    @Override
    public void onStartGetAdsInfo() {
        Log.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        Log.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        Log.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        Log.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        Log.d(TAG,"onError what -->"+what);
        Log.d(TAG,"onError extra -->"+extra);
    }

    //===============播放器状态相关的接口=============
    @Override
    public void onPlayStart() {
        Log.d(TAG,"onPlayStart");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG,"onPlayPause");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG,"onPlayStop");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayStop();
        }

    }

    @Override
    public void onSoundPlayComplete() {
        Log.d(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        Log.d(TAG,"onSoundPrepared");
        if(mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED){
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        Log.d(TAG,"onSoundSwitch");
        mCurrentIndex=mPlayerManager.getCurrentIndex();
        if(curModel instanceof Track){
            Track currentTrack= (Track) curModel;
            mCurrentTrack=currentTrack;
            //  LogUtil.d(TAG,"title --->"+currentTrack.getTrackTitle());
            //更新UI
            for (IPlayerCallback playerCallback : mPlayerCallbacks) {
                playerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        Log.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        Log.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int i) {
        Log.d(TAG,"onBufferProgress");
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.d(TAG,"onError");
        return false;
    }
}
