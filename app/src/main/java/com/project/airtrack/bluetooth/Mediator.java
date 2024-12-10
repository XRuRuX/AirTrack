package com.project.airtrack.bluetooth;

/**
 * The Mediator interface is used manage communication between components.
 */
public interface Mediator {
    void handleData(byte[] data);
}
