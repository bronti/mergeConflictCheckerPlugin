var MCCReportApp = angular.module('mccr-report', []);

MCCReportApp.controller('mccrRootCtrl', ['$scope', '$http', function($scope, $http) {
	$scope.loaded = false;
	var url = "/httpAuth/app/rest/builds/id:"
		+ MergeConflictCheckerRunReport.buildId
		+ "/artifacts/content/.teamcity/mccr-report/mccr-report.json";
	$http.get(url)
        .success(function(data, status, headers, config) {
            $scope.results = data.merge_results;
            $scope.loaded = true;
        })
        .error(function(data, status, headers, config) {
            $scope.loaded = false;
            $scope.alerts.push(data);
        });
}]);
