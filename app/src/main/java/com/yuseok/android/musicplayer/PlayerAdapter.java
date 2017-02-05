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

/**
 * Created by pc on 2/2/2017.
 */

public class PlayerAdapter extends PagerAdapter{

    ArrayList<Music> datas;
    Context context;
    LayoutInflater inflater;

    public PlayerAdapter(ArrayList<Music> datas, Context context){
        this.datas = datas;
        this.context = context;
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
        View view = inflater.inflate( R.layout.player_card_item , null);

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) view.findViewById(R.id.txtArtist);

        // 실제 음악 데이터 가져오기
        Music music = datas.get(position);

        txtTitle.setText(music.title);
        txtArtist.setText(music.artist);


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
