'use strict'

describe 'Controller: AdduserCtrl', ->

  # load the controller's module
    beforeEach module 'uiApp'

    scope = {}
    $httpBackend = {}

    users = [
      {
        email: "john@doe.com"
        firstname: "John"
        lastname: "Doe"
        password: "topsecret"
      },
      {
        email: "jane@miller.com"
        firstname: "Jane"
        lastname: "Miller"
        password: "password"
      }
    ]

    user = {
        email: "john@doe.com"
        firstname: "John"
        lastname: "Doe"
        password: "verytopsecret"
      }

  # Initialize the controller and a mock scope
    beforeEach inject ($controller, $rootScope,_$httpBackend_) ->
      scope = $rootScope.$new()
      $httpBackend = _$httpBackend_
      $controller 'AdduserCtrl', $scope: scope

    it "should add and delete new users", ->
      $httpBackend.expectGET("/user").respond 200, users
      expect(scope.users.length).toBe 0
      scope.user = angular.extend scope.user,user
      $httpBackend.expectPOST("/user", user).respond 200
      scope.save()
      $httpBackend.expectGET("/views/main.html").respond 200
      $httpBackend.flush()
      expect(scope.users.length).toBe 2
      $httpBackend.expectDELETE("/user/1").respond 200
      scope.deleteUser(1)
      $httpBackend.flush()



