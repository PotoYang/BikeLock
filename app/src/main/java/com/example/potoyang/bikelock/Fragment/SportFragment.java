package com.example.potoyang.bikelock.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.potoyang.bikelock.MyView.SportView;
import com.example.potoyang.bikelock.R;

public class SportFragment extends Fragment {

    private SportView cc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_sport, null, false);

        cc = (SportView) view.findViewById(R.id.cc);

        cc.setCurrentCount(20, 8);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
