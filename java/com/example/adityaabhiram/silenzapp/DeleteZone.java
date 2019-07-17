package com.example.adityaabhiram.silenzapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class DeleteZone extends AppCompatDialogFragment {
    private ImageView imageView;
    private DeleteZoneListener deleteZoneListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_zone_dialog, null);
        imageView = (ImageView) view.findViewById(R.id.delete_image);
        imageView.setImageResource(R.drawable.delete_loc);
        builder.setView(view)
                .setTitle("Delete Zone")
                .setMessage("Do you want to delete this zone??")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteZoneListener.deleteFlag(1);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            deleteZoneListener = (DeleteZoneListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DeleteZoneListener");
        }
    }

    public interface DeleteZoneListener {
        void deleteFlag(int flag);
    }
}
