package com.posin.functiontest.fragment;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.posin.functiontest.R;
import com.posin.functiontest.activity.MainActivity;
import com.posin.functiontest.global.AppConfig;
import com.posin.functiontest.impl.TabReselectedListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Greetty on 2017/8/27.
 * 喇叭
 */

public class FragmentSpeaker extends BaseFragment implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private static final String TAG = "FragmentSpeaker";

    @BindView(R.id.sb_speaker_size)
    SeekBar sbSpeakerSize;
    @BindView(R.id.ll_speaker)
    LinearLayout llSpeaker;
    Unbinder unbinder;

    private View view;
    private MediaPlayer player;
    private boolean isPlaying = false;
    private boolean isPause = false;
    private AssetFileDescriptor afd;
    private int mSpeakerPos = -1;


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_speaker, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        sbSpeakerSize.setOnSeekBarChangeListener(this);

        sbSpeakerSize.setOnTouchListener(this);

        MainActivity mActivity = (MainActivity) getActivity();
//        String[] mTitle = mContext.getResources().getStringArray(R.array.tab_title);
        String[] mTitle = AppConfig.getTitleItem(mContext);
        for (int i = 0; i < mTitle.length; i++) {
            if (mTitle[i].equals("喇叭") || mTitle[i].equals("Horn"))
                mSpeakerPos = i;
        }
        mActivity.setTabReselectedListener(new TabReselectedListener() {
            @Override
            public void TabReselectedChange(int position) {
                if (mSpeakerPos != -1) {
                    if (position != mSpeakerPos) {
                        if (player != null) {
                            player.stop();
                            player.release();
                            player = null;
                            isPlaying = false;
                            isPause = false;
                        }
                    }
                }

            }
        });
    }

    @OnClick(R.id.btn_speaker_start)
    public void startPlay() {
        if (player == null)
            player = new MediaPlayer();

        if (isPlaying)
            return;

        Log.d(TAG, "isPause: " + isPause);

        if (!isPause) {
            try {
                afd = mContext.getResources().openRawResourceFd(R.raw.test);
                player.reset();
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                        afd.getLength());
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        try {
                            player.setVolume(1f, 1f);
                            player.start();
                            isPlaying = true;
                            isPause = false;
                            afd.close();
                            afd = null;
                        } catch (IOException e) {
                            Log.e(TAG, "error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            player.start();
            isPlaying = true;
            isPause = true;
        }

    }

    @OnClick(R.id.btn_speaker_stop)
    public void stopPlay() {
        Log.d(TAG, "isPlaying: " + isPlaying);

        if (player != null) {
            if (isPlaying) {
                player.pause();
                isPause = true;
                isPlaying = false;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            isPause = false;
            isPlaying = false;
            if (afd != null) {
                try {
                    afd.close();
                    afd = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float mVolume = (float) (0.1 * progress);
        if (player != null)
            player.setVolume(mVolume, mVolume);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_HOVER_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return false;
    }
}
