package jack.com.servicekeep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.trustmobi.devicem.DeviceManger;

import java.lang.reflect.AccessibleObject;
import java.util.List;

import jack.com.servicekeep.manager.KeepAliveManager;
import pub.devrel.easypermissions.EasyPermissions;

public class NoCaremaActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private DeviceManger deviceManger;
    ImageView ivSaoMiao;
    private static final int REQUEST_CODE_SCAN_ONE = 1001;
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carema);
        KeepAliveManager.INSTANCE.startKeepAliveService(NoCaremaActivity.this);
        ivSaoMiao = findViewById(R.id.iv_saoma);
        btn = findViewById(R.id.btn_guanbishebeiguanqi);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceManger.disableDeviceManager();
                Toast.makeText(NoCaremaActivity.this, "关闭设备管理器", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(MainActivity.this, LuYinActivity.class);
//                startActivity(intent);
            }
        });
        deviceManger = new DeviceManger(this);
        ivSaoMiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ScanUtil.startScan(NoCaremaActivity.this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());
             //   deviceManger.enableDeviceManager();
            }
        });
        String[] perms = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        EasyPermissions.requestPermissions(NoCaremaActivity.this, "申请开启app需要的权限", 0, perms);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NoCaremaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
                Toast.makeText(NoCaremaActivity.this, obj.originalValue, Toast.LENGTH_LONG).show();
                if (obj.originalValue.equals("开")) {
                    //激活设备管理器
                    deviceManger.enableDeviceManager();
                } else {
                    deviceManger.disableDeviceManager();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("LoginActivity_xx", "通过了......");

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //UIHelper.ToastMessage(mContext, "拒绝了");
        Log.i("LoginActivity_xx", "拒绝了......");

    }

}
