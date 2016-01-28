'use strict'

describe 'Controller: AddcategoryCtrl', ->

  # load the controller's module
  beforeEach module 'uiApp'

  scope = {}
  $httpBackend = {}
  $location = {}

  categories = [
    {
      name: "Albanisch"
    },
    {
      name: "Italienisch"
    }
  ]

  category = {
    name: "Russisch"
  }

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    $controller 'AddcategoryCtrl', $scope: scope


  it 'should create a new category', ->
    $httpBackend.expectGET("/categories").respond 200, categories
    expect(scope.categories.length).toBe 0
    scope.category = angular.extend scope.category, category
    $httpBackend.expectPOST("/categories", category).respond 200
    scope.save()
    $httpBackend.expectGET("/views/main.html").respond 200
    $httpBackend.flush()
    expect(scope.categories.length).toBe 2

  it 'should delete a category', ->
    $httpBackend.expectGET("/categories").respond 200, categories
    expect(scope.categories.length).toBe 0
    scope.category = angular.extend scope.category, category
    $httpBackend.expectPOST("/categories", category).respond 200
    scope.save()
    $httpBackend.expectGET("/views/main.html").respond 200
    $httpBackend.flush()
    expect(scope.categories.length).toBe 2
    $httpBackend.expectDELETE("/categories/1").respond 200
    scope.deleteCategory(1)
    $httpBackend.flush()


  it "should provide an error message if creation fails", ->
    $httpBackend.expectGET("/categories").respond 200, categories
    expect(scope.categories.length).toBe 0
    scope.category = angular.extend scope.category,category
    $httpBackend.expectPOST("/categories",category).respond 500,message: "An Error occured!"
    scope.save()
    $httpBackend.expectGET("/views/main.html").respond 200
    $httpBackend.flush()
    expect(scope.error).toBe "An Error occured!"

