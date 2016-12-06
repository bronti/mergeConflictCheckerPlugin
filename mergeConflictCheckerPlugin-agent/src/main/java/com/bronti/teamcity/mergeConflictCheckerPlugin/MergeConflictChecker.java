package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Created by bronti on 06.12.16.
 */
public class MergeConflictChecker {

//    MergeConflictCheckerRunService runner;
    private StringBuilder script = new StringBuilder(100);

    private final UsernamePasswordCredentialsProvider creds;
    private final String[] allBranches;
    private final String currentBranch;
    private final URIish fetchUri;
    private final File repoDirectory;
    private final String originName = "origin";

    private Repository repository;
    private Git git;

    MergeConflictChecker(File repoDir,
                         String branch,
                         String branches,
                         URIish uri_,
                         UsernamePasswordCredentialsProvider creds_) throws IOException, RunBuildException {
        script.append("#!/bin/bash\n\n");
        creds = creds_;
        allBranches = branches.split("\\s+");
        fetchUri = uri_;
        currentBranch = branch;
        repoDirectory = repoDir;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(repoDirectory).readEnvironment().findGitDir().build();
        git = new Git(repository);
    }

    private void echo(String msg)
    {
        script.append("echo '");
        script.append(msg);
        script.append("'\n");
    }

    public String getFeedback()
    {
        return script.toString();
    }

    private void fetchRemote(Git git) throws GitAPIException {
        RemoteAddCommand addOrigin = git.remoteAdd();
        addOrigin.setName(originName);
        addOrigin.setUri(fetchUri);
        addOrigin.call();

        // one remote
        RefSpec refSpec = new RefSpec();
        refSpec = refSpec.setForceUpdate(true);
        // all branches
        refSpec = refSpec.setSourceDestination("refs/heads/*", "refs/remotes/" + originName + "/*");

        git.fetch()
                .setRemote(originName)
//                   .setCredentialsProvider(UsernamePasswordCredentialsProvider.getDefault())
                .setCredentialsProvider(creds)
                .setRefSpecs(refSpec)
                .call();
        echo("Successfully fetched " + originName);
    }

    public void check() throws GitAPIException {
        echo("Current branch is " + currentBranch);

        fetchRemote(git);

        List<Ref> brchs = git.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call();
        for (Ref br : brchs) {
            echo(br.getName());
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
}
