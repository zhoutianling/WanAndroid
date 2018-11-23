package com.example.hy.wanandroid.presenter.homepager;

import com.example.hy.wanandroid.base.presenter.BasePresenter;
import com.example.hy.wanandroid.contract.homepager.ArticleContract;
import com.example.hy.wanandroid.core.network.entity.BaseResponse;
import com.example.hy.wanandroid.core.network.entity.DefaultObserver;
import com.example.hy.wanandroid.core.network.entity.mine.Collection;
import com.example.hy.wanandroid.model.homepager.ArticleModel;
import com.example.hy.wanandroid.model.homepager.HomeModel;
import com.example.hy.wanandroid.utils.RxUtils;

import javax.inject.Inject;

/**
 * 文章详情的Presenter
 * Created by 陈健宇 at 2018/11/8
 */
public class ArticlePresenter extends BasePresenter<ArticleContract.View> implements ArticleContract.Presenter {

    private ArticleContract.Model mModel;

    @Inject
    public ArticlePresenter(ArticleModel articleModel) {
        mModel = articleModel;
    }

    @Override
    public void collectArticle(int id) {
        addSubcriber(
                mModel.getCollectRequest(id)
                        .compose(RxUtils.switchSchedulers())
                        .subscribeWith(new DefaultObserver<BaseResponse<Collection>>(mView, false, false){
                            @Override
                            public void onNext(BaseResponse<Collection> baseResponse) {
                                super.onNext(baseResponse);
                                mView.collectArticleSuccess();
                            }
                        })
        );
    }

    @Override
    public void unCollectArticle(int id) {
        addSubcriber(
                mModel.getUnCollectRequest(id)
                        .compose(RxUtils.switchSchedulers())
                        .subscribeWith(new DefaultObserver<BaseResponse<Collection>>(mView, false, false){
                            @Override
                            public void onNext(BaseResponse<Collection> baseResponse) {
                                super.onNext(baseResponse);
                                mView.unCollectArticleSuccess();
                            }
                        })
        );
    }
}
