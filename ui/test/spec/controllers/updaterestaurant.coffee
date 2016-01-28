'use strict'

describe 'Controller: UpdaterestaurantCtrl', ->

# load the controller's module
  beforeEach module 'uiApp'


  scope = {}
  $httpBackend = {}
  $routeParams = {}

  createCategories = (nr) ->
    [0..nr].map (n) ->
      id: n
      name: "Category #{n}"


  restaurant =
    name: "Rest"
    description: "desc"
    catgory: 2
    phone:  "123987123"
    email: "email"
    website: "web"
    rating: 3
    street:"Street 1"
    city: "Graz"
    zip:  "8020"
    lat: 47.069718
    lng: 15.409874

  newRestaurant =
    name: "Restaurant"
    description: "desc"
    catgory: 2
    phone:  "123987123"
    email: "email"
    website: "web"
    rating: 3
    street:"Street 1"
    city: "Graz"
    zip:  "8020"
    lat: 47.069718
    lng: 15.409874

  restId = undefined

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$routeParams_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_
    $controller 'UpdaterestaurantCtrl', $scope: scope

  it 'should update a restaurant', ->
    categories = createCategories(3)
    $httpBackend.expectGET('/categories').respond 200, categories
    expect(scope.restaurant).toEqual {}
    expect(scope.categories).toEqual []
    $httpBackend.flush()
    expect(scope.categories).toEqual categories
    $routeParams.restId = 1
    restId = $routeParams.restId
    $httpBackend.expectGET('/restaurants/1').respond 200, restaurant
    newRestaurant.lat = location.lat
    newRestaurant.lng = location.lng
    $httpBackend.expectPOST('/restaurants', newRestaurant).respond 200
    GeoCoder.expectCoding("8020 Graz, Street 1").respond(location.lat,location.lng)
    scope.updateRestaurant()
    GeoCoder.flush()
    expect(scope.restaurant.lat).toBe location.lat
    expect(scope.restaurant.lng).toBe location.lng
    $httpBackend.flush()
    expect($location.path()).toBe "/addRestaurant"
