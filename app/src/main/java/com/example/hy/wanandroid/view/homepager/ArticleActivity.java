package com.example.hy.wanandroid.view.homepager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.activity.BaseActivity;
import com.example.hy.wanandroid.config.Constant;
import com.example.hy.wanandroid.config.User;
import com.example.hy.wanandroid.contract.homepager.ArticleContract;
import com.example.hy.wanandroid.core.network.entity.homepager.Article;
import com.example.hy.wanandroid.di.component.activity.DaggerArticleActivityComponent;
import com.example.hy.wanandroid.presenter.homepager.ArticlePresenter;
import com.example.hy.wanandroid.view.mine.LoginActivity;
import com.example.hy.wanandroid.widget.layout.WebLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import javax.inject.Inject;

import androidx.annotation.IntRange;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class ArticleActivity extends BaseActivity implements ArticleContract.View {

    private AgentWeb mAgentWeb;
    private String mAddress;
    private String mTitle;
    private boolean isCollection;
    private boolean isHideCollection;
    private int mArticleId;
    private MenuItem mCollectionItem;

    @BindView(R.id.tv_common_title)
    TextView tvCommonTitle;
    @BindView(R.id.iv_common_search)
    ImageView ivCommonSearch;
    @BindView(R.id.tl_common)
    Toolbar tlCommon;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    @Inject
    ArticlePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_article;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        DaggerArticleActivityComponent.builder().appComponent(getAppComponent()).build().inject(this);
        mPresenter.attachView(this);

        Intent intent = getIntent();
        if(intent != null){
            mAddress = intent.getStringExtra(Constant.KEY_ARTICLE_ADDRESS);
            mTitle = intent.getStringExtra(Constant.KEY_ARTICLE_FLAG);
            isCollection = intent.getBooleanExtra(Constant.KEY_ARTICLE_ISCOLLECTION, false);
            isHideCollection = intent.getBooleanExtra(Constant.KEY_ARTICLE_FLAG, false);
            mArticleId = intent.getIntExtra(Constant.KEY_ARTICLE_ID, -1);
        }

        //标题栏
        setSupportActionBar(tlCommon);
        tlCommon.setTitle("");
        tlCommon.setNavigationIcon(R.drawable.ic_arrow_left);
        ivCommonSearch.setVisibility(View.INVISIBLE);
        tlCommon.setNavigationOnClickListener(v -> {
            Intent intent2 = new Intent();
            intent2.putExtra(Constant.KEY_DATA_RETURN, isCollection);
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    protected void initData() {

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(flContainer, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(getResources().getColor(R.color.colorPrimary))
                .setMainFrameErrorView(R.layout.error_view, -1)
                .setWebLayout(new WebLayout(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .createAgentWeb()
                .ready()
                .go(mAddress);

        //得到WebView
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        WebSettings settings = webView.getSettings();
        setSettings(settings);

        //得到AgentWeb最底层的控件
        FrameLayout frameLayout = mAgentWeb.getWebCreator().getWebParentLayout();
        addBgChild(frameLayout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_tl_menu, menu);
        mCollectionItem = menu.findItem(R.id.item_collection);
        if(isHideCollection) mCollectionItem.setVisible(false);
        if(isCollection) mCollectionItem.setTitle(getString(R.string.articleActivity_cancel_collection));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_share:
                shareText();
                break;
            case R.id.item_collection:
                if(mArticleId == -1) return true;
                collection();
                break;
            case R.id.item_browser:
                openBrowser();
                break;
            case R.id.item_copy:
                copy(this, mAddress);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == Constant.REQUEST_COLLECT_ARTICLE){
            if(isCollection) mPresenter.unCollectArticle(mArticleId);
            else mPresenter.collectArticle(mArticleId);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_DATA_RETURN, isCollection);
        setResult(RESULT_OK);
        return mAgentWeb.handleKeyEvent(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        if(mPresenter != null){
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void collectArticleSuccess() {
        mCollectionItem.setTitle(R.string.articleActivity_cancel_collection);
        isCollection = true;
        showToast(getString(R.string.articleActivity_collection_success));
    }

    @Override
    public void unCollectArticleSuccess() {
        mCollectionItem.setTitle(R.string.articleActivity_collection);
        isCollection = false;
        showToast(getString(R.string.articleActivity_uncollection_success));
    }

    /**
     * 收藏事件
     */
    private void collection() {
        if(!User.getInstance().isLoginStatus()){
            LoginActivity.startActivityForResult(this, Constant.REQUEST_COLLECT_ARTICLE);
            return;
        }
        if (isCollection) {
            mPresenter.unCollectArticle(mArticleId);
        }else {
            mPresenter.collectArticle(mArticleId);
        }
    }

    /**
     * 复制字符串
     */
    private void copy(Context context, String text) {
        if(TextUtils.isEmpty(text)) return;
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert mClipboardManager != null;
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        showToast(getString(R.string.articleActivity_copy_success));
    }

    /**
     * 分享
     */
    private void shareText() {
        if(!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mAddress)){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.articleActivity_share_text) + "\n" + mTitle + "\n" + mAddress
            );
            intent.setType("text/plain");
            startActivity(intent);
        }
    }

    /**
     * 打开浏览器
     */
    private void openBrowser() {
        if (TextUtils.isEmpty(mAddress) || mAddress.startsWith("file://")) {
            showToast(getString(R.string.articleActivity_browser_error));
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mAddress));
        startActivity(intent);
    }


    /**
     * 配置WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setSettings(WebSettings settings) {
        //支持缩放
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //不显示缩放按钮
        settings.setDisplayZoomControls(false);
        //设置自适应屏幕，两者合用
        //将图片调整到适合WebView的大小
        settings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    /**
     * 设置下拉回弹的文字
     */
    protected void addBgChild(FrameLayout frameLayout) {
        TextView textView = new TextView(frameLayout.getContext());
        textView.setText(R.string.articleActivity_support);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#727779"));
        frameLayout.setBackgroundColor(Color.parseColor("#272b2d"));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        final float scale = frameLayout.getContext().getResources().getDisplayMetrics().density;
        params.topMargin = (int) (30 * scale + 0.5f);
        frameLayout.addView(textView, 0, params);
    }

    /**
     * 启动活动
     * @param address html地址
     */
    public static void startActivity(Context context, String address, String title, int id, boolean isCollection, boolean isHideCollection){
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(Constant.KEY_ARTICLE_ADDRESS, address);
        intent.putExtra(Constant.KEY_ARTICLE_TITLE, title);
        intent.putExtra(Constant.KEY_ARTICLE_ISCOLLECTION, isCollection);
        intent.putExtra(Constant.KEY_ARTICLE_FLAG, isHideCollection);
        intent.putExtra(Constant.KEY_ARTICLE_ID, id);
        context.startActivity(intent);
    }

    /**
     * 启动活动
     * @param address html地址
     */
    public static void startActivityForResult(Activity activity, String address, String title, int id, boolean isCollection, boolean isHideCollection, int request){
        Intent intent = new Intent(activity, ArticleActivity.class);
        intent.putExtra(Constant.KEY_ARTICLE_ADDRESS, address);
        intent.putExtra(Constant.KEY_ARTICLE_TITLE, title);
        intent.putExtra(Constant.KEY_ARTICLE_ISCOLLECTION, isCollection);
        intent.putExtra(Constant.KEY_ARTICLE_FLAG, isHideCollection);
        intent.putExtra(Constant.KEY_ARTICLE_ID, id);
        activity.startActivityForResult(intent, request);
    }
}


