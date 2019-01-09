

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

    /**
     *  @constant DeviceInfoTypeOfProductionModel     Product model
     *  @constant DeviceInfoTypeOfHardwareVersion     Hardware version
     *  @constant DeviceInfoTypeOfFirmwareVersion     Firmware version
     *  @constant DeviceInfoTypeOfProductionDate      Production Date
     *  @constant DeviceInfoTypeOfProductionMac       Mac
     *  @constant DeviceInfoTypeOfProductionClock     Clock
     */
    static getDeviceInfo(lockObj, cb) {
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.getDeviceInfo(JSON.stringify(lockObj), cb)
    }

    static getLockPasswordListKey(lockObj, cb) {
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.getLockPasswordListKey(JSON.stringify(lockObj), cb)
    }

    static getLockPasswordInfoKey(lockObj, cb) {
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.getLockPasswordInfoKey(JSON.stringify(lockObj), cb)
    }

    static getAdminKeyboardPwd(lockObj, cb) {
        TtLockModule.getAdminKeyboardPwd(JSON.stringify(lockObj), cb)
    }

    static addKeyboardPassword(keyboardPassword, passwordType, startDate, endDate, lockObj, cb){
        TtLock.startBTDeviceScan()
        if(Platform.OS === Platform_IOS) {
            return TtLockModule.addKeyboardPassword(keyboardPassword, startDate, endDate, JSON.stringify(lockObj), cb)
        }
        TtLockModule.addKeyboardPassword(keyboardPassword, passwordType, startDate, endDate, JSON.stringify(lockObj), cb)
    }

    /* KeyboardPsTypeOnce = 1,
    KeyboardPsTypePermanent = 2,
    KeyboardPsTypePeriod = 3,
    KeyboardPsTypeCycle = 4 */
    static recoverKeyboardPassword(passwordType, cycleType, keyboardPassword, startDate, endDate, lockObj, cb) {
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.recoverKeyboardPassword(passwordType, cycleType, keyboardPassword, startDate, endDate, JSON.stringify(lockObj), cb)
    }

    static modifyKeyboardPassword(passwordType, oldPass, newPass, startDate, endDate, lockObj, cb) {
        if(Platform.OS === Platform_IOS){
            TtLock.startBTDeviceScan()
        }
        TtLockModule.modifyKeyboardPassword(passwordType, oldPass, newPass, startDate, endDate, JSON.stringify(lockObj), cb)
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
