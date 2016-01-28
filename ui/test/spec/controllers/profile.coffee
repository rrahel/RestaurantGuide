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

  it 'change user', ->
    expect(scope.user).toEqual {}
    $httpBackend.expectGET('/whoami').respond 200, user
    $httpBackend.flush()
    expect(scope.user).toEqual user
    updateduser={
      id: 1
      firstname: "jupp"
      lastname: "Cena"
      email: "johncena@wad.com"
    }
    $httpBackend.expectPOST('/user/update', scope.user).respond 200, updateduser
    scope.user.firstname = "jupp"
    scope.update()
    $httpBackend.flush()
    expect(scope.user).toEqualData updateduser

