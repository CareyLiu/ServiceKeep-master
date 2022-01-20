package jack.com.servicekeep;

import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.core.content.ContextCompat;



import static jack.com.servicekeep.App.mApplicationContext;


/**
 * Created by Administrator on 2017/3/31.
 */
//计时器
public class TimeCount extends CountDownTimer {
    private TextView view;
    private int colorId;

    public TimeCount(long millisInFuture, long countDownInterval, TextView view) {
        super(millisInFuture, countDownInterval);
        this.view = view;
        colorId = R.color.app_bg;
    }

    public TimeCount(long millisInFuture, long countDownInterval, TextView view, int type) {
        super(millisInFuture, countDownInterval);
        this.view = view;
        colorId = R.color.white;
    }

    @Override
    public void onFinish() {// 计时完毕
        view.setClickable(true);
        view.setText("获取验证码");
        view.setTextColor(ContextCompat.getColor(mApplicationContext, colorId));
        view.setTextSize(14);
    }

    @Override
    public void onTick(long millisUntilFinished) {// 计时过程
        view.setClickable(false);
        view.setTextSize(12);
        view.setText(millisUntilFinished / 1000 + "s后重新发送");
        view.setTextColor(ContextCompat.getColor(mApplicationContext, colorId));

    }


}