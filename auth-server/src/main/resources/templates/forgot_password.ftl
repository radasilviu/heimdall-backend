<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Heimdall Mail</title>
    <style>
        .container {
            font-family: Ubuntu;
        }
    </style>
</head>

<body>
<div style="
 background: linear-gradient(277deg, #1946db, #ca2580, #1946db);
 padding: 2em;
" class="container">
    <p>
        Click the link to reset your password:
        <a href="${clientFrontedURL}/change-password?email=${email}&forgotPasswordCode=${forgotPasswordCode}">Link</a>
    </p>
</div>
</body>
</html>
