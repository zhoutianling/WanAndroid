package com.example.hy.wanandroid.view.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.fragment.BaseFragment;
import com.example.hy.wanandroid.base.fragment.BaseLoadFragment;
import com.example.hy.wanandroid.base.fragment.BaseMvpFragment;
import com.example.hy.wanandroid.config.Constant;
import com.example.hy.wanandroid.config.User;
import com.example.hy.wanandroid.contract.mine.MineContract;
import com.example.hy.wanandroid.di.module.fragment.MineFragmentModule;
import com.example.hy.wanandroid.presenter.mine.MinePresenter;
import com.example.commonlib.utils.AnimUtil;
import com.example.commonlib.utils.StatusBarUtil;
import com.example.hy.wanandroid.view.MainActivity;
import com.example.hy.wanandroid.widget.dialog.LogoutDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import javax.inject.Inject;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import dagger.Lazy;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * 我的tab
 * Created by 陈健宇 at 2018/10/23
 */
public class MineFragment extends BaseMvpFragment<MinePresenter> implements MineContract.View {

    @BindView(R.id.iv_face)
    CircleImageView ivFace;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_collection)
    ImageView ivCollection;
    @BindView(R.id.tv_collection)
    TextView tvCollection;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.iv_about_us)
    ImageView ivAboutUs;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    @BindView(R.id.cl_about_us)
    ConstraintLayout clAboutus;
    @BindView(R.id.iv_logout)
    ImageView ivLogout;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.srl_mine)
    SmartRefreshLayout srlMine;
    @BindView(R.id.cl_collection)
    ConstraintLayout clCollection;
    @BindView(R.id.cl_settings)
    ConstraintLayout clSettings;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_logout)
    RelativeLayout rlLogout;
    @BindView(R.id.cl_logout)
    ConstraintLayout clLogout;

    @Inject
    MinePresenter mPresenter;
    @Inject
    Lazy<LogoutDialog> mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void inject() {
        if (!(getActivity() instanceof MainActivity)) return;
        ((MainActivity) getActivity()).getComponent().getMineFragmentComponent(new MineFragmentModule()).inject(this);
    }

    @Override
    protected MinePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void initView() {
        super.initView();
        if (User.getInstance().isLoginStatus()) {
            showLoginView();
        } else {
            showLogoutView();
        }

        if (mPresenter.getNightModeState())
            ivBack.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        btnLogin.setOnClickListener(v -> LoginActivity.startActivity(mActivity));
        clSettings.setOnClickListener(v -> SettingsActivity.startActivity(mActivity));
        clCollection.setOnClickListener(v -> {
            if (!User.getInstance().isLoginStatus()) {
                LoginActivity.startActivityForResultByFragment(mActivity, this, Constant.REQUEST_SHOW_COLLECTIONS);
                showToast(getString(R.string.first_login));
            } else
                CollectionActivity.startActivity(mActivity);
        });
        clAboutus.setOnClickListener(v -> AboutUsActivity.startActivity(mActivity));
        clLogout.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            mDialog.get().show(getFragmentManager(), "tag7");
        });
    }

    @Override
    protected void loadData() {
        mPresenter.subscribleEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDialog != null)
            mDialog = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constant.REQUEST_SHOW_COLLECTIONS)
            CollectionActivity.startActivity(mActivity);
    }

    @Override
    public void useNightNode(boolean isNight) {
        if (isNight)
            ivBack.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        else
            ivBack.getDrawable().clearColorFilter();
    }

    @Override
    public void setStatusBarColor(boolean isSet) {
        StatusBarUtil.immersiveInFragments(mActivity, Color.TRANSPARENT, 1);
    }

    @Override
    public void showLoginView() {
        AnimUtil.hideByAlpha(btnLogin);
        AnimUtil.showByAlpha(tvUsername);
        AnimUtil.showByAlpha(rlLogout);
        tvUsername.setText(User.getInstance().getUsername());
    }

    @Override
    public void showLogoutView() {
        AnimUtil.hideByAlpha(rlLogout);
        AnimUtil.hideByAlpha(tvUsername);
        AnimUtil.showByAlpha(btnLogin);
    }

    public static MineFragment newInstance() {

        return new MineFragment();
    }
}
