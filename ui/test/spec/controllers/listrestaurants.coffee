'use strict'

describe 'Controller: ListrestaurantsCtrl', ->

# load the controller's module
    beforeEach module 'uiApp'


    scope = {}
    $httpBackend = {}
    $routeParams = {}

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

    catId = undefined

  # Initialize the controller and a mock scope
    beforeEach inject ($controller, $rootScope,_$httpBackend_,_$routeParams_) ->
      scope = $rootScope.$new()
      $httpBackend = _$httpBackend_
      $routeParams = _$routeParams_
      $controller 'ListrestaurantsCtrl', $scope: scope

    it 'list some restaurants',  ->
      expect(scope.restaurants).toEqual []
      $routeParams.catId = 1
      catId = $routeParams.catId
      $httpBackend.expectGET("/categories/#{catId}").respond 200, restaurants
      expect(scope.restaurants.length).toBe 0
      $httpBackend.flush()
      expect(scope.restaurants.length).toBe 2
