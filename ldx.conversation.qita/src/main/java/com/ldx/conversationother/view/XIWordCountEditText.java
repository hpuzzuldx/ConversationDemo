package com.ldx.conversationother.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldx.conversationother.R;

/**
 * Created by v-doli1 on 2018/4/4.
 */

public class XIWordCountEditText extends LinearLayout {
    private int MaxNum = 1000;
    private int MaxLine = 3;
    private int MaxWordLimit = 50;
    private MulLineEditText mEditText;
    private TextView mTextView;
    public XIWordCountEditText(Context context) {
        this(context, null);
    }

    public XIWordCountEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.xiconversation_customeedittext_wordcount, this, true);
        mEditText = (MulLineEditText) findViewById(R.id.xiconversation_customview_edittext);
        mEditText.setMaxLines(MaxNum);
        mTextView = (TextView) findViewById(R.id.xiconversation_customview_wordcount);
        mTextView.setVisibility(View.GONE);
    }

    public void setMaxNum(int maxNum){
        MaxNum = maxNum;
    }
    public int getMaxNum(){
        return MaxNum;
    }

    public MulLineEditText getEditText(){
        return mEditText;
    }

    public void setRightCount() {
        long inputCount = getInputCount();
        if (inputCount > MaxWordLimit &&  mEditText.getLineCount() >= MaxLine){
            if (mTextView.getVisibility() == View.GONE){
                mTextView.setVisibility(View.VISIBLE);
            }
            mTextView.setText(inputCount+"/"+MaxNum);
        }else{
            if (mTextView.getVisibility() == View.VISIBLE){
                mTextView.setVisibility(View.GONE);
            }
        }
    }

    public TextView getBottomControl(){
        return mTextView;
    }

    public long getInputCount() {
        return calculateLength(mEditText.getText().toString());
    }

    public static long calculateLength(CharSequence cs) {
        double len = 0;
        for (int i = 0; i < cs.length(); i++) {
            int tmp = (int) cs.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

}
