package com.trustmobi.devicem;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.UserHandle;
import android.widget.Toast;

/**
 * Created by dds on 2019/6/27.
 * android_shuai@163.com
 */
public class DeviceReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        // 设备管理：可用
      //  Toast.makeText(context, "设备管理：可用", Toast.LENGTH_SHORT).show();
        //禁用相机
        DeviceManger deviceManger = new DeviceManger(context);
        deviceManger.disableCamera(true);
//
//        //获取音频服务
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
////设置声音模式
//        audioManager.setMode(AudioManager.STREAM_MUSIC);
////关闭麦克风
//        audioManager.setMicrophoneMute(true);
    }

    @Override
    public void onDisabled(final Context context, Intent intent) {
        // 设备管理：不可用
      //  Toast.makeText(context, "设备管理：不可用", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        // 设备管理：密码己经改变
        Toast.makeText(context, "设备管理：密码己经改变", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        // 设备管理：改变密码失败
        Toast.makeText(context, "设备管理：改变密码失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        // 设备管理：改变密码成功
        Toast.makeText(context, "设备管理：改变密码成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent, UserHandle user) {
        super.onPasswordExpiring(context, intent, user);
    }

}
