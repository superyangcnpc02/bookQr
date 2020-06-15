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
                <div class="main1">

                    很抱歉，您的课件申请未通过审核，如需重新申请，请重新扫描课件二维码，填写完整信息,并提交教师身份证明材料


                </div>
                    驳回理由：${reason ? if_exists }
                    <br />
                    申请邮箱：${client.email ? if_exists }
                    <br />
                    书籍名称:《${book.name ? if_exists }》
                    课件列表  <br />
                <div class="main1">
                    <b>
                    <#list bqList ? if_exists as bq>
                        课件名称：${bq.name ? if_exists }
                        课件大小：${bq.size ? if_exists }
                        <br />
                    </#list>


                    </b>
                </div>

            </div>
        </body>
    </html>

</xml-body>