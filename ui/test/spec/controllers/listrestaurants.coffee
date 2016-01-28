###
'use strict'

describe 'Controller: ListrestaurantsCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $location = {}

  restaurants = [{
    id: 1
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
  },{
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
  ]

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'ListrestaurantsCtrl', $scope: scope

  it 'should properly initialize',  ->
    expect(scope.restaurants).toEqual []
    expect(scope.rows).toEqual []
    expect(scope.numberOfRestaurants).toBe 0
    expect(scope.page).toBe 1
    expect(scope.error).toBe null
    expect(scope.pageSize).toBe 10

    $httpBackend.expectGET("/categories/1").respond 200, restaurants

    expect(scope.restaurants.length).toBe 2###
