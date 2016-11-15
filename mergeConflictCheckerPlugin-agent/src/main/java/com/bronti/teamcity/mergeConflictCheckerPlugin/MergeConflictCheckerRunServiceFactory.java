package com.bronti.teamcity.mergeConflictCheckerPlugin;

//import com.intellij.openapi.util.SystemInfo;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

import com.bronti.teamcity.mergeConflictCheckerPlugin.MergeConflictCheckerConstants;

/**
 * Created by bronti on 15.11.16.
 */
public class MergeConflictCheckerRunServiceFactory implements CommandLineBuildServiceFactory, AgentBuildRunnerInfo {

    private ArtifactsWatcher artifactsWatcher;

    public MergeConflictCheckerRunServiceFactory(@NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @Override
    public String getType() {
        return MergeConflictCheckerConstants.RUN_TYPE;
    }

    @Override
    public boolean canRun(BuildAgentConfiguration agentConfiguration) {
        // todo: ????
        return true;
    }

    @Override
    public CommandLineBuildService createService() {
        return new MergeConflictCheckerRunService(artifactsWatcher);
    }

    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return this;
    }
}
