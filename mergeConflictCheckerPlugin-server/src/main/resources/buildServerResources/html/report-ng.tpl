<div data-ng-controller="mccrRootCtrl" class="mccr-report">
<div data-ng-hide="loaded">
    Loading MCC Run report...  {{ str }}
</div>
<div data-ng-show="loaded">
    MCC report here. {{ str }}
</div>