'use strict'

describe 'Controller: ProfileCtrl', ->

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
  $httpBackend={}
  $location={}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'ProfileCtrl', $scope: scope


  user = {
    id: 1
    firstname: "John"
    lastname: "Cena"
    email: "johncena@wad.com"
  }

  createRestaurants = (nr) ->
    [1..nr].map (n)->
      id: n
      name: "Rest #{n}"
      description: "desc #{n}"
      catgory: 2
      phone:  "123987123"
      email: "email #{n}"
      website: "web #{n}"
      rating: 3
      street:"Alte Poststraße 147"
      city: "Graz"
      zip:  "8020"
      lat: 47.069718
      lng: 15.409874



  it 'should change the user', ->
    restaurants = createRestaurants(4)
    expect(scope.user).toEqualData {}
    $httpBackend.expectGET('/whoami').respond 200, user
    $httpBackend.expectGET('/rating/favorites').respond 200, restaurants
    $httpBackend.flush()
    expect(scope.user).toEqualData user
    expect(scope.restaurants).toEqualData restaurants
    updateduser={
      id: 1
      firstname: "Jim"
      lastname: "Cena"
      email: "johncena@wad.com"
    }
    $httpBackend.expectPOST('/user/update', scope.user).respond 200, updateduser
    scope.user.firstname = "Jim"
    scope.update()
    $httpBackend.flush()
    expect(scope.user).toEqualData updateduser

