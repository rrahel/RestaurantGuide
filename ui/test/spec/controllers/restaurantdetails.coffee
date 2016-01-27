###
'use strict'

describe 'Controller: RestaurantdetailsCtrl', ->

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
  $controller = {}
  $httpBackend = {}
  $routeParams = {}

  createMember = (id) ->
    id: id
    name: "name#{id}"
    lat: id
    lng: id

  createMarkerFromRestaurant = (r) ->
    lat: r.lat
    lng: r.lng
    message: "#{r.name}"
    draggable: false
    focus: false

  addMarker = (markers,member) ->
    markers["ID_#{member.id}"] = createMarkerFromRestaurant(member)
    markers

  createMarkers = (members) -> members.reduce addMarker,{}

  beforeEach inject ($rootScope,_$controller_,_$httpBackend_,_$routeParams_)->
    scope = $rootScope.$new()
    $controller = _$controller_
    $httpBackend = _$httpBackend_
    $routeParams = _$routeParams_


  it "should fetch the restaurant details and the group members", ->
    members = [1..5].map createMember
    $routeParams.groupId = 7
    $httpBackend.expectGET('/groups/7')
    .respond 200, {id:7,name:"Group 7",description:"Desc7"}
    $httpBackend.expectGET('/groups/7/members')
    .respond 200,members
    $controller 'GroupdetailsCtrl', $scope: scope
    expect(scope.group).toEqualData {}
    expect(scope.members).toEqualData []
    expect(scope.center).toEqual {}
    expect(scope.error).toBe null
    expect(scope.markers).toEqual {}
    $httpBackend.flush()
    expect(scope.group).toEqualData {id:7,name:"Group 7",description:"Desc7"}
    expect(scope.members).toEqualData members
    expect(scope.markers).toEqual createMarkers members
    expect(scope.center).toEqual {zoom: 10, lat: members[0].lat, lng: members[0].lng }


  it "should create an error message if reading the group fails", ->
    $routeParams.groupId = 7
    $httpBackend.expectGET('/groups/7').respond 500, message:"Error!!"
    $httpBackend.expectGET('/groups/7/members').respond 200, []
    $controller 'GroupdetailsCtrl', $scope: scope
    expect(scope.group).toEqualData {}
    expect(scope.members).toEqualData []
    expect(scope.error).toBe null
    $httpBackend.flush()
    expect(scope.error).toBe "Error!!"

  it "should create an error message if reading group members fails", ->
    $routeParams.groupId = 7
    $httpBackend.expectGET('/groups/7')
    .respond 200, {id:7,name:"Group 7",description:"Desc7"}
    $httpBackend.expectGET('/groups/7/members')
    .respond 500, message: "No Members today"
    $controller 'GroupdetailsCtrl', $scope: scope
    expect(scope.group).toEqualData {}
    expect(scope.members).toEqualData []
    expect(scope.error).toBe null
    $httpBackend.flush()
    expect(scope.group).toEqualData {id:7,name:"Group 7",description:"Desc7"}
    expect(scope.error).toBe "No Members today"

  it "should allow to highlight markers programmatically", ->
    members = [1..5].map createMember
    $routeParams.groupId = 7
    $httpBackend.expectGET('/groups/7')
    .respond 200, {id:7,name:"Group 7",description:"Desc7"}
    $httpBackend.expectGET('/groups/7/members')
    .respond 200,members
    $controller 'GroupdetailsCtrl', $scope: scope
    expect(scope.group).toEqualData {}
    expect(scope.members).toEqualData []
    expect(scope.center).toEqual {}
    expect(scope.error).toBe null
    expect(scope.markers).toEqual {}
    $httpBackend.flush()
    markers = createMarkers members
    scope.show(3)
    show3 = (angular.copy markers)
    show3.ID_3.focus=true
    expect(scope.markers).toEqual show3
    scope.show(5)
    show5 = (angular.copy markers)
    show5.ID_5.focus=true
    expect(scope.markers).toEqual show5
###
