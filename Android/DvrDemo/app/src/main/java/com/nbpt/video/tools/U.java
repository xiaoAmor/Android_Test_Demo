package com.nbpt.video.tools;

import android.content.res.Resources;
import android.util.TypedValue;

public class U {
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}
}
