package com.yuseok.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2/1/2017.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    List<Music> datas;
    Context context;
    Intent intent = null;

    // public으로 선언된 MusicAdapter에 datas와 context를 넣어준다.
    public MusicAdapter(Context context) {
        // 전역변수에 선언된 datas와 구분해주는 this.
        // ArrayList<Music> datas를 this.datas로 받아준다.
        this.datas = DataLoader.get(context);
        this.context = context;
        this.intent = new Intent(context,PlayerActivity.class);
    }

    // Holder는 한 화면에 보이는 List를 전환때마다 생성하는 방식이 아닌
    // 화면에서 사라지는 리스트를 새로 보이는 리스트로 옮겨 보여주는 방식
    // ex) 돌려막기, 카드깡(?)
    @Override
    // 재활용할 가상 뷰 생성
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }


    @Override
    // 사용한 뷰를 재활용하기위해 돌려주는 역할
    public void onBindViewHolder(Holder holder, int position) {
        // 재활용 될 뷰 안에 들어갈 정보들
        // holder로 리턴한 Holder형태의 정보를 받아오므로, holder를 붙여준다.
        Music music = datas.get(position);
        holder.txtTitle.setText(music.title);
        holder.txtArtist.setText(music.artist);
        holder.position = position;


        // holder.image.setImageURI(music.album_image);
        //
        Glide.with(context)              // 0. 글라이드 사용
                .load(music.album_image) // 1. 로드할 대상 Uri
                .into(holder.image);     // 2. 입력될 이미지뷰
    }

    // 데이터의 총 개수
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        // RecyclerView에 들어갈 위젯들 선언.
        CardView cardView;
        TextView txtTitle, txtArtist;
        ImageView image;
        int position;

        public Holder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
            image = (ImageView) itemView.findViewById(R.id.image);


            cardView.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        };
    }
}
