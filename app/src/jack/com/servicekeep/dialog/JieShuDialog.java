package jack.com.servicekeep.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundRelativeLayout;

import jack.com.servicekeep.R;

public class JieShuDialog extends Dialog {

    private RoundRelativeLayout rllQueDing;

    public JieShuDialog(@NonNull Context context) {
        super(context,R.style.dialogBaseBlur);
        setContentView(R.layout.dialog_jieshu);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //设置确定按钮被点击后，向外界提供监听

        rllQueDing = findViewById(R.id.rrl_queding);
        rllQueDing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
            }
        });
    }


    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;

    public JieShuDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        public void onPositiveClick();

    }

}
