//
//  Copyright © Sello.
//

#import "TtLockModule.h"
#import "PTBLE.h"

typedef NS_ENUM(NSInteger,TTLockOption){
    TTLockOptionInitLock = 1,
    TTLockOptionUnlock,
    TTLockOptionSetTime,
    TTLockOptionGetRecordLog,
    TTLockOptionResetLock,
    TTLockOptionAddKeyboardPassword,
    TTLockOptionRecoverKeyboardPassword,
    TTLockOptionGetSystemLockKey,
    TTLockOptionGetLockPasswordListKey,
    TTLockOptionGetLockPasswordInfoKey,
    TTLockOptionGetAdminKeyboardPwd
};

@interface TtLockModule()
@property (strong) NSMutableArray *scanModelArray;
@end


@implementation TtLockModule
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initTTlockApi:(NSInteger )uid){
    [[PTBLE shareInstance] setUid:[NSString stringWithFormat:@"%ld",uid]];
}

- (NSArray<NSString *> *)supportedEvents
{
    
  return @[@"ScanBtDeviceEvent"];
}


RCT_EXPORT_METHOD(startBTDeviceScan)
{
    self.scanModelArray = @[].mutableCopy;
  [[PTBLE shareInstance] scan:^(BOOL success, id info) {
      
    if (success) {
        [self cacheScanModel:info];
        [self sendEventWithName:@"ScanBtDeviceEvent" body:[info yy_modelToJSONObject]];
    }
  }];
}

RCT_EXPORT_METHOD(stopBTDeviceScan){
    [[PTBLE shareInstance] stopScan];
    self.scanModelArray = nil;
}

RCT_EXPORT_METHOD(disconnect)
{
  [[PTBLE shareInstance] disconnect];
}

