package com.bronti.teamcity.mergeConflictCheckerPlugin;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by bronti on 24.12.16.
 */
public class MergeConflictCheckerReportTab extends ViewLogTab {

    public MergeConflictCheckerReportTab(@NotNull PagePlaces pagePlaces,
                                         @NotNull SBuildServer server,
                                         @NotNull PluginDescriptor descriptor) {
        super("", "", pagePlaces, server);
        setTabTitle(getTitle());
        setPluginName(getClass().getSimpleName());
        setIncludeUrl(descriptor.getPluginResourcesPath("buildResultsTab.jsp"));
        addCssFile(descriptor.getPluginResourcesPath("css/style.css"));
        addJsFile(descriptor.getPluginResourcesPath("js/angular.min.js"));
        addJsFile(descriptor.getPluginResourcesPath("js/angular-app.js"));
    }

    private String getTitle() {
        return "Merge Conflict Checker Log";
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model,
                             @NotNull HttpServletRequest request,
                             @NotNull SBuild build) {
//        String artifactPath = MergeConflictCheckerConstants.ARTIFACTS_DIR + MergeConflictCheckerConstants.JSON_REPORT_FILENAME;
//        BuildArtifact report = build.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(artifactPath);
//        try {
//            InputStream reportStream = report.getInputStream();
//            JsonFactory factory = new JsonFactory();
//            JsonParser parser  = factory.createParser(reportStream);
//            ObjectMapper objectMapper = new ObjectMapper();
//            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, OneMergeResult.class);
//            List<OneMergeResult> results = objectMapper.readValue(parser, type);
//
//
//            super.fillModel(model, request);
////            model.put("resultsList", results);
//            model.put("checkString", "Hi!");
//        }
//        catch (IOException ex) {
//            // todo: do something
//            throw new RuntimeException();
//        }
//        model.put("checkString", "Hi!");
    }

    @Override
    protected boolean isAvailable(@NotNull final HttpServletRequest request, @NotNull final SBuild build) {
        return build.getBuildType().getRunnerTypes().contains(MergeConflictCheckerConstants.RUN_TYPE);
    }

}

