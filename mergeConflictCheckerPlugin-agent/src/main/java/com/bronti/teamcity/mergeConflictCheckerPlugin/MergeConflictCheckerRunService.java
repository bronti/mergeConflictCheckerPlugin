package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildParametersMap;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bronti on 15.11.16.
 */

public class MergeConflictCheckerRunService extends BuildServiceAdapter {


    private ArtifactsWatcher artifactsWatcher;

    public MergeConflictCheckerRunService(ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        String workingDir = getWorkingDirectory().getPath();
        String script = createScript();
        List<String> args = Collections.emptyList();

        return new SimpleProgramCommandLine(
                getEnvironmentVariables(),
                workingDir,
                createExecutable(script),
                args);
    }

    private String createScript() throws RunBuildException {
        Map<String, String> params = getRunnerParameters();
        String myOption = params.get(MergeConflictCheckerConstants.MY_OPTION_KEY);
//        MergeConflictCheckerMyOption rlOption = MergeConflictCheckerMyOption.valueOf(myOption);
        String allBranches = params.get(MergeConflictCheckerConstants.BRANCHES);
        String result = "#!/bin/bash\n";
        result += "echo 'My option is " + myOption + ".'\n";
        result += "echo 'Branches are " + allBranches + ".'\n";

        BuildRunnerContext context = getRunnerContext();
        Map<String, String> configParams = context.getConfigParameters();
        String currentBranch = configParams.get("vcsroot.branch");
        result += "echo 'Current branch is " + currentBranch + ".'\n";

        String[] branches = allBranches.split("\\s+");
        for (String branch : branches)
        {
            if (("refs/heads/" + branch).equals(currentBranch)){
                continue;
            }
            result += "git fetch origin\n";
            result += "git merge origin/" + branch + "\n";
            result += "git merge --abort\n";
            result += "git reset --hard HEAD\n";
        }

//        BuildRunnerContext context = getRunnerContext();
//        BuildParametersMap parametersMap = context.getBuildParameters();
//        Map<String, String> allParams = parametersMap.getAllParameters();
//        String keys = String.join(" ", allParams.keySet());
//        result += "echo 'Build params are:'\
//        result += "echo '" + keys + "'\n";n";
//
//        Map<String, String> configParams = context.getConfigParameters();
//        keys = String.join(" ", configParams.keySet());
//        result += "echo 'Config params are:'\n";
//        result += "echo '" + keys + "'\n";
//
//        Map<String, String> runnerParams = context.getRunnerParameters();
//        keys = String.join(" ", runnerParams.keySet());
//        result += "echo 'Runner params are:'\n";
//        result += "echo '" + keys + "'\n";
        return result;
    }

    private String createExecutable(String script) throws RunBuildException {
        File scriptFile;
        try {
            scriptFile = File.createTempFile("simple_build", null, getBuildTempDirectory());
            FileUtil.writeFileAndReportErrors(scriptFile, script);
        } catch (IOException e) {
            throw new RunBuildException("Cannot create a temp file for execution script.");
        }
        if (!scriptFile.setExecutable(true, true)) {
            throw new RunBuildException("Cannot set executable permission to execution script file");
        }
        return scriptFile.getAbsolutePath();
    }
}
