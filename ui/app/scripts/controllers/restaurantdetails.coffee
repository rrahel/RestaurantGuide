'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:RestaurantdetailsCtrl
 # @description
 # # RestaurantdetailsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'RestaurantdetailsCtrl', ($scope, $routeParams, RestaurantFactory, RatingFactory, $http, $route) ->
    restId = $routeParams.restId
    $scope.user = {}
    $scope.restaurant = {}
    $scope.rating = {}
    $scope.comment = {}
    $scope.comments = []

    $http.get("restaurants/#{restId}")
    .then (resp) ->
      $scope.restaurant = resp.data
      getComments()

    $scope.rate = ->
      $http.get("/whoami")
       .then (response) ->
        $scope.user = response.data
        $http.get("rating/#{restId}")
        .then (resp) ->
          $scope.rating.id = resp.data.id
          $scope.rating.rating = parseFloat($scope.newRate)
          $scope.rating.userId = $scope.user.id
          $scope.rating.restaurantId = $scope.restaurant.id
          $http.post("/rating/#{$scope.rating.id}", $scope.rating)
          .then -> $route.reload()
          .catch (resp) -> $scope.error = resp.data.message or resp.data
        .catch (error) ->
          $scope.rating.rating = parseFloat($scope.newRate)
          $scope.rating.userId = $scope.user.id
          $scope.rating.restaurantId = $scope.restaurant.id
          $http.post("/rating", $scope.rating)
          .then -> $route.reload()
          .catch (resp) -> $scope.error = resp.data.message or resp.data

    $scope.addComment = ->
      $http.get("/whoami")
      .then (response) ->
        $scope.user = response.data
        $scope.comment.content = $scope.newComment
        $scope.comment.userId = $scope.user.id
        $scope.comment.restaurantId = $scope.restaurant.id
        $http.post("/comment", $scope.comment)
        .then -> $route.reload()
        .catch (resp) -> $scope.error = resp.data.message or resp.data

    getComments = () ->
      $http.get("/comments/#{$scope.restaurant.id}")
       .then (response) ->
        $scope.comments = response.data





