'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddcategoryCtrl
 # @description
 # # AddcategoryCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddcategoryCtrl', ($scope,Categories,$location) ->
   $scope.category = new Categories()
   $scope.save = ->
     $scope.category.$save()
     .then -> $location.path "/"
     .catch (resp) -> $scope.error = resp.data.message or resp.data
