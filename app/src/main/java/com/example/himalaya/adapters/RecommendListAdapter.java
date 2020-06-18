package com.example.himalaya.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData=new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //这里设置数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if(mData!=null){
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList){
        if(mData!=null){
            mData.clear();
            mData.addAll(albumList);
        }
        //更新一个UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() + "");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");

            String coverUrlLarge = album.getCoverUrlLarge();
            Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
        }
    }
}
