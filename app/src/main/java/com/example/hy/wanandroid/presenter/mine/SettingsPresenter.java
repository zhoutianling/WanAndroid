package com.example.hy.wanandroid.presenter.mine;

import android.content.res.Resources;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.presenter.BasePresenter;
import com.example.hy.wanandroid.config.App;
import com.example.hy.wanandroid.config.Constant;
import com.example.hy.wanandroid.config.RxBus;
import com.example.hy.wanandroid.contract.mine.SettingsContract;
import com.example.hy.wanandroid.event.AutoCacheEvent;
import com.example.hy.wanandroid.event.NightModeEvent;
import com.example.hy.wanandroid.event.NoImageEvent;
import com.example.hy.wanandroid.event.StatusBarEvent;
import com.example.hy.wanandroid.event.UpdataEvent;
import com.example.hy.wanandroid.model.DataModel;
import com.example.hy.wanandroid.model.network.entity.DefaultObserver;
import com.example.hy.wanandroid.model.network.entity.Version;
import com.example.hy.wanandroid.utils.FileUtil;
import com.example.hy.wanandroid.utils.RxUtils;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Settings的Presenter
 * Created by 陈健宇 at 2018/11/26
 */
public class SettingsPresenter extends BasePresenter<SettingsContract.View> implements SettingsContract.Presenter{

    private boolean isUpdata = false;

    @Inject
    public SettingsPresenter(DataModel dataModel) {
        super(dataModel);
    }

    @Override
    public void addSubcriber(Disposable disposable) {
        super.addSubcriber(disposable);
        addSubcriber(
                RxBus.getInstance().toObservable(UpdataEvent.class)
                .subscribe(updataEvent -> mView.upDataVersion())
        );
    }

    @Override
    public void setNoImageState(boolean isNoImage) {
        mModel.setNoImageState(isNoImage);
        RxBus.getInstance().post(new NoImageEvent());
    }

    @Override
    public void setAutoCacheState(boolean isAuto) {
        mModel.setAutoCacheState(isAuto);
        RxBus.getInstance().post(new AutoCacheEvent());
    }

    @Override
    public void setNightModeState(boolean isNight) {
        mModel.setNightModeState(isNight);
        RxBus.getInstance().post(new NightModeEvent(isNight));
    }

    @Override
    public void setStatusBarState(boolean isStatusBar) {
        mModel.setStatusBarState(isStatusBar);
        RxBus.getInstance().post(new StatusBarEvent(isStatusBar));
    }

    @Override
    public void checkVersion(String currentVersion) {
        addSubcriber(
                mModel.getVersionDetails()
                .compose(RxUtils.switchSchedulers())
                .filter(new Predicate<Version>() {
                    @Override
                    public boolean test(Version version) throws Exception {
                        isUpdata = Float.valueOf(currentVersion.replace("v", "")) < Float.valueOf(version.getTag_name().replace("v", ""));
                        return isUpdata;
                    }
                })
                .map(new Function<Version, String>() {
                    @Override
                    public String apply(Version version) throws Exception {
                        StringBuilder content = new StringBuilder();
                        Resources resources = App.getContext().getResources();
                        mView.setNewVersionName(version.getTag_name());
                        content.append(resources.getString(R.string.dialog_versionName)).append(version.getTag_name())
                                .append(resources.getString(R.string.dialog_versionSize)).append(FileUtil.getFormatSize(version.getAssets().get(0).getSize()))
                                .append(resources.getString(R.string.dialog_versionContent)).append(version.getBody());
                        return content.toString();
                    }
                })
                .subscribeWith(new DefaultObserver<String>(mView, false, true){
                    @Override
                    public void onNext(String s){
                        super.onNext(s);
                        mView.showUpdataDialog(s);
                    }
                    @Override
                    public void onComplete() {
                        if(!isUpdata)
                            mView.showAlareadNewToast(App.getContext().getResources().getString(R.string.dialog_version_already));
                    }
                })
        );
    }

}
