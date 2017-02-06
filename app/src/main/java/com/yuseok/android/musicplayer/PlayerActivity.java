package com.yuseok.android.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton btnRew, btnPlay, btnFf;

    ArrayList<Music> datas;
    PlayerAdapter adapter;

    MediaPlayer player;
    SeekBar seekBar;
    TextView txtDuration,txtCurrent;

    // 플레이어 상태 플래그
    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;

    // 현재 플레이어 상태
    private static int playStatus = STOP;

    // 현재 음원의 index
    int position = 0; // 현재 음악 위치
   /*
    // 핸들러 상태 플래그
    public static final int PROGRESS_SET = 101;


    // 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case PROGRESS_SET:
                    if(player != null) {
                        seekBar.setProgress(player.getCurrentPosition());
                        txtCurrent.setText(player.getCurrentPosition()/1000 + "");
                    }
                    break;
            }
        }
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playStatus = STOP;

        // 볼륨 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        // seekBar의 변경사항을 체크하는 리스너 등록
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtCurrent = (TextView) findViewById(R.id.txtCurrent);

        btnRew = (ImageButton) findViewById(R.id.btnRew);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnFf = (ImageButton) findViewById(R.id.btnFf);

        btnRew.setOnClickListener(clickListener);
        btnPlay.setOnClickListener(clickListener);
        btnFf.setOnClickListener(clickListener);

        // 0. 데이터 가져오기
        datas = DataLoader.get(this);

        // 1. 뷰페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 2. 뷰페이저용 아답터 생성
        adapter = new PlayerAdapter(datas ,this);
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter( adapter );
        // 4. 뷰페이저 리스너 연결
        viewPager.addOnPageChangeListener(viewPagerListener);

        // 5. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");

            // 실제 페이지 값 계산 처리...
            // 페이지 이동
            // viewPager.setCurrentItem(position);
            // 음원 길이 같은 음악 기본정보를 설정해 준다.
            // 첫 페이지일 경우만 init호출
            // 이유 :  첫 페이지가 아닐 겨우 위의 setCurrentItem에 의해서 ViewPager의 inPageSelected가 호출된다.
            if (position == 0) {
                init();
            } else {
                viewPager.setCurrentItem(position);
            }
        }
    }

    private void init() {
        // 뷰페이저로 이동할 경우 플레이어에 세팅된 값을 해제한 후 로직을 실행한다.
        if(player != null) {
            // 플레이 상태를 STOP으로 변경
            playStatus = STOP;
            // 아이콘을 플레이 버튼으로 변경
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            player.release();
        }

        Uri musicUri = datas.get(position).uri;
        // 플레이어에 음원 세팅
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false); // 반복여부

        // seekBar 길이
        seekBar.setMax(player.getDuration());
        // seelBar 현재값  0으로
        seekBar.setProgress(0);
        // 전체 플레이시간 설정
        txtDuration.setText(convertMiliToTime(player.getDuration()) + "");
        // 현재 실행시간을 0으로 설정
        txtCurrent.setText("0");

        // 미디어 플레이어에 완료체크 리스너를 등록한다.
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
        play();
    }

    private void play() {
        // 플레이중이 아니면 음악 실행
        switch(playStatus) {
            case STOP:

                player.start();

                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                /*
                // sub thread 를 생성해서 mediaplayer 의 현재 포지션 값으로 seekbar 를 변경해준다. 매 1초마다
                new Thread() {
                    @Override
                    public void run() {
                        while (playStatus < STOP) {
                            handler.sendEmptyMessage(PROGRESS_SET);
                            try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        }
                    }
                }.start();
*/
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (playStatus < STOP) {
                            if(player != null) {

                                // 이 부분은 메인쓰레드에서 동작하도록 Runnable 객체를 메인쓰레드에 던져준다
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            seekBar.setProgress(player.getCurrentPosition());
                                            txtCurrent.setText(convertMiliToTime(player.getCurrentPosition()) + "");
                                        } catch (Exception e) { e.printStackTrace();}
                                    }
                                });
                            }
                            try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        }
                    }
                };
                // 새로운 쓰레드로 스타트
                thread.start();

                break;
            // 플레이중이면 멈춤
            case PLAY :
                player.pause();
                playStatus = PAUSE;
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                break;
            // 멈춤상태이면 거기서 부터 재생
            case PAUSE:
                //player.seekTo(player.getCurrentPosition());
                player.start();

                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                break;
        }
    }

    private String convertMiliToTime(long mili) {

        long min = mili / 1000 / 60;
        long sec = mili / 1000 % 60;


        return String.format("%02d",min) + ":" + String.format("%02d",sec);
    }
    private void prev() {
        if(position > 0)
            viewPager.setCurrentItem(position - 1);

    }

    private void next() {
        if(position < datas.size())
            viewPager.setCurrentItem(position + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player != null){
            player.release(); // 사용이 끝나면 해제해야만 한다.
        }
        playStatus = STOP;
    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnPlay:
                    play();
                    break;
                case R.id.btnRew:
                    prev();
                    break;
                case R.id.btnFf:
                    next();
                    break;
            }
        }
    };


    // 뷰페이저의 체인지 리스너
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Logger.print("=========정보들어옴", "viewPager Listener");
            PlayerActivity.this.position = position;
            init();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (player != null && fromUser) {
                player.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
