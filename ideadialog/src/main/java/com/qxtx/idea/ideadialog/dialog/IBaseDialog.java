package com.qxtx.idea.ideadialog.dialog;

import android.view.View;

import com.qxtx.idea.ideadialog.base.IBaseAnimate;

/**
 * @author QXTX-WIN
 * @date 2019/5/4 17:58
 * Description:
 */
public interface IBaseDialog {
    boolean isReady();

    View getRootView();

    View getDialogView();

    IBaseAnimate getEnterAnimate();

    IBaseAnimate getCloseAnimate();

    void forceFullScreen(boolean shouldBefullScreen);

    boolean isShowing();

    void cancel();

    void dismiss();
}
