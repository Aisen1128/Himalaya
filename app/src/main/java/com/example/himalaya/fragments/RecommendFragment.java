package com.example.himalaya.fragments;

import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    public static final String TAG="RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container,false);

        //RecyclerView的使用
        //1、找到控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        //3、设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        //去拿数据回来
        getRecommendData();

        return mRootView;
    }

    private void getRecommendData() {
        //封装参数
        Map<String,String> map=new HashMap<>();
        //这个参数表示一页数据返回
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name -->"+Thread.currentThread().getName());
                //数据获取成功
                if(gussLikeAlbumList!=null){
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    //数据回来以后，去更新UI
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"error --> "+i);
                LogUtil.d(TAG,"errorMsg --> "+s);
            }
        });

    }

    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器，并更新UI
        mRecommendListAdapter.setData(albumList);
    }
}
