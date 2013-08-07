package com.smfandroid.game100;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	protected View createGridElement(int value) {
		TextView txtV = new TextView(this);
		txtV.setBackgroundColor(Color.WHITE);
		txtV.setText(Integer.toString(value));
		txtV.setWidth(0);
		txtV.setGravity(Gravity.CENTER);
		return txtV;
	}
	
	protected View CreateRow(int nbChildElements) {
		LinearLayout row = new LinearLayout(this);

		LayoutParams layoutElement = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		layoutElement.setMargins(2, 2, 2, 2);

		for(int j = 0; j < nbChildElements; j++) {
			row.addView(createGridElement(0), layoutElement);
		}		
		row.setGravity(Gravity.CENTER);
		return row;
	}

	protected void createGrid(int nbElementsX, int nbELementsY, int id) {
		LinearLayout gL = (LinearLayout)findViewById(id);
		gL.setBackgroundColor(Color.BLACK);
		
		LayoutParams layoutRow = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		
		for(int i = 0; i< 10; i++) {
			gL.addView(CreateRow(10), layoutRow);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		createGrid(10, 10, R.id.grid);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
