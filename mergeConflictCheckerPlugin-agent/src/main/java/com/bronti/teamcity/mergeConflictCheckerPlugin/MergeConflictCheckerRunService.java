package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
        return "echo 'Hi, beauty! Option is " + myOption + ".'";
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
