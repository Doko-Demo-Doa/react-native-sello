
# react-native-ttlock

## 自动下载组件

`$ npm install react-native-ttlock --save`

### 连接组件

`$ react-native link react-native-ttlock`


### 手动安装使用组件如下：

#### iOS

1.用XCode打开ios目录下的工程文件 将 node_modules/react-native-ttlock/ios/TtLockModule/TtLockModule/下的TTLock.framework 拖拽到XCode目录下的 ‘Libraries’文件中
2. 在XCode中选中当前工程.再选中'General' ➜ 'Embedded Binaries'➜添加'TTLock.framework
3. XCode中选中'info' ➜‘Custom iOS Target Projectes’ ➜添加键'Privacy - Bluetooth Peripheral Usage Description' 添加值'你想要的描述 如（蓝牙与锁通信必须需要您的授权）'

#### Android

1. 在项目的Manifest中添加以下内容：
  - 必要的权限
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  - <!-- 注册蓝牙服务 -->
    <service android:name="com.ttlock.bl.sdk.service.BluetoothLeService" />
		
2. 添加以下内容到 `android/settings.gradle`中:
  	```
  	include ':react-native-ttlock'
  	project(':react-native-ttlock').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-ttlock/android')
  	```
3. 添加依赖到 `android/app/build.gradle`:
  	```
      compile project(':react-native-ttlock')
}



## 使用以及接口说明
目前1.0.0版本有以下接口：

###
initTTlockApi(uid):初始化sdk，使用前必须先完成初始化。uid为服务器返回当前用户的uid，类型Number

###
addReceiveScanDeviceListener(cb)：注册获取蓝牙扫描设备回调的监听，开启回调之前需注册该监听，才能获取到周围的蓝牙设备

###
removeReceiveScanDeviceListener():移除扫描蓝牙设备回调监听

###
startBTDeviceScan():启动蓝牙扫描，获取周围的蓝牙设备

###
lockInitialize(lockMac,cb)：初始化蓝牙锁

###
unlockByUser(keyObj,cb):蓝牙钥匙开锁

###
getOperateLog(keyObj,cb)：获取锁中的操作记录

###
setLockTime(timestamp,lockObj,cb)：校准锁时间

###
resetLock(lockObj,cb)：重置锁

