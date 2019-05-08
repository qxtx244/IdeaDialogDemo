package com.qxtx.idea.ideadialog.tools;

import android.app.AlertDialog;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author  QXTX-WORK
 * @date 2019/5/6 9:42
 *
 * Description
 */
public class Idea {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BTN_POSITIVE, BTN_NEGATIVE, BTN_NEUTRAL})
    public @interface DialogBtnWhich {}
    public static final int BTN_POSITIVE = AlertDialog.BUTTON_POSITIVE;
    public static final int BTN_NEGATIVE = AlertDialog.BUTTON_NEGATIVE;
    public static final int BTN_NEUTRAL = AlertDialog.BUTTON_NEUTRAL;

    public static final float GOLD_SPILT = 0.618f;
}
