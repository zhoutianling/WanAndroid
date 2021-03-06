package com.example.hy.wanandroid.contract.homepager;

import com.example.hy.wanandroid.base.presenter.IPresenter;
import com.example.hy.wanandroid.base.view.BaseView;
import com.example.hy.wanandroid.model.network.entity.BaseResponse;
import com.example.hy.wanandroid.model.network.entity.Article;
import com.example.hy.wanandroid.model.network.entity.Articles;
import com.example.hy.wanandroid.model.network.entity.BannerData;
import com.example.hy.wanandroid.model.network.entity.Collection;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 陈健宇 at 2018/10/23
 */
public interface HomeContract {

    interface View extends BaseView {
        void showBannerDatas(List<BannerData> bannerDataList);//展示banner数据
        void showArticles(List<Article> articleList);//展示首页文章数据
        void showMoreArticles(List<Article> articleList);//加载更多文章数据
        void topping();//置顶
        void collectArticleSuccess();//收藏文章成功
        void unCollectArticleSuccess();//取消收藏成功
        void refreshCollections(List<Integer> ids);//刷新文章列表中的收藏
        void collect();//收藏
        void autoRefresh();//自动刷新
    }

    interface Presenter extends IPresenter<HomeContract.View> {
        void loadBannerDatas();//加载首页banner数据
        void loadArticles(int pageNum);//加载首页文章数据
        void loadMoreArticles(int pageNum);//加载更多首页文章数据
        void collectArticle(int id);//收藏文章
        void unCollectArticle(int id);//取消收藏
    }

}
