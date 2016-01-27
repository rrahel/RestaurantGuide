'use strict'

describe 'Controller: RestaurantdetailsCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $location = {}

  restaurant = {
    id: 2
    name: "oaojfoa"
    description: "ß2933urqjef"
    category: 1
    phone: "9298"
    email: "091823091283"
    website: "ajsfoiahjfioaw"
    rating: 2.2
    street: "ajsdjoasidj"
    city: "posjapoj"
    zip: "lajfjpawpoj"
    lat: 123.12312
    lng: 123.3123124
  }


  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'RestaurantdetailsCtrl', $scope: scope

  it 'should properly initialize',  ->
    expect(scope.user).toEqual {}
    expect(scope.restaurant).toEqual {}
    expect(scope.rating).toEqual {}

    $httpBackend.expectGET("/restaurants/1").respond 200, restaurant

    expect(scope.restaurants.name).toBe "oaojfoa"