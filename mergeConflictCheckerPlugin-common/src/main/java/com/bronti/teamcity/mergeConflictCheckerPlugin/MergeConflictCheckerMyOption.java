package com.bronti.teamcity.mergeConflictCheckerPlugin;

/**
 * Created by bronti on 15.11.16.
 */

public enum MergeConflictCheckerMyOption {

    FIRST, SECOND;

    public String getValue() {
        return this.name();
    }

}