RCT_EXPORT_METHOD(lockInitialize:(NSString *)mac callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel new];
    lockModel.lockMac = mac;
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionInitLock callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionInitLock callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(unlockByUser:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionUnlock callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionUnlock callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(setLockTime:(double)time lockJsonString:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    NSDictionary *param = @{@"time":@(time)};
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:param option:TTLockOptionSetTime callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:param option:TTLockOptionSetTime callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(addKeyboardPassword:(NSString *)keyboardPassword startDate:(double)startDate endDate:(double)endDate lockJsonString:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    NSDictionary *param = @{@"keyboardPassword":keyboardPassword, @"startDate":@(startDate), @"endDate":@(endDate)};
    NSLog(@"aaa %@", param);
    
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:param option:TTLockOptionAddKeyboardPassword callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:param option:TTLockOptionAddKeyboardPassword callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(recoverKeyboardPassword:(int)passwordType cycleType:(int)cycleType keyboardPassword:(NSString *)keyboardPassword startDate:(double)startDate endDate:(double)endDate lockJsonString:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    NSDictionary *param = @{
                            @"passwordType":@(passwordType),
                            @"cycleType":@(cycleType),
                            @"keyboardPassword":keyboardPassword,
                            @"startDate":@(startDate),
                            @"endDate":@(endDate)
                        };
    
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:param option:TTLockOptionRecoverKeyboardPassword callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:param option:TTLockOptionRecoverKeyboardPassword callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(getDeviceInfo:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];

    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionGetSystemLockKey callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionGetSystemLockKey callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(getLockPasswordListKey:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionGetLockPasswordListKey callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionGetLockPasswordListKey callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(getLockPasswordInfoKey:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionGetLockPasswordInfoKey callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionGetLockPasswordInfoKey callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(getAdminKeyboardPwd:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionGetAdminKeyboardPwd callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionGetAdminKeyboardPwd callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(getOperateLog:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionGetRecordLog callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionGetRecordLog callBlock:callBlock];
    }
}

RCT_EXPORT_METHOD(resetLock:(NSString *)lockJsonString callBlock:(RCTResponseSenderBlock)callBlock)
{
    LockModel *lockModel = [LockModel yy_modelWithJSON:lockJsonString];
    if ([PTBLE shareInstance].isBLEConnected) {
        [self lock:lockModel param:nil option:TTLockOptionResetLock callBlock:callBlock];
    }else{
        [self connectLock:lockModel param:nil option:TTLockOptionResetLock callBlock:callBlock];
    }
}

#pragma mark - Private

- (void)connectLock:(LockModel *)lock param:(NSDictionary *)param option:(TTLockOption)option callBlock:(RCTResponseSenderBlock)callBlock{
    [[PTBLE shareInstance] connectMac:lock.lockMac completion:^(BOOL success, id info) {
        if (callBlock) {
            if (success) {
                [self lock:lock param:param option:option callBlock:callBlock];
            }else{
                callBlock(@[[self optionSuccess:success info:info]]);
            }
        }
    }];
}

- (void)lock:(LockModel *)lock param:(NSDictionary *)param option:(TTLockOption)option callBlock:(RCTResponseSenderBlock)callBlock{
    switch (option) {
        case TTLockOptionInitLock:
        {
            ScanModel *scanModel = nil;
            for (ScanModel *model in self.scanModelArray) {
                if ([model.lockMac isEqualToString:lock.lockMac]) {
                    scanModel = model;
                    break;
                }
            }
            [[PTBLE shareInstance] addLock:scanModel completion:^(BOOL success, id info) {
                id data = success ? @{@"lockDataJsonString":info ? info :@""} : info;
                callBlock(@[[self optionSuccess:success info:data]]);
            }];
        }
            break;
        case TTLockOptionUnlock:
        {
            [[PTBLE shareInstance] unlockKey:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
        case TTLockOptionSetTime:
        {
            [[PTBLE shareInstance] setLockTimeValue:[param[@"time"] doubleValue] key:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
        case TTLockOptionAddKeyboardPassword:
        {
            [[PTBLE shareInstance] addKeyboardPassword:param[@"keyboardPassword"] startDate:[param[@"startDate"] doubleValue] endDate:[param[@"endDate"] doubleValue] key:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionRecoverKeyboardPassword:
        {
            [[PTBLE shareInstance] recoverKeyboardPassword:param[@"keyboardPassword"] passwordType:[param[@"passwordType"] integerValue] cycleType:[param[@"cycleType"] integerValue] startDate:[param[@"startDate"] doubleValue] endDate:[param[@"endDate"] doubleValue] key:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionGetSystemLockKey:
        {
            [[PTBLE shareInstance] getLockSystemLockKey:lock.lockKey aesKey:lock.aesKeyStr completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionGetLockPasswordListKey:
        {
            [[PTBLE shareInstance] getLockPasswordListKey:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionGetLockPasswordInfoKey:
        {
            [[PTBLE shareInstance] getLockPasswordInfoKey:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionGetAdminKeyboardPwd:
        {
            [[PTBLE shareInstance] getAdminKeyboardPwd:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        case TTLockOptionGetRecordLog:
        {
            [[PTBLE shareInstance] getUnlockRecordKey:lock completion:^(BOOL success, id info) {
                id data = success ? @{@"lockOperateLog":info?:@""} : info;
                callBlock(@[[self optionSuccess:success info:data]]);
            }];
        }
            break;
        case TTLockOptionResetLock:
        {
            [[PTBLE shareInstance] resetLockKey:lock completion:^(BOOL success, id info) {
                callBlock(@[[self optionSuccess:success info:info]]);
            }];
        }
            break;
            
        default:
            break;
    }
}

- (NSDictionary *)optionSuccess:(BOOL)success info:(id)info{
    NSMutableDictionary *dict = @{}.mutableCopy;
    dict[@"success"] = @(success);
    if (success) {
        if (info) [dict setValuesForKeysWithDictionary:info];
    }else{
        if ([info isKindOfClass:[NSError class]]) {
            NSError *error = (NSError *)info;
            dict[@"errorCode"] = [NSString stringWithFormat:@"%ld",error.code];
            dict[@"errorMsg"] = error.userInfo[NSLocalizedDescriptionKey];
        }else if([info isKindOfClass:[NSString class]]){
            dict[@"errorMsg"] = info;
            dict[@"errorCode"] = @"100010";
        }
    }
    
    return dict;
}

- (void)cacheScanModel:(ScanModel *)scanModel{
    if (scanModel == nil) return;
    BOOL isContainScan = NO;
    ScanModel *containScanModel = nil;
    for (ScanModel *model in self.scanModelArray) {
        if ([model.lockMac isEqualToString:scanModel.lockMac]) {
            isContainScan = true;
            containScanModel = scanModel;
            break;
        }
    }
    if (isContainScan) {
        containScanModel.rssi = scanModel.rssi;
        containScanModel.isTouch = scanModel.isTouch;
        containScanModel.isSettingMode = scanModel.isSettingMode;
    }else{
        [self.scanModelArray addObject:scanModel];
    }
}

@end
