package com.wang.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

public class HeightChose extends View {

    public static final int vertical = 1;
    public static final int horizontal = 2;

    Context mContext;
    //滚动计算辅助类
    private Scroller scroller;
    //速度追踪器
    private VelocityTracker velocityTracker;
    private int minValue = 0;
    private int maxValue = 1000;

    /**
     * 每一间隔的距离
     */
    private int space = 20;
    private int orientation = 2;

    private int scaleHeightLong = 60;
    private int scaleHeightCenter = 20;
    private int scaleHeightShort = 10;

    private int textSize;
    Paint paint;
    int weight;
    int height;

    /**
     * 系统默认的滑动的最小速度
     */
    private float minFlingVelocity;
    /**
     * 一行或者一列绘制的最大个数间隔数
     */
    int maxMun;

    /**
     * 中间距离刻度距离最小刻度的距离
     */
    float minOffect;
    /**
     * 中间距离刻度距离最大刻度的距离
     */
    float maxOffect;

    /**
     * 选中的值
     */
    int selectValue;
    /**
     * 总刻度的个数
     */
    int totalScaleNum;

    int textColor;

    public HeightChose(Context context) {
        this(context, null);
    }

    public HeightChose(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeightChose(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.HeightChose);
        maxValue = typedArray.getInteger(R.styleable.HeightChose_maxValue, 1000);
        selectValue = minValue = typedArray.getInteger(R.styleable.HeightChose_minValue, 0);

        space = typedArray.getDimensionPixelOffset(R.styleable.HeightChose_spacing, 10);
        scaleHeightLong = typedArray.getDimensionPixelOffset(R.styleable.HeightChose_scaleHeightLong, 90);
        scaleHeightCenter = scaleHeightLong / 3 * 2;
        scaleHeightShort = scaleHeightLong / 3;
        orientation = typedArray.getInt(R.styleable.HeightChose_orientation, 2);
        textSize = typedArray.getDimensionPixelSize(R.styleable.HeightChose_textSeize, sp2px(context, 14));
        typedArray.recycle();

        //初始化offset
        minOffect = 0;
        selectValue = (maxValue - minValue) / 2;
        maxOffect = (maxValue - minValue) * space;
        totalScaleNum = (maxValue - minValue);

        scroller = new Scroller(context);
        minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        minOffect = width / 2 - (selectValue - minValue) * space;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (scroller.getCurrX() == scroller.getFinalX()) {
                smartMove();
            } else {
                int x = scroller.getCurrX();
                moveX = x - lastX;
                onMove();
                lastX = x;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setColor(Color.parseColor("#e2e2e2"));
            paint.setTextSize(textSize);
        }
        if (extraRect == null) {
            extraRect = new Rect();
        }
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);
        if (orientation == horizontal) {
            drawHorizontal(canvas);
        }
    }

    /**
     * 附近字体范围矩形
     */
    private Rect extraRect;

    float moveX = 0;

    void drawHorizontal(Canvas canvas) {
        //横线
        maxMun = getWidth() / space;
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);
        for (int i = 0; i <= (maxValue - minValue); i++) {
            float startX = space * i + minOffect;
            float endX = space * i + minOffect;
            if (startX > 0 && startX <= getWidth()) {
                if ((i + minValue) % 10 == 0) {
                    canvas.drawLine(startX, getHeight() / 2 - scaleHeightLong, endX, getHeight() / 2, paint);
                    paint.getTextBounds(String.valueOf((i + minValue) / 10), 0, String.valueOf((i + minValue) / 10).length(), extraRect);
                    canvas.drawText(String.valueOf((i + minValue) / 10), startX - extraRect.width() / 2, getHeight() / 2 - scaleHeightLong - 10, paint);
                } else if ((i + minValue) % 5 == 0) {
                    canvas.drawLine(startX, getHeight() / 2 - scaleHeightCenter, endX, getHeight() / 2, paint);
                } else {
                    canvas.drawLine(startX, getHeight() / 2 - scaleHeightShort, endX, getHeight() / 2, paint);
                }

            }
        }
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                lastX = x;
                moveX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = x - lastX;
                onMove();

                break;
            case MotionEvent.ACTION_UP:
                smartMove();
                fling();
                return false;


        }
        lastX = x;
        return true;
    }


    void onMove() {
        minOffect = minOffect + moveX;
        if (minOffect > getWidth() / 2) {
            minOffect = getWidth() / 2;
            scroller.forceFinished(true);
        } else if (minOffect < 0 && -(minOffect - getWidth() / 2) > maxOffect) {
            minOffect = -(maxOffect - getWidth() / 2);
            scroller.forceFinished(true);
        }
        selectValue = minValue + Math.round(Math.abs(minOffect - getWidth() / 2) / space);

        if (onSelectValueChange != null) {
            onSelectValueChange.onSelect(selectValue + "");
        }
        postInvalidate();
    }

    /**
     * 刚停止滚动时，滚动的位置不在刻度上，修改到选中的刻度上
     */
    void smartMove() {
        minOffect = getWidth() / 2 - ((selectValue - minValue) * space);
        lastX = 0;
        moveX = 0;
        postInvalidate();
    }


    void fling() {
//        velocityTracker.computeCurrentVelocity(1000);
        velocityTracker.computeCurrentVelocity(1000);
        float speed = velocityTracker.getXVelocity(); //计算水平方向的速度（单位秒）
        //大于这个值才会被认为是fling
        if (Math.abs(speed) > minFlingVelocity) {
            scroller.fling(0, 0, (int) speed, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
        }
    }

    OnSelectValueChange onSelectValueChange;

    interface OnSelectValueChange {
        void onSelect(String value);
    }

    public void setOnSelectValueChange(OnSelectValueChange onSelectValueChange) {
        this.onSelectValueChange = onSelectValueChange;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getScaleHeightLong() {
        return scaleHeightLong;
    }

    public void setScaleHeightLong(int scaleHeightLong) {
        this.scaleHeightLong = scaleHeightLong;
    }

    public int getScaleHeightCenter() {
        return scaleHeightCenter;
    }

    public void setScaleHeightCenter(int scaleHeightCenter) {
        this.scaleHeightCenter = scaleHeightCenter;
    }

    public int getScaleHeightShort() {
        return scaleHeightShort;
    }

    public void setScaleHeightShort(int scaleHeightShort) {
        this.scaleHeightShort = scaleHeightShort;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
