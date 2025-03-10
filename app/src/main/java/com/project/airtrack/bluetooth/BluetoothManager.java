package com.project.airtrack.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages Bluetooth connectivity, including device pairing, connection, and data transfer.
 * Handles interactions with Bluetooth APIs and communicates data to a Mediator.
 */
public class BluetoothManager {
    private static final int PACKET_LENGTH = 16;
    private Mediator mediator;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private boolean isRunning;
    private Context context;
    private ScheduledExecutorService scheduler;
    private String deviceName;

    public BluetoothManager(Context context, Mediator mediator) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.isRunning = false;
        this.context = context;
        this.mediator = mediator;
    }

    // Checks if Bluetooth is available and enabled on the device
    public boolean isBluetoothAvailable() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    // Searches for a paired Bluetooth device by name
    private BluetoothDevice findPairedDevice(String deviceName) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Checks Bluetooth permissions for Android 12+ (API 31+)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BluetoothManager", "BLUETOOTH_CONNECT permission not granted");
                return null;
            }
        } else {
            // Checks Bluetooth permissions for Android 7-11 (API 24-30)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BluetoothManager", "BLUETOOTH permission not granted");
                return null;
            }
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            Log.e("BluetoothManager", "No paired devices found");
            return null;
        }

        for (BluetoothDevice device : pairedDevices) {
            Log.d("BluetoothManager", "Paired device: " + device.getName());
            if (device.getName().equals(deviceName)) {
                return device;
            }
        }

        Log.e("BluetoothManager", "Device not found: " + deviceName);
        return null;
    }

    // Logs an error message and notifies the mediator about the error
    private void notifyError(String errorMessage) {
        Log.e("BluetoothManager", errorMessage);

        byte[] errorByte = new byte[]{(byte) 0xFF}; // Temporary error code
        mediator.handleData(errorByte);
    }

    // Establishes a connection to the specified Bluetooth device
    private void connectToSocket(BluetoothDevice device) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Checks Bluetooth permissions for Android 12+ (API 31+)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                notifyError("BLUETOOTH_CONNECT permission not granted");
                return;
            }
        } else {
            // Checks Bluetooth permissions for Android 7-11 (API 24-30)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {
                notifyError("BLUETOOTH permission not granted");
                return;
            }
        }

        // If the connection to the socket succeeds then the scheduler is shutdown
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID standard for Bluetooth Classic
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            if((socket != null) && (socket.isConnected()))
            {
                scheduler.shutdownNow();
                inputStream = socket.getInputStream();
                isRunning = true;
                Log.d("BluetoothManager", "Connected to: " + device.getName());
                listenForData();
            }
        } catch (IOException e) {
            Log.e("BluetoothManager", "Error while connecting to device: " + e.getMessage(), e);
        }
    }

    // Attempts to connect to a specified device by repeatedly calling the connect function every 10 seconds using a scheduler
    public void tryToConnectToDevice(String deviceName)
    {
        this.deviceName = deviceName;
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(() -> connectToDevice(deviceName), 0, 10, TimeUnit.SECONDS);
    }

    // Starts a new thread to connect to a device and listen for incoming data
    private void connectToDevice(String deviceName) {
        new Thread(() -> {
            try {
                BluetoothDevice device = findPairedDevice(deviceName);
                if (device != null) {
                    connectToSocket(device);
                } else {
                    notifyError("Device not paired: " + deviceName);
                }
            } catch (Exception e) {
                notifyError("Connection error: " + e.getMessage());
            }
        }).start();
    }

    // Continuously reads data from the Bluetooth connection in a loop
    private void listenForData() {
        ByteArrayOutputStream packetBuffer = new ByteArrayOutputStream();   // Buffer for building the package
        int bytesRead;

        try {
            byte[] buffer = new byte[512]; // Temporary buffer for reading from the stream

            while (isRunning && (bytesRead = inputStream.read(buffer)) != -1) {
                // Add the read data to the packet buffer
                packetBuffer.write(buffer, 0, bytesRead);

                // Check if we have enough data for a full packet
                while (packetBuffer.size() >= PACKET_LENGTH) {
                    // Extract a complete packet from the buffer
                    byte[] completePacket = Arrays.copyOfRange(packetBuffer.toByteArray(), 0, PACKET_LENGTH);

                    // Process the complete packet
                    mediator.handleData(completePacket);

                    // Displays the packet in hexadecimal
                    StringBuilder hexData = new StringBuilder();
                    for (byte b : completePacket) {
                        hexData.append(String.format("%02x ", b));
                    }
                    Log.d("BluetoothManager", "Packet in hex: " + hexData.toString().trim());

                    // Remove the processed packet from the buffer
                    byte[] remainingData = Arrays.copyOfRange(packetBuffer.toByteArray(), PACKET_LENGTH, packetBuffer.size());
                    packetBuffer.reset();
                    packetBuffer.write(remainingData);
                }
            }
        } catch (IOException e) {
            // If the scheduler is shut down and the device is disconnected, then turn the scheduler on
            if(scheduler.isShutdown())
            {
                tryToConnectToDevice(deviceName);
                Log.e("BluetoothManager", "Bluetooth device disconnected.");
            }
            notifyError("Data read error: " + e.getMessage());
        }
    }

    // Disconnects from the Bluetooth device and releases resources
    public void disconnect() {
        isRunning = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception ignored) {}
    }
}