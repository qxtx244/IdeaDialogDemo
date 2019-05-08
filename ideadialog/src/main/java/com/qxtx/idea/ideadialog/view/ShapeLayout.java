package com.qxtx.idea.ideadialog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author QXTX-WORK
 * @date 2019/5/8 9:54
 * <p>
 * Description
 */
public class ShapeLayout extends RelativeLayout {
    /** Value of px, must be convert to dp. */
    public static final int SHAPE_RADIUS = 15;

    private RectF mRectF;
    private Path mPath;

    public ShapeLayout(Context context) {
        super(context);
        init();
    }
    public ShapeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ShapeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectF = new RectF();
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        mPath.reset();
        mPath.rewind();

        mRectF.left = (float)getPaddingLeft();
        mRectF.top = (float)getPaddingTop();
        mRectF.right = (float)getPaddingRight();
        mRectF.bottom = (float)getPaddingBottom();
        mPath.addRoundRect(mRectF, SHAPE_RADIUS, SHAPE_RADIUS, Path.Direction.CW);
        canvas.clipPath(mPath);

        canvas.restore();
    }
}
