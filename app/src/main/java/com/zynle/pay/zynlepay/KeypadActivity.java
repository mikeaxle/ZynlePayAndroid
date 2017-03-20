package com.zynle.pay.zynlepay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeypadActivity extends Fragment {

    ImageButton addNote;


    public KeypadActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_keypad, container, false);

        //resize add note button
        /*addNote = (ImageButton) v.findViewById(R.id.addNoteImageButton);
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_add_note);
        Bitmap bpScaled = Bitmap.createScaledBitmap(bp, 100, 100, true);
        addNote.setImageBitmap(bpScaled);*/

        return v;
    }



}
