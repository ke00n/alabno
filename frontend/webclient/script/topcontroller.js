theapp.controller('mycontroller', function($scope) {

    $globals.top_scope = $scope;

    $scope.professor_button_clicked = function()
    {
        window.location.hash = 'professor';
    };

    $scope.student_button_clicked = function()
    {
        window.location.hash = 'student';
    };

    $scope.go_to_dashboard = function()
    {
        console.log("Redirecting to the dashboard of a user type: " + $globals.usertype);
        if ($globals.usertype == "s") {
          $scope.$broadcast('stud_show_default_flags_only');
        } else {
          $scope.$broadcast('prof_show_default_flags_only');
        }
    };

    $scope.login_clicked = function()
    {
        var the_password = '';
        if ($globals.is_secure()) {
        the_password = $scope.password;
        }

        var login_msg_obj = {
        "type": "login",
        "username": $scope.username,
        "password": the_password
        };

        $globals.send(JSON.stringify(login_msg_obj));
    };

    $scope.logged_in_flag = false;
    
    $scope.login_enter = function(keyEvent) {
        if (keyEvent.which === 13) {
        $scope.login_clicked();
        }
    }
    
    $scope.init = function() {
        $localstore.ready += 1;
        $localstore.check_ready();
    }
    
    $scope.logout = function() {
        $scope.logged_in_flag = false;
        
        $localstore.save_username('');
        $localstore.save_token(undefined);
        
        window.location.hash = 'landing';
        // location.reload();
    }
    
    // #########################################################################
    // Status information panels
    
    $scope.statusinformation = [];
    
    $scope.dismiss_statusinformation = function(idx) {
        $scope.statusinformation.splice(idx, 1);
    }
});
