angular.module('UsCensusApp', ['ui.bootstrap']);

angular.module('UsCensusApp').controller('ConfigurationController', function($scope, $http, $log) {
	// Init
	$scope.selectedTableName = "None";
	$scope.selectedCategory = "None";
	$scope.colToAvg = "age";
	$scope.startOffset = 0;
	$scope.endOffset = 0;
	$scope.totalNumberOfResults = 0;
	$scope.pageSize = 100;
	$scope.currentPage = 1;
	$scope.loading = false;
	$scope.loaded = false;
	
	// First action = get all available tables names from config servlet
	$http.get('config/tableNames').then(function (response) {
		$scope.tableNames = response.data;
		if ($scope.tableNames.length == 1) {
			$scope.didSelectTableName($scope.tableNames[0]);
		}
	}, function (data) {
		alert("Global error while contacting server " + response.status + "/" + response.statusText);
	});

	// When a table name, affect the model, and update column name UIs
	$scope.didSelectTableName = function (name) {
		$scope.selectedTableName = name;
		$scope.updateCategories();
	}

	// Fetch column names from config servlets
	$scope.updateCategories = function() {
		$http.get('config/columnNames?tableName=' + $scope.selectedTableName).then(function(response) {
			$scope.categoryNames = response.data;
		});
	}

	function fetchDatas() {
		$scope.loading = true;
		$scope.loaded = false;
		$scope.averages = [];
		$http.get('average?table=' + $scope.selectedTableName 
					+ '&colToGroup=' + $scope.selectedCategory
					+ '&colToAvg=' + $scope.colToAvg 
					+ '&startOffset=' + $scope.startOffset
					+ '&pageSize=' + $scope.pageSize).then(function(response) {
			 if(response.data.error != null) {
				 alert("Erreur accessing server : " + response.data.error);
			 } else {
				 $scope.loading = false;
				 $scope.loaded = true;
				 $scope.startOffset = response.data.startOffset;
				 $scope.totalNumberOfResults = response.data.totalResults;
				 $scope.averages = response.data.averages;
				 // Pagination
				 $scope.endOffset = Math.min($scope.totalNumberOfResults, $scope.startOffset + $scope.pageSize)
				 $scope.currentPage = $scope.startOffset / $scope.pageSize + 1;
			 }
		 }, function(response) {
			alert("Global error while contacting server " + response.status + "/" + response.statusText);
		 });
	}
	
	// Fetch average data on categories pick
	$scope.didSelectCategory = function(category) {
		$scope.selectedCategory = category;
		$scope.startOffset = 0; // Reset offset when switching categories
		fetchDatas();
	}
	
	$scope.pageChanged = function() {
		var newPage = $scope.currentPage;
		$scope.startOffset = (newPage-1) * $scope.pageSize;
		fetchDatas();
	};	
});

