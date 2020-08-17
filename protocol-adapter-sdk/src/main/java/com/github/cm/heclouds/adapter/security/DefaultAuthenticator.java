package com.github.cm.heclouds.adapter.security;

/**
 * {@link IAuthenticator}的默认实现
 */
public class DefaultAuthenticator implements IAuthenticator {

    @Override
    public boolean checkValid(String productId, String deviceName) {
        return true;
    }

}
