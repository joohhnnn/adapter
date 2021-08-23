package com.txznet.adapter.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.txznet.adapter.R;

/**
 * 自定义dialog
 *
 */
public class SelfDialog extends Dialog {

    private final String TAG = "SelfDialog";

    private SelfDialog(@NonNull Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param themeResId    设置主题效果，R.style.xxx
     */
    private SelfDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     *
     * @param context           context
     * @param cancelable        点击屏幕以外区域，dialog是否消失
     * @param cancelListener    cancelable为true，点击外部dialog消失，listener中做事件监听；false不消失，回调不被调用
     */
    private SelfDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public static final class Builder {

        private final String tag = "SelfDialog.Builder";

        private Context context;
        private String title;
        private String message;
        private String okText;
        private String cancelText;
        private boolean clickable;
        private ClickListener clickListener;

        private View rootLayout;
        private SelfDialog selfDialog;
        private TextView title_tv, message_tv;
        private Button ok_bt, cancel_bt;

        public Builder(Context context) {
            this.context = context;

            selfDialog = new SelfDialog(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootLayout = inflater.inflate(R.layout.view_self_dialog, null);
            // rootLayout.setClickable(true);
            selfDialog.addContentView(rootLayout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setOkText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder setCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder setClickable(boolean clickable, ClickListener clickListener) {
            this.clickable = clickable;
            this.clickListener = clickListener;
            return this;
        }

        public SelfDialog build() {
            title_tv = rootLayout.findViewById(R.id.self_dialog_title);
            message_tv = rootLayout.findViewById(R.id.self_dialog_message);
            ok_bt = rootLayout.findViewById(R.id.dialog_ok_bt);
            cancel_bt = rootLayout.findViewById(R.id.dialog_cancel_bt);

            if (!TextUtils.isEmpty(title))
                title_tv.setText(title);
            if (!TextUtils.isEmpty(message))
                message_tv.setText(message);
            if (!TextUtils.isEmpty(okText))
                ok_bt.setText(okText);
            if (!TextUtils.isEmpty(cancelText))
                cancel_bt.setText(cancelText);
            if (clickListener != null) {
                ok_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.okClick();
                    }
                });
                cancel_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.cancleClick();
                    }
                });
                rootLayout.setClickable(true);
                rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.outFocusClick();
                    }
                });

             }

            return selfDialog;
        }
    }

    /**
     * 点击事件回调
     */
    public interface ClickListener {
        void okClick();
        void cancleClick();
        void outFocusClick();
    }


    public void init(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("title")
                .setMessage("content")
                .setPositiveButton("sure", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create();
    }

}
