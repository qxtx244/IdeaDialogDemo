package com.qxtx.idea.ideadialog.tools;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * @author QXTX-WIN
 * @date 2018/11/5.
 *
 * Description
 */
public class IdeaToast {
    private Toast toast;
    private WeakReference<Context> mContext;

    public static final int DURATION_SHORT = Toast.LENGTH_SHORT;
    public static final int DURATION_LONG = Toast.LENGTH_LONG;

    public IdeaToast(Context mContext) {
        this.mContext = new WeakReference<Context>(mContext);
    }

    public void showToast(CharSequence msg, int duration) {
        if (toast != null) {
            toast.cancel();
        }

        if (mContext == null || mContext.get() == null) {
            return ;
        }

        toast = new Toast(mContext.get());
        toast = Toast.makeText(mContext.get(), msg, duration);
        toast.show();
    }

    public void deathSelf() {
        mContext = null;
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
