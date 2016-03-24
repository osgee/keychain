package com.superkeychain.keychain.action;

/**
 * Created by taofeng on 3/23/16.
 */
public interface ActionFinishedListener {
    public void doFinished(int status, String message, Object object);
}
