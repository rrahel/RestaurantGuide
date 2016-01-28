#A fake google service for the production service
google =
  maps:
    Geocoder: ->
    GeoCoderStatus:
      OK: "Ok"

#A module containing the fake service using the same name ('GeoCoder') as in the application module
geoMock = angular.module 'geoCoderMock',[]
geoMock.service "GeoCoder", ($q,$rootScope) ->
  @expectedCodings = []
  @expectCoding = (address) ->
    self = @
    respond: (lat,lng) ->
      self.expectedCodings.push address: address, lat: lat, lng: lng
    respondError: ->
      self.expectedCodings.push address: address, error: "Error"
  @geocode = (address) ->
    if @expectedCodings.length is 0
      throw Error "No more geocodings expected!"
    coding = @expectedCodings.shift()
    if address isnt coding.address
      throw Error "Expected #{coding.address} but got #{address}"
    defer = $q.defer()
    if coding.error?
      defer.reject "Could not geocode your address!"
    else
      defer.resolve(
        lat: -> coding.lat
        lng: -> coding.lng)
    defer.promise
  @flush = () ->
    $rootScope.$digest()
    if @expectedCodings.length > 0
      throw Error "Not all geocodings happened!"
  return



'use strict'

describe 'Controller: UpdaterestaurantCtrl', ->

# load the controller's module
  beforeEach ->
    module 'uiApp'
    module 'geoCoderMock'

  scope = {}
  $httpBackend = {}
  $routeParams = {}
  $location = {}
  $controller={}
  GeoCoder = {}

  createCategories = (nr) ->
    [1..nr].map (n) ->
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

  restId = 1

  location =
    lat:47.069718
    lng:15.409874

  # Initialize the controller and a mock scope
  beforeEach inject (_$controller_, $rootScope,_$httpBackend_,_$routeParams_,_$location_,_GeoCoder_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_
    $location = _$location_
    GeoCoder = _GeoCoder_
    $controller = _$controller_


  it 'should update a restaurant', ->
    categories = createCategories(3)
    $routeParams.restId = 1
    $controller 'UpdaterestaurantCtrl', $scope: scope

    $httpBackend.expectGET('/categories').respond 200, categories
    $httpBackend.expectGET("/restaurants/#{restId}").respond 200, restaurant
    expect(scope.restaurant).toEqual {}
    expect(scope.categories).toEqual []
    $httpBackend.flush()
    expect(scope.categories).toEqual categories
    newRestaurant.lat = location.lat
    newRestaurant.lng = location.lng
    $httpBackend.expectPOST("/restaurants/#{restId}", scope.restaurant).respond 200, newRestaurant
    scope.restaurant.name="Restaurant"
    scope.restaurant.description = "desc"
    scope.restaurant.category = 2
    scope.restaurant.phone = "123987123"
    scope.restaurant.email = "email"
    scope.restaurant.website = "web"
    scope.restaurant.rating = 3
    scope.restaurant.zip = "8020"
    scope.restaurant.city = "Graz"
    scope.restaurant.street = "Street 1"
    GeoCoder.expectCoding("8020 Graz, Street 1").respond(location.lat,location.lng)
    scope.updateRestaurant()
    GeoCoder.flush()
    expect(scope.restaurant.lat).toBe location.lat
    expect(scope.restaurant.lng).toBe location.lng
    $httpBackend.flush()
    expect($location.path()).toBe "/addRestaurant"
