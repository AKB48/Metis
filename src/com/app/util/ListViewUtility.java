package com.app.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtility {
	public void setListViewHeightBasedOnChildren(ListView lv) {
		ListAdapter listAdapter = lv.getAdapter();
		if (listAdapter==null) {
			return;
		}
		
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i<len; i++) {
			View listItem = listAdapter.getView(i, null, lv);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = lv.getLayoutParams();
		params.height = totalHeight + (lv.getDividerHeight()*(listAdapter.getCount()-1));
		lv.setLayoutParams(params);
	}

}
