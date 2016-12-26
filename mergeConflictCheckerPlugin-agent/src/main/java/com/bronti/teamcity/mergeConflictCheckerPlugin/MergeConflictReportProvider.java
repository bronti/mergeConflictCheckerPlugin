package com.bronti.teamcity.mergeConflictCheckerPlugin;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bronti on 25.12.16.
 */
public class MergeConflictReportProvider {

//    private class OneMergeResult {
//        public final String branch;
//        public final boolean isSuccessful;
//        public final boolean exists;
//        public final String state;
//
//        OneMergeResult(String branch, boolean isSuccessful, String state) {
//            this.branch = branch;
//            this.isSuccessful = isSuccessful;
//            this.exists = true;
//            this.state = state;
//        }
//
//        OneMergeResult(String branch) {
//            this.branch = branch;
//            this.isSuccessful = false;
//            this.exists = false;
//            this.state = "";
//        }
//    }

    private List<OneMergeResult> results = new ArrayList<>();
    private File logFile;
    private ArtifactsWatcher artifactsWatcher;

    MergeConflictReportProvider(File logFile,
                                ArtifactsWatcher artifactsWatcher) {
        this.logFile = logFile;
        this.artifactsWatcher = artifactsWatcher;
    }

    void logMergeResult(String branch, boolean isSuccessful, String state)
    {
        results.add(new OneMergeResult(branch, isSuccessful, state));
    }

    void logNonexistentBranch(String branch)
    {
        results.add(new OneMergeResult(branch));
    }

    void flushLog() throws IOException {
        JsonFactory jf = new MappingJsonFactory();
        try (JsonGenerator jg = jf.createGenerator(logFile, JsonEncoding.UTF8)) {
            jg.writeStartObject();
            jg.writeFieldName("merge_results");
            jg.writeObject(results);
            jg.writeEndObject();
        }
        artifactsWatcher.addNewArtifactsPath(logFile.getAbsolutePath() + "=>" + MergeConflictCheckerConstants.ARTIFACTS_DIR);
    }


}
