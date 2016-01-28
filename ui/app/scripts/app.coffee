'use strict'

###*
 # @ngdoc overview
 # @name uiApp
 # @description
 # # uiApp
 #
 # Main module of the application.
###
app = angular
.module('uiApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ngQuantum',
    'satellizer',
    'ui.bootstrap',
    'leaflet-directive'
  ])

app.run ($rootScope)->
  $rootScope.user = {}

app.config ($routeProvider,$httpProvider, $authProvider) ->
  $routeProvider
  .when '/',
    templateUrl: '/views/main.html'
    controller: 'MainCtrl'
  .when '/signUp',
    templateUrl: '/views/signup.html'
    controller: 'SignUpCtrl'
  .when '/signIn',
    templateUrl: 'views/signin.html'
    controller: 'SigninCtrl'
  .when '/listUsers',
    templateUrl: 'views/listusers.html'
    controller: 'ListusersCtrl'
  .when '/addCategory',
    templateUrl: 'views/addcategory.html'
    controller: 'AddcategoryCtrl'
    controllerAs: 'addcategory'
  .when '/addRestaurant',
    templateUrl: 'views/addrestaurant.html'
    controller: 'AddrestaurantCtrl'
  .when '/updateComment',
    templateUrl: 'views/updatecomment.html'
    controller: 'UpdatecommentCtrl'
    controllerAs: 'updateComment'
  .when '/restaurantDetails',
    templateUrl: 'views/restaurantdetails.html'
    controller: 'RestaurantdetailsCtrl'
    controllerAs: 'restaurantDetails'
  .when '/listRestaurants',
    templateUrl: 'views/listrestaurants.html'
    controller: 'ListrestaurantsCtrl'
    controllerAs: 'listRestaurants'
  .when '/listCategories',
    templateUrl: 'views/listcategories.html'
    controller: 'ListcategoriesCtrl'
    controllerAs: 'listCategories'
  .when '/profile',
    templateUrl: 'views/profile.html'
    controller: 'ProfileCtrl'
    controllerAs: 'profile'
  .when '/search',
    templateUrl: 'views/search.html'
    controller: 'SearchCtrl'
    controllerAs: 'search'
  .when '/updateCategory',
    templateUrl: 'views/updatecategory.html'
    controller: 'UpdatecategoryCtrl'
    controllerAs: 'updateCategory'
  .when '/addUser',
    templateUrl: 'views/adduser.html'
    controller: 'AdduserCtrl'
    controllerAs: 'addUser'
  .otherwise '/'

  $httpProvider.interceptors.push ($q, $injector) =>
    request: (request) =>
      # Add auth token for Silhouette if user is authenticated
      $auth = $injector.get('$auth');
      if $auth.isAuthenticated()
        request.headers['X-Auth-Token'] = $auth.getToken()
      # Add CSRF token for the Play CSRF filter
      cookies = $injector.get('$cookies');
      token = cookies.get('PLAY_CSRF_TOKEN');
      if token
        # Play looks for a token with the name Csrf-Token
        # https://www.playframework.com/documentation/2.4.x/ScalaCsrf
        request.headers['Csrf-Token'] = token
      request
    responseError: (rejection) ->
      if rejection.status == 401
        $injector.get('$location').path('/signIn')
      $q.reject(rejection)

  # Auth config
  $authProvider.httpInterceptor = true; # Add Authorization header to HTTP request
  $authProvider.loginOnSignup = true
  $authProvider.loginRedirect = '/'
  $authProvider.logoutRedirect = '/'
  $authProvider.signupRedirect = '/'
  $authProvider.loginUrl = '/signin'
  $authProvider.signupUrl = '/signup'
  $authProvider.loginRoute = '/signin'
  $authProvider.signupRoute = '/signup'
  $authProvider.tokenName = 'token'
  $authProvider.tokenPrefix = 'satellizer' # Local Storage name prefix
  $authProvider.authHeader = 'X-Auth-Token'
  $authProvider.platform = 'browser'
  $authProvider.storage = 'localStorage'


  # Facebook
  $authProvider.facebook({
   clientId: '1503078423241610',
   url: '/authenticate/facebook',
   scope: 'email',
   scopeDelimiter: ',',
   requiredUrlParams: ['display', 'scope'],
   display: 'popup',
   type: '2.0',
   popupOptions: { width: 481, height: 269 }
  })

  # Google
  $authProvider.google({
   clientId: '526391676642-nbnoavs078shhti3ruk8jhl4nenv0g04.apps.googleusercontent.com',
   url: '/authenticate/google',
   scope: ['profile', 'email'],
   scopePrefix: 'openid',
   scopeDelimiter: ' ',
   requiredUrlParams: ['scope'],
   optionalUrlParams: ['display'],
   display: 'popup',
   type: '2.0',
   popupOptions: { width: 580, height: 400 }
  })

  # VK
  $authProvider.oauth2({
   clientId: '4782746',
   url: '/authenticate/vk',
   authorizationEndpoint: 'http://oauth.vk.com/authorize',
   name: 'vk',
   scope: 'email',
   scopeDelimiter: ' ',
   requiredUrlParams: ['display', 'scope'],
   display: 'popup',
   popupOptions: { width: 495, height: 400 }
  })

  # Twitter
  $authProvider.twitter({
   url: '/authenticate/twitter',
   type: '1.0',
   popupOptions: { width: 495, height: 645 }
  });

  # Xing
  $authProvider.oauth1({
   url: '/authenticate/xing',
   name: 'xing',
   popupOptions: { width: 495, height: 500 }
  }) #


