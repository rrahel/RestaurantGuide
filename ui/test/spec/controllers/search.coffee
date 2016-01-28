'use strict'

describe 'Controller: SearchCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}

  restaurants = [{
    id: 1
    name: "Rrahels Dinner"
    description: "Enjoy your dinner"
    category: 1
    phone: "9298"
    email: "test@test.com"
    website: "www.test.com"
    rating: 4.9
    street: "Alte Poststraße 148"
    city: "Graz"
    zip: "8020"
    lat: 47.06
    lng: 15.40
  },{
    id: 2
    name: "Max Pub"
    description: "Beer!!!"
    category: 1
    phone: "9298"
    email: "test@test.com"
    website: "www.test.com"
    rating: 4.5
    street: "Alte Poststraße 149"
    city: "Graz"
    zip: "8020"
    lat: 47.06
    lng: 15.40
  }
  ]

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $controller 'SearchCtrl', $scope: scope

  it 'list some restaurants',  ->
    $httpBackend.expectGET("/restaurants").respond 200, restaurants
    expect(scope.restaurants.length).toBe 0
    $httpBackend.flush()
    expect(scope.restaurants.length).toBe 2

  it 'should react to backend errors',  ->
    $httpBackend.expectGET("/restaurants").respond 500
    expect(scope.restaurants.length).toBe 0
    $httpBackend.flush()
    expect(scope.restaurants.length).toBe 0

