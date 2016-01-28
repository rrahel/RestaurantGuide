'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:SearchCtrl
 # @description
 # # SearchCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'SearchCtrl', ($scope, RestaurantFactory) ->
    $scope.restaurants = []
    $scope.restaurants = RestaurantFactory.query()

