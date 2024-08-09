package com.project.airtrack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * TemperatureFragment is responsible for displaying detailed information about
 * temperature and humidity
 * Additional functionality for showing temperature and humidity data will be added in the future.
 */
public class TemperatureFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }
}