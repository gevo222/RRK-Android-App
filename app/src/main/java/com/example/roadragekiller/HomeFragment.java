package com.example.roadragekiller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Button countBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Button startButton = findViewById(R.id.button2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {

                TextView test = findViewById(R.id.userSpeed);


                test.setVisibility(View.VISIBLE);           // speed pops up
                startButton.setVisibility(View.INVISIBLE);  // button goes away

                onLocationChanged(null);                    // calls the current speed tracker
            }
        });
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        countBtn = (Button) view.findViewById(R.id.startButton3);
        countBtn.setVisibility(View.VISIBLE);
        return view;
    }


}

