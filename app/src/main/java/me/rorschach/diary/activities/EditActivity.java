package me.rorschach.diary.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.diary.R;
import me.rorschach.diary.utils.FontUtils;

public class EditActivity extends AppCompatActivity {

    @Bind(R.id.title)
    EditText mTitle;
    @Bind(R.id.body)
    EditText mBody;
    @Bind(R.id.end)
    EditText mEnd;
    @Bind(R.id.done)
    TextView mDone;
    @Bind(R.id.linearLayout)
    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        mDone.setTypeface(FontUtils.getTypeface(this));
    }
}
