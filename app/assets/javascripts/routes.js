(function() {
    var app = angular.module('vesselApp.routes', ['ngRoute']);

    app.config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/', {
                    templateUrl: '/assets/partials/view.html'
                }).
                when('/vessels/create', {
                    templateUrl: '/assets/partials/create.html'
                }).
                when('/vessels/edit/:name', {
                    templateUrl: '/assets/partials/update.html'
                }).
                otherwise({
                    redirectTo: '/'
                });
        }
    ]);

    app.config(['$locationProvider',
        function($locationProvider) {
            $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            })
        }
    ]);
})();