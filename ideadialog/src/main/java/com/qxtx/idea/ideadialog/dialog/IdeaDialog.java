package com.qxtx.idea.ideadialog.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qxtx.idea.ideadialog.R;
import com.qxtx.idea.ideadialog.base.IBaseAnimate;
import com.qxtx.idea.ideadialog.listener.DefaultBtnClickListener;
import com.qxtx.idea.ideadialog.manager.IdeaDialogManager;
import com.qxtx.idea.ideadialog.tools.Idea;

import java.lang.ref.WeakReference;

/**
 * @author QXTX-WIN
 * @date 2019/5/4 17:45
 * Description: A very flexible dialog.
 */
public class IdeaDialog extends Dialog implements IBaseDialog {
    private static final String TAG = "IdeaDialog";

    private View viewRoot;
    private View viewDialog;
    private IBaseAnimate enterAnimate;
    private IBaseAnimate closeAnimate;
    private boolean isFullscreen;

    /** IdeaDialog can be allowed to show only when it is true. */
    private volatile boolean isReady;

    private IdeaDialog(@NonNull Context mContext) {
        this(mContext, -1);
    }

    private IdeaDialog(Context context, int themeResId) {
        super(context, themeResId == -1 ? R.style.IdeaDialogTheme : themeResId);

        setReady(false);
        forceFullScreen(true);
        setRootView(R.layout.ideadialog_default);
        IdeaDialogManager.getInstance().add(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int size = ViewGroup.LayoutParams.MATCH_PARENT;
        setContentView(viewRoot, new ViewGroup.LayoutParams(size, size));
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void show() {
        if (isShowing()) {
            Log.e(TAG, "Unable to show ideaDialog! IdeaDialog is showing.");
            return ;
        }

        if (!isReady) {
            Log.e(TAG, "Unable to show ideaDialog! IdeaDialog is not ready.");
            return ;
        }

        super.show();

        /* Resolve for ideaDialog is unable to fullscreen. */
        maybeFullScreen();

        /* Enter animate for ideaDialog. */
        if (enterAnimate != null) {
            viewRoot.post(enterAnimate::execute);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        IdeaDialogManager.getInstance().remove(this);

        /* Enter animate for ideaDialog. */
        if (closeAnimate != null) {
            viewRoot.post(closeAnimate::execute);
            viewRoot.postDelayed(this::cancel, closeAnimate.getDuration());
        } else {
            super.cancel();
        }
    }

    @Override
    public View getRootView() {
        return viewRoot;
    }

    @Override
    public View getDialogView() {
        return viewDialog;
    }

    @Override
    public IBaseAnimate getEnterAnimate() {
        return enterAnimate;
    }

    @Override
    public IBaseAnimate getCloseAnimate() {
        return closeAnimate;
    }

    @Override
    public void forceFullScreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    public void addView(int viewLayoutId, int width, int height, int id) {
        View view = LayoutInflater.from(getContext()).inflate(viewLayoutId, null);
        addView(view, width, height, id);
    }

    public void addView(View view, int width, int height, @IdRes int id) {
        if (viewDialog instanceof ViewGroup) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            view.setId(id);
            ((ViewGroup)viewDialog).addView(view, lp);
        } else {
            Log.e(TAG, "Failed to add view to dialog, dialog is not a ViewGroup.");
        }
    }

    public boolean replaceDialogView(int dialogViewResId) {
        View dialogView = LayoutInflater.from(getContext()).inflate(dialogViewResId, null);
        return replaceDialogView(dialogView, null);
    }

    /**
     * Must be called after ideaDialog is created, that means {@link #isReady} is true.
     */
    public boolean replaceDialogView(@NonNull View dialogView, @Nullable ViewGroup.LayoutParams lp) {
        if (!isReady) {
            Log.e(TAG, "IdeaDialog is not ready, not allowed to replace dialog layout.");
            return false;
        }

        if (viewRoot instanceof ViewGroup) {
            if (lp == null) {
                if (viewDialog instanceof ViewGroup) {
                    lp = viewDialog.getLayoutParams();
                } else {
                    lp = new ViewGroup.LayoutParams(viewDialog.getWidth(), viewDialog.getHeight());
                }
            }

            ((ViewGroup)viewRoot).removeView(viewDialog);
            ((ViewGroup)viewRoot).addView(dialogView, lp);

            viewDialog = dialogView;
        } else {
            Log.e(TAG, "Root view of ideaDialog is not a ViewGroup! Failed to add dialog view.");
            return false;
        }

        return true;
    }

    public boolean replaceRootView(@LayoutRes int dialogViewResId) {
        View v = LayoutInflater.from(getContext()).inflate(dialogViewResId, null);
        return replaceRootView(v);
    }

    public boolean replaceRootView(View view) {
        if (!isReady) {
            Log.e(TAG, "IdeaDialog is not ready, not allowed to replace dialog layout.");
            return false;
        }

        Window window = getWindow();
        if (window == null) {
            return false;
        }

        getWindow().setContentView(view);
        setRootView(view);

        return true;
    }

    private void setRootView(int rootLayoutId) {
        rootLayoutId = rootLayoutId == -1 ? R.layout.ideadialog_default : rootLayoutId;
        View view = LayoutInflater.from(getContext()).inflate(rootLayoutId, null);
        setRootView(view);
    }

    private void setRootView(@NonNull View view) {
        viewRoot = view;
    }

    private void setDialogView(@IdRes int dialogId) {
        if (viewRoot != null) {
            viewDialog = viewRoot.findViewById(dialogId);
        }
    }

    private void setEnterAnimate(IBaseAnimate enterAnimate) {
        this.enterAnimate = enterAnimate;
    }

    private void setCloseAnimate(IBaseAnimate closeAnimate) {
        this.closeAnimate = closeAnimate;
    }

    private synchronized void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /** Make ideaDialog fullscreen. It must be called after {@link #show()}. */
    private void maybeFullScreen() {
        if (!isFullscreen) {
            return ;
        }

        Window window = getWindow();
        if (window == null) {
            Log.e(TAG, "Unable to take ideaDialog fullscreen because of failed to get the WINDOW of ideaDialog.");
            return ;
        }

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    /**
     * Use default dialog layout, include title, message, positiveButton,
     *  negativeButton, neutralButton and style of white background and the shape.
     */
    public final static class Builder {
        private IdeaDialog dialog;
        private DialogParams params;
        private WeakReference<Context> context;

        public Builder(Context context) {
            this(context, -1);
        }

        public Builder(Context context, @StyleRes int themeId) {
            this.context = new WeakReference<>(context);
            dialog = new IdeaDialog(context, themeId);
            params = new DialogParams();
        }

        public Builder enterAnimate(IBaseAnimate animate) {
            dialog.setEnterAnimate(animate);
            return this;
        }

        public Builder closeAnimate(IBaseAnimate animate) {
            dialog.setCloseAnimate(animate);
            return this;
        }

        public Builder layoutId(@LayoutRes int layoutId) {
            params.setRootViewId(layoutId);
            return this;
        }

        public Builder fullScreen(boolean isFullScreen) {
            dialog.forceFullScreen(isFullScreen);
            return this;
        }

        public Builder title(CharSequence title) {
            params.setTitle(title);
            return this;
        }

        public Builder message(CharSequence message) {
            params.setMessage(message);
            return this;
        }

        public Builder positiveBtn(CharSequence name, DefaultBtnClickListener listener) {
            params.setPositiveBtnInfo(new Pair<>(name, listener));
            return this;
        }

        public Builder negativeBtn(CharSequence name, DefaultBtnClickListener listener) {
            params.setNegativeBtnInfo(new Pair<>(name, listener));
            return this;
        }

        /** Listen event of dialog canceled. This listener is called after {@link #cancel()} called. */
        public Builder onCancel(OnCancelListener listener) {
            params.setCancelListener(listener);
            return this;
        }

        public Builder cancelWithBackPress(boolean cancelable) {
            params.setCancelable(cancelable);
            return this;
        }

        public Builder cancelOnTouchOutside(boolean cancelable) {
            params.setCancelOnTouchOutside(cancelable);
            return this;
        }

        public IdeaDialog create() {
            boolean ready = createDialog();
            dialog.setReady(ready);
            return dialog;
        }

        public void show() {
            dialog.show();
        }

        private boolean createDialog() {
            int rootViewId = params.getRootViewId() == -1
                    ? R.layout.ideadialog_default : params.getRootViewId();
            dialog.setRootView(rootViewId);
            dialog.setDialogView(R.id.ideaDlg_dialog);

            View dialogView = dialog.getRootView().findViewById(R.id.ideaDlg_dialog);

            /* contentView */
            TextView messageView = dialogView.findViewById(R.id.ideaDlg_message);
            CharSequence message = params.getMessage();
            if (message != null) {
                messageView.setText(params.getMessage());
                messageView.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                messageView.setVisibility(View.GONE);
            }

            /* titleView */
            TextView titleView = dialogView.findViewById(R.id.ideaDlg_title);
            CharSequence title = params.getTitle();
            if (title != null) {
                titleView.setText(params.getMessage());
                titleView.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                if (messageView.getVisibility() == View.GONE) {
                    titleView.setVisibility(View.GONE);
                } else {
                    titleView.setText("");
                }
            }
            titleView.setText(params.getTitle());

            /* button */
            Button positiveButton = dialogView.findViewById(R.id.ideaDlg_positiveBtn);
            Button negativeButton = dialogView.findViewById(R.id.ideaDlg_negativeBtn);
            Button neutralButton = dialogView.findViewById(R.id.ideaDlg_neutralBtn);

            /* pBtn */
            Pair<CharSequence, DefaultBtnClickListener> pBtnInfo = params.getPositiveBtnInfo();
            if (pBtnInfo != null) {
                positiveButton.setText(pBtnInfo.first);
                positiveButton.setOnClickListener(dialog.new BtnListener(pBtnInfo.second));
            } else {
                /* remove positiveButton. */
                positiveButton.setVisibility(View.GONE);
                if (negativeButton.getVisibility() != View.GONE) {
                    ((RelativeLayout.LayoutParams) negativeButton.getLayoutParams()).removeRule(RelativeLayout.START_OF);
                }
            }

            /* nBtn */
            Pair<CharSequence, DefaultBtnClickListener> nBtnInfo = params.getNegativeBtnInfo();
            if (nBtnInfo != null) {
                negativeButton.setText(nBtnInfo.first);
                negativeButton.setOnClickListener(dialog.new BtnListener(nBtnInfo.second));
            } else {
                /* remove negativeButton. */
                negativeButton.setVisibility(View.GONE);
                if (positiveButton.getVisibility() != View.GONE) {
                    ((RelativeLayout.LayoutParams) positiveButton.getLayoutParams()).removeRule(RelativeLayout.END_OF);
                }
            }

            /* neBtn */
            Pair<CharSequence, DefaultBtnClickListener> neBtnInfo = params.getNeutralBtnInfo();
            if (neBtnInfo == null
                    && (positiveButton.getVisibility() == View.GONE || negativeButton.getVisibility() == View.GONE)) {
                neutralButton.setVisibility(View.GONE);
            }

            /* Set view is GONE if dialog is empty. */
            if (neutralButton.getVisibility() == View.GONE
                    && titleView.getVisibility() == View.GONE
                    && messageView.getVisibility() == View.GONE) {
                View horLine = dialogView.findViewById(R.id.horLine);
                horLine.setVisibility(View.GONE);
            }

            if (context == null || context.get() == null) {
                return false;
            }
            //LYX_TAG 2019/5/6 20:48 约定对话框的尺寸（黄金比例），其实应该用所有子容器占用的空间尺寸作为对话框尺寸
            WindowManager wm = (WindowManager)context.get().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            /* Help the gold-split size of dialog. */
            ViewGroup.LayoutParams dialogLp = dialogView.getLayoutParams();
            dialogLp.width = (int)((float)dm.widthPixels * 0.8f);
            dialogLp.height = (int)((float)dialogLp.width * Idea.GOLD_SPILT);

            /* Set cancelable for ideaDialog, and it was usually cancel cause by the event require that. */
            dialog.setCancelable(params.isCancelable());
            dialog.setCanceledOnTouchOutside(params.isCancelOnTouchOutside());

            return true;
        }
    }

    /**
     * Dialog data.
     */
    public static final class DialogParams {
        private int themeId;
        private int rootViewId;
        private CharSequence title;
        private CharSequence message;
        private Pair<CharSequence, DefaultBtnClickListener> positiveBtnInfo;
        private Pair<CharSequence, DefaultBtnClickListener> negativeBtnInfo;
        private Pair<CharSequence, DefaultBtnClickListener> neutralBtnInfo;
        private OnCancelListener cancelListener;
        private boolean cancelable;
        private boolean cancelOnTouchOutside;

        private DialogParams() {
            themeId = -1;
            rootViewId = -1;
            title = null;
            message = null;
            positiveBtnInfo = null;
            negativeBtnInfo = null;
            neutralBtnInfo = null;
            cancelListener = null;
            cancelable = false;
            cancelOnTouchOutside = false;
        }

        public int getThemeId() {
            return themeId;
        }
        public void setThemeId(int themeId) {
            this.themeId = themeId;
        }

        public int getRootViewId() {
            return rootViewId;
        }
        public void setRootViewId(int rootViewId) {
            this.rootViewId = rootViewId;
        }

        public CharSequence getTitle() {
            return title;
        }
        public void setTitle(CharSequence title) {
            this.title = title;
        }

        public CharSequence getMessage() {
            return message;
        }
        public void setMessage(CharSequence message) {
            this.message = message;
        }

        public Pair<CharSequence, DefaultBtnClickListener> getPositiveBtnInfo() {
            return positiveBtnInfo;
        }
        public void setPositiveBtnInfo(Pair<CharSequence, DefaultBtnClickListener> positiveBtnInfo) {
            this.positiveBtnInfo = positiveBtnInfo;
        }

        public Pair<CharSequence, DefaultBtnClickListener> getNeutralBtnInfo() {
            return neutralBtnInfo;
        }
        public void setNeutralBtnInfo(Pair<CharSequence, DefaultBtnClickListener> neutralBtnInfo) {
            this.neutralBtnInfo = neutralBtnInfo;
        }

        public Pair<CharSequence, DefaultBtnClickListener> getNegativeBtnInfo() {
            return negativeBtnInfo;
        }
        public void setNegativeBtnInfo(Pair<CharSequence, DefaultBtnClickListener> negativeBtnInfo) {
            this.negativeBtnInfo = negativeBtnInfo;
        }

        public OnCancelListener getCancelListener() {
            return cancelListener;
        }
        public void setCancelListener(OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
        }

        public boolean isCancelable() {
            return cancelable;
        }
        public void setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
        }

        public boolean isCancelOnTouchOutside() {
            return cancelOnTouchOutside;
        }
        public void setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
            this.cancelOnTouchOutside = cancelOnTouchOutside;
        }
    }

    public class BtnListener implements View.OnClickListener {
        private final DefaultBtnClickListener listener;

        private BtnListener(DefaultBtnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener == null) {
                return;
            }

            listener.onClick(v);

            if (listener.autoCancel()) {
                cancel();
            }
        }
    }
}