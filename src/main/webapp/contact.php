<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet"	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
	<!--<link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css" rel="stylesheet" >-->
	<link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"	rel="stylesheet" type="text/css">
	<link href='http://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'	rel='stylesheet' type='text/css'>
	<link href="css/clean-blog.css" rel="stylesheet">
	<link href="css/bootstrap-select.min.css" rel="stylesheet">
	<link href="http://cdn.datatables.net/1.10.7/css/jquery.dataTables.css" rel="stylesheet" type="text/css" >
	<link href="css/materialize.css" rel="stylesheet">
	<link href="css/clean-blog.css" rel="stylesheet">
	<link href="css/bootstrap-select.css" rel="stylesheet">
 
<title>Biodiversity Linked Organisms Occurrences Megadatasets</title>
</head>
<body>
<nav>
	<div class="nav-wrapper #4db6ac teal lighten-2">
		<ul id="nav" class="right hide-on-med-and-down">
			<li><a href="HomePage.html">Home</a></li>
			<li><a href="LaunchPage.html">Start</a></li>
			<li><a href="AboutPage.html">About</a></li>
			<li><a href="DocumentationPage.html">Documentation</a></li>
			<li><a href="Contact.html">Contact</a></li>
		</ul>
	</div>
</nav>
<header>
	<div class="container section no-pad-bot">
		<div class="row">
			<div class="col-lg-12 col-lg-offset-2 col-md-10 col-md-offset-1">
				<div class="post-heading">
					<h1>BLOOM</h1>
					<h2 class="subheading">Workflow for open data curation</h2>
				</div>
			</div>
		</div>
	</div>
</header>
	<div class="container">
		<div id="divBody" class="row">
            
				<?php
				if (isset ( $_POST ) && isset ( $_POST ['name'] ) && isset ( $_POST ['email'] ) && isset ( $_POST ['object'] ) && isset ( $_POST ['message'] )) {
					if (! empty ( $_POST ['name'] ) && ! empty ( $_POST ['email'] ) && ! empty ( $_POST ['message'] )) {
						$destinataire = "melanie.hachet@gmail.com";
						$sujet = "Contact request from " . $_POST ['name'] . " - " . $_POST ['object'];
						$message = "Name : " . $_POST ['name'] . "<br>";
						$message .= "email : " . $_POST ['email'] . "<br>";
						$message .= "Message : <br><br>" .  str_replace("\r\n", "<br>", wordwrap($_POST ['message'], 70, "<br>"));
						$header = 'From: ' . $_POST ['email'] . "\r\n" . 'Reply-To: ' . $_POST ['email'] . "\r\n" . "Content-type: text/html; charset=UTF-8" ;
						echo "<div class='col-lg-12'>";
						if (mail ( $destinataire, $sujet, $message, $header )) {
							echo "<h3>Thank you for your message. It has been sent to our team.</h3>";
							echo "<hr class='col-lg-12'</hr>";
							echo "<div class='col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1'>";
							echo "<h4 class='contactRequest'>Your message sent</h4>";
							echo "<blockquote>";
                            echo "<p>" . str_replace("\r\n", "<br>", $_POST ['message']) . "<p>";
                        	echo "</blockquote>";
                        	echo "</div>";
						} else {
							echo "<h3>Sorry ... A error occured when submitting the form by email.</h3>";
							echo "<hr class='col-lg-12'</hr>";
							echo "<h4 class='contactRequest'>Your message </h4>";
							echo "<blockquote>";
                            echo "<p>" . str_replace("\r\n", "<br>", $_POST ['message']) . "<p>";
                        	echo "</blockquote>";
                        	echo "</div>";
						}
						echo "</div>";
					}
				}
				?>
            </div>
        </div>
<!--	<hr></hr>
	 Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <ul class="list-inline text-center">
                        <li>
                            <a href="http://www.cnrs.fr" target="_blank">
                                <img src="images/logo_cnrs.jpg" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="https://www.mnhn.fr/fr" target="_blank">
                                <img src="images/logo_mnhn.jpg" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://isyeb.mnhn.fr/" target="_blank">
                               <img src="images/logo_ISYEB.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://www.upmc.fr/" target="_blank">
                                <img src="images/logo_upmc.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://www.ephe.fr/" target="_blank">
                                <img src="images/logo_ephe.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                    </ul>
                    <p class="copyright text-muted">Copyright &copy; BLOOM 2015</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script src="../bootstrap/js/bootstrap.min.js"></script>
	<script src="../js/jquery.watable.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript"></script>
    <script type="text/javascript" src="js/functionsFinal.js"></script>
</body>
</html>              
                