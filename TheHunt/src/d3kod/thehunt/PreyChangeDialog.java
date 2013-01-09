package d3kod.thehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PreyChangeDialog extends DialogFragment {
	public interface PreyChangeDialogListener {
        public void onPreyChanged(int which);
    }
	
	PreyChangeDialogListener mListener;
	
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
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    String[] dummyItems = {"dummy prey", "default prey"};
	    builder.setTitle("Prey Change Dialog")
	           .setItems(dummyItems, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   mListener.onPreyChanged(which);
	           }
	    });
	    return builder.create();
	}
}
