'use strict'

###*
 # @ngdoc service
 # @name uiApp.GeoCoder
 # @description
 # # GeoCoder
 # Factory in the uiApp.
###
angular.module 'uiApp'
.factory 'GeoCoder', ($q)->
  geocoder = new google.maps.Geocoder()
  geocode: (address) ->
    deferred = $q.defer()
    geocoder.geocode address: address, (results, status) ->
      if status == google.maps.GeocoderStatus.OK
        deferred.resolve(
          lat: -> results[0].geometry.location.lat()
          lng: -> results[0].geometry.location.lng()
        )
      else deferred.reject "Could not geocode your address"
    deferred.promise
