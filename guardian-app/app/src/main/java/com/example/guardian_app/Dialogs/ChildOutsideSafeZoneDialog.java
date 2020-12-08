package com.example.guardian_app.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ChildOutsideSafeZoneDialog extends AppCompatDialogFragment {

    private OutsideDialogListener dialogListener;

    private float _distanceOutsideSafeZone;

    public ChildOutsideSafeZoneDialog(float distance) {
        _distanceOutsideSafeZone = distance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Your child is outside of Safe Zone!").
                setMessage("Your child is" + _distanceOutsideSafeZone + " meters away from Safe Zone")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.okButtonPressed();
                    }
                });
        return builder.create();
    }

    public interface OutsideDialogListener {
        void okButtonPressed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (OutsideDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement Dialog Listener");
        }
    }
}
