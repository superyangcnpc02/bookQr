<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/10/9
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<br>
<br>

<div align="center">
    <h1>Crunchify - Spring MVC Upload Multiple Files Example</h1>

    <form method="post" action="/bookQr/file/upload"
          enctype="multipart/form-data">

        <p>Select files to upload. Press Add button to add more file
            inputs.</p>

        <table id="fileTable">
            <tr>
                <td><input name="file" type="file"/></td>
            </tr>
            <tr>
                <td><input name="file" type="file"/></td>
            </tr>
        </table>
        <br/>

        <input type="submit" value="Upload"/>

    </form>

    <br/>
</div>
</body>
</html>
