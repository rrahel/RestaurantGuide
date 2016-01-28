'use strict'

describe 'Controller: AddcategoryCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  scope = {}
  $httpBackend = {}
  $location = {}

  category =
    name: "Albanisch"

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'AddcategoryCtrl', $scope: scope


  it 'should create a new category and go to the start page if successful', ->
    scope.category = angular.extend scope.category,category
    $location.path "/addcategory"
    $httpBackend.expectPOST("/categories",category).respond 200
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/"


  it "should provide an error message if creation fails", ->
    scope.category = angular.extend scope.category,category
    $location.path "/addcategory"
    $httpBackend.expectPOST("/categories",category).respond 500,message: "An Error occured!"
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/addcategory"
    expect(scope.error).toBe "An Error occured!"

