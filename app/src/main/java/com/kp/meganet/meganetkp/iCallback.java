package com.kp.meganet.meganetkp;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 11/22/2015.
 */
public interface iCallback {

    void SetReadData(Map<String, QryParams> data_prm);
    void ReadData(byte[] dataArr_prm);
    void GetTime(byte[] dataArr_prm);
    boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly);
    void OnParameters(String deviceName_prm, List<QryParams> parameters);
    void OnRead(String deviceName_prm, String ndevice_pam);
    void OnProgramm(boolean result_prm, String err_prm);
    void OnPowerOff(boolean result_prm, String err_prm);
    void OnSleep(boolean result_prm, String err_prm);
    void OnErrorCb(String error_prm);
    void OnMessageCb(String message_prm);
}

