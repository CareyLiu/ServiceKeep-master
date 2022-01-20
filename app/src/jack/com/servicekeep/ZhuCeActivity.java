package jack.com.servicekeep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.flyco.roundview.RoundRelativeLayout;
import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import jack.com.servicekeep.bean.Message;
import jack.com.servicekeep.callback.JsonCallback;

public class ZhuCeActivity extends Activity {
    @BindView(R.id.tv_shenfenzhenghao)
    EditText tvShenfenzhenghao;
    @BindView(R.id.tv_xingming)
    EditText tvXingming;
    @BindView(R.id.tv_shoujihao)
    EditText tvShoujihao;
    @BindView(R.id.tv_fangkedanwei)
    EditText tvFangkedanwei;
    @BindView(R.id.tv_cheliangxinxi)
    EditText tvCheliangxinxi;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_getcode)
    TextView mTvGetCode;
    @BindView(R.id.rrl_qubaifang)
    RoundRelativeLayout rrlQubaifang;
    @BindView(R.id.tvyanzhegnma)
    EditText tvyanzhegnma;
    private TimeCount timeCount;


    private String ShenFenZhengHao;
    private String XingMing;
    private String ShouJiHao;
    private String yanZhengMa;
    private String fangKeDanWei;
    private String cheLiangXinXi;


    private String fkjkr_id;

    ErWeiMaJson erWeiMaJson = null;
    TelephonyManager telephonyMgr;

    private String smsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_zhuce);
        ButterKnife.bind(this);
        telephonyMgr = (TelephonyManager) ZhuCeActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        erWeiMaJson = (ErWeiMaJson) getIntent().getSerializableExtra("erWeiMaJson");
        ImmersionBar immersionBar = ImmersionBar.with(ZhuCeActivity.this);
        immersionBar.with(this)
                .titleBar(rlBack)
                .init();
        timeCount = new TimeCount(60000, 1000, mTvGetCode);

        mTvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> map = new HashMap<>();
                map.put("code", "00001");
                map.put("key", Urls.key);
                map.put("user_phone", tvShoujihao.getText().toString());
                map.put("mod_id", "0372");
                Gson gson = new Gson();
                OkGo.<AppResponse<Message.DataBean>>
                        post(Urls.FANGKETONG_URL_3).
                        tag(ZhuCeActivity.this).
                        upJson(gson.toJson(map)).
                        execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                            @Override
                            public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                                //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);

                                smsId = response.body().data.get(0).getSms_id();
                                timeCount.start();

                            }

                            @Override
                            public void onError(Response<AppResponse<Message.DataBean>> response) {
                                //    AlertUtil.t(context, response.getException().getMessage());
                                Toast.makeText(ZhuCeActivity.this, response.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        rrlQubaifang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvyanzhegnma.getText().toString().trim() == "") {
                    Toast.makeText(ZhuCeActivity.this, "请您填写短信验证码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getFangKeNet();
                }
            }
        });

        ShenFenZhengHao = inPut(tvShenfenzhenghao);
        XingMing = inPut(tvXingming);
        ShouJiHao = inPut(tvShoujihao);
        yanZhengMa = inPut(tvyanzhegnma);
        fangKeDanWei = inPut(tvFangkedanwei);
        cheLiangXinXi = inPut(tvCheliangxinxi);


    }

    public String inPut(EditText editText) {
        String str;
        tvShenfenzhenghao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return editText.getText().toString();
    }


    public static void actionStart(Context context, ErWeiMaJson erWeiMaJson) {
        Intent intent = new Intent(context, ZhuCeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("erWeiMaJson", (Serializable) erWeiMaJson);
        context.startActivity(intent);
    }


    public void getFangKeNet() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "15001");
        map.put("key", Urls.key);
        map.put("fk_name", tvXingming.getText().toString());

        map.put("id_number", tvShenfenzhenghao.getText().toString());

        map.put("fk_company", tvFangkedanwei.getText().toString());

        if (tvShoujihao.getText().toString().trim().length() == 11) {
            map.put("fk_phone", tvShoujihao.getText().toString());
        } else {
            Toast.makeText(ZhuCeActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
        }

        map.put("en_cqc_id", erWeiMaJson.getCqc_id());

        map.put("inst_id", erWeiMaJson.getInst_id());

        map.put("entrance_door_name", erWeiMaJson.getDoorName());

        map.put("gps_switch", erWeiMaJson.getGpsSwitch());

        map.put("phone_mondel", android.os.Build.BRAND + android.os.Build.MODEL);

        map.put("phone_type", "1");

        map.put("sms_id", smsId);
        map.put("sms_code", tvyanzhegnma.getText().toString());


        map.put("imei", getUniquePsuedoID());


        Gson gson = new Gson();
        OkGo.<AppResponse<Message.DataBean>>
                post(Urls.FANGKETONG_URL_1).
                tag(ZhuCeActivity.this).
                upJson(gson.toJson(map)).

                execute(new JsonCallback<AppResponse<Message.DataBean>>() {
                    @Override
                    public void onSuccess(Response<AppResponse<Message.DataBean>> response) {
                        //  UIHelper.ToastMessage(context, "发送成功", Toast.LENGTH_SHORT);
                        FangKeYeActivity.actionStart(ZhuCeActivity.this, fkjkr_id);

                        fkjkr_id = response.body().fkjkr_id;
                        PreferenceHelper.getInstance(ZhuCeActivity.this).putString("fkjkr", fkjkr_id);

                    }

                    @Override
                    public void onError(Response<AppResponse<Message.DataBean>> response) {
                        //    AlertUtil.t(context, response.getException().getMessage());
                        Toast.makeText(ZhuCeActivity.this, response.getException().getMessage(), Toast.LENGTH_SHORT).show();
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


}
