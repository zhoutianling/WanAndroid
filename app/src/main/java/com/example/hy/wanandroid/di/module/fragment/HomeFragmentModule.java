package com.example.hy.wanandroid.di.module.fragment;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.adapter.ArticlesAdapter;
import com.example.hy.wanandroid.config.App;
import com.example.hy.wanandroid.di.scope.PerFragment;
import com.example.hy.wanandroid.model.network.entity.Article;
import com.example.hy.wanandroid.widget.popup.PressPopup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;

/**
 * HomeFragment的Module
 * Created by 陈健宇 at 2018/10/26
 */
@Module
public class HomeFragmentModule {

    @Provides
    @PerFragment
    @Named("bannerTitles")
    List<String> provideBannerTitles(){
        return new ArrayList<>();
    }

    @Provides
    @PerFragment
    @Named("bannerImages")
    List<String> provideBannerImages(){
        return new ArrayList<>();
    }


    @Provides
    @PerFragment
    @Named("bannerAddress")
    List<String> provideBannerAddress(){
        return new ArrayList<>();
    }

    @Provides
    @PerFragment
    LinearLayoutManager provideLinearLayoutManager(){
        return new LinearLayoutManager(App.getContext());
    }


    @Provides
    @PerFragment
    List<Article> provideArticles(){
        return new ArrayList<>();
    }

    @Provides
    @PerFragment
    ArticlesAdapter provideArticlesAdapter(List<Article> articles){
        return new ArticlesAdapter(R.layout.item_acticles, articles);
    }
}
