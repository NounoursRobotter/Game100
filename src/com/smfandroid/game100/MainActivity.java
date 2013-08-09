package com.smfandroid.game100;

import java.util.EmptyStackException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
		gmg.resetGame();
	}

	public void popClicked(View origin) {
		Log.i(TAG, "Pop");
		GameGrid gmg = (GameGrid)findViewById(R.id.game_grid);
		try {
			gmg.popLastMove();
		} catch(EmptyStackException e) {
			Toast.makeText(this, R.string.msg_empty_stack, Toast.LENGTH_SHORT).show();
		}
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
		GameGrid gmg = (GameGrid)findViewById(R.id.game_grid);
		gmg.setDifficulty(d);
	}

}
