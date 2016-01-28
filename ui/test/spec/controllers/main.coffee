'use strict'

describe 'Controller: MainCtrl', ->

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

  categories = [
    {
      id: 1
      name: "Category1"
    },
    {
      id: 2
      name: "Category2"
    }
  ]

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $controller 'MainCtrl', $scope: scope

  it 'list some categories and top restaurants',  ->
    expect(scope.categories).toEqual []
    expect(scope.restaurants).toEqual []
    $httpBackend.expectGET("/categories").respond 200, categories
    $httpBackend.expectGET("/rating/top").respond 200, restaurants
    expect(scope.categories.length).toBe 0
    expect(scope.restaurants.length).toBe 0
    $httpBackend.flush()
    expect(scope.categories.length).toBe 2
    expect(scope.restaurants.length).toBe 2

  it 'should provide an error message if something went wrong',  ->
    expect(scope.categories).toEqual []
    expect(scope.restaurants).toEqual []
    $httpBackend.expectGET("/categories").respond 500,message: "An Error occured!"
    $httpBackend.expectGET("/rating/top").respond 500,message: "An Error occured!"
    expect(scope.categories.length).toBe 0
    expect(scope.restaurants.length).toBe 0
    $httpBackend.flush()
    expect(scope.error).toBe "An Error occured!"

