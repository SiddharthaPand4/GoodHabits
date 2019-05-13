<html>
<head>

    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <title>Login</title>
    <script src="/webjars/jquery/3.0.0/jquery.min.js"></script>
    <script src="/webjars/bootstrap/4.2.1/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/webjars/bootstrap/4.2.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container"><br/>

    <nav class="navbar navbar-inverse navbar-static-top navbar-dark navbar-expand-md">

        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="http://jazari.ai">JAZARI</a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active">
                        <a href="/">Home</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <h1>Login page</h1>
            <form action="/login_check" method="post">
                <div>
                    <div class="alert alert-danger">
                        Invalid username or password.
                    </div>
                </div>
                <div>
                    <div class="alert alert-info">
                        You have been logged out.
                    </div>
                </div>
                <div class="form-group">
                    <label for="username">Username</label>:
                    <input type="text"
                           id="username"
                           name="username"
                           class="form-control"
                           autofocus="autofocus"
                           placeholder="Username">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>:
                    <input type="password"
                           id="password"
                           name="password"
                           class="form-control"
                           placeholder="Password">
                </div>
                <div class="form-group">
                    <div class="row">
                        <div class="col-sm-6 col-sm-offset-3">
                            <input type="submit"
                                   name="login-submit"
                                   id="login-submit"
                                   class="form-control btn btn-info"
                                   value="Log In">
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>
                &copy; SynergyLabs
            </p>
        </div>
    </footer>
</div>
</body>
</html>