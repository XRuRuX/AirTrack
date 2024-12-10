package com.project.airtrack;

import com.project.airtrack.bluetooth.BluetoothManager;
import com.project.airtrack.bluetooth.DataMediator;
import com.project.airtrack.bluetooth.Mediator;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.DataParser;
import com.project.airtrack.data.DataProcessor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * HomeFragment handles the UI for the home screen of the app.
 */
public class HomeFragment extends Fragment implements OnDataReceivedListener {

    private BluetoothManager bluetoothManager;
    private TextView tvIndicatorValue;
    private Mediator mediator;
    private DataProcessor processor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = initializeLayout(inflater, container);
        processor = new DataParser();
        mediator = new DataMediator(processor, this);
        setupBluetoothManager();
        setupProgressBar(view);

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvIndicatorValue = view.findViewById(R.id.tv_indicator_value);
        return view;
    }

    // Configures and initializes BluetoothManager. Connects to the device if Bluetooth is available
    private void setupBluetoothManager() {
        bluetoothManager = new BluetoothManager(requireContext(), mediator);

        if (!bluetoothManager.isBluetoothAvailable() && tvIndicatorValue != null) {
            tvIndicatorValue.setText("ERR");
            return;
        }
        bluetoothManager.connectToDevice("AirTrack");
    }

    // Configures the progress bar UI element
    private void setupProgressBar(View view) {
        CircularSegmentedProgressBar progressBar = view.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setProgress(3);
        }
    }

    @Override
    public void onDataReceived(String data) {
        // Update the UI with data received via Bluetooth
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (tvIndicatorValue != null) {
                    tvIndicatorValue.setText(data);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Disconnect BluetoothManager when the fragment is destroyed
        if (bluetoothManager != null) {
            bluetoothManager.disconnect();
        }
    }
}
