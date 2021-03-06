package com.example.hy.wanandroid.contract.mine;

import com.example.hy.wanandroid.base.presenter.IPresenter;
import com.example.hy.wanandroid.base.view.BaseView;

/**
 * 设置的Contract
 * Created by 陈健宇 at 2018/11/26
 */
public interface SettingsContract {

    interface View extends BaseView {
        void showUpdataDialog(String content);//显示更新弹窗
        void setNewVersionName(String versionName);//设置最新版本号
        void showAlareadNewToast(String content);//已经是最新版本
        void clearCache();//清空缓存
        void upDataVersion();//更新
        void showChangeAnimation();//显示一个夜间模式切换动画
    }

    interface Presenter extends IPresenter<View>{
        void setNoImageState(boolean isNight);
        void setAutoCacheState(boolean isNight);
        void setNightModeState(boolean isNight);
        void setStatusBarState(boolean isStatusBar);
        void checkVersion(String currentVersion);
    }
}
