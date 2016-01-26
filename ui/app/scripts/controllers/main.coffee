'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:MainCtrl
 # @description
 # # MainCtrl
 # Controller of the uiApp
###
angular.module('uiApp')
  .controller 'MainCtrl', ($scope, $http) ->
    $scope.categories = []
    $http.get('/categories')
    .then (resp) ->
      $scope.categories = resp.data
