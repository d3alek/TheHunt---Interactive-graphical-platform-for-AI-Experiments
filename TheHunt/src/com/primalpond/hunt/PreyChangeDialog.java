package com.primalpond.hunt;

import com.primalpond.hunt.world.logic.TheHuntRenderer.PreyType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A dialog to select a Prey. 
 * 
 * @see PreyChangeDialog#onCreateDialog(Bundle)
 * @author Aleksandar Kodzhabashev (d3kod)
 *
 */
public class PreyChangeDialog extends DialogFragment {
	/**
	 * Activity that processes the value chosen in the dialog
	 * 
	 * @author Aleksandar Kodzhabashev (d3kod) 
	 *
	 */
	public interface PreyChangeDialogListener {
        /**
         * The function to call in the listening activity when the dialog returns. 
         * 
         * @param which the identifier of the prey. For possible values and their meaning see {@link PreyType}.
         * 
         * @see PreyType
         */
        public void onPreyChanged(int which);
    }
	
	PreyChangeDialogListener mListener;
	
	/** 
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PreyChangeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	/** 
	 * Make a dialog with a list of the possible {@link PreyType}s.
	 * 
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 * @see PreyType
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    PreyType[] possiblePreyValues = PreyType.values();
	    String[] dummyItems = new String[possiblePreyValues.length];//{"dummy prey", "default prey"};
	    
	    for (int i = 0; i < possiblePreyValues.length; ++i) {
	    	dummyItems[i] = possiblePreyValues[i].toString();
	    }
	    
	    builder.setTitle("Prey Change Dialog")
	           .setItems(dummyItems, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   mListener.onPreyChanged(which);
	           }
	    });
	    return builder.create();
	}
}
