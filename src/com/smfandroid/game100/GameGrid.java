package com.smfandroid.game100;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameGrid extends LinearLayout {

	protected final String TAG = getClass().getSimpleName();
	
	private int mNbRows;
	private int mNbColumns;
	
	public void setSize(int nbRows, int nbColumns) {
		if(nbRows <= 0 || nbRows <=0)
			throw new IllegalArgumentException("No size value can be zero or less");

		this.mNbRows = nbRows;
		this.mNbColumns = nbColumns;
		
		refreshGrid();
	}
	
	public int getNbRows() {
		return mNbRows;
	}

	public int getNbColumns() {
		return mNbColumns;
	}


	protected View createGridElement(int value) {
		TextView txtV = new TextView(getContext());
		txtV.setBackgroundColor(Color.WHITE);
		txtV.setText(Integer.toString(value));
		txtV.setWidth(0);
		txtV.setGravity(Gravity.CENTER);
		return txtV;
	}
	
	protected View CreateRow() {
		LinearLayout row = new LinearLayout(getContext());
		row.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams layoutElement = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		layoutElement.setMargins(2, 2, 2, 2);

		for(int j = 0; j < getNbColumns(); j++) {
			row.addView(createGridElement(0), layoutElement);
		}		
		row.setGravity(Gravity.CENTER);
		return row;
	}
	
	public void refreshGrid() {
		this.removeAllViews();

		this.setOrientation(LinearLayout.VERTICAL);
		LayoutParams layoutRow = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

		for(int i = 0; i< getNbRows(); i++) {
			this.addView(CreateRow(), layoutRow);
		}
	}	

	public GameGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundColor(Color.BLACK);
		setSize(10, 10);		
	}
	
	public GameGrid(Context context, int nbRows, int nbColumns) {
		super(context);
		this.setBackgroundColor(Color.BLACK);
		setSize(nbRows, nbColumns);		
	}
	
	public GameGrid(Context context) {
		super(context);
		this.setBackgroundColor(Color.BLACK);
		
		setSize(10, 10);
	}
}
