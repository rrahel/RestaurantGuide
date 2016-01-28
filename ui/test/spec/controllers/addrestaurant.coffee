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

describe 'Controller: AddrestaurantCtrl', ->

# load the controller's module
  beforeEach ->
    module 'uiApp'
    module 'geoCoderMock'

  scope = {}
  $httpBackend = {}
  $location = {}
  GeoCoder = {}

  createRestaurants = (nr) ->
    [1..nr].map (n)->
      id: n
      name: "Rest #{n}"
      description: "desc #{n}"
      category: 2
      phone:  "123987123"
      email: "email #{n}"
      website: "web #{n}"
      rating: 3
      street:"Alte Poststraße 147"
      city: "Graz"
      zip:  "8020"
      lat: 47.069718
      lng: 15.409874

  createCategories = (nr) ->
    [0..nr].map (n) ->
      id: n
      name: "Category #{n}"


  restaurant =
    name: "Rest"
    description: "desc"
    category: 2
    phone:  "123987123"
    email: "email"
    website: "web"
    rating: 3
    street:"Street 1"
    city: "Graz"
    zip:  "8020"
    lat: 47.069718
    lng: 15.409874


  location =
    lat:  47.069718
    lng:  15.409874
  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope,_$httpBackend_,_$location_,_GeoCoder_) ->
    scope = $rootScope.$new()
    $location = _$location_
    $httpBackend = _$httpBackend_
    GeoCoder = _GeoCoder_
    $controller 'AddrestaurantCtrl', $scope: scope


  it 'should create a new restaurant', ->
    categories = createCategories(3)
    restaurants = createRestaurants(5)
    $httpBackend.expectGET('/restaurants').respond 200, restaurants
    $httpBackend.expectGET('/categories').respond 200, categories
    expect(scope.restaurant).toEqual {}
    expect(scope.categories).toEqual []
    $httpBackend.expectGET('/views/main.html').respond 200
    $httpBackend.flush()
    expect(scope.categories).toEqual categories
    newRestaurant = angular.copy restaurant
    newRestaurant.lat = location.lat
    newRestaurant.lng = location.lng
    $httpBackend.expectPOST('/restaurants', newRestaurant).respond 200
    scope.restaurant.name="Rest"
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
    scope.save()
    GeoCoder.flush()
    expect(scope.restaurant.lat).toBe location.lat
    expect(scope.restaurant.lng).toBe location.lng
    $httpBackend.flush()
    $httpBackend.expectDELETE('/restaurants/1').respond 200
    scope.deleteRestaurant(1)
    expect($location.path()).toBe "/"
