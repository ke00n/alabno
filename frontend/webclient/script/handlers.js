var $handlers = {};

$handlers.handle_login_success = function(msgobj) {
  var token = msgobj.id;
  var username = $globals.top_scope.username;
  $globals.token = token;
  $globals.professor_scope.name = username;
  $globals.professor_scope.$apply();
  
  // Store into local storage
  $localstore.save_username(username);
  $localstore.save_token(token);
  
  $globals.top_scope.logged_in_flag = true;
  $globals.top_scope.$apply();
};

$handlers.handle_login_failure = function(msgobj) {
  alert('Login failed');
};

$handlers.handle_alert = function(msgobj) {
  if (!msgobj.message) {
    console.log("alert message: no message field found");
  }

  alert(msgobj.message);
};

$handlers.handle_job_sent = function(msgobj) {
  console.log('handlers: resetting');
  $globals.professor_scope.reset_submit();
  $globals.professor_scope.$apply();
};

$handlers.handle_job_list = function(msgobj) {
  var jobs = msgobj.jobs;

  if (!jobs) {
    console.log("Error, job_list.job is null");
    return;
  }

  if (!(jobs.constructor === Array)) {
    console.log("Error, job_list.job is not an Array");
    return;
  }

  // Write to model
  $globals.professor_scope.all_jobs = [];

  for (var i = 0; i < jobs.length; i++) {
    var in_job = jobs[i];
    var in_job_title = in_job.title;
    var in_job_status = in_job.status;
    
    var color = "green";
    if (in_job_status == "pending") {
      color = "yellow";
    } else if (in_job_status == "error") {
      color = "red";
    }

    var a_job = {};
    a_job.title = in_job_title;
    a_job.status = in_job_status;
    a_job.color = color;
    a_job.displayed = false;
    a_job.display = function(title) {
      a_job.displayed = !a_job.displayed;
      if (!a_job.displayed) {
        a_job.students = []
      } else {
        console.log("Clicked display on job " + title);
        var msgobj = {};
        msgobj.type = "get_job";
        msgobj.id = $globals.token;
        msgobj.title = title;
        $globals.send(JSON.stringify(msgobj));
      }
    };
    a_job.students = [];

    $globals.professor_scope.all_jobs.push(a_job);
  }

  $globals.professor_scope.$apply();
};

$handlers.handle_job_group = function(msgobj) {
  var title = msgobj.title;
  var group = msgobj.group;

  // check that group is valid
  if (!group) {
    console.log("Error, group is null");
  }
  if (!(group.constructor === Array)) {
    console.log("Error, group is not an array");
  }

  // The student array has objects with field id
  var final_array = [];
  for (var i = 0; i < group.length; i++) {
    var a_student_obj = {};
    a_student_obj.id = group[i];
    final_array.push(a_student_obj);
  }

  // Find the job with the correct title
  for (var i = 0; i < $globals.professor_scope.all_jobs.length; i++) {
    var a_job = $globals.professor_scope.all_jobs[i];

    if (a_job.title == title) {
      // add the students array to this job
      $globals.professor_scope.all_jobs[i].students = final_array;
    }
  }

  $globals.professor_scope.$apply();
};

$handlers.handle_postpro_result = function(msgobj) {
  var title = msgobj.title;
  var student = msgobj.student;
  var data = msgobj.data;

  var dataobj = JSON.parse(data);
  $globals.professor_scope.student_result.letter_score = dataobj.letter_score;
  $globals.professor_scope.student_result.number_score = dataobj.number_score;
  $globals.professor_scope.student_result.annotations = dataobj.annotations;
  $globals.professor_scope.student_result.title = title;
  $globals.professor_scope.student_result.student = student;

  // change view
  $globals.professor_scope.show_section('show_student_result');

  // apply
  $globals.professor_scope.$apply();
};

$handlers.handle_annotated_file = function(msgobj) {

  $globals.professor_scope.annotated_files = [];

  $globals.professor_scope.student_exercise_type = msgobj.exercise_type;
  
  $globals.professor_scope.student_exercise_mark = msgobj.mark;
  
  var files = msgobj.files;

  for (var i = 0; i < files.length; i++) {
    var file = {};
    file.filename = files[i].filename;

    file.displayed = false;
    file.display = function(f) {
      f.displayed = !f.displayed;
    };

    var data_list = [];

    var data_list_length = files[i].data.length;
    for (var j = 0; j < data_list_length; j++) {
      var data_entry = {};
      data_entry.no = files[i].data[j].no;
      data_entry.content = files[i].data[j].content;
      data_entry.annotation = files[i].data[j].annotation;
      data_entry.show_icons = false;
      data_entry.show_icons_unlocked = true;
      data_entry.show_editor = false;
      data_list.push(data_entry);
    }
    file.data = data_list;
    $globals.professor_scope.annotated_files.push(file);
  }

  // change view
  $globals.professor_scope.show_section('show_annotated_file');

  // apply
  $globals.professor_scope.$apply();
};

$handlers.handle_type_list = function(msgobj) {
    var data = msgobj.data;
    $globals.professor_scope.valid_exercise_types = [];
    for (var i = 0; i < data.length; i++) {
        $globals.professor_scope.valid_exercise_types.push(data[i]);
    }
    
    $globals.professor_scope.$apply();
};
