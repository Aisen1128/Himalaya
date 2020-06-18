package com.example.himalaya.interfaces;

//逻辑层和UI层分开
public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();

    /**
     * 用于注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消UI的回调
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);

}
