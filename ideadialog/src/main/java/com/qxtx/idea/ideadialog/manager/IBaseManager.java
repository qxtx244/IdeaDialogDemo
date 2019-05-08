package com.qxtx.idea.ideadialog.manager;

import com.qxtx.idea.ideadialog.dialog.IBaseDialog;

import java.util.List;

/**
 * @author QXTX-WORK
 * @date 2019/5/8 12:05
 * <p>
 * Description
 */
public interface IBaseManager {
    void add(IBaseDialog dialog);

    void remove(IBaseDialog dialog);

    List<IBaseDialog> get();

    IBaseDialog currentDialog();

    void clear();
}
