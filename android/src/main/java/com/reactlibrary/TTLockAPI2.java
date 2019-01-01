package com.reactlibrary;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.command.CommandUtil;
import com.ttlock.bl.sdk.constant.APICommand;
import com.ttlock.bl.sdk.entity.TransferData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.util.DigitUtil;

public class TTLockAPI2 extends TTLockAPI {

  public TTLockAPI2(Context context, @NonNull TTLockCallback ttLockCallback) {
    super(context, ttLockCallback);
  }

  @Override
  public void addPeriodKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
    byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
    /**
     *   public static final int OP_ADD_ONCE_KEYBOARD_PASSWORD = 20;
     *   public static final int OP_ADD_PERMANENT_KEYBOARD_PASSWORD = 21;
     *   public static final int OP_ADD_PERIOD_KEYBOARD_PASSWORD = 22;
     *   public static final int OP_MODIFY_KEYBOARD_PASSWORD = 23;
     */
    // 20 or start+end = period || 22 = permanent || 21 = once
    TransferData transferData = new TransferData();
    transferData.setAPICommand(20);
    transferData.setmUid(uid);
    transferData.setLockVersion(lockVersion);
    transferData.setAdminPs(adminPs);
    transferData.setStartDate(startDate);
    transferData.setEndDate(endDate);
    transferData.setUnlockKey(unlockKey);
    transferData.setKeyboardPwdType((byte) 1);
    transferData.setLockFlagPos(lockFlagPos);
    transferData.setOriginalPwd(password);
    transferData.setTimezoneOffSet(timezoneOffset);
    TransferData.setAesKeyArray(aesKeyArray);
    CommandUtil.A_checkAdmin(transferData);
  }

  @Override
  public void modifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
    super.modifyKeyboardPassword(extendedBluetoothDevice, uid, lockVersion, adminPs, unlockKey, lockFlagPos, keyboardPwdType, originalPwd, newPwd, startDate, endDate, aesKeyStr, timezoneOffset);
    byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
    TransferData transferData = new TransferData();
    transferData.setAPICommand(23);
    transferData.setmUid(uid);
    transferData.setLockVersion(lockVersion);
    transferData.setAdminPs(adminPs);
    transferData.setUnlockKey(unlockKey);
    transferData.setLockFlagPos(lockFlagPos);
    transferData.setStartDate(startDate);
    transferData.setEndDate(endDate);
    transferData.setOriginalPwd(originalPwd);
    transferData.setNewPwd(newPwd);
    TransferData.setAesKeyArray(aesKeyArray);
    transferData.setTimezoneOffSet(timezoneOffset);
    transferData.setKeyboardPwdType((byte) 1);
    CommandUtil.A_checkAdmin(transferData);
  }
}
