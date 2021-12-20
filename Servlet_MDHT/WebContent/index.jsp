<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="">
<!--<![endif]-->

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-select.min.css">
    <link rel="stylesheet" href="css/bootstrap-datepicker3.min.css">
    <style>
        body {
            padding-top: 50px;
            padding-bottom: 20px;
        }
        
        hr {
            border-color: #fff;
        }
    </style>
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="css/main.css">

    <script src="js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script>
</head>

<body>

    <!--[if lt IE 8]>
        <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
    <![endif]-->

    <div class="container">
        <div class="row">
            <div class="col-md-4 col-md-offset-4">

                <div class="alert alert-success alert-dismissible fade in" role="alert" ${hidden}>
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                    <strong>Success!</strong> You have successfully merged <i>'${file1}'</i> and <i>'${file2}'</i> together.
                </div>
            </div>

            <div class="col-md-4 col-md-offset-4 well">
                <form class="form" action="ServletMain" method="post" enctype="multipart/form-data" id="form">

                    <h3 style="margin-top:0px">Client Information</h3>

                    <hr>

                    <div class="form-group">
                        <label for="file_from_client">Client records</label>
                        <input type="file" id="file_from_client" name="file_from_client" required>
                    </div>

                    <div class="form-group">
                        <label for="client_name">Last name</label>
                        <input type="text" class="form-control" name="client_name" id="client_name" placeholder="Smith" required>
                    </div>

                    <div class="form-group">
                        <label for="client_dob">Birth date</label>
                        <div class="input-group">
                            <input type="text" name="client_dob" class="datepicker form-control" id="client_dob" placeholder="e.g. 12-23-1990" required> <span class="input-group-addon"> <span
				class="glyphicon glyphicon-calendar" aria-hidden="true"></span>
                            </span>
                        </div>
                    </div>
					
					<div class="form-group">
						<label for="file_from_server">Stored client records</label>
						<div class="input-group" style="width: 100%">
							<select class="form-control file_from_server required"
							id="file_from_server" name="file_from_server"
							title='Choose a file...' required>
								<option selected="selected">Select a file...</option>
								<c:forEach var="file" items="${files}">
									<option><c:out value="${file.getName()}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					
                    <hr>

                    <label for="merge_method">How do you wish to merge?</label>
                    <div class="radio">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios1" value="1" checked> Merge with duplicates
                        </label>
                    </div>
                    <div class="radio">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios2" value="2" disabled> Merge without duplicates
                        </label>
                    </div>
                    <div class="radio">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios2" value="3" disabled> Ask me what to do
                        </label>
                    </div>

                    <button type="submit" class="btn btn-primary submit" style="width: 100%;">Merge!</button>
                </form>
            </div>
        </div>
    </div>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script>
        window.jQuery || document
            .write('<script src="js/vendor/jquery-1.11.2.min.js"><\/script>')
    </script>
    <script src="js/vendor/bootstrap.min.js"></script>
    <script src="js/bootstrap-select.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/bootstrap-datepicker.min.js"></script>
    <script src="js/main.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
			
        	$("#file_from_server option").each(function() {
        		$(this).hide();
     		});
        	
            $(".alert").alert();
            window.setTimeout(function() {
                $(".alert").alert('close');
            }, 4000);

            $('.selectpicker').selectpicker();

            $('select').on('change', function() {
                if ($("select").val() != -1) {
                    $(".submit").prop("disabled", false);
                }
            });

            $('.datepicker').datepicker({
                format: "yyyy-mm-dd",
                clearBtn: true
            }).on("changeDate", function(e) {
			
            	var name = $("#client_name").val();
            	var dob = $(".datepicker").val().replace(/-/g, "");
            	 
            	var expected_file_name = (name + dob + ".xml").toLowerCase();
            	
            	$("#file_from_server option").each(function() {
            		$(this).show();
            		
         			if ($(this).val() != expected_file_name) {
         				$(this).hide();
         			}
         		});
            });
        });
        
        (function($, window) {
        	  $.fn.replaceOptions = function(options) {
        	    var self, $option;

        	    this.empty();
        	    self = this;

        	    $.each(options, function(index, option) {
        	      $option = $("<option></option>")
        	        .attr("value", option.value)
        	        .text(option.text);
        	      self.append($option);
        	    });
        	  };
        	})(jQuery, window);
    </script>
</body>
</html>