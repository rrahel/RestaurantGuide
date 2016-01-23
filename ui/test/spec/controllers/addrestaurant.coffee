'use strict'

describe 'Controller: AddrestaurantCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $location = {}

  restaurant =
    name: "My Restaurant"
    description: "My Description"

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'AddrestaurantCtrl', $scope: scope

###
  it 'should create a new restaurant and go to the start page if successful', ->
    scope.restaurant = angular.extend scope.restaurant,restaurant
    $location.path "/addrestaurant"
    $httpBackend.expectPOST("/restaurants",restaurant).respond 200
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/"


  it "should provide an error message if creation fails", ->
    scope.restaurant = angular.extend scope.restaurant,restaurant
    $location.path "/addrestaurant"
    $httpBackend.expectPOST("/restaurants",restaurant).respond 500,message: "An Error occured!"
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/addrestaurant"
    expect(scope.error).toBe "An Error occured!"
###
