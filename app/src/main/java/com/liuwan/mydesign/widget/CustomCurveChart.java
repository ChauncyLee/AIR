package com.liuwan.mydesign.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.liuwan.mydesign.R;

/**
 * Created by liuwan on 2016/10/26.
 * 自定义曲线图
 */
public class CustomCurveChart extends View {

    // 坐标单位
    private String[] xLabel;
    private String[] yLabel;
    // 正常范围
    private float[] range;
    // 曲线数据
    private float[] data;
    private float yStep;
    private int color;
    private boolean dot;
    // 默认边距
    private float margin = 20.0f;
    // 原点坐标
    private float xPoint;
    private float yPoint;
    // X,Y轴的单位长度
    private float xScale;
    private float yScale;
    // 画笔
    private Paint paintAxes;
    private Paint paintCoordinate;
    private Paint paintCurve;
    private Paint paintDot;

    public CustomCurveChart(Context context, String[] xLabel, String[] yLabel, float[] data, int color,
                            boolean dot) {
        super(context);
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.data = data;
        this.yStep = Float.parseFloat(yLabel[1]) - Float.parseFloat(yLabel[0]);
        this.color = color;
        this.dot = dot;
    }

    public CustomCurveChart(Context context) {
        super(context);
    }

    /**
     * 初始化数据值和画笔
     */
    public void init() {
        // 距离左边偏移量
        float marginX = 30.0f;
        xPoint = margin + marginX;
        yPoint = this.getHeight() - margin;
        xScale = (this.getWidth() - 2 * margin - marginX) / (xLabel.length - 1.0f);
        yScale = (this.getHeight() - 2 * margin) / (yLabel.length - 1);

        paintAxes = new Paint();
        paintAxes.setStyle(Paint.Style.STROKE);
        paintAxes.setAntiAlias(true);
        paintAxes.setDither(true);
        paintAxes.setColor(ContextCompat.getColor(getContext(), R.color.application_green));
        paintAxes.setStrokeWidth(4);

        paintCoordinate = new Paint();
        paintCoordinate.setStyle(Paint.Style.STROKE);
        paintCoordinate.setDither(true);
        paintCoordinate.setAntiAlias(true);
        paintCoordinate.setColor(ContextCompat.getColor(getContext(), R.color.application_green));
        paintCoordinate.setTextSize(15);

        paintCurve = new Paint();
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setDither(true);
        paintCurve.setAntiAlias(true);
        paintCurve.setColor(ContextCompat.getColor(getContext(), color));
        paintCurve.setStrokeWidth(3);
        paintCurve.setPathEffect(new CornerPathEffect(25));

        paintDot = new Paint();
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setDither(true);
        paintDot.setAntiAlias(true);
        paintDot.setColor(ContextCompat.getColor(getContext(), color));
        paintDot.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.application_white));
        init();
        drawAxesLine(canvas, paintAxes);
        drawCoordinate(canvas, paintCoordinate);
        drawCurve(canvas, paintCurve, data);
        if (dot) {
            drawDot(canvas, paintDot, data);
        }
    }

    /**
     * 绘制坐标轴
     */
    private void drawAxesLine(Canvas canvas, Paint paint) {
        // X
        canvas.drawLine(xPoint, yPoint, this.getWidth() - margin / 6, yPoint, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint - margin / 3, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint + margin / 3, paint);

        // Y
        canvas.drawLine(xPoint, yPoint, xPoint, margin / 6, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint - margin / 3, margin / 2, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint + margin / 3, margin / 2, paint);
    }

    /**
     * 绘制刻度
     */
    private void drawCoordinate(Canvas canvas, Paint paint) {
        // X轴坐标
        for (int i = 0; i <= (xLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.CENTER);
            float startX = xPoint + i * xScale;
            canvas.drawText(xLabel[i], startX, this.getHeight() - margin / 6, paint);
        }

        // Y轴坐标
        for (int i = 0; i <= (yLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.LEFT);
            float startY = yPoint - i * yScale;
            float offsetX;
            switch (yLabel[i].length()) {
                case 1:
                    offsetX = 28;
                    break;

                case 2:
                    offsetX = 20;
                    break;

                case 3:
                    offsetX = 12;
                    break;

                case 4:
                    offsetX = 5;
                    break;

                default:
                    offsetX = 0;
                    break;
            }
            float offsetY;
            if (i == 0) {
                offsetY = 0;
            } else {
                offsetY = margin / 5;
            }
            // x默认是字符串的左边在屏幕的位置，y默认是字符串是字符串的baseline在屏幕上的位置
            canvas.drawText(yLabel[i], margin / 4 + offsetX, startY + offsetY, paint);
        }
    }

    /**
     * 绘制曲线
     */
    private void drawCurve(Canvas canvas, Paint paint, float[] data) {
        Path path = new Path();
        for (int i = 0; i <= (xLabel.length - 1); i++) {
            if (i == 0) {
                path.moveTo(xPoint, toY(data[0] - Float.parseFloat(yLabel[0])));
            } else {
                path.lineTo(xPoint + i * xScale, toY(data[i] - Float.parseFloat(yLabel[0])));
            }

            if (i == xLabel.length - 1) {
                path.lineTo(xPoint + i * xScale, toY(data[i] - Float.parseFloat(yLabel[0])));
            }
        }
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制圆点
     */
    private void drawDot(Canvas canvas, Paint paint, float[] data) {
        for (int i = 0; i <= (xLabel.length - 1); i++) {
            float startX = xPoint + i * xScale;
            canvas.drawCircle(startX, toY(data[i] - Float.parseFloat(yLabel[0])), 6, paint);
        }
    }

    /**
     * 数据值按比例转成坐标值
     */
    private float toY(float num) {
        float y;
        try {
            float a = num / yStep;
            y = yPoint - a * yScale;
        } catch (Exception e) {
            return 0;
        }
        return y;
    }

}
