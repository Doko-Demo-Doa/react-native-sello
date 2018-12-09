using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace React.Native.Ttlock.Android.RNReactNativeTtlockAndroid
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNReactNativeTtlockAndroidModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNReactNativeTtlockAndroidModule"/>.
        /// </summary>
        internal RNReactNativeTtlockAndroidModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNReactNativeTtlockAndroid";
            }
        }
    }
}
