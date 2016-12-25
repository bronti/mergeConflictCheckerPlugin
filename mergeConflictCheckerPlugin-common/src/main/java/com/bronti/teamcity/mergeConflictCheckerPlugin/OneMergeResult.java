package com.bronti.teamcity.mergeConflictCheckerPlugin;

/**
 * Created by bronti on 25.12.16.
 */

class OneMergeResult {
    public final String branch;
    public final boolean isSuccessful;
    public final boolean exists;
    public final String state;

    OneMergeResult(String branch, boolean isSuccessful, String state) {
        this.branch = branch;
        this.isSuccessful = isSuccessful;
        this.exists = true;
        this.state = state;
    }

    OneMergeResult(String branch) {
        this.branch = branch;
        this.isSuccessful = false;
        this.exists = false;
        this.state = "";
    }
}