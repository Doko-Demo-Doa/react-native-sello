
package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.DeviceInfo;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.entity.LockData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.lang.reflect.Type;
import java.util.HashMap;


public class TtLockModule extends ReactContextBaseJavaModule implements TTLockCallback{

    private final ReactApplicationContext reactContext;
    private static TTLockAPI mLockApi;
    private Callback mLockOperationCallback;
    private static HashMap<String,ExtendedBluetoothDevice> mCachedDevice = new HashMap<>();
    private static int mLockAction = -1;
    private static LockData mSelectLock;
    private static int mUid;

    private final static int ACTION_INIT_LOCK = 0x11;
    private final static int ACTION_DELETE_LOCK = 0x11 << 1;
    private final static int ACTION_CLICK_UNLOCK = 0x11 << 2;
    private final static int ACTION_TOUCH_UNLOCK = 0x11 << 3;
    private final static int ACTION_RESET_LOCK = 0x11 << 4;
    private final static int ACTION_GET_LOG = 0x11 << 5;
    private final static int ACTION_SET_TIME = 0x11 << 6;

    private final static String RECEIVER_BT_SCAN_DEVICE_EVENT = "ScanBtDeviceEvent";

    private final static String ERROR_CODE_TIME_OUT = "100010";
    private long mLockSetTimeStamp;

