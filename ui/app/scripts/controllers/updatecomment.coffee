'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:UpdatecommentCtrl
 # @description
 # # UpdatecommentCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'UpdatecommentCtrl', ($scope,CommentFactory,$location,$routeParams) ->
    $scope.comment = new CommentFactory()
    $scope.update = (commentId) ->
      $scope.comment.$save id: $routeParams.commentId
      .then -> $location.path "/"
      .catch (resp) -> $scope.error = resp.data.message or resp.data
