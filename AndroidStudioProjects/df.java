package com.example.administrator.sunshine;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class DerailActivityFragment extends Fragment {

    public DerailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container);

        Bundle extras = getActivity().getIntent().getExtras();

        String value = extras.getString("weather");
        if (value != null)
            Toast.makeText(getActivity(), value, Toast.LENGTH_LONG).show();

        if (savedInstanceState!=null)
        {
            //TextView t = findViewById
            String text = savedInstanceState.getString("weather");
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();

        }
        return rootView;
    }
}
