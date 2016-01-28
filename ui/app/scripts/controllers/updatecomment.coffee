'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:UpdatecommentCtrl
 # @description
 # # UpdatecommentCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'UpdatecommentCtrl', ($scope,CommentFactory,$location,$routeParams,$http) ->
    commId = $routeParams.commId
    $scope.comment = {}
    $http.get("/comment/#{commId}")
     .then (response) ->
      $scope.comment = response.data
    $scope.updateComment = ->
      $http.post("/comment/#{commId}", $scope.comment)
      .then -> $location.path "/restaurantDetails?restId=#{comment.restaurantId}"
      .catch (resp) -> $scope.error = resp.data.message or resp.data
