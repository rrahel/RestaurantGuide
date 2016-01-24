'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:DeletecommentCtrl
 # @description
 # # DeletecommentCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'DeletecommentCtrl', ($scope,CommentFactory,$location,$routeParams) ->
    $scope.comment = new CommentFactory()
    $scope.delete = (commentId) ->
      $scope.comment.$delete id: $routeParams.commentId
      .then -> $location.path "/"
      .catch (resp) -> $scope.error = resp.data.message or resp.data
