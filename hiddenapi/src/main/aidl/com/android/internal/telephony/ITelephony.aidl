package com.android.internal.telephony;

interface ITelephony {
    long getAllowedNetworkTypesForReason(int subId, int reason);
    boolean setAllowedNetworkTypesForReason(int subId, int reason, long allowedNetworkTypes);
    long getAllowedNetworkTypes(int subId);
    boolean setAllowedNetworkTypes(int subId, long allowedNetworkTypes);
}