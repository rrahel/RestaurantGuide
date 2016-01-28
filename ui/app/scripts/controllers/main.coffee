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
    $scope.restaurants = []

    getCategories = () =>
      $http.get('/categories')
      .then (resp) ->
        $scope.categories = resp.data
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getCategories()

    getRestaurants = () ->
      $http.get("/rating/top")
      .then (resp) ->
        $scope.restaurants = resp.data
        $scope.restaurants = (item for item in $scope.restaurants when item.rating?)
      .catch (resp) -> $scope.error = resp.data.message or resp.data

    getRestaurants();


