package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/22/2015.
 */
public interface iReadMeterCallBack {
    void OnReadMeters(byte[] data_prm);
    void OnFilterSet(boolean status);
}
