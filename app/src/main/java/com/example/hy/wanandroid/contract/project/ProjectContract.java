package com.example.hy.wanandroid.contract.project;

import com.example.hy.wanandroid.base.presenter.IPresenter;
import com.example.hy.wanandroid.base.view.BaseView;
import com.example.hy.wanandroid.model.network.entity.BaseResponse;
import com.example.hy.wanandroid.model.network.entity.Tab;

import java.util.List;

import io.reactivex.Observable;

/**
 * 项目的Contract
 * Created by 陈健宇 at 2018/10/23
 */
public interface ProjectContract {

    interface View extends BaseView {
        void showProjectList(List<Tab> projectList);
    }

    interface Presenter extends IPresenter<ProjectContract.View> {
        void loadProjectList();
    }

}
