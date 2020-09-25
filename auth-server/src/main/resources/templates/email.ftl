<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Heimdall Mail</title>
    <link href="src/main/resources/templates/style.css" rel="stylesheet" type="text/css">
</head>

<body>
<div style="
 background: linear-gradient(277deg, #1946db, #ca2580, #1946db);
 padding: 2em;
">
    <div style="text-align: center;">
        <h1>Dear ${Username}</h1>
    </div>
    <div style="text-align: center"><h2>The Heimdall Project</h2></div>
    <p style="text-align: center">Please click the link below to activate your account:</p>
    <h3 style="text-align: center"><a href="${VerifyUrl}" style="text-decoration: none;color: aquamarine">Activate
            account</a></h3>
    <p style="text-align: center">The ${Name} thanks you for using our services!</p>
</div>
</body>
</html>