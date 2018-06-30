package com.wang.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/27/027.
 */

public class LineChartView extends View {


    List<String> lstMouth = new ArrayList<>();
    List<Double> lstIn = new ArrayList<>();
    List<Double> lstOut = new ArrayList<>();
    /**
     * 绘制坐标轴的画笔
     */
    private Paint xPaint;
    private Paint yPaint;
    private Paint inPaint;
    private Paint inCirPaint;

    private Paint outPaint;
    private Paint outCirPaint;
    private Paint ySelectPaint;
    private Paint xTextPaint;
    Context context;
    private final int marginBorder = 0;

    private int marginTop;

    private int textChartSpace;
    /**
     * 附近字体范围矩形
     */
    private Rect extraRect;
    private DisplayMetrics dm;
    private List<TouchArea> touchAreaList = new ArrayList();
    private final int paddingLeftRight = 40;

    private int selectIndex = -1;

    boolean isHasData = true;
    int spacing;

    /**
     * 最大钱
     */
    Double maxMouny = 0d;

    public LineChartView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    void init() {
        dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        textChartSpace = dip2px(8);
        marginTop = dip2px(24);
        if (extraRect == null) {
            extraRect = new Rect();
        }
        lstMouth.add("1月");
        lstMouth.add("2月");
        lstMouth.add("3月");
        lstMouth.add("4月");
        lstMouth.add("5月");
        lstMouth.add("6月");
        lstMouth.add("7月");
        lstMouth.add("8月");
        lstMouth.add("9月");
        lstMouth.add("10月");
        lstMouth.add("11月");
        lstMouth.add("12月");

        setBackgroundColor(Color.parseColor("#ffffff"));
        initXpaint();
        initYpaint();
        initInPaint();
        initySelectPaint();
        initOutPaint();
        initXTextPaint();
    }

    void initXpaint() {
        xPaint = new Paint();
        xPaint.setColor(Color.parseColor("#e2e2e2"));
        xPaint.setAntiAlias(true);
        xPaint.setStrokeWidth(3);
    }


    void initInPaint() {
        inPaint = new Paint();
        inPaint.setColor(Color.parseColor("#10C990"));
        inPaint.setAntiAlias(true);
        inPaint.setStrokeWidth(2);

        inCirPaint = new Paint();
        inCirPaint.setColor(Color.parseColor("#10C990"));
        inCirPaint.setAntiAlias(true);
        inCirPaint.setStrokeWidth(6);
    }

    void initOutPaint() {
        outPaint = new Paint();
        outPaint.setColor(Color.parseColor("#FF6F6F"));
        outPaint.setAntiAlias(true);
        outPaint.setStrokeWidth(2);

        outCirPaint = new Paint();
        outCirPaint.setColor(Color.parseColor("#FF6F6F"));
        outCirPaint.setAntiAlias(true);
        outCirPaint.setStrokeWidth(6);
    }

    void initYpaint() {
        yPaint = new Paint();
        yPaint.setColor(Color.parseColor("#e2e2e2"));
        yPaint.setAntiAlias(true);
        yPaint.setStrokeWidth(3);
    }

    void initySelectPaint() {
        ySelectPaint = new Paint();
        ySelectPaint.setColor(Color.parseColor("#2C3142"));
        ySelectPaint.setAntiAlias(true);
        ySelectPaint.setStrokeWidth(5);
    }

    void initXTextPaint() {
        xTextPaint = new Paint();
        xTextPaint.setColor(Color.parseColor("#454A59"));
        xTextPaint.setAntiAlias(true);
        xTextPaint.setStrokeWidth(5);
        xTextPaint.setTextSize(sp2px(context, 13));
    }

