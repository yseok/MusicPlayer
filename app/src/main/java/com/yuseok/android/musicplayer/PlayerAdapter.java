package com.yuseok.android.musicplayer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2/2/2017.
 */

public class PlayerAdapter extends PagerAdapter{

    // ArrayList형태의 데이터 <Music>을 datas로 선언.
    List<Music> datas;
    // System상의 메소드를 사용하기 위해 Context를 context로 선언
    Context context;
    // xml을 메모리에 올리기 위한 LayoutInflater를 inflater로 선언
    LayoutInflater inflater;

    // PlayerAdapter함수에 datas와 context값을 넣어준다.
    public PlayerAdapter(List<Music> datas, Context context){
        // 전역변수에 선언된 datas와 구분주는 this.
        // ArrayList<Music> datas를 this.datas로 받아온다.
        this.datas = datas;
        this.context = context;
        // SystemService를 사용하기위한 inflater
        // 메소드에 context를 받아왔으므로 context상의 기능을 사용하기 위해 앞에 context. 를 받아온다.
        // LayoutInflater로 캐스팅.
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 데이터 총 개수
    @Override
    public int getCount() {
        return datas.size();
    }

    // listView 의 getView 와 같은 역할
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        // xml인 player_card_item을 View형식으로 나타내기 위함
        View view = inflater.inflate( R.layout.player_card_item , null);

        // xml에 있는 ImageView와 TextView를 호출한다.
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) view.findViewById(R.id.txtArtist);

        // 실제 음악 데이터 가져오기
        // datas의 위치 가져오기
        Music music = datas.get(position);

        // 위에 datas의 위치를 넣은 music에서 title의 Text정보를 txtTitle아이디값을 가진 TextView에 출력해준다
        txtTitle.setText(music.title);
        txtArtist.setText(music.artist);


        // Glide사용
        // 이미지를 출력해준다.
        // 구글에 glide검색해서 git에 dependers의 첫번째 복붙
        // compile 'com.github.bumptech.glide:glide:3.7.0'
        // build.gradle (Module: app)
        Glide.with(context)
                .load(music.album_image)
                // 이미지가 없을 경우 대체 이미지
                .placeholder(android.R.drawable.ic_menu_close_clear_cancel)
                .into(imageView);


        // 생성한 뷰를 컨테이너에 담아준다. 컨테이너 = 뷰페이저를 생성한 최외곽 레이아웃 개념
        container.addView(view);

        return view;
    }

    // 화면에서 사라진 뷰를 메모리에서 제거하기 위한 함수
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }

    // instantiateItem 에서 리턴된 Object 가 View 가 맞는지를 확인하는 함수
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
