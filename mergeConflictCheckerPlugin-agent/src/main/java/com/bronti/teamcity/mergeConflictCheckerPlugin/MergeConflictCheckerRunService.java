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

    private static String echo(String msg)
    {
        return "echo '" + msg + "'\n";
    }

    private String fetchRemote(Git git, String name, URIish uri) throws GitAPIException, RunBuildException {
        RemoteAddCommand addOrigin = git.remoteAdd();
        addOrigin.setName(name);
        addOrigin.setUri(uri);
        addOrigin.call();

        BuildRunnerContext context = getRunnerContext();
        Map<String, String> configParams = context.getConfigParameters();
        String user = configParams.get("vcsroot.username");

        try {
            // one remote
            git.fetch()
                    .setRemote(name)
//                   .setCredentialsProvider(UsernamePasswordCredentialsProvider.getDefault())
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, "<password>"))
                    .call();
            return echo("Successfully fetched " + name);
        } catch (TransportException ex)
        {
            throw new RunBuildException( ex.getMessage(), ex.getCause());
        }
    }

    private String createScript() throws RunBuildException {

        Map<String, String> params = getRunnerParameters();
        String myOption = params.get(MergeConflictCheckerConstants.MY_OPTION_KEY);
//        MergeConflictCheckerMyOption rlOption = MergeConflictCheckerMyOption.valueOf(myOption);
        String allBranches = params.get(MergeConflictCheckerConstants.BRANCHES);

        String result = "#!/bin/bash\n";

        BuildRunnerContext context = getRunnerContext();
        Map<String, String> configParams = context.getConfigParameters();
        String currentBranch = configParams.get("vcsroot.branch");
        String fetchUrl = configParams.get("vcsroot.url");
        String user = configParams.get("vcsroot.username");

        result += echo("Current branch is " + currentBranch);

        String[] branches = allBranches.split("\\s+");

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File coDir = getCheckoutDirectory();
        File repoDir = new File(coDir.getPath() + "/.git");

        try (Repository repository = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();
             Git git = new Git(repository)) {

            result += fetchRemote(git, "origin", new URIish(fetchUrl));

            List<Ref> brchs = git.branchList()
                                 .setListMode(ListBranchCommand.ListMode.ALL)
                                 .call();
            for (Ref br : brchs) {
                result += echo(br.getName());
            }

//            for (String branch : branches) {
//                if (("refs/heads/" + branch).equals(currentBranch)) {
//                    continue;
//                }
//                MergeCommand mgCmd = git.merge();

//                ObjectId commitId = repository.resolve("refs/remotes/origin/" + branch);
//                ObjectId commitId = repository.resolve("origin/" + branch);
//                mgCmd.include(commitId);
//                MergeResult res = mgCmd.call();
//                if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
////                    throw new RunBuildException("Merge conflict.");
//                    result += "echo 'Merge with branch " + branch + "is failed'\n";
//                } else {
//                    result += "echo 'Merge with branch " + branch + "is successful'\n";
//                }
//
//                repository.writeMergeCommitMsg(null);
//                repository.writeMergeHeads(null);
//
//                Git.wrap(repository).reset().setMode(ResetCommand.ResetType.HARD).call();

                // mind http://stackoverflow.com/questions/29807016/abort-merge-using-jgit

    //            result += "git merge origin/" + branch + "\n";
    //            result += "git merge --abort\n";
    //            result += "git reset --hard HEAD\n";
//            }
        }
        catch (TransportException ex) {
//            throw new RunBuildException(ex.getMessage(), ex.getCause());
            result += ex.getMessage() + "\n" + ex.getCause();
        }
        catch(IOException | GitAPIException | URISyntaxException ex) {
            throw new RunBuildException(ex.getMessage(), ex.getCause());
        }

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
