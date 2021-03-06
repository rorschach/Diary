package me.rorschach.diary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import me.rorschach.diary.R;
import me.rorschach.diary.util.DisplayUtil;

public class MultipleVerticalTextView extends View {

    //    public static final int LAYOUT_CHANGED = 1;
    private Paint paint;
    private int mTextPosX = 0;// x坐标
    private int mTextPosY = 0;// y坐标
    private int mTextWidth = 0;// 绘制宽度
    private int mTextHeight = 0;// 绘制高度
    private int mFontHeight = 0;// 绘制字体高度
    private float mFontSize = 24;// 字体大小
    private int mRealLine = 0;// 字符串真实的行数
    private int mLineWidth = 0;//列宽度
    private int TextLength = 0;//字符串长度
    //    private int oldWidth = 0;//存储旧的width
    private String text = "";//待显示的文字
//    private Handler mHandler = null;

    private static char ch;
    private static int h = 0;
    private static float[] widths = new float[1];
    private static float[] space = new float[1];
    private static int measuredHeight;
    private static int specMode;
    private static int specSize;
    private static int result;

    public MultipleVerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public MultipleVerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if(isInEditMode()){
            return;
        }

        setupPaint();
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.MultipleVerticalTextView, 0, 0);
        try {
            float textSizePixel = typedArray.getDimension(
                    R.styleable.MultipleVerticalTextView_MultiTextSize,
                    getResources().getDimension(R.dimen.normal_text_size));
            int textSizeSp = DisplayUtil.px2sp(context, textSizePixel);
            mFontSize = DisplayUtil.sp2px(context, textSizeSp);

            boolean bold = typedArray.getBoolean(
                    R.styleable.MultipleVerticalTextView_MultiTextBold, false);

            mLineWidth = typedArray.getInteger(
                    R.styleable.MultipleVerticalTextView_MultiTextLineWidth, 60);

            mFontHeight = typedArray.getInteger(
                    R.styleable.MultipleVerticalTextView_MultiTextFontHeight, 60);

            if (bold) {
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            }
        } finally {
            typedArray.recycle();
        }
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setTextAlign(Align.CENTER);//文字居中
        paint.setAntiAlias(true);//平滑处理
        paint.setColor(Color.BLACK);
    }

    public final void setText(String text) {
        this.text = text;
        this.TextLength = text.length();
        if (mTextHeight > 0) {
            CalculateText();
        }
        invalidate();
        requestLayout();
    }

    public final String getText() {
        return this.text;
    }

    public final void setTextSize(float size) {
        if (size != paint.getTextSize()) {
            mFontSize = size;
            if (mTextHeight > 0) {
                CalculateText();
            }
        }
    }

    public final void setTextColor(int color) {
        paint.setColor(color);
    }

    public final void setTextARGB(int a, int r, int g, int b) {
        paint.setARGB(a, r, g, b);
    }

    public void setTypeface(Typeface tf) {
        if (this.paint.getTypeface() != tf) {
            this.paint.setTypeface(tf);
            this.requestLayout();
            this.invalidate();
        }
    }

    public void setLineWidth(int LineWidth) {
        mLineWidth = LineWidth;
    }

    public int getTextWidth() {
        return mTextWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawText(canvas, this.text);
    }

    private void drawText(Canvas canvas, String text) {
        char ch;
        mTextPosY = 0;//初始化y坐标
        mTextPosX = mTextWidth - mLineWidth;//初始化x坐标
        for (int i = 0; i < this.TextLength; i++) {
            ch = text.charAt(i);
            if (ch == '\n') {
                mTextPosX -= mLineWidth;// 换列
//                mTextPosX -= DisplayUtil.dp2px(getContext(), 2);
                mTextPosY = 0;
            } else {
                mTextPosY += mFontHeight;
                if (mTextPosY > this.mTextHeight) {
                    mTextPosX -= mLineWidth;// 换列
                    i--;
                    mTextPosY = 0;
                } else {
//                    canvas.clipRect(getLeft(), getTop(), getLeft() + mTextWidth, getBottom());

                    canvas.drawText(String.valueOf(ch), mTextPosX, mTextPosY, paint);
                    mTextPosY += DisplayUtil.dp2px(getContext(), 3);
                }
            }
        }

    }

    //计算文字行数和总宽
    private void CalculateText() {
        paint.setTextSize(mFontSize);
        //获得字宽
        if (mLineWidth == 0) {
            paint.getTextWidths("正", widths);//获取单个汉字的宽度
            paint.getTextWidths(" ", space);
//            mLineWidth = (int) Math.ceil((widths[0] + space[0]) * 1.1 + 2);
            mLineWidth = (int) Math.ceil((widths[0] + space[0]) * 1.1 + 2)  //获得字体宽度
                    + DisplayUtil.dp2px(getContext(), 2);                  //增加间距
        }

        FontMetrics fm = paint.getFontMetrics();
//        mFontHeight = (int) (Math.ceil(fm.descent - fm.top) * 0.9);// 获得字体高度
        mFontHeight = (int) (Math.ceil(fm.descent - fm.ascent)) // 获得字体高度
                + DisplayUtil.dp2px(getContext(), 2);          //增加间距
        //计算文字行数
        mRealLine = 0;
        for (int i = 0; i < this.TextLength; i++) {
            ch = this.text.charAt(i);
            if (ch == '\n') {
                mRealLine++;// 真实的行数加一
                h = 0;
            } else {
                h += mFontHeight;
                if (h > this.mTextHeight) {
                    mRealLine++;// 真实的行数加一
                    i--;
                    h = 0;
                } else {
                    if (i == this.TextLength - 1) {
                        mRealLine++;// 真实的行数加一
                    }
                }
            }
        }
        mRealLine += 1;//额外增加一行
        mTextWidth = mLineWidth * mRealLine;//计算文字总宽度
        measure(mTextWidth, getHeight());//重新调整大小
        layout(getLeft(), getTop(), getLeft() + mTextWidth, getBottom());//重新绘制容器
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredHeight = measureHeight(heightMeasureSpec);
        if (mTextWidth == 0) {
            CalculateText();
        }
        setMeasuredDimension(mTextWidth, measuredHeight);
//        if (oldWidth != getWidth()) {//
//            oldWidth = getWidth();
//            if (mHandler != null) {
//                mHandler.sendEmptyMessage(LAYOUT_CHANGED);
//                mHandler = null;
//            }
//        }
    }

    private int measureHeight(int measureSpec) {
        specMode = MeasureSpec.getMode(measureSpec);
        specSize = MeasureSpec.getSize(measureSpec);
        result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        mTextHeight = result;//设置文本高度
        return result;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mLineWidth", mLineWidth);
        bundle.putInt("mTextWidth", this.mTextWidth);
        bundle.putInt("mTextHeight", this.getHeight());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mTextWidth = bundle.getInt("mTextWidth");
            this.mTextHeight = bundle.getInt("mTextHeight");
            this.mLineWidth = bundle.getInt("mLineWidth");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

}
