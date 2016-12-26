<style type="text/css">
    .tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;}
    .tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#fff;}
    .tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#f0f0f0;}
    .tg .tg-nqgz{font-weight:bold;background-color:#fe7520;color:#000000;text-align:center;vertical-align:top}
    .tg .tg-u1yq{font-weight:bold;background-color:#c0c0c0;text-align:center;vertical-align:top}
    .tg .tg-00rj{font-weight:bold;background-color:#009901;color:#000000;text-align:center;vertical-align:top}
    .tg .tg-nonex{font-weight:bold;background-color:#fe0000;color:#000000;text-align:center;vertical-align:top}
</style>
<div data-ng-controller="mccrRootCtrl" class="mccr-report">
<div data-ng-hide="loaded">
    <span class="icon-refresh icon-spin"></span>&nbsp;Loading report...
</div>
<div data-ng-show="loaded">
    <table class="tg" width="500">
        <tr>
            <th class="tg-u1yq">Branch</th>
            <th class="tg-u1yq">Merge status</th>
        </tr>
        <tr data-ng-repeat="res in results">
            <td data-ng-show="res.exists && res.isSuccessful" class="tg-00rj">{{ res.branch }}</td>
            <td data-ng-show="res.exists && !res.isSuccessful" class="tg-nqgz">{{ res.branch }}</td>
            <td data-ng-hide="res.exists" class="tg-nonex">{{ res.branch }}</td>
            <td data-ng-show="res.exists && res.isSuccessful" class="tg-00rj">{{ res.state }}</td>
            <td data-ng-show="res.exists && !res.isSuccessful" class="tg-nqgz">{{ res.state }}</td>
            <td data-ng-hide="res.exists" class="tg-nonex">Branch does not exist</td>
        </tr>
    </table>
</div>
