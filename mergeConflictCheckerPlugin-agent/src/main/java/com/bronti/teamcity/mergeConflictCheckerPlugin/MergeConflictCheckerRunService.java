package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildParametersMap;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by bronti on 15.11.16.
 */

public class MergeConflictCheckerRunService extends BuildServiceAdapter {


    private ArtifactsWatcher artifactsWatcher;

    public MergeConflictCheckerRunService(ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @Override
    public void beforeProcessStarted() throws RunBuildException {
        // todo: check
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

    private File createTempLogFile() throws IOException {
        File tmpDir = new File(getBuildTempDirectory(), "mcc_run_results");
        boolean exists = tmpDir.exists();

        if (!exists && !tmpDir.mkdir()) {
            throw new IOException("Cannot create temporary directory for build.");
        }
        String fileName = "mcc_run_results";
        File logFile = new File(tmpDir, fileName);
        logFile.createNewFile();
        return logFile;
    }

    private String createScript() throws RunBuildException {

        Map<String, String> params = getRunnerParameters();
        String myOption = params.get(MergeConflictCheckerConstants.MY_OPTION_KEY);
//        MergeConflictCheckerMyOption rlOption = MergeConflictCheckerMyOption.valueOf(myOption);
        String allBranches = params.get(MergeConflictCheckerConstants.BRANCHES);

        BuildRunnerContext context = getRunnerContext();
        Map<String, String> configParams = context.getConfigParameters();
//        String user = configParams.get("vcsroot.username");
//        UsernamePasswordCredentialsProvider credentials =
//                new UsernamePasswordCredentialsProvider(user, "12345678");
        CredentialsProvider credentials =
                UsernamePasswordCredentialsProvider.getDefault();

        String currBranch = configParams.get("vcsroot.branch");
        String fetchUrl = configParams.get("vcsroot.url");

        try {
            URIish uri = new URIish(fetchUrl);

            File coDir = getCheckoutDirectory();
            File repoDir = new File(coDir.getPath() + "/.git");

            MergeConflictCheckerRunResultsLogger logger;
            try {
                logger = new MergeConflictCheckerRunResultsLogger(createTempLogFile());
            }
            catch (IOException ex) {
                throw new RunBuildException("Can not create temporary log file", ex.getCause());
            }

            MergeConflictChecker checker =
                    new MergeConflictChecker(repoDir, currBranch, allBranches, uri, credentials, logger);
            checker.check();
            return checker.getFeedback();
        }
        catch (URISyntaxException | IOException | GitAPIException ex) {
            throw new RunBuildException(ex.getMessage(), ex.getCause());
        }
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
