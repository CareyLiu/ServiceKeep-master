package jack.com.servicekeep;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import org.bytedeco.javacpp.annotation.NoException;

import jack.com.servicekeep.manager.KeepAliveManager;
import jack.com.servicekeep.service.ServiceToActivityCallBack;
import jack.com.servicekeep.utils.RxBus;
import jack.com.servicekeep.utils.RxUtils;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mKillService, mStartService;
    public Button btnXiangJi;
    protected CompositeSubscription _subscriptions = new CompositeSubscription();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(this);
        btnXiangJi = findViewById(R.id.btn_xiangjiye);
        mKillService = (TextView) findViewById(R.id.kill_service);
        mStartService = (TextView) findViewById(R.id.start_service);
        mKillService.setOnClickListener(this);
        mStartService.setOnClickListener(this);

        _subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(_subscriptions);
        _subscriptions.add(toObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Notice>() {
            @Override
            public void call(Notice message) {
                if (message.type == ConstanceValue.RECEIVE_LUYIN) {
                    Log.i(MainActivity.class.getSimpleName(), (String) message.content);


                }
            }
        }));
        btnXiangJi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoCaremaActivity.actionStart(MainActivity.this);
            }
        });
    }

    /**
     * 注册事件通知
     */
    public Observable<Notice> toObservable() {
        return RxBus.getDefault().toObservable(Notice.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.kill_service:
                Toast.makeText(getApplicationContext(), "kill service", Toast.LENGTH_LONG).show();
                KeepAliveManager.INSTANCE.stopKeepAliveSerice(MainActivity.this);
                break;
            case R.id.start_service:
                Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
                KeepAliveManager.INSTANCE.startKeepAliveService(MainActivity.this);
                break;
        }
    }
}
