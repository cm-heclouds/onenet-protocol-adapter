package com.github.cm.heclouds.adapter.security;

/**
 * {@link IAuthorizatorPolicy}的默认实现
 */
public class PermitAllAuthorizatorPolicy implements IAuthorizatorPolicy {

    @Override
    public boolean canWrite(String productId, String deviceName) {
        return true;
    }

    @Override
    public boolean canRead(String productId, String deviceName) {
        return true;
    }

}
