package jack.com.servicekeep;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;

import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class SplashActivity extends AppCompatActivity{

    public static final int UPDATE_OK = 2;
    public static final int OVERTIME = 1;
    protected boolean isAnimationEnd;
    private final NotLeakHandler mHandler = new NotLeakHandler(this);
    private ImageView iv_background;


    private static class NotLeakHandler extends Handler {
        private WeakReference<SplashActivity> mWeakReference;

        private NotLeakHandler(SplashActivity reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity reference = mWeakReference.get();
            if (reference == null) { // the referenced object has been cleared
                return;
            }
            // do something
            switch (msg.what) {
                case UPDATE_OK:
                    SplashOverToGo();
                    break;
                case OVERTIME:
                    SplashOverToGo();
            }
        }

        /**
         * 进入主界面
         */
        private void SplashOverToGo() {

            SharedPreferences sharedPreferences = mWeakReference.get().getSharedPreferences("share", MODE_PRIVATE);
            boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
            SharedPreferences.Editor editor = sharedPreferences.edit();


            mWeakReference.get().finish();

            if (isFirstRun) {
                Log.d("debug", "第一次运行");
                editor.putBoolean("isFirstRun", false);
                editor.apply();
                mWeakReference.get().startActivity(new Intent(mWeakReference.get(), WelcomeActivity.class));
                mWeakReference.get().finish();
            } else {
                Log.d("debug", "不是第一次运行");
                Log.d("isLogin", PreferenceHelper.getInstance(mWeakReference.get()).getBoolean("ISLOGIN", false) + "");
                String fkjkr = PreferenceHelper.getInstance(mWeakReference.get()).getString("fkjkr", "");

                if (fkjkr.trim().equals("")) {
                    mWeakReference.get().startActivity(new Intent(mWeakReference.get(), SaoMaYeActivity.class));
                    mWeakReference.get().finish();
                } else {
                    FangKeYeActivity.actionStart(mWeakReference.get(), fkjkr);
                }
                mWeakReference.get().finish();

            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout logoView = findViewById(R.id.iv_welcome);
        iv_background = findViewById(R.id.iv_image);
        AppManager.getAppManager().addActivity(this);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画效果时间为2秒
        alphaAnimation.setDuration(2000);
        // 设置开始动画
        logoView.startAnimation(alphaAnimation);
        // 动画监听
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { // 动画开始时执行此方法

            }

            @Override
            public void onAnimationRepeat(Animation animation) { // 动画重复调用时执行此方法
            }

            @Override
            public void onAnimationEnd(Animation animation) { // 动画结束时执行此方法
//                String[] perms = {
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                EasyPermissions.requestPermissions(SplashActivity.this, "申请开启app需要的权限", 0, perms);
                isAnimationEnd = true;
                mHandler.sendEmptyMessage(UPDATE_OK);

            }
        });

        ImmersionBar immersionBar = ImmersionBar.with(SplashActivity.this);
        immersionBar.with(this)
                .titleBar(iv_background)
                .init();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }


}