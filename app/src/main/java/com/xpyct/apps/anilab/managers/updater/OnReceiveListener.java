package com.xpyct.apps.anilab.managers.updater;

public interface OnReceiveListener {

    /**
     * @param status response code from HTTP request
     * @param result response data from HTTP request
     * @return return true to show default library dialog
     */
    boolean onReceive(int status, String result);
}