package jack.com.servicekeep;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundRelativeLayout;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jack.com.servicekeep.bean.Message;
import jack.com.servicekeep.callback.JsonCallback;

/**
 * 实现首次启动的引导页面
 */
public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private int[] imageIdArray;//图片资源的数组

    //参考自http://stackoverflow.com/questions/32061934/permission-from-manifest-doesnt-work-in-android-6
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    Context context;
    //最后一页的按钮
    private RoundRelativeLayout ib_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StatusBarUtil.setTransparent(this);
        context = this;
        ib_start = (RoundRelativeLayout) findViewById(R.id.guide_ib_start);
        ib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fkjkr = PreferenceHelper.getInstance(WelcomeActivity.this).getString("fkjkr", "");

                if (fkjkr.trim().equals("")) {

                    getChongFuNet();

                } else {
                    FangKeYeActivity.actionStart(WelcomeActivity.this, fkjkr);
                }

            }
        });

        //加载ViewPager
        initViewPager();

    }

    public void getChongFuNet() {
        Map<String, String> map = new HashMap<>();
        map.put("code", "15006");
        map.put("key", Urls.key);
        map.put("imei", getUniquePsuedoID());

        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_1).
                tag(WelcomeActivity.this).
                upJson(gson.toJson(map)).
                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
//                        if (getUniquePsuedoID().equals(response.body().available_balance)) {
//                            FangKeYeActivity.actionStart(WelcomeActivity.this, "");
//                        }
                        if (!StringUtils.isEmpty(response.body().fkjkr_id)) {
                            PreferenceHelper.getInstance(WelcomeActivity.this).putString("fkjkr",response.body().fkjkr_id);
                            FangKeYeActivity.actionStart(WelcomeActivity.this, response.body().fkjkr_id);

                        } else {
                            startActivity(new Intent(WelcomeActivity.this, SaoMaYeActivity.class));
                        }

                        finish();
                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(WelcomeActivity.this, response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位



        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 加载图片ViewPager
     */
    private void initViewPager() {
        ViewPager vp = (ViewPager) findViewById(R.id.guide_vp);
        //实例化图片资源
        imageIdArray = new int[]{R.drawable.splash_one, R.drawable.splash_two, R.drawable.splash_three};
        List<View> viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        for (int anImageIdArray : imageIdArray) {
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(params);
            //使用Glide加载高清图片以减少Viewpage的卡顿现象
            Glide.with(getApplicationContext()).load(anImageIdArray).into(imageView);
            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        //View集合初始化好后，设置Adapter
        vp.setAdapter(new WelcomeAdapter(viewList));
        //设置滑动监听
        vp.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动后的监听
     */
    @Override
    public void onPageSelected(int position) {
        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1) {
            ib_start.setVisibility(View.VISIBLE);
        } else {
            ib_start.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}