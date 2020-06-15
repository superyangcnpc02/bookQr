package com.yxtech.sys.controller;

import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.dao.QrUserInfoMapper;
import com.yxtech.sys.domain.QrUserInfo;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.bookQr.BookQrParamVO;
import com.yxtech.utils.file.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * 图书管理控制器
 * @author yanfei
 * @since 2015-10-14
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/powerQr")
public class AuthQrController {
    private static final Logger log = LoggerFactory.getLogger(AuthQrController.class);
    @Autowired
    private QrExportService qrExportService;
    @Autowired
    private BookService bookService;
    @Autowired
    private QrUserInfoService qrUserInfoService;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private QrUserInfoMapper qrUserInfoMapper;
    @Autowired
    private UserCenterService userCenterService;
    @Value("#{configProperties['qq.appId']}")
    private String qqAppId;
    @Value("#{configProperties['qq.appKey']}")
    private String qqAppKey;
    @Value("#{configProperties['qq.callBackUrl']}")
    private String qqCallBackUrl;

    /**
     * 图书下面权限二维码导出记录列表
     * @param bookId
     * @author cuihao
     * @return
     */
    @RequestMapping(value = "/exportRecord", method = RequestMethod.GET)
    public JsonResultList exportRecord(@RequestParam("id") Integer bookId) {
        Assert.isTrue(bookId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));

        return qrExportService.exportRecord(bookId,2);
    }

    /**
     * 导出 图书下面权限二维码 zip
     *
     * @param bookId 图书id
     * @param number 倒出二维码数量
     * @param request   request
     * @throws IOException
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void serialNumExport(Integer bookId,Integer number,  String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.isTrue(bookId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        Assert.isTrue(number>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "导出数量"));
        Assert.isTrue(!StringUtils.isEmpty(data), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "data对象字符串"));


        qrExportService.authNumExportExcel(bookId,number,data,request,response);
    }

    /**
     * 判断权限二维码是否没有被绑定
     * @param authkey  权限二维码序列号
     * @return
     * @throws Exception
     * @author cuihao
     * @date 2017.8.1
     */
    @RequestMapping(value = "/checkAuthkey", method = RequestMethod.GET)
    public JsonResultData checkAuthkey(String authkey) throws Exception {
        QrUserInfo userInfo = new QrUserInfo();
        userInfo.setAuthkey(authkey);
        //查看表中authkey是否存在
        List<QrUserInfo> list = qrUserInfoMapper.select(userInfo);
        if(CollectionUtils.isEmpty(list)){
            return new JsonResultData(true, "没有被绑定!");
        }
        return new JsonResultData(false, "已被绑定!");
    }

    /**
     * 微信扫描 权限二维码
     * @param id  权限二维码id
     * @return
     * @throws Exception
     * @author cuihao
     * @date 2017.8.1
     */
    @RequestMapping(value = "/scanning", method = RequestMethod.GET)
    public JsonResultData scanning(@RequestParam(name = "id") Integer id,
                                   @RequestParam(value="authkey",required = false) String authkey,
                                   @RequestParam(value="code",required = false) String code,
                                   @RequestParam(value="state",required = false) String state,
                                   HttpServletRequest request) throws Exception {
        //校验序列号是否非法
        userCenterService.checkLicense(authkey);
        //用户同意微信授权
        String openid = "";
        if(!org.apache.commons.lang3.StringUtils.isBlank(code) && !org.apache.commons.lang3.StringUtils.isBlank(state)){
            if ("wx".equals(state)) {
                String openidPointUnionid = qrUserInfoService.authQrUserInfoSave(id,code,authkey);
                if(openidPointUnionid == null || "".equals(openidPointUnionid)){
                    return new JsonResultData(false,"网络环境不好,请重新扫描!");
                }
                //接入出版社用户中心
                userCenterService.userCenter(1, openidPointUnionid,request);
                openid = openidPointUnionid.split(",")[0];
            }else if ("qq".equals(state)) {
                // 查询是否已经扫过权限二维码
                openid = qrUserInfoService.saveQQUserInfo(id, code, qqAppId, qqAppKey, qqCallBackUrl, authkey);
                // QQ接入出版社用户中心
                userCenterService.userCenter(2, openid,request);
            }else {
                // 其他
                return new JsonResultData(false, "请使用微信或QQ扫描该二维码！");
            }

        }
        boolean flag = qrExportService.scanning(id,authkey,openid,code,state);

        if(!flag){
            return new JsonResultData(false,"此二维码已被使用!");
        }else{
            //激活此序列号
            userCenterService.activeSerializedUuid(authkey,openid);
            return new JsonResultData(true,"注册成功!");
        }
    }

    /**
     * 出版部图书列表
     * @param editor        编辑 ID
     * @param isbns         字符串，分隔，模糊查询
     * @param keyword       书名，编辑，作者搜索
     * @param pressId       分社 ID
     * @param type          1 正式图书   2 非正式图书
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage getPowerBookList(@RequestParam(value = "editor", defaultValue = "0", required = false) Integer editor,
                                           @RequestParam(value = "isbns", defaultValue = "", required = false) String isbns,
                                           @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                           @RequestParam(value = "pressId", defaultValue = "0", required = false) Integer pressId,
                                           @RequestParam(value = "type", defaultValue = "0", required = false) int type,
                                           @RequestParam(value = "categorysuperId",defaultValue = "0",required = false) int categorySuperId,
                                           @RequestParam(value = "categoryId",defaultValue = "0",required = false) int categoryId,
                                           @RequestParam("pageNo") int pageNo,
                                           @RequestParam("pageSize") int pageSize){
        Assert.notNull(pageNo, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.notNull(pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return bookService.getPowerBookList(editor, isbns, StringUtil.escape4Like(keyword), pressId, type,categoryId,categorySuperId, pageNo, pageSize);
    }

    @RequestMapping(value = "/readLimit", method = RequestMethod.PUT)
    public JsonResult editExtendedQr(@RequestBody BookQrParamVO bookQrParamVO){
        Assert.notNull(bookQrParamVO.getQrId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.isTrue(bookQrParamVO.getFlag() == 1 || bookQrParamVO.getFlag() == 2, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        return bookQrService.editExtendedQr(bookQrParamVO);
    }

    @RequestMapping(value = "/exportList",method = RequestMethod.GET)
    public void exportList(@RequestParam(value = "editor", defaultValue = "0", required = false) Integer editor,
                           @RequestParam(value = "isbns", defaultValue = "", required = false) String isbns,
                           @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                           @RequestParam(value = "pressId", defaultValue = "0", required = false) Integer pressId,
                           @RequestParam(value = "type", defaultValue = "0", required = false) int type,
                           @RequestParam(value = "categoryId",defaultValue = "0",required = false) int categoryId,
                           @RequestParam(value = "categorysuperId",defaultValue = "0",required = false) int categorysuperId,
                            HttpServletRequest request, HttpServletResponse response)throws Exception{
        bookQrService.exportList(editor,isbns, StringUtil.escape4Like(keyword),pressId,type,categoryId,categorysuperId,request,response);
    }



}
