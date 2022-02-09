package jack.com.servicekeep.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.ServiceSettings;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jack.com.servicekeep.AppResponse;
import jack.com.servicekeep.ConstanceValue;
import jack.com.servicekeep.FangKeYeActivity;
import jack.com.servicekeep.Notice;
import jack.com.servicekeep.PreferenceHelper;
import jack.com.servicekeep.R;
import jack.com.servicekeep.StringUtils;
import jack.com.servicekeep.Urls;
import jack.com.servicekeep.bean.Message;
import jack.com.servicekeep.callback.JsonCallback;
import jack.com.servicekeep.utils.LogUtils;
import jack.com.servicekeep.utils.RxBus;
import jack.com.servicekeep.utils.Utils;

import static jack.com.servicekeep.App.mAudioManger;


/**
 * 需要保活的业务服务
 * 完美世界
 *
 * @Author Jack
 * @Date 2017/11/22 18:02
 * @Copyright:wanmei.com Inc. All rights reserved.
 */
public class WorkService extends Service {

    private static final String TAG = "WorkService";
    private final static String ACTION_START = "action_start";

    public static Context mContext;

    private Button mButton1;
    private Boolean mMicIsBusy = true;

    private ServiceToActivityCallBack serviceToActivityCallBack;
    private int progress;

    public static int weiGuiZhuangTai;//0 没有违规 1.违规
    public static int shangChuanGuo = 0;//0 没有上传过 1.上传过

    private TelephonyManager mTm;

    Handler handler;
    /**
     * 监听电话的监听器
     */
    private MyPhoneStateListener mPhoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();

        //第一次开启服务之后,就需要去管理Toast的显示

