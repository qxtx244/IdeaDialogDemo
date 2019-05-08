package com.qxtx.idea.ideadialog.base;

/**
 * @author QXTX-WORK
 * @date 2019/5/6 13:22
 * <p>
 * Description
 */
public interface IBaseAnimate {
    /**
     * Will be called when dialog is showing.
     */
    void execute();

    /**
     * Duration of animate.
     * @return duration of animate in milliSecond
     */
    long getDuration();
}
