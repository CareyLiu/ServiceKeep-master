package jack.com.servicekeep;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.trustmobi.devicem.DeviceManger;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SaoMaYeActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_SCAN_ONE = 1001;
    private DeviceManger deviceManger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        deviceManger = new DeviceManger(this);
        deviceManger.disableDeviceManager();


        String[] perms = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        EasyPermissions.requestPermissions(SaoMaYeActivity.this, "申请开启app需要的权限", 0, perms);

        //    ScanUtil.startScan(SaoMaYeActivity.this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("LoginActivity_xx", "通过了......");
        ScanUtil.startScan(SaoMaYeActivity.this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //UIHelper.ToastMessage(mContext, "拒绝了");
        Log.i("LoginActivity_xx", "拒绝了......");


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
                ZhuCeActivity.actionStart(SaoMaYeActivity.this, erWeiMaJson);
                finish();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                // 处理自己的逻辑break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                // showToast("再按一次返回键退出");
                //UIHelper.ToastMessage(this, "再按一次返回键退出");
                Toast.makeText(SaoMaYeActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
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

}
