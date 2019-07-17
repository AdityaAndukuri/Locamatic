package com.example.adityaabhiram.silenzapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class KeyInputDialog extends AppCompatDialogFragment {
    private EditText addname;
    private EditText addradius;
    private KeyInputDialogListener listener;
    ImageView imageView;
    Activity myActivity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.key_input_dialog_layout,null);
        imageView=(ImageView)view.findViewById(R.id.map_id);
        imageView.setImageResource(R.drawable.map);
        builder.setView(view)
                .setCancelable(false)
                .setTitle("Add Name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(TextUtils.isEmpty(addname.getText().toString())||TextUtils.isEmpty(addradius.getText().toString()))
                        {   Toast.makeText(getContext(),"Invalid inputs",Toast.LENGTH_SHORT).show();
                            KeyInputDialog keyInputDialog = new KeyInputDialog();
                            keyInputDialog.show(getFragmentManager(),"add zone name");
                        }
                        else {
                            String zone_name = addname.getText().toString();
                            try {
                                int radius = Integer.parseInt(addradius.getText().toString());
                                listener.applyText(zone_name, radius);
                            }
                            catch (NumberFormatException ne)
                            {   Toast.makeText(getContext(),"Invalid number format",Toast.LENGTH_SHORT).show();
                                KeyInputDialog keyInputDialog = new KeyInputDialog();
                                keyInputDialog.show(getFragmentManager(),"add zone name");
                            }

                        }
                    }
                });
        addname=(EditText)view.findViewById(R.id.zonename);
        addradius=(EditText)view.findViewById(R.id.radiustext);
        return builder.create();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener=(KeyInputDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement KeyInputDialogListener");
        }
    }

    public interface KeyInputDialogListener{
        void applyText(String zone_name,int radius);
    }
}
