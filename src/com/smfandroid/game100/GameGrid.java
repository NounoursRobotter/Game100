package com.smfandroid.game100;

import java.security.spec.MGF1ParameterSpec;
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
import com.smfandroid.game100.DifficultyDialog.Difficulty;
import com.smfandroid.game100.tests.GameCore;

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

		public void setVoidValue() {
			m.mValue = -3;
			updateValue();
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
			else if (m.mValue == -3)
				setBackgroundColor(Color.BLACK);
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
	private C100GameCore mGameCore;
	
	public void setSize(int nbRows, int nbColumns) {
		if(nbRows <= 0 || nbRows <=0)
			throw new IllegalArgumentException("No size value can be zero or less");

		this.mNbRows = nbRows;
		this.mNbColumns = nbColumns;

		createGridWidget();
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
	public final int VOID_VALUE = -3;
	

	public void reset() {
		mGameCore.createGame(new Point(mNbRows, mNbColumns));
		redrawValues();
	}
	

	protected void setValueAt(Point position, int value) {
		LinearLayout l = (LinearLayout)getChildAt(position.y);
		GridElement gridE = (GridElement)l.getChildAt(position.x);
		if(value == EMPTY_VALUE)
			gridE.setEmptyValue();
		if(value == VOID_VALUE)
			gridE.setVoidValue();
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
		for(Point p:mGameCore.getCurrentState()) {
			val += 1; 
			setValueAt(p, val);
		}
		for(Point p:mGameCore.possibleMoves()) {
			setValueAt(p, POSSIBLE_VALUE);
		}
		for(Point p:mGameCore.getVoidPlaces()) {
			setValueAt(p, VOID_VALUE);
		}		
	}


	
	public void nextPointSelected(Point p) {
		try {
			mGameCore.pushMove(p);
			redrawValues();
		} catch (IllegalMoveException e) {
			Toast.makeText(getContext(), R.string.msg_illegal_move, Toast.LENGTH_SHORT).show();
		}
		
	}

	public void createGridWidget() {
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
		mGameCore = new C100GameCore(new Point(10, 10), 1);
		mGameCore.autoInitGame(0);
		setSize(10, 10);	
		redrawValues();
	}

	public void setDifficulty(Difficulty d) {
		mGameCore.createGame(new Point(d.width, d.height));
		mGameCore.autoInitGame(d.nbVoids);
		setSize(d.height, d.width);
		redrawValues();
	}
	
	public void popLastMove() {
		mGameCore.popMove();
		redrawValues();
	}
}
