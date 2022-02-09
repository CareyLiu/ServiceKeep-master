package jack.com.servicekeep;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.UnicodeSetSpanner;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.flyco.roundview.RoundRelativeLayout;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.trustmobi.devicem.DeviceManger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jack.com.servicekeep.bean.Message;
import jack.com.servicekeep.callback.JsonCallback;
import jack.com.servicekeep.dialog.JieShuDialog;
import jack.com.servicekeep.manager.KeepAliveManager;
import jack.com.servicekeep.service.WorkService;
import jack.com.servicekeep.utils.RxBus;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static jack.com.servicekeep.App.mAudioManger;

public class FangKeYeActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.ll_weigui)
    LinearLayout llWeigui;
    @BindView(R.id.ll_hefa)
    LinearLayout llHefa;
    @BindView(R.id.rrl_qubaifang1)
    RoundRelativeLayout rrlQubaifang1;
    @BindView(R.id.rrl_qubaifang2)
    RoundRelativeLayout rrlQubaifang2;
    @BindView(R.id.tv_xinxi)
    TextView tvXinxi;
    public static DeviceManger deviceManger;
    private boolean quBaiFang;
    protected CompositeSubscription _subscriptions = new CompositeSubscription();
    private static final int REQUEST_CODE_SCAN_ONE = 1001;

    private String fkjkr_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("WorkService", "onCreate");
        setContentView(R.layout.layout_fangkeye);

        deviceManger = new DeviceManger(this);
        deviceManger.enableDeviceManager();

        ButterKnife.bind(this);
        String[] perms = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        EasyPermissions.requestPermissions(FangKeYeActivity.this, "申请开启app需要的权限", 0, perms);
        AppManager.getAppManager().addActivity(this);
        ImmersionBar immersionBar = ImmersionBar.with(FangKeYeActivity.this);
        immersionBar.with(this)
                .titleBar(rlBack)
                .init();

        if (!isServiceRunning(getApplicationContext(), "WorkService")) {
            KeepAliveManager.INSTANCE.startKeepAliveService(FangKeYeActivity.this);
        }

        fkjkr_id = getIntent().getStringExtra("fkjkr_id");
        if (quBaiFang) {

            rrlQubaifang1.setVisibility(View.VISIBLE);
            rrlQubaifang1.setVisibility(View.GONE);
        } else {

            rrlQubaifang1.setVisibility(View.GONE);
            rrlQubaifang2.setVisibility(View.VISIBLE);
        }

        _subscriptions.add(toObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Notice>() {
            @Override
            public void call(Notice message) {
                if (message.type == ConstanceValue.RECEIVE_LUYIN) {
                    rrlQubaifang1.setVisibility(View.GONE);
                    rrlQubaifang2.setVisibility(View.VISIBLE);
                }
            }
        }));

        rrlQubaifang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JieShuDialog dialog = new JieShuDialog(FangKeYeActivity.this);
                dialog.setOnClickBottomListener(new JieShuDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {

                        deviceManger.disableDeviceManager();
                        ScanUtil.startScan(FangKeYeActivity.this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());
                    }
                });
                dialog.show();

            }
        });


