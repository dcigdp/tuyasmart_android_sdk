package com.tuya.smart.android.demo.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.tuya.smart.android.demo.bean.SwitchBean;
import com.tuya.smart.android.demo.view.ISwitchView;
import com.tuya.smart.android.hardware.model.IControlCallback;
import com.tuya.smart.android.mvp.presenter.BasePresenter;
import com.tuya.smart.sdk.TuyaDevice;
import com.tuya.smart.sdk.TuyaUser;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.HashMap;


/**
 * Created by letian on 16/7/21.
 */
public class SwitchPresenter extends BasePresenter implements IDevListener {
    private final ISwitchView mView;
    private final Context mContext;
    private final ITuyaDevice mDevice;
    private SwitchBean mSwitchBean;

    public SwitchPresenter(Context context, ISwitchView view) {
        mView = view;
        mContext = context;
        mDevice = new TuyaDevice("");
        mDevice.registerDevListener(this);
        DeviceBean deviceBean = TuyaUser.getDeviceInstance().getDev("gwId");
        if (deviceBean != null) {
            boolean open = (boolean) TuyaUser.getDeviceInstance().getDp(deviceBean.getDevId(), SwitchBean.SWITCH_DPID);
            mSwitchBean = new SwitchBean(open);
        }
    }

    public void onClickSwitch() {
        mSwitchBean.setOpen(!mSwitchBean.isOpen());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SwitchBean.SWITCH_DPID, mSwitchBean.isOpen());
        mDevice.publishDps(JSONObject.toJSONString(hashMap), new IControlCallback() {
            @Override
            public void onError(String s, String s1) {
                mView.showErrorTip();
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    @Override
    public void onDpUpdate(String devId, String dps) {
        JSONObject jsonObject = JSONObject.parseObject(dps);
        Boolean open = (Boolean) jsonObject.get(SwitchBean.SWITCH_DPID);
        if (open) mView.showOpenView();
        else mView.showCloseView();
    }

    @Override
    public void onRemoved(String devId) {
        mView.showRemoveTip();
    }

    @Override
    public void onStatusChanged(String devId, boolean status) {
        mView.statusChangedTip(status);
    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {
        mView.changeNetworkErrorTip(status);
    }

    @Override
    public void onDevInfoUpdate(String devId) {
        mView.devInfoUpdateView();
    }
}