'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:AddcommentsCtrl
 # @description
 # # AddcommentsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'AddcommentsCtrl', ($scope,CommentFactory,$location) ->
    $scope.comment = new CommentFactory()
    $scope.save = ->
      $scope.comment.$save()
      .then -> $location.path "/"
      .catch (resp) -> $scope.error = resp.data.message or resp.data
