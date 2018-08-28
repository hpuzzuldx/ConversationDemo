package com.ldx.conversationbase.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class XIExpandGridView extends GridView {

	public XIExpandGridView(Context context) {
		super(context);
	}
	
	public XIExpandGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
