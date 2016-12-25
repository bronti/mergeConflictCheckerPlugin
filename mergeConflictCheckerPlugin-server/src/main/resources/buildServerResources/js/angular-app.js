var MCCReportApp = angular.module('mccr-report', []);

MCCReportApp.controller('mccrRootCtrl', ['$scope', '$http', function($scope, $http) {
	$scope.loaded = false;
//	var url = "/httpAuth/app/rest/builds/id:"
//		+ MergeConflictCheckerRunReport.buildId
//		+ "/.teamcity/mcc_report/mcc_report.json";
	var url = "/httpAuth/app/rest/builds/id:"
		+ MergeConflictCheckerRunReport.buildId
		+ "/artifacts/content/.teamcity/mccr-report/mccr-report.json";
	$scope.str = url;
	$http.get(url)
        .success(function(data, status, headers, config) {
            $scope.loaded = true;
        })
        .error(function(data, status, headers, config) {
            $scope.loaded = false;
        });
}]);
