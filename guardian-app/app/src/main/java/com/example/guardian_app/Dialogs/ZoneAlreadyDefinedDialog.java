package com.example.guardian_app.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ZoneAlreadyDefinedDialog extends AppCompatDialogFragment {

    private DefinedDialogListener dialogListener;

    private final String title = "Safe Zone already defined";
    private final String message = "Do you want to delete the previously defined zone and create a new one?";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.okButtonPressed();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.cancelButtonPressed();
            }
        });
        return builder.create();
    }

    public interface DefinedDialogListener {
        void okButtonPressed();
        void cancelButtonPressed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DefinedDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement Dialog Listener");
        }
    }
}
