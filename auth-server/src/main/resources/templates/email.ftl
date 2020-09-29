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
    <div style="text-align: center;">
        <h1>Dear ${Username}</h1>
    </div>
    <div style="text-align: center"><h2>The Heimdall Project</h2></div>
    <p style="text-align: center">Please click the link below to activate your account:</p>
    <div style="text-align: center;"><h3 style="text-align: center"><a
                    href="${VerifyUrl}"
                    style="text-decoration: none;color:red; background-color: #e7e7e7;color: black;border: none;padding: 15px 32px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;">Activate
                account</a></h3></div>

    <p style="text-align: center">The ${Name} thanks you for using our services!</p>
</div>
</body>
</html>