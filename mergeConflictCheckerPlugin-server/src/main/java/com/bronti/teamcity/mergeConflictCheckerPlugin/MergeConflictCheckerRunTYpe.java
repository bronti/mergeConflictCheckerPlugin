package com.bronti.teamcity.mergeConflictCheckerPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
//import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

import org.jetbrains.annotations.NotNull;

//import ee.elinyo.teamcity.plugins.ansible.common.AnsibleRunConfig;


/**
 * Created by bronti on 15.11.16.
 */

public class MergeConflictCheckerRunTYpe extends RunType {

    private PluginDescriptor pluginDescriptor;

    public MergeConflictCheckerRunTYpe(@NotNull final RunTypeRegistry reg,
                                       @NotNull final PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
        reg.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return MergeConflictCheckerConstants.RUN_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Merge Conflict Checker";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Checks for merge conflicts.";
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("editMergeConflictCheckerRun.jsp");
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("viewMergeConflictCheckerRun.jsp");
    }

    @NotNull
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        Map<String, String> defaults = new HashMap<String, String>();
        defaults.put(MergeConflictCheckerConstants.MY_OPTION_KEY, MergeConflictCheckerMyOption.SECOND.getValue());
//        defaults.put(MergeConflictCheckerConstants.BRANCHES, "");
        return defaults;
    }

    @NotNull
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(final Map<String, String> properties) {
                List<InvalidProperty> errors = new ArrayList<InvalidProperty>();
                return errors;
            }
        };
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        String value = params.get(MergeConflictCheckerConstants.MY_OPTION_KEY);
        String result = value == null ? "something went wrong (my option is null)\n" : "my option: " + value + "\n";
        String branches = params.get(MergeConflictCheckerConstants.BRANCHES);
        result += "branches: " + (branches == null ? "null" : branches) + "\n";
        return result;
    }


}
