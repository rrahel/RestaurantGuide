'use strict'

describe 'Controller: RestaurantdetailsCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'

  beforeEach ->
    jasmine.addMatchers
      toEqualData: (util, customEqualityTesters) ->
        compare: (actual, expected) ->
          result = {}
          result.pass = angular.equals actual, expected
          result

  scope = {}
  $controller = {}
  $httpBackend = {}
  $routeParams = {}



  createMarkerFromRestaurant = (r) ->
    lat: r.lat
    lng: r.lng
    message: "#{r.name}"
    draggable: false
    focus: false

  addMarker = (markers,restaurant) ->
    markers["ID_#{restaurant.id}"] = createMarkerFromRestaurant(restaurant)
    markers

  createMarkers = (restaurant) -> restaurant.reduce addMarker,{}

  restaurant =
      id: 1
      name: "Rest 1"
      description: "Desc 1"
      category: "Cat 1"
      phone: "128761"
      email: "Rest1.aoi@uia.at"
      website: "Web 1"
      rating: {}
      street: "Alte Poststraße 147"
      city: "Graz"
      zip: "8020"
      lat: 47.069718
      lng: 15.409874

  comment =
    id: 1
    content: "Best Restaurant"
    userId: 1
    restaurantId: 1

  restId = 1
  commId = 1


  beforeEach inject ($rootScope,_$controller_,_$httpBackend_,_$routeParams_)->
    scope = $rootScope.$new()
    $controller = _$controller_
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_

  it "should fetch the restaurant details", ->
    $routeParams.restId = 1
    $controller 'RestaurantdetailsCtrl', $scope: scope
    expect(scope.restaurant).toEqualData {}
    $httpBackend.expectGET("/restaurants/#{restId}").respond 200, restaurant
    $httpBackend.expectGET('/views/main.html').respond 200
    $httpBackend.expectGET("/comments/#{commId}").respond 200, comment
    $httpBackend.flush()
    expect(scope.restaurant).toEqualData restaurant