//       // Log.i("ThreadMMMMM", "Thread_FangKe: " + android.os.Process.myTid() + "");

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        //deviceManger.enableDeviceManager();

        if (quanXianFlag.equals("0")) {
            rrlQubaifang1.setVisibility(View.VISIBLE);
            rrlQubaifang2.setVisibility(View.GONE);

            llWeigui.setVisibility(View.VISIBLE);
            llHefa.setVisibility(View.GONE);

            tvXinxi.setText("您的手机未开启相册、定位等相应权限");

//            if (isServiceRunning(getApplicationContext(), "WorkService")) {
//                KeepAliveManager.INSTANCE.stopKeepAliveSerice(getApplicationContext());
//            }


            return;
        } else {
//            if (!isServiceRunning(getApplicationContext(), "WorkService")) {
//                KeepAliveManager.INSTANCE.startKeepAliveService(FangKeYeActivity.this);
//            }

            Log.i("WorkService", "onResume");
            if (mAudioManger.getActiveRecordingConfigurations().isEmpty()) {
                Log.i("WorkService", "没有开启录音");

                //luYinState = "0";

                rrlQubaifang1.setVisibility(View.GONE);
                rrlQubaifang2.setVisibility(View.VISIBLE);

                llWeigui.setVisibility(View.GONE);
                llHefa.setVisibility(View.VISIBLE);
            } else {


                rrlQubaifang1.setVisibility(View.VISIBLE);
                rrlQubaifang2.setVisibility(View.GONE);

                llWeigui.setVisibility(View.VISIBLE);
                llHefa.setVisibility(View.GONE);
            }
        }

    }




    /**
     * 注册事件通知
     */
    public Observable<Notice> toObservable() {
        return RxBus.getDefault().toObservable(Notice.class);
    }

    public static void actionStart(Context context, String fkjkr_id) {
        Intent intent = new Intent(context, FangKeYeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fkjkr_id", fkjkr_id);
        context.startActivity(intent);
    }

    private boolean isExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                // showToast("再按一次返回键退出");
                //UIHelper.ToastMessage(this, "再按一次返回键退出");
                Toast.makeText(FangKeYeActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Thread() {
                    public void run() {
                        SystemClock.sleep(3000);
                        isExit = false;
                    }

                }.start();
                return true;
            }
            // finish();
            AppManager.getAppManager().finishAllActivity();
        }
        return super.onKeyDown(keyCode, event);

    }

    WorkService myService;
    /**
     * 把 service 链接起来
     * 拿到 service 对象
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.i("WorkService", "disconnect");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象

            Log.i("WorkService", "connect");

//            myService = ((WorkService) service).getService();
//            //响应接口返回的数据
//            if (myService.getLuYinState().equals("0")) {
//                rrlQubaifang1.setVisibility(View.VISIBLE);
//                rrlQubaifang1.setVisibility(View.GONE);
//            } else {
//                rrlQubaifang1.setVisibility(View.GONE);
//                rrlQubaifang1.setVisibility(View.VISIBLE);
//            }
        }
    };


    private String ex_cqc_id;
    private String export_door_name;

    public void getFangKeNet() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15002");
        map.put("key", Urls.key);
        map.put("fkjkr_id", fkjkr_id);
        map.put("ex_cqc_id", ex_cqc_id);
        map.put("export_door_name", export_door_name);


        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_2).
                tag(FangKeYeActivity.this).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        Toast.makeText(FangKeYeActivity.this, "结束访客成功", Toast.LENGTH_SHORT).show();
                        PreferenceHelper.getInstance(FangKeYeActivity.this).removeKey("fkjkr");
                        KeepAliveManager.INSTANCE.stopKeepAliveSerice(getApplicationContext());
                        System.exit(0);
                        finish();
                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(FangKeYeActivity.this, response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                //激活设备管理器
//                Toast.makeText(SaoMaYeActivity.this, obj.originalValue, Toast.LENGTH_LONG).show();
//                if (obj.originalValue.equals("开")) {
//                    //激活设备管理器
//                    deviceManger.enableDeviceManager();
//                } else {
//                    deviceManger.disableDeviceManager();
//                }
                Gson gson = new Gson();
                ErWeiMaJson erWeiMaJson = gson.fromJson(obj.originalValue.toString(), ErWeiMaJson.class);

                Log.i("SaoMaYeActivity", obj.originalValue);
                //     ZhuCeActivity.actionStart(FangKeYeActivity.this, erWeiMaJson);

                ex_cqc_id = erWeiMaJson.getCqc_id();
                export_door_name = erWeiMaJson.getDoorName();

                getFangKeNet();


            }
        }
    }


    /**
     * 判断服务是否开启
     *
     * @param mContext
     * @param className 这里是包名+类名 xxx.xxx.xxx.TestService
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return isRunning;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发到EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        startActivity(new Intent(this, ScanActivity.class));
        // mHandler.sendEmptyMessage(UPDATE_OK);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        AppToast.makeShortToast(this, getString(R.string.get_error));
        //mHandler.sendEmptyMessage(UPDATE_OK);


        Log.i("WorkService", "拒绝了");

        quanXianFlag = "0";

    }


    private String quanXianFlag = "1";

}
