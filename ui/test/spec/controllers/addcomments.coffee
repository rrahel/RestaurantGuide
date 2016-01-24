'use strict'

describe 'Controller: AddcommentsCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $location = {}

  comment =
    content: "testComment"
    userId: 1
    restaurantId: 1

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'AddcommentsCtrl', $scope: scope

  it 'should create a new comment and go to the start page if successful', ->
    scope.comment = angular.extend scope.comment,comment
    $location.path "/addComments"
    $httpBackend.expectPOST("/comment",comment).respond 200
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/"


  it "should provide an error message if creation fails", ->
    scope.comment = angular.extend scope.comment,comment
    $location.path "/addComments"
    $httpBackend.expectPOST("/comment",comment).respond 500,message: "An Error occured!"
    scope.save()
    $httpBackend.flush()
    expect($location.path()).toBe "/addComments"
    expect(scope.error).toBe "An Error occured!"