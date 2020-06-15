<?xml version="1.0" encoding="UTF-8"?>

<xml-body>
    <html>

        <head>
            <style type="text/css">
                #main{
                    width:966px;
                    text-align:left;
                    margin:10px 50px 10px;
                    background-color:#F5F5F5;
                    border:1px #E1E1E1 solid;
                    padding: 20px 20px;
                }  　　

            </style> 　　

        </head>
        <body>
            <div id="main">

                    文泉云盘客服:<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好！<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;读者${email ? if_exists }反馈${type ? if_exists }内容如下：<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${detail.remark ? if_exists }<br /><br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请尽快处理！

            </div>
        </body>
    </html>

</xml-body>