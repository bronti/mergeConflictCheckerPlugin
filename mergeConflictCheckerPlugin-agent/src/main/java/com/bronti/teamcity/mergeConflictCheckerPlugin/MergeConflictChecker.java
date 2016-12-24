package com.bronti.teamcity.mergeConflictCheckerPlugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
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

    private final CredentialsProvider creds;
    private final String[] toCheckBranches;
    private final String currentBranch;
    private final URIish fetchUri;
    private final String originName = "origin";

    private Repository repository;
    private Git git;

    MergeConflictChecker(File repoDir,
                         String branch,
                         String branches,
                         URIish uri_,
                         CredentialsProvider creds_) throws IOException {
        script.append("#!/bin/bash\n\n");
        creds = creds_;
        toCheckBranches = branches.split("\\s+");
        fetchUri = uri_;
        currentBranch = branch;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();
        git = new Git(repository);
    }

    private enum TcMessageStatus {
        NORMAL, WARNING, FAILURE, ERROR
    }

    private void tcMessage(String msg, TcMessageStatus status, String details)
    {
        script.append("echo \"##teamcity[message text='");
        script.append(msg);
        if (!details.equals("") && status == TcMessageStatus.ERROR)
        {
            script.append("'  errorDetails='");
            script.append(details);
        }
        script.append("' status='");
        script.append(status.name());
        script.append("']\"\n");
    }

    private void tcMessage(String msg, TcMessageStatus status)
    {
        tcMessage(msg, status, "");
    }

    String getFeedback()
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
                .setCredentialsProvider(creds)
                .setRefSpecs(refSpec)
                .call();
        // todo: injections
        tcMessage("Successfully fetched " + originName, TcMessageStatus.NORMAL);
    }

    void check() throws GitAPIException, IOException {

        fetchRemote(git);

//        List<Ref> brchs = git.branchList()
//                .setListMode(ListBranchCommand.ListMode.ALL)
//                .call();
//        for (Ref br : brchs) {
//            echo(br.getName());
//        }

        for (String branch : toCheckBranches) {
            MergeCommand mgCmd = git.merge();
            ObjectId commitId = repository.resolve("refs/remotes/" + originName + "/" + branch);
            if (commitId == null) {
                // todo: injections
                tcMessage("Branch |'" + branch + "|' not found", TcMessageStatus.ERROR);
                continue;
            }
            mgCmd.include(commitId);
            MergeResult.MergeStatus resStatus = mgCmd.call().getMergeStatus();
            if (resStatus.isSuccessful()) {
                // todo: injections
                String msg = "Merge with branch |'" + branch + "|' is successful. Status is " + resStatus.toString() + ".";
                tcMessage(msg, TcMessageStatus.NORMAL);
            } else {
                // todo: injections
                String msg = "Merge with branch |'" + branch + "|' is failed. Status is " + resStatus.toString() + ".";
                tcMessage(msg, TcMessageStatus.WARNING);
            }

            repository.writeMergeCommitMsg(null);
            repository.writeMergeHeads(null);

            Git.wrap(repository).reset().setMode(ResetCommand.ResetType.HARD).call();
        }
    }
}
