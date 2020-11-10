package com.kp.meganet.meganetkp;

/**
 * Created by alex on 09-Jun-16.
 */
public interface iPulseCallback {
    void OnRead(String serial_num, String reading);
    void OnErrorRead(String error);
    void OnWriteResult(boolean result);
    boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly);
    void OnMessageCb(String message_prm);
}