    public TtLockModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "TtLockModule";
    }

    @ReactMethod
    public void initTTlockApi(int uid){
        mUid = uid;
        mLockApi = new TTLockAPI(reactContext.getApplicationContext(),this);
    }

    @ReactMethod
    public void startBleService(){
        mLockApi.startBleService(getCurrentActivity());
    }

    @ReactMethod
    public void stopBleService(){
        mLockApi.stopBleService(getCurrentActivity());
    }

    @ReactMethod
    public void startBTDeviceScan(){
        mCachedDevice.clear();
        mLockApi.startBTDeviceScan();
    }

    @ReactMethod
    public void stopBTDeviceScan(){
        mLockApi.stopBTDeviceScan();
    }

    @ReactMethod
    public void setLockTime(double timestamp,String keyJson,Callback callback){
        mLockSetTimeStamp = (long) timestamp;
        mLockAction = ACTION_SET_TIME;
        mLockOperationCallback = callback;
        saveCurrentKeyObj(keyJson);
        mLockApi.connect(mSelectLock.getLockMac());
    }

    @ReactMethod
    public void lockInitialize(String lockMac,Callback callback){
        mLockAction = ACTION_INIT_LOCK;
        mLockOperationCallback = callback;
        ExtendedBluetoothDevice selectBtDevice = mCachedDevice.get(lockMac);
        if(selectBtDevice != null){
            mLockApi.connect(selectBtDevice);
        }
    }

    private void saveCurrentKeyObj(String keyJson){
        Type lockDataType = new TypeToken<LockData>(){}.getType();
        mSelectLock = new Gson().fromJson(keyJson,lockDataType);
    }

    @ReactMethod
    public void resetLock(String keyJson,Callback callback){
        mLockAction = ACTION_RESET_LOCK;
        mLockOperationCallback = callback;
        saveCurrentKeyObj(keyJson);
        mLockApi.connect(mSelectLock.getLockMac());
    }

    @ReactMethod
    public void unlockByUser(String keyJson,Callback callback){
        mLockAction = ACTION_CLICK_UNLOCK;
        mLockOperationCallback = callback;
        saveCurrentKeyObj(keyJson);
        mLockApi.connect(mSelectLock.getLockMac());
    }

    @ReactMethod
    public void getOperateLog(String keyJson,Callback callback){
        mLockAction = ACTION_GET_LOG;
        mLockOperationCallback = callback;
        saveCurrentKeyObj(keyJson);
        mLockApi.connect(mSelectLock.getLockMac());
    }

    @Override
    public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
        String name = extendedBluetoothDevice.getName();
        ExtendedBluetoothDevice needAddDevice = cacheAndFilterScanDevice(extendedBluetoothDevice);
        if(needAddDevice != null){
            WritableMap map = Arguments.createMap();
            map.putString("lockName",name);
            map.putString("lockMac",extendedBluetoothDevice.getAddress());
            map.putBoolean("isSettingMode",extendedBluetoothDevice.isSettingMode());
            map.putInt("rssi",extendedBluetoothDevice.getRssi());
            map.putBoolean("isTouch",extendedBluetoothDevice.isTouch());
            getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(RECEIVER_BT_SCAN_DEVICE_EVENT,map);
        }
    }

    private ExtendedBluetoothDevice cacheAndFilterScanDevice(ExtendedBluetoothDevice btDevice){
        ExtendedBluetoothDevice newAddDevice = btDevice;
        String lockMac = btDevice.getAddress();
        if(mCachedDevice.isEmpty()){
            mCachedDevice.put(lockMac,btDevice);
        }else {
            ExtendedBluetoothDevice child = mCachedDevice.get(lockMac);
            if(child == null){
                mCachedDevice.put(lockMac,btDevice);
            }else {
                if(newAddDevice.isSettingMode() != child.isSettingMode()){
                    mCachedDevice.remove(lockMac);
                    mCachedDevice.put(lockMac,btDevice);
                }else {
                    newAddDevice = null;
                }
            }
        }

        return newAddDevice;
    }

    @Override
    public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        if(mLockAction != -1){
            switch (mLockAction){
                case ACTION_INIT_LOCK:
                    mLockApi.lockInitialize(extendedBluetoothDevice);
                    break;
                case ACTION_DELETE_LOCK:
                    break;
                case ACTION_CLICK_UNLOCK:
                case ACTION_TOUCH_UNLOCK:
                    mLockApi.unlockByUser(extendedBluetoothDevice,mUid,mSelectLock.getLockVersion(),0,0,mSelectLock.getLockKey(),mSelectLock.getLockFlagPos(),mSelectLock.getAesKeyStr(),mSelectLock.getTimezoneRawOffset());
                    break;
                case ACTION_RESET_LOCK:
                    mLockApi.resetLock(extendedBluetoothDevice,mUid,mSelectLock.getLockVersion(),mSelectLock.getAdminPwd(),mSelectLock.getLockKey(),mSelectLock.getLockFlagPos(),mSelectLock.getAesKeyStr());
                    break;
                case ACTION_GET_LOG:
                    mLockApi.getOperateLog(extendedBluetoothDevice,mSelectLock.getLockVersion(),mSelectLock.getAesKeyStr(),mSelectLock.getTimezoneRawOffset());
                    break;
                case ACTION_SET_TIME:
                    mLockApi.setLockTime(extendedBluetoothDevice,mUid,mSelectLock.getLockVersion(),mSelectLock.getLockKey(),mLockSetTimeStamp,mSelectLock.getLockFlagPos(),mSelectLock.getAesKeyStr(),mSelectLock.getTimezoneRawOffset());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        if (mLockOperationCallback != null) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success",false);
            map.putString("errorCode",ERROR_CODE_TIME_OUT);
            if(mLockAction != -1){
                mLockOperationCallback.invoke(map);
            }
            mLockOperationCallback = null;
        }
    }

    @Override
    public void onLockInitialize(ExtendedBluetoothDevice extendedBluetoothDevice, LockData lockData, Error error) {
        if(mLockOperationCallback != null){
            WritableMap map = Arguments.createMap();
            map.putString("lockDataJsonString",new Gson().toJson(lockData));
            map.putBoolean("success",error == Error.SUCCESS);
            map.putString("errorCode",error.getErrorCode());
            mLockOperationCallback.invoke(map);
            mLockOperationCallback = null;
        }
    }

    @Override
    public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
        if(mLockOperationCallback != null){
            WritableMap map = Arguments.createMap();
            map.putBoolean("success",error == Error.SUCCESS);
            map.putString("errorCode",error.getErrorCode());
            mLockOperationCallback.invoke(map);
            mLockOperationCallback = null;
        }
    }

    @Override
    public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, Error error) {
        if(mLockOperationCallback != null){
            WritableMap map = Arguments.createMap();
            map.putBoolean("success",error == Error.SUCCESS);
            map.putString("errorCode",error.getErrorCode());
            mLockOperationCallback.invoke(map);
            mLockOperationCallback = null;
        }
    }

    @Override
    public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
        if(mLockOperationCallback != null){
            WritableMap map = Arguments.createMap();
            map.putBoolean("success",error == Error.SUCCESS);
            map.putString("errorCode",error.getErrorCode());
            mLockOperationCallback.invoke(map);
            mLockOperationCallback = null;
            mLockApi.getLockTime(extendedBluetoothDevice,mSelectLock.getLockVersion(),mSelectLock.getAesKeyStr(),mSelectLock.getTimezoneRawOffset());
        }
    }

    @Override
    public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String lockLog, Error error) {
        if(mLockOperationCallback != null){
            WritableMap map = Arguments.createMap();
            map.putString("lockOperateLog",lockLog);
            map.putBoolean("success",error == Error.SUCCESS);
            map.putString("errorCode",error.getErrorCode());
            mLockOperationCallback.invoke(map);
            mLockOperationCallback = null;
        }
    }

    @Override
    public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

    }

    @Override
    public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

    }

    @Override
    public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

    }

    @Override
    public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long timestamp, Error error) {

    }

    @Override
    public void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String s, long l, Error error) {

    }

    @Override
    public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }


    @Override
    public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, long l, long l1, Error error) {

    }

    @Override
    public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, String s1, Error error) {

    }

    @Override
    public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {

    }

    @Override
    public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

    }

    @Override
    public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

    }

    @Override
    public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, int i4, Error error) {

    }

    @Override
    public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, Error error) {

    }

    @Override
    public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2, Error error) {

    }

    @Override
    public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {

    }

    @Override
    public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onSetWristbandKeyToDev(Error error) {

    }

    @Override
    public void onSetWristbandKeyRssi(Error error) {

    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, Error error) {

    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, long l, int i2, Error error) {

    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

    }

    @Override
    public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, long l1, long l2, Error error) {

    }

    @Override
    public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, long l, Error error) {

    }

    @Override
    public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, Error error) {

    }

    @Override
    public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

    }

    @Override
    public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, DeviceInfo deviceInfo, Error error) {

    }

    @Override
    public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

    }

    @Override
    public void onGetLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

    }

    @Override
    public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, long l, Error error) {

    }

    @Override
    public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

    }

    @Override
    public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onSearchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {

    }

    @Override
    public void onSearchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, Error error) {

    }

    @Override
    public void onSearchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, String s, Error error) {

    }

    @Override
    public void onSearchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int i, String s, long l, Error error) {

    }

    @Override
    public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, int i3, Error error) {

    }

    @Override
    public void onGetElectricQuantity(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }

    @Override
    public void onOperateAudioSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

    }

    @Override
    public void onOperateRemoteControl(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

    }

    @Override
    public void onOperateDoorSensorLocking(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, int i2, Error error) {

    }

    @Override
    public void onGetDoorSensorState(ExtendedBluetoothDevice extendedBluetoothDevice, int i, int i1, Error error) {

    }

    @Override
    public void onSetNBServer(ExtendedBluetoothDevice extendedBluetoothDevice, int i, Error error) {

    }
}