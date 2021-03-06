package com.example.gsyvideoplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.gsyvideoplayer.R;
import com.example.gsyvideoplayer.model.VideoModel;
import com.example.gsyvideoplayer.video.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuyu on 2016/11/12.
 */

public class ListNormalAdapter extends BaseAdapter {

    public static final String TAG = "ListNormalAdapter";

    private List<VideoModel> list = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    private StandardGSYVideoPlayer curPlayer;
    private StandardGSYVideoPlayer itemPlayer;

    protected OrientationUtils orientationUtils;

    protected boolean isPlay;

    protected boolean isFull;

    public ListNormalAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        for (int i = 0; i < 40; i++) {
            list.add(new VideoModel());
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_video_item_normal, null);
            holder.gsyVideoPlayer = (SampleCoverVideo) convertView.findViewById(R.id.video_item_player);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //final String url = "https://res.exexm.com/cw_145225549855002";
        final String urlH = "https://test-haichi.oss-cn-hangzhou.aliyuncs.com/goods.core.base/VIDEO/4007067452261301700161221090_VIDEO?version=0";
        final String urlV = "http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4";
        final String url = (position % 2 == 0) ? urlH : urlV;
        //final String url = "http://111.198.24.133:83/yyy_login_server/pic/YB059284/97778276040859/1.mp4";


        if (position % 2 == 0) {
            holder.gsyVideoPlayer.loadCoverImage(url, R.mipmap.xxx1);
        } else {
            holder.gsyVideoPlayer.loadCoverImage(url, R.mipmap.xxx2);
        }

        //???????????????????????????
        //holder.gsyVideoPlayer.initUIState();

        //??????????????????
        //??????lazy???set????????????????????????????????????
        holder.gsyVideoPlayer.setUpLazy(url, true, null, null, "??????title");

        //holder.gsyVideoPlayer.setNeedShowWifiTip(false);

        /************************?????????????????????************************************/
        //?????????????????????????????????????????????
        //holder.gsyVideoPlayer.setUp(url, true, new File(FileUtils.getTestPath()), "??????title");

        /************************?????????????????????************************************/
        //?????????????????????????????????????????????
        //int playPosition = GSYVideoManager.instance().getPlayPosition();
        //???????????????????????????????????????
        /*if (playPosition < 0 || playPosition != position ||
                !GSYVideoManager.instance().getPlayTag().equals(ListNormalAdapter.TAG)) {
            holder.gsyVideoPlayer.initUIState();
        }*/
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????
        /*holder.gsyVideoPlayer.setThumbPlay(true);

        holder.gsyVideoPlayer.getStartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????
                holder.gsyVideoPlayer.setUp(url, true, new File(FileUtils.getTestPath(), ""));
                holder.gsyVideoPlayer.startPlayLogic();
            }
        });

        holder.gsyVideoPlayer.getThumbImageViewLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????
                holder.gsyVideoPlayer.setUp(url, true, new File(FileUtils.getTestPath(), ""));
                holder.gsyVideoPlayer.startPlayLogic();
            }
        });*/

        //??????title
        holder.gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

        //???????????????
        holder.gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

        //????????????????????????
        holder.gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(holder.gsyVideoPlayer);
            }
        });
        holder.gsyVideoPlayer.setRotateViewAuto(!getListNeedAutoLand());
        holder.gsyVideoPlayer.setLockLand(!getListNeedAutoLand());
        holder.gsyVideoPlayer.setPlayTag(TAG);
        //holder.gsyVideoPlayer.c(true);
        holder.gsyVideoPlayer.setReleaseWhenLossAudio(false);
        holder.gsyVideoPlayer.setAutoFullWithSize(true);
        holder.gsyVideoPlayer.setShowFullAnimation(!getListNeedAutoLand());
        holder.gsyVideoPlayer.setIsTouchWiget(false);
        //??????
        //holder.gsyVideoPlayer.setLooping(true);
        holder.gsyVideoPlayer.setNeedLockFull(true);

        //holder.gsyVideoPlayer.setSpeed(2);

        holder.gsyVideoPlayer.setPlayPosition(position);

        holder.gsyVideoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onClickStartIcon(String url, Object... objects) {
                super.onClickStartIcon(url, objects);
            }

            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                Debuger.printfLog("onPrepared");
                boolean full = holder.gsyVideoPlayer.getCurrentPlayer().isIfCurrentIsFullscreen();
                if (!holder.gsyVideoPlayer.getCurrentPlayer().isIfCurrentIsFullscreen()) {
                    GSYVideoManager.instance().setNeedMute(true);
                }
                if (holder.gsyVideoPlayer.getCurrentPlayer().isIfCurrentIsFullscreen()) {
                   GSYVideoManager.instance().setLastListener(holder.gsyVideoPlayer);
                }
                curPlayer = (StandardGSYVideoPlayer) objects[1];
                itemPlayer = holder.gsyVideoPlayer;
                isPlay = true;
                if (getListNeedAutoLand()) {
                    //?????????????????????
                    initOrientationUtils(holder.gsyVideoPlayer, full);
                    ListNormalAdapter.this.onPrepared();
                }
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                isFull = false;
                GSYVideoManager.instance().setNeedMute(true);
                if (getListNeedAutoLand()) {
                    ListNormalAdapter.this.onQuitFullscreen();
                }
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                GSYVideoManager.instance().setNeedMute(false);
                isFull = true;
                holder.gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                curPlayer = null;
                itemPlayer = null;
                isPlay = false;
                isFull = false;
                if (getListNeedAutoLand()) {
                    ListNormalAdapter.this.onAutoComplete();
                }
            }
        });

        return convertView;
    }

    /**
     * ?????????????????????
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        if (getListNeedAutoLand() && orientationUtils != null) {
            resolveFull();
        }
        standardGSYVideoPlayer.startWindowFullscreen(context, false, true);
    }

    class ViewHolder {
        SampleCoverVideo gsyVideoPlayer;
    }

    public void clearCache() {
        if (curPlayer != null) {
            curPlayer.getCurrentPlayer().clearCurrentCache();
        }
    }

    public boolean isFull() {
        return isFull;
    }

    /**************************?????????????????????????????????**************************/

    /**
     * ???????????????????????????????????????
     *
     * @return ??????true???????????????????????????
     */
    public boolean getListNeedAutoLand() {
        return true;
    }

    private void initOrientationUtils(StandardGSYVideoPlayer standardGSYVideoPlayer, boolean full) {
        orientationUtils = new OrientationUtils((Activity) context, standardGSYVideoPlayer);
        //????????????????????????????????????
        //orientationUtils.setRotateWithSystem(false);
        orientationUtils.setEnable(false);
        orientationUtils.setIsLand((full) ? 1 : 0);
    }

    private void resolveFull() {
        if (getListNeedAutoLand() && orientationUtils != null) {
            //????????????
            orientationUtils.resolveByClick();
        }
    }

    private void onQuitFullscreen() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    public void onAutoComplete() {
        if (orientationUtils != null) {
            orientationUtils.setEnable(false);
            orientationUtils.releaseListener();
            orientationUtils = null;
        }
        isPlay = false;
    }

    public void onPrepared() {
        if (orientationUtils == null) {
            return;
        }
        //????????????????????????????????????
        orientationUtils.setEnable(true);
    }

    /**
     * orientationUtils ???  detailPlayer.onConfigurationChanged ????????????????????????????????????
     */
    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        //????????????????????????
        if (isPlay && itemPlayer != null && orientationUtils != null) {
            itemPlayer.onConfigurationChanged(activity, newConfig, orientationUtils, false, true);
        }
    }

    public OrientationUtils getOrientationUtils() {
        return orientationUtils;
    }


    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    public void onDestroy() {
        if (isPlay && curPlayer != null) {
            curPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
            orientationUtils = null;
        }
    }



}
