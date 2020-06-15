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
               .main1 {border: 2px  solid Bisque; padding: 2px 20px 2px;}

            </style> 　　

        </head>
        <body>
            <div id="main">

                    您提供的图书配套资源收到了客户${email ? if_exists }的反馈,内容如下:<br />
                    ${type ? if_exists }<br /><br />
                    ${detail.remark ? if_exists }<br /><br />
                    资源详情如下:<br />
                    <div class="main1">
                        <b>
                            书籍名称:${detail.bookName ? if_exists }<br />
                            二维码名称:${detail.qrName ? if_exists }<br />
                            文件名称:${detail.resName ? if_exists }<br />
                            文件大小:${detail.size ? if_exists }<br />
                            文件后缀:${detail.suffix ? if_exists }<br />
                            下载次数:${detail.num ? if_exists }<br />
                            <a href="${toUrl ? if_exists }">点击查看</a>
                         </b>
                    </div>

            </div>
        </body>
    </html>

</xml-body>