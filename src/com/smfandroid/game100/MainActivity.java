package com.smfandroid.game100;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.smfandroid.game100.DifficultyDialog.Difficulty;
import com.smfandroid.game100.DifficultyDialog.NoticeDialogListener;

public class MainActivity extends Activity implements NoticeDialogListener{

	protected final String TAG = getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void resetClicked(View origin) {
		Log.i(TAG, "Reset");
		GameGrid gmg = (GameGrid)findViewById(R.id.game_grid);
		gmg.reset();
	}

	public void popClicked(View origin) {
		Log.i(TAG, "Pop");
		GameGrid gmg = (GameGrid)findViewById(R.id.game_grid);
		gmg.popLastMove();
	}


	public void difficultyClicked(View origin) {
		Log.i(TAG, "difficulty");
		new DifficultyDialog().show(getFragmentManager(), "DifficultyDialog");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDialogSelect(Difficulty d) {
		Log.i(TAG, "New difficulty selected - " + d);
	}

}