        //电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听了)
        //1, 电话管理者对象
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2, 监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


    }


    /**
     * 停止服务
     *
     * @param context
     */
    public static void stopservice(Context context) {
        if (context != null) {
            LogUtils.d(TAG, "WorkService ------- stopService");
            Intent intent = new Intent(context, WorkService.class);
            context.stopService(intent);

        }
    }

    /**
     * 开启PushService
     *
     * @param context
     */
    public static void startService(Context context) {
        LogUtils.d(TAG, "WorkService ------- startService");
        if (context != null) {
            Intent intent = new Intent(context, WorkService.class);
            intent.setAction(ACTION_START);
            context.startService(intent);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "WorkService -------   onBind");

        return new MyBinder();
    }

    public String getLuYinState() {

        return luYinState;
    }

    String luYinState = "";


    int i = 0;


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mTm != null) {
            //第一次开启服务之后,就需要去管理Toast的显示

            //电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听了)
            //1, 电话管理者对象
            mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //2, 监听电话状态
            mPhoneStateListener = new MyPhoneStateListener();
            mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        initLocation();
        //todo 启动子线程执行耗时操作
        TimerTask timerTask = new TimerTask() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void run() {
                LogUtils.d(TAG, "WorkService ---------- onStartCommand Service工作了" + i);


                if (StringUtils.isEmpty(PreferenceHelper.getInstance(getApplication()).getString("fkjkr", ""))) {
                    return;
                }
                if (i == 0) {
                    getShangChuanXinTiao();
                    LogUtils.d(TAG, "WorkService ---------- 执行心跳" + i);
                } else if (i % 120 == 0) {
                    getShangChuanXinTiao();
                    LogUtils.d(TAG, "WorkService ---------- 执行心跳" + i);
                } else if (i % 15 == 0) {

                    startLocation();

                    LogUtils.d(TAG, "WorkService ---------- 开启了定位" + i);

                } else {

                    if (mAudioManger.getActiveRecordingConfigurations().isEmpty()) {
                        Log.i("WorkService", "没有开启录音");

                        weiGuiZhuangTai = 0;//没有违规
                        shangChuanGuo = 0;//
                    } else {
                        if (shangChuanGuo == 1) {

                        } else {
                            Log.i("WorkService", "有开启录音");
                            weiGuiZhuangTai = 1;//违规
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    System.out.println("WorkService  Thread=========" + Thread.currentThread().getName());
                                    handler = new Handler(Looper.getMainLooper()) {

                                        @Override
                                        public void handleMessage(android.os.Message msg) {
                                            setToast();
                                            super.handleMessage(msg);
                                        }


                                    };
                                    android.os.Message message = handler.obtainMessage();
                                    message.arg1 = 1;
                                    handler.sendMessage(message);

                                    Looper.loop();
                                }
                            }).start();


                        }

                    }

                    if (weiGuiZhuangTai == 1) { //违规了
                        if (shangChuanGuo == 0) {//没有上传过
                            getFangKeNet();
                        }
                    }

                }

                i = i + 1;


            }


        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        return START_STICKY;
    }


    public void setToast() {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);

        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.your_custom_layout, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消对电话状态的监听   如果不取消监听的话,则即使停止了Service,还是在监听着的
        if (mTm != null && mPhoneStateListener != null) {
            mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        LogUtils.d(TAG, "WorkService ------- is onDestroy!!!");
    }


    /**
     * 通过这个方法，可以在 activity 中拿到 service 对象
     */
    class MyBinder extends Binder {
        public WorkService getService() {
            return WorkService.this;
        }
    }

    public void getFangKeNet() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15004");
        map.put("key", Urls.key);
        map.put("fkjkr_id", PreferenceHelper.getInstance(getApplication()).getString("fkjkr", ""));
        map.put("violations", "2");


        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_2).
                tag(getApplication()).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        // Toast.makeText(getApplicationContext(), "结束访客成功", Toast.LENGTH_SHORT).show();
                        //   PreferenceHelper.getInstance(FangKeYeActivity.this).removeKey("fkjkr");
                        //finish();
                        shangChuanGuo = 1;

                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(getApplication(), response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getShangChuanXinTiao() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15005");
        map.put("key", Urls.key);
        map.put("fkjkr_id", PreferenceHelper.getInstance(getApplication()).getString("fkjkr", ""));


        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_2).
                tag(getApplication()).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        //   Toast.makeText(getApplicationContext(), "上传心跳", Toast.LENGTH_SHORT).show();
                        //   PreferenceHelper.getInstance(FangKeYeActivity.this).removeKey("fkjkr");
                        //finish();

                        Log.i("WorkService", "上传心跳");

                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(getApplication(), response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void initLocation() {

        if (locationClient == null) {
            ServiceSettings.updatePrivacyShow(getApplicationContext(), true, true);
            ServiceSettings.updatePrivacyAgree(getApplicationContext(), true);
            //初始化client
            try {
                locationClient = new AMapLocationClient(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            locationOption = getDefaultOption();
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 设置定位监听
            locationClient.setLocationListener(gaodeDingWeiListener);
        }

    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }


    /**
     * 定位监听
     */
    AMapLocationListener gaodeDingWeiListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

//                    PreferenceHelper.getInstance(getApplicationContext()).putString(JINGDU, String.valueOf(location.getLongitude()));
//                    PreferenceHelper.getInstance(getApplicationContext()).putString(WEIDU, String.valueOf(location.getLatitude()));

                    locationClient.stopLocation();
                    //解析定位结果，
                    String result = sb.toString();
                    Log.i("Location_result", result);


                    getShangChuanDingWei(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));

                }

            }

        }
    };

    public void getShangChuanDingWei(String jingdu, String weidu) {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15003");
        map.put("key", Urls.key);
        map.put("fkjkr_id", PreferenceHelper.getInstance(getApplication()).getString("fkjkr", ""));
        map.put("x", weidu);
        map.put("y", jingdu);

        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_2).
                tag(getApplication()).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        //Toast.makeText(getApplicationContext(), "结束访客成功", Toast.LENGTH_SHORT).show();
                        //   PreferenceHelper.getInstance(FangKeYeActivity.this).removeKey("fkjkr");
                        //finish();


                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(getApplication(), response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
        //resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 电话状态的监听
     * 监听器类，用于监视设备上特定电话状态的变化，包括服务状态，信号强度，消息等待指示符（语音信箱）等。
     * 覆盖您希望接收更新的状态的方法，
     * 并将您的PhoneStateListener对象与按位或LISTEN_标志一起传递给TelephonyManager.listen（）。150004
     * 请注意，对某些电话信息的访问权限受到保护。 您的应用程序将不会收到受保护信息的更新，
     * 除非它的清单文件中声明了相应的权限。 在适用权限的情况下，它们会在相应的LISTEN_标志中注明。
     */
    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //无任何状态时    空闲状态
                    //  LogUtil.d(TAG, "空闲状态");

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接起电话时   摘机
                    // LogUtil.d(TAG, "摘机状态");
                    Log.i("WorkService", "接听电话");
                    getFangKeCallPhoneNet();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话进来时   响铃
                    // LogUtil.d(TAG, "响铃状态");
                    Log.i("WorkService", "响铃状态");
                    getFangKeCallPhoneNet();
                    break;
            }
        }
    }

    public void getFangKeCallPhoneNet() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15004");
        map.put("key", Urls.key);
        map.put("fkjkr_id", PreferenceHelper.getInstance(getApplication()).getString("fkjkr", ""));
        map.put("violations", "4");


        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_2).
                tag(getApplication()).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        // Toast.makeText(getApplicationContext(), "结束访客成功", Toast.LENGTH_SHORT).show();
                        //   PreferenceHelper.getInstance(FangKeYeActivity.this).removeKey("fkjkr");
                        //finish();
                        shangChuanGuo = 1;

                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(getApplication(), response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
