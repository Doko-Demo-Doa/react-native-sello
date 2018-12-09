

import {
    NativeModules ,
    DeviceEventEmitter,
    NativeEventEmitter,
    Platform } from 'react-native';

const TtLockModule  = NativeModules.TtLockModule;
const TtLockIOSEmitter = new NativeEventEmitter(TtLockModule);

let listener = {}
const receiverBtDeviceScanEvent = 'ScanBtDeviceEvent'
const Platform_Android = "android"
const Platform_IOS = "ios"

// export default TtLockModule;
export default class TtLock {

    static initTTlockApi(uid){
        TtLockModule.initTTlockApi(uid)
        if(Platform.OS === Platform_Android){
            TtLock.startBleService()
        }
    }

    /**
     * Android Only
     */
    static startBleService(){
        TtLockModule.startBleService()
    }

    /**
     * Android Only
     */
    static stopBleService(){
        TtLockModule.stopBleService()
    }

    static startBTDeviceScan(){
        TtLockModule.startBTDeviceScan()
    }

    static stopBTDeviceScan(){
        TtLockModule.stopBTDeviceScan()
    }

    static lockInitialize(lockMac,cb){
        TtLockModule.lockInitialize(lockMac,cb)
    }

    static setLockTime(timestamp,lockObj,cb){
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.setLockTime(timestamp,JSON.stringify(lockObj),cb)
    }

    static resetLock(lockObj,cb){
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.resetLock(JSON.stringify(lockObj),cb)
    }

    static unlockByUser(keyObj,cb){
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.unlockByUser(JSON.stringify(keyObj),cb)
    }

    static getOperateLog(keyObj,cb){
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.getOperateLog(JSON.stringify(keyObj),cb)
    }

    static setLockTime(timestamp,lockObj,cb){
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.setLockTime(timestamp,JSON.stringify(lockObj),cb)
    }

    static addReceiveScanDeviceListener(cb){
        if(Platform.OS === Platform_IOS){
            listener = TtLockIOSEmitter.addListener(receiverBtDeviceScanEvent,lockItemMap => {
                    cb(lockItemMap)
                }
            )
        }else{
            listener = DeviceEventEmitter.addListener(receiverBtDeviceScanEvent,lockItemMap => {
                cb(lockItemMap)
            })
        }
    }

    static removeReceiveScanDeviceListener(){
        if(listener != null){
            listener.remove
        }
        listener = null;
    }
}
