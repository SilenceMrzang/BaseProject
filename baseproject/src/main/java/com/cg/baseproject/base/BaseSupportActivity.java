package com.cg.baseproject.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cg.baseproject.manager.ActivityStackManager;
import com.cg.baseproject.manager.ScreenManagerSupportActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation_swipeback.SwipeBackActivity;

/**
 * @author sam
 * @version 1.0
 * @date 2018/5/4
 * https://blog.csdn.net/xx244488877/article/details/65937778
 */
public abstract class BaseSupportActivity extends SwipeBackActivity {
    private static final String TAG = "BaseSupportActivity";
    protected LinearLayout badnetworkLayout, loadingLayout, baseactivityLayout;
    protected LayoutInflater inflater;
    protected boolean isStatusBar = false;//是否沉浸状态栏
    protected boolean isFullScreen = false;//是否允许全屏
    protected boolean isScreenPortrait = true;//是否禁止旋转屏幕
    protected Context ctx;//Context
    private boolean isDebug;// 是否输出日志信息

    public abstract int getActivityLayoutId();////布局中Fragment的ID

    public abstract void initView();//初始化界面

    public abstract void registerListener();//绑定事件

    public abstract void initData();// 初始化数据,请求网络数据等

    //布局中Fragment的ID
    public abstract int getFragmentContentId();

    public abstract void setScreenManager();

    private ScreenManagerSupportActivity screenManager;
    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.getActivityStackManager().pushActivity(this);
        initScreenManage();
        setContentView(getActivityLayoutId());
        inflater = LayoutInflater.from(this);
//        initView();
        unbinder = ButterKnife.bind(this);
        registerListener();
        initData();
        ctx = this;
    }


    private void initScreenManage() {
        setScreenManager();
        screenManager = ScreenManagerSupportActivity.getInstance();
        screenManager.setStatusBar(isStatusBar, this);
        screenManager.setScreenRoate(isScreenPortrait, this);
        screenManager.setFullScreen(isFullScreen, this);
    }

    /**
     * 跳转Activity
     * skip Another Activity
     *
     * @param activity
     * @param cls
     */
    public static void skipAnotherActivity(Activity activity, Class<? extends Activity> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 退出应用
     * called while exit app.
     */
    public void exitLogic() {
        ActivityStackManager.getActivityStackManager().popAllActivity();//remove all activity.
        System.exit(0);//system exit.
    }


    //添加fragment
    protected void addFragment(BaseSupportFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(getFragmentContentId(),
                    fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //返回键返回事件  
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "--->onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "--->onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "--->onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "--->onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "--->onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--->onDestroy()");
        ActivityStackManager.getActivityStackManager().popActivity(this);
        unbinder.unbind();
    }
}
