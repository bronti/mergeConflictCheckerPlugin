package com.bronti.teamcity.mergeConflictCheckerPlugin;

/**
 * Created by bronti on 15.11.16.
 */
//import ee.elinyo.teamcity.plugins.ansible.common.AnsibleCommand;

import java.util.Arrays;
import java.util.Collection;

public class MergeConflictCheckerRunConfigBean {

    public String getMyOption() {
        return MergeConflictCheckerConstants.MY_OPTION_KEY;
    }

    public Collection<MergeConflictCheckerMyOption> getMyOptionValues() {
        return Arrays.asList(MergeConflictCheckerMyOption.values());
    }

    public String getFirstMyValue() {
        return MergeConflictCheckerMyOption.FIRST.getValue();
    }

    public String getSecondMyValue() {
        return MergeConflictCheckerMyOption.SECOND.getValue();
    }

}
