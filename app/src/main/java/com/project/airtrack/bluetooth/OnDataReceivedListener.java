package com.project.airtrack.bluetooth;

/**
 * Functional interface that defines a callback method  to handle data received from
 * a Bluetooth device. Classes implementing this interface must define the behavior for
 * processing the received data.
 */
public interface OnDataReceivedListener {
    void onDataReceived(String data);
}
