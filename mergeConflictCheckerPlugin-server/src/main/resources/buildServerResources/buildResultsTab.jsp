<jsp:useBean id="buildData" type="jetbrains.buildServer.serverSide.SBuild" scope="request"/>
<script type="text/javascript">
    var MergeConflictCheckerRunReport = {
        buildId: '${buildData.buildId}'
    };
</script>

<div data-ng-app="mccr-report" data-ng-include="'/plugins/mergeConflictCheckerPlugin/html/report-ng.tpl'">
</div>