(function() {
    var app = angular.module('vesselApp.vessels', []);

    app.controller('CreateVesselCtrl', ['$log', '$location', 'VesselSvc', function($log, $location, VesselSvc) {
        $log.debug("Constructing CreateVesselCtrl");
        var createVesselCtrl = this;
        this.vessel = {};

        this.createVessel = function() {
            $log.debug("createVessel()");

            VesselSvc.createVessel(this.vessel).then(function(data) {
                $log.debug("Promise returned " + data);
                createVesselCtrl.vessel = data;
                $location.path("/");
            }, function(error) {
                $log.error("Unable to create vessel: error=" + error);
            });
        };
    }]);

    app.controller('UpdateVesselCtrl', ['$log', '$location', '$routeParams', '$filter', 'VesselSvc', function($log, $location, $routeParams, $filter, VesselSvc) {
        $log.debug("Constructing UpdateVesselCtrl");
        var updateVesselCtrl = this;
        this.vessel = {};

        this.findVessel = function() {
            var name = $routeParams.name;
            $log.debug("findVessel(" + name + ")");

            VesselSvc.listVessels().then(function(data) {
                $log.debug("Promise returned " + data.length + " vessels");
                updateVesselCtrl.vessel = $filter('filter')(data, {name: name})[0];
            }, function(error) {
                $log.error("Unable to find user: error=" + error);
            });
        };

        this.deleteVessel = function() {
            $log.debug("deleteVessel()");

            VesselSvc.deleteVessel($routeParams.name, this.vessel).then(function(data) {
                $log.debug("Promise returned " + data);
                updateVesselCtrl.vessel = data;
                $location.path("/");
            }, function(error) {
                $log.error("Unable to delete vessel: error=" + error);
            });
        };

        this.updateVessel = function() {
            $log.debug("updateVessel()");

            VesselSvc.updateVessel($routeParams.name, this.vessel).then(function(data) {
                $log.debug("Promise returned " + data);
                updateVesselCtrl.vessel = data;
                $location.path("/");
            }, function(error) {
                $log.error("Unable to update vessel: error=" + error);
            });
        };

        this.findVessel();
    }]);

    app.controller('ReadVesselCtrl', ['$log', 'VesselSvc', function($log, VesselSvc) {
        $log.debug("Constructing ReadVesselCtrl");
        var readVesselCtrl = this;
        this.vessels = [];

        this.getAllVessels = function() {
            $log.debug("getAllVessels()");

            VesselSvc.listVessels().then(function(data) {
                $log.debug("Promise returned " + data.length + " vessels");
                readVesselCtrl.vessels = data;
            }, function(error) {
                $log.error("Unable to get vessels: error=" + error);
            });
        };

        this.getAllVessels();
    }]);

    app.service('VesselSvc', ['$log', '$http', '$q', function($log, $http, $q) {
        $log.debug("Constructing VesselSvc");

        this.listVessels = function() {
            $log.debug("listVessels()");

            var deferred = $q.defer();

            $http.get("/vessels")
                .success(function(data, status, headers) {
                    $log.info("Successfully listed vessels: status=" + status);
                    deferred.resolve(data);
            })
                .error(function(data, status, headers) {
                    $log.info("Failed to list vessels: status=" + status);
                    deferred.reject(data);
            });

            return deferred.promise;
        };

        this.createVessel = function(vessel) {
            $log.debug("createVessel(" + angular.toJson(vessel, true) + ")");

            var deferred = $q.defer();

            $http.post("/vessel", vessel)
                .success(function(data, status, headers) {
                    $log.info("Successfully created vessel: status=" + status);
                    deferred.resolve(data);
                })
                .error(function(data, status, headers) {
                    $log.info("Failed to create vessel: status=" + status);
                    deferred.reject(data);
                });

            return deferred.promise;
        };

        this.updateVessel = function(name, vessel) {
            $log.debug("updateVessel(" + angular.toJson(vessel, true) + ")");

            var deferred = $q.defer();

            $http.put("/vessel/" + name, vessel)
                .success(function(data, status, headers) {
                    $log.info("Successfully updated vessel: status=" + status);
                    deferred.resolve(data);
                })
                .error(function(data, status, headers) {
                    $log.info("Failed to update vessel: status=" + status);
                    deferred.reject(data);
                });

            return deferred.promise;
        };

        this.deleteVessel = function(name, vessel) {
            $log.debug("deleteVessel(" + angular.toJson(vessel, true) + ")");

            var deferred = $q.defer();

            $http.delete("/vessel/" + name, vessel)
                .success(function(data, status, headers) {
                    $log.info("Successfully deleted vessel: status=" + status);
                    deferred.resolve(data);
                })
                .error(function(data, status, headers) {
                    $log.info("Failed to delete vessel: status=" + status);
                    deferred.reject(data);
                });

            return deferred.promise;
        };
    }]);
})();
