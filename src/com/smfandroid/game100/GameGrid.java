package com.smfandroid.game100;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smfandroid.game100.C100GameCore.IllegalMoveException;

public class GameGrid extends LinearLayout {


	protected class GridElement extends TextView {
		protected class MyValue {
			protected int mValue;
		}
		MyValue m = new MyValue();

		protected Point mCurrentPoint;
		
		protected class MyElementClickListener implements OnClickListener {
			protected Point mPoint;
			public MyElementClickListener(Point p) {
				mPoint = p;
			}
			@Override
			public void onClick(View v) {
				if(m.mValue == -2)
					nextPointSelected(mPoint);
			}
		}
		
		public int getValue() {
			return m.mValue;
		}
		
		public void setEmptyValue() {
			m.mValue = -1;
			updateValue();
		}

		public void setPossibleValue() {
			m.mValue = -2;
			updateValue();

		}
		
		public void setValue(int value) {
			if(value <=0)
				throw new IllegalArgumentException("The value is a stricly positive integer");
			
			m.mValue = value;
			updateValue();
		}

		private void updateValue() {
			if(m.mValue > 0)
				setText(Integer.toString(m.mValue));
			else
				setText(" ");
			if(m.mValue == -2)
				setBackgroundColor(Color.GREEN);
			else
				setBackgroundColor(Color.WHITE);
		}
		
		public GridElement(Context context, Point p) {
			super(context);
			this.mCurrentPoint = p;
			this.setBackgroundColor(Color.WHITE);
			this.setWidth(0);
			this.setGravity(Gravity.CENTER);
			this.setOnClickListener( new MyElementClickListener(mCurrentPoint) );

			setEmptyValue();
			
		}
		
	}
	protected final String TAG = getClass().getSimpleName();

	private int mNbRows;
	private int mNbColumns;
	private Random mRndGenerator = new Random();
	private C100GameCore mGameCore;
	
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

	protected View CreateRow(int numRow) {
		LinearLayout row = new LinearLayout(getContext());
		row.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams layoutElement = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		layoutElement.setMargins(2, 2, 2, 2);

		for(int j = 0; j < getNbColumns(); j++) {
			row.addView(new GridElement(getContext(), new Point(j, numRow)), layoutElement);
		}		
		row.setGravity(Gravity.CENTER);
		return row;
	}

	public final int EMPTY_VALUE = -1;
	public final int POSSIBLE_VALUE = -2;


	public void reset() {
		Point initPoint = new Point();
		initPoint.x = mRndGenerator.nextInt(mNbColumns);
		initPoint.y = mRndGenerator.nextInt(mNbRows);

		mGameCore = new C100GameCore(new Point(mNbRows, mNbColumns), initPoint, 1);
		redrawValues();
	}
	

	protected void setValueAt(Point position, int value) {
		LinearLayout l = (LinearLayout)getChildAt(position.y);
		GridElement gridE = (GridElement)l.getChildAt(position.x);
		if(value == EMPTY_VALUE)
			gridE.setEmptyValue();
		else if(value == POSSIBLE_VALUE) {
			gridE.setPossibleValue();
		}
		else			
			gridE.setValue(value);
	}
	
	private void redrawValues() {
		for(int i = 0; i < getChildCount(); i++) {
			LinearLayout l = (LinearLayout)getChildAt(i);
			for(int j = 0; j< l.getChildCount(); j++) {
				((GridElement)l.getChildAt(j)).setEmptyValue();
			}
		}
		int val = 0;
		for(Point p:mGameCore.GetState()) {
			val += 1; 
			setValueAt(p, val);
		}
		for(Point p:mGameCore.PossibleMoves()) {
			setValueAt(p, POSSIBLE_VALUE);
		}
	}

	public void popLastMove() {
		mGameCore.PopMove();
		redrawValues();
	}
	
	public void nextPointSelected(Point p) {
		try {
			mGameCore.PushMove(p);
			redrawValues();
		} catch (IllegalMoveException e) {
			Toast.makeText(getContext(), R.string.msg_illegal_move, Toast.LENGTH_SHORT).show();
		}
		
	}

	public void refreshGrid() {
		this.removeAllViews();

		this.setOrientation(LinearLayout.VERTICAL);
		LayoutParams layoutRow = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

		for(int i = 0; i< getNbRows(); i++) {
			this.addView(CreateRow(i), layoutRow);
		}
	}	

	public GameGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundColor(Color.BLACK);
		setSize(10, 10);		
		reset();
	}
	
}
