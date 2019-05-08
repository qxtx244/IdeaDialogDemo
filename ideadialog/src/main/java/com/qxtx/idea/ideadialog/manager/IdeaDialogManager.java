package com.qxtx.idea.ideadialog.manager;

import android.support.annotation.NonNull;

import com.qxtx.idea.ideadialog.dialog.IBaseDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author QXTX-WORK
 * @date 2019/5/8 12:03
 * <p>
 * Description
 */
public class IdeaDialogManager implements IBaseManager {
    private static IdeaDialogManager manager;
    private List<IBaseDialog> dialogList;

    private IdeaDialogManager() {
        dialogList = new ArrayList<>();
    }

    public static IdeaDialogManager getInstance() {
        if (manager == null) {
            synchronized (IdeaDialogManager.class) {
                if (manager == null) {
                    manager = new IdeaDialogManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void add(@NonNull IBaseDialog dialog) {
        dialogList.add(dialog);
    }

    @Override
    public void remove(IBaseDialog dialog) {
        dialogList.remove(dialog);
    }

    @Override
    public List<IBaseDialog> get() {
        return dialogList;
    }

    @Override
    public IBaseDialog currentDialog() {
        IBaseDialog dialog = null;
        for (int i = 0; i < dialogList.size(); i++) {
            IBaseDialog d = dialogList.get(i);
            if (d.isShowing()) {
                dialog = d;
                break;
            }
        }

        return dialog;
    }

    @Override
    public void clear() {
        for (int i = 0; i < dialogList.size(); i++) {
            IBaseDialog d = dialogList.get(i);
            if (d.isShowing()) {
                d.cancel();
                break;
            }
        }

        dialogList.clear();
    }
}