    int height;
    int width;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight() - 20;
        width = getWidth();
        spacing = (width - paddingLeftRight * 2) / 11;
        drawXAxes(canvas);
        drawYAxes(canvas);
        if (maxMouny == 0) {
            isHasData = false;
            xTextPaint.getTextBounds("本年无账单", 0, lstMouth.get(0).length(), extraRect);
            canvas.drawText("本年无账单", width / 2 - extraRect.width() / 2, height / 2 - extraRect.height() / 2, xTextPaint);
        } else {
            isHasData = true;
            drawInOut(canvas);
        }
    }

    void drawInOut(Canvas canvas) {
        int maxHeight = height - marginBorder - extraRect.height() - textChartSpace - marginTop;


        for (int i = 0; i < lstOut.size(); i++) {
            int cy = (int) (maxHeight - (maxHeight * lstOut.get(i)) / maxMouny + marginTop);
            if (i == selectIndex) {
                canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, cy, 10, outCirPaint);
            } else {
                canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, cy, 5, outCirPaint);
            }
            if (i + 1 < lstOut.size()) {
                int cyNext = (int) (maxHeight - (maxHeight * lstOut.get(i + 1)) / maxMouny + marginTop);
                canvas.drawLine(getPaddingLeft() + paddingLeftRight + spacing * i, cy, getPaddingLeft() + paddingLeftRight + spacing * (i + 1), cyNext, outPaint);
            }
        }

        for (int i = 0; i < lstIn.size(); i++) {
            int cy = (int) (maxHeight - (maxHeight * lstIn.get(i)) / maxMouny + marginTop);
            if (i == selectIndex) {
                canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, cy, 10, inCirPaint);
            } else {
                canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, cy, 5, inCirPaint);
            }
            if (i + 1 < lstIn.size()) {
                int cyNext = (int) (maxHeight - (maxHeight * lstIn.get(i + 1)) / maxMouny + marginTop);
                canvas.drawLine(getPaddingLeft() + paddingLeftRight + spacing * i, cy, getPaddingLeft() + paddingLeftRight + spacing * (i + 1), cyNext, inPaint);
            }

        }
    }

    void drawXAxes(Canvas canvas) {

        xTextPaint.getTextBounds(lstMouth.get(0), 0, lstMouth.get(0).length(), extraRect);
        canvas.drawLine(getPaddingLeft() + marginBorder, height - marginBorder - extraRect.height() - textChartSpace, width - getPaddingRight() / 2 - marginBorder, height - marginBorder - extraRect.height() - textChartSpace, xPaint);
    }

    void drawYAxes(Canvas canvas) {

        for (int i = 0; i < 12; i++) {
            xTextPaint.getTextBounds(lstMouth.get(i), 0, lstMouth.get(i).length(), extraRect);
            if (i == selectIndex) {
                canvas.drawLine(getPaddingLeft() + paddingLeftRight + spacing * i, 0 + getPaddingTop(), getPaddingLeft() + paddingLeftRight + spacing * i, height - marginBorder - extraRect.height() - textChartSpace, ySelectPaint);
                //  canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, height - getPaddingBottom(), 10, pointXPaint);
            } else {
                canvas.drawLine(getPaddingLeft() + paddingLeftRight + spacing * i, 0 + getPaddingTop(), getPaddingLeft() + paddingLeftRight + spacing * i, height - marginBorder - extraRect.height() - textChartSpace, yPaint);
                //  canvas.drawCircle(getPaddingLeft() + paddingLeftRight + spacing * i, height - getPaddingBottom(), 5, pointXPaint);
            }

            canvas.drawText(lstMouth.get(i), getPaddingLeft() + paddingLeftRight + spacing * i - extraRect.width() / 2, height - getPaddingBottom(), xTextPaint);
            TouchArea touchArea = new TouchArea();
            touchArea.setL(getPaddingLeft() + paddingLeftRight + spacing * i - spacing / 2);
            touchArea.setT(0 + getPaddingTop());
            touchArea.setR(getPaddingLeft() + paddingLeftRight + spacing * i + spacing / 2);
            touchArea.setB(height - getPaddingBottom());
            touchAreaList.add(touchArea);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                for (int i = 0; i < touchAreaList.size(); i++) {
                    if (isHasData && x < touchAreaList.get(i).getR() && touchAreaList.get(i).getL() < x && y < touchAreaList.get(i).getB() && touchAreaList.get(i).getT() < y) {
                        touchAreaList.clear();
                        selectIndex = i;
                        if (lineClick != null) {
                            lineClick.onClick(lstIn.get(i), lstOut.get(i));
                        }
                        invalidate();
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return true;

    }

    class TouchArea {
        int l;
        int t;
        int r;
        int b;

        public int getL() {
            return l;
        }

        public void setL(int l) {
            this.l = l;
        }

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        return (int) (dpValue * dm.density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setInAndOut(List<Double> in, List<Double> out) {
        lstIn.clear();
        lstOut.clear();
        lstIn.addAll(in);
        lstOut.addAll(out);
        getMax();
        selectIndex = -1;
        invalidate();
    }

    void getMax() {
        for (Double item : lstIn) {
            if (item > maxMouny) {
                maxMouny = item;
            }
        }

        for (Double item : lstOut) {
            if (item > maxMouny) {
                maxMouny = item;
            }
        }
    }

    LineClick lineClick;

    public void setLineClick(LineClick lineClick) {
        this.lineClick = lineClick;
    }

    public interface LineClick {
        void onClick(Double in, Double out);
    }
}
