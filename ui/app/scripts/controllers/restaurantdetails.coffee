'use strict'

###*
 # @ngdoc function
 # @name uiApp.controller:RestaurantdetailsCtrl
 # @description
 # # RestaurantdetailsCtrl
 # Controller of the uiApp
###
angular.module 'uiApp'
  .controller 'RestaurantdetailsCtrl', ($scope, $routeParams, RestaurantFactory, RatingFactory, $http, $route, $q) ->
    ###createMarkerFromRestaurant = (r) ->
      lat: r.lat
      lng: r.lng
      message: "#{r.name}"
      draggable: false
      focus: false

    addMarker = (markers) ->
      markers["ID_#{$scope.restaurant.id}"] = createMarkerFromRestaurant($scope.restaurant)
      markers

    createMarkers = (restaurants) -> members.reduce addMarker,{}

    $scope.error = null
    $scope.center = {}
    $scope.markers = {}
    $q.all [$scope.restaurant.$promise]
    .then (results) ->
      $scope.markers = createMarkers results[1]
      $scope.center =
        zoom: 10
        lat: results[1][0].lat
        lng: results[1][0].lng
    .catch (resp) -> $scope.error = resp.data.message or resp.data

    $scope.show = (id) ->
      $scope.markers[prop].focus = false for prop of $scope.markers
      $scope.markers["ID_#{id}"].focus = true###

    ##rating
    restId = $routeParams.restId
    $scope.restaurant = {}
    $http.get("restaurants/#{restId}")
    .then (resp) ->
      $scope.restaurant = resp.data


    $scope.rate = ->
      $scope.rating = {}
      $scope.rating.rating = parseFloat($scope.newRate)
      $scope.rating.userId = 1
      $scope.rating.restaurantId = $scope.restaurant.id
      $http.post("/rating", $scope.rating)
       .then -> $route.reload()
       .catch (resp) -> $scope.error = resp.data.message or resp.data


