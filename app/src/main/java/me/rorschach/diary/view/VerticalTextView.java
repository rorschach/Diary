package me.rorschach.diary.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lei on 16-1-18.
 */
public class VerticalTextView extends TextView {

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if(isInEditMode()){
            return;
        }

//        FontUtil.applyFont(context, this);
//
//        TypedArray ta = context.getTheme()
//                .obtainStyledAttributes(attrs, R.styleable.VerticalTextView, 0, 0);
//
//        try {
//
//            String font = ta.getString(R.styleable.VerticalTextView_font);
//            String fontPath = "fonts/" + font;
//            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
//            this.setTypeface(typeface);
//        } finally {
//            ta.recycle();
//        }

//        this.setBackground(context.getResources().getDrawable(R.drawable.text_wrapper));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            stringBuffer.append(text.charAt(i) + "\n");
        }
        super.setText(stringBuffer, type);
    }
}
