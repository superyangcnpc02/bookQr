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
                <#if passUrl??>
                    <div class="main1"><b><a href="${passUrl ? if_exists }">审核通过</a></b></div>
                </#if>

                    书籍名称:《${book.name ? if_exists }》<br />
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

                    申请邮箱：${client.email ? if_exists }<br />
                    资源地址：   <a href="${url ? if_exists }">点击查看</a>

                    <div class="main1">
                        <b>
                            姓名:${client.name ? if_exists }<br />
                            性别:<#if client.sex = 1> 男 <#else> 女 </#if> <br />
                            年龄:${client.age ? if_exists }<br />
                            学校:${client.school ? if_exists }<br />
                            院系:${client.depart ? if_exists }<br />
                            城市:${client.seat ? if_exists }<br />
                            手机:${client.phone ? if_exists }<br />

                            <br />
                         </b>
                    </div>


            </div>
        </body>
    </html>

</xml-body>