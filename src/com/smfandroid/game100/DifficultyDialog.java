package com.smfandroid.game100;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

public class DifficultyDialog extends DialogFragment implements DialogInterface.OnClickListener {

    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onDialogSelect(Difficulty d);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    	Resources res = getResources();
    	int width   = getIntValue(res,  R.array.difficulty_width, which);
    	int height  = getIntValue(res,  R.array.difficulty_height, which);
    	int nbVoids = getIntValue(res,  R.array.difficulty_voids, which);
    	Difficulty d = new Difficulty(width, height, nbVoids);
    	mListener.onDialogSelect(d);
    }
    
    protected int getIntValue(Resources res, int id, int num) {
    	return res.getIntArray(id)[num];
    }
    
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    Builder builder = new Builder(getActivity());
	    builder.setTitle(R.string.pick_difficulty);
	    builder.setItems(R.array.difficulty_descriptions, this);
	    return builder.create();
	}

    
    public class Difficulty {

    	public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getNbVoids() {
			return nbVoids;
		}
		
    	protected int width;
    	protected int height;
    	protected int nbVoids;
    	
    	public Difficulty(int width, int height, int nbVoids) {
    		this.width = width;
    		this.height = height;
    		this.nbVoids = nbVoids;
    	}
    	
    	public String toString() { return width + "x" + height + ", voids : " + nbVoids; }
    	
    }
}
