package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by bronti on 24.12.16.
 */
public class MergeConflictCheckerRunResultsTab extends ViewLogTab {

    public MergeConflictCheckerRunResultsTab(@NotNull PagePlaces pagePlaces,
                                             @NotNull SBuildServer server,
                                             @NotNull PluginDescriptor descriptor) {
        super("", "", pagePlaces, server);
        setTabTitle(getTitle());
        setIncludeUrl(descriptor.getPluginResourcesPath("buildResultsTab.jsp"));
        setPluginName(getClass().getSimpleName());
    }

    private String getTitle() {
        return "Merge Conflict Checker Log";
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model,
                             @NotNull HttpServletRequest request,
                             @NotNull SBuild build) {
    }

    @Override
    protected boolean isAvailable(@NotNull final HttpServletRequest request, @NotNull final SBuild build) {
        return build.getBuildType().getRunnerTypes().contains(MergeConflictCheckerConstants.RUN_TYPE);
    }

}

