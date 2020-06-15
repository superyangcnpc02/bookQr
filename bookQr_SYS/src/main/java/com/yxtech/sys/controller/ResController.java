package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.dto.BookResourceDto;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.OfficeToHtml;
import com.yxtech.sys.vo.UploadFlagVo;
import com.yxtech.sys.vo.client.ClientBookVO;
import com.yxtech.sys.vo.client.ClientDetailVO;
import com.yxtech.sys.vo.resource.*;
import com.yxtech.utils.file.StringUtil;
import com.yxtech.utils.qr.HttpTookit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.text.MessageFormat;
import java.util.List;

import static jodd.util.ThreadUtil.sleep;


/**
 * Created by Administrator on 2015/10/17.
 */

@RestController("ResourceController")
@Scope("prototype")
@RequestMapping(value = "/resource")
@EnableAsync
public class ResController {
    @Autowired
    private ResService resService;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private ClientService clientService;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private OperationService operationService;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private LBookResourceService lBookResourceService;
    @Value("#{configProperties['zhixueyun.host']}")
    private String host;

    private static final Logger log = LoggerFactory.getLogger(ResController.class);

    /**
     * 根据二维码ID打包下载扩展资源
     * @param id
     */
    @RequestMapping(value = "/zipDownload", method = RequestMethod.GET)
    public JsonResultString zipDownload(@RequestParam(value = "id")int id,@RequestParam(value = "openId",required = false,defaultValue = "0")String openId, HttpServletRequest request, HttpServletResponse response)throws Exception{
        Assert.isTrue(id>0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"二维码ID"));

        return resService.yunZipDownload(id,openId, request, response);
    }

    /**
     * 多文件下载加一
     * @param id
     */
    @RequestMapping(value = "/downNum", method = RequestMethod.GET)
    public JsonResultString downNum(@RequestParam(value = "id")int id,@RequestParam(value = "openId")String openId, HttpServletRequest request, HttpServletResponse response)throws Exception{
        Assert.isTrue(id>0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"二维码ID"));
        Assert.isTrue(!StringUtils.isEmpty(openId),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"openid"));

        return resService.downNum(id,openId, request, response);
    }

    /**
     * 二维码新增资源
     * @param resPushVo
     * @param request
     * @return
     */
    @RequestMapping(value = "/push", method = RequestMethod.POST)
    public JsonResult resPush(@RequestBody ResPushVo resPushVo, HttpServletRequest request) {
        Assert.isTrue(resPushVo.getFiles().size() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "文件对象"));
        Assert.isTrue(resPushVo.getQrcodeId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, ""));
        return resService.resPush(resPushVo.getFiles(), resPushVo.getQrcodeId(), request);
    }


    /**
     * 导入资源（新建二维码并绑定与资源的关系）
     * type  1：扩展资源；2：课件资源
     * @param resVo     资源对象、书籍ID、类型
     * @param request
     * @return 二维码ID
     * @author lyj
     * @since 2015-10-17
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @RequiresPermissions("/resource/import")
    public JsonResultId importResource(@RequestBody ResVo resVo, HttpServletRequest request) throws Exception {
        Assert.isTrue(resVo.getFiles().size() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "文件对象"));
        Assert.isTrue(resVo.getId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书籍ID"));
        Assert.notNull(resVo.getQrType(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "类型"));
        return new JsonResultId((int) resService.saveResource(resVo.getFiles(), resVo.getId(), resVo.getQrType(), request).get("qrId"));
    }

    /**
     * 根据id获取资源信息
     *
     * @param id 资源id
     * @return JsonResultData
     * @author cuihao
     * @since 2016-9-30
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResultData getResourceByid(Integer id) throws Exception {
        Assert.notNull(id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源ID"));
        ResourceMailVo mailVo = resService.getByResId(id);
        if(mailVo == null){
            return new JsonResultData(false,"此资源不存在！");
        }else{
            return new JsonResultData(mailVo);
        }

    }

    /**
     * 删除资源
     * @param resIds 资源ID列表
     * @param qrId   二维码ID
     * @param tag    1课件资源 2扩展资源
     * @return
     * @author lyj
     * @since 2015-10-19
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @RequiresPermissions("/resource/delete")
    public JsonResult deleteResource(@RequestParam("ids") List<Integer> resIds, @RequestParam("qrcodeId") int qrId, @RequestParam("tag") int tag, HttpServletRequest request) throws Exception {
        Assert.isTrue(qrId > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.isTrue(resIds.size() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        //记录操作行为
        operationService.record(resIds,Constant.RES,request);

        resService.deleteResource(resIds, qrId, tag, request);

        return new JsonResult(true, MessageFormat.format(ConsHint.SUCCESS, "删除"));
    }


    /**
     * 二维码课件下载(已废弃,前端不在调用)
     *
     * @param id       二维码id
     * @param email    客户邮箱
     * @param password 客户密码
     * @param resId    资源id  如果是0  打包下载全部课件文件
     * @param request
     * @param response
     * @throws IOException
     * @author hesufang
     * @date 2015.11.3
     */
    @RequestMapping(value = "/coursewareDownload", method = RequestMethod.GET)
    public void downCourseware(@RequestParam("id") Integer id
            , @RequestParam("email") String email
            , @RequestParam("password") String password
            , @RequestParam("resourceId") Integer resId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.isTrue(id > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.isTrue(email != null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));
        Assert.isTrue(resId >= 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "课件资源ID"));
        File file = this.resService.export(id, email, password, resId, request);
        if(file.exists()){
            int len = 0;
            //下载文件
            FileInputStream fis = new FileInputStream(file);
            String filename ="";
            if (resId == 0){
                filename = URLEncoder.encode(file.getName(), "utf-8"); //解决中文文件名下载后乱码的问题
            }else{

                Res res = resService.selectByPrimaryKey(resId);
                if (res == null){
                    throw new ServiceException("资源不存在");
                }
                FileRes fileRes = new FileRes();
                fileRes.setUuid(res.getFileUuid());
                fileRes =  fileResService.selectOne(fileRes);
                filename = URLEncoder.encode(fileRes.getName()+"."+fileRes.getSuffix(),"utf-8");//解决中文文件名下载后乱码的问题

            }
            byte[] b = new byte[2048];
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition","attachment; filename="+filename+"");
            //获取响应报文输出流对象
            ServletOutputStream out =response.getOutputStream();
            //输出
            while ((len = fis.read(b)) > 0){
                out.write(b, 0, len);
            }
            out.flush();
            out.close();

        }

    }



    /**
     * 验证下载密码是否正确
     *
     * @param qrId
     * @param email
     * @param password
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.4
     */
    @RequestMapping(value = "/visit", method = RequestMethod.GET)
    public JsonResult visitPassword(@RequestParam("id") int qrId, @RequestParam("email") String email, @RequestParam("password") String password) throws Exception {
        Assert.isTrue(qrId > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));

        int i=resService.getSecrecyByqrId(qrId);
        if(i==1){
            return new JsonResult(true,"无密码");
        }else{
            Assert.isTrue(email != null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));
            Assert.isTrue(password!=null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "密码"));
            return new JsonResult(resService.visitPassword(qrId, email, password), "密码正确");
        }
    }


    /**
     * 编写申请信息
     *
     * @param detailVO
     * @return
     * @author yanfei
     * @date 2015.11.6
     */
    @RequestMapping(value = "/passwordApply", method = RequestMethod.POST)
    public JsonResultId passwordApply(@RequestBody @Valid ClientDetailVO detailVO) throws Exception {
        Client client = mapper.map(detailVO, Client.class);
        List<ClientBookVO> clientBookVOList = detailVO.getBooks();
        Assert.notNull(client, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "申请信息"));
        resService.mergeClientDetail(client, clientBookVOList, detailVO.getQrcodeId());
        return new JsonResultId(true, "有权访问信息!", 1);
    }


    /**
     * 替换二维码
     * @param resourceVO
     * @return
     * @author yanfei
     * @date 2015.11.7
     */
    @RequestMapping(value = "/replace", method = RequestMethod.PUT)
    @RequiresPermissions(value = "/resource/replace")
    public JsonResult replaceResource(@RequestBody @Valid ResourceVO resourceVO, HttpServletRequest request){
        resService.replaceResource(resourceVO, request);
        return new JsonResult(true, "替换二维码资源成功");
    }

    /**
     * 获取申请信息
     *
     * @param email 邮箱
     * @return
     * @author hesufang
     * @date 2015.11.7
     */
    @RequestMapping(value = "/passwordInfo", method = RequestMethod.GET)
    public JsonResultData passwordInfo(@RequestParam(value="email",required = false) String email,
                                       @RequestParam(value="openId",required = false) String openId)throws  Exception{
//        Assert.isTrue(email != null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));
        return new JsonResultData(clientService.getClientDetail(email,openId));
    }

    /**
     * 申请信息列表
     * @param keyword 申请人邮箱或者申请人姓名
     * @param bookId
     * @param page
     * @param pageSize
     * @return
     * @author yanfei
     * @date 2015.12.9
     */
    @RequestMapping(value = "/apply", method = RequestMethod.GET)
    public JsonResultPage apply(@RequestParam(required = false,defaultValue = "") String keyword,@RequestParam int status, @RequestParam int bookId, @RequestParam int page, @RequestParam int pageSize){
        Assert.isTrue(page > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.isTrue(pageSize > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return resService.applyList(StringUtil.escape4Like(keyword),status, bookId,page, pageSize);
    }

    /**
     * 更新客户信息
     * @param client
     * @return
     */
    @RequestMapping(value = "/updateInfo", method = RequestMethod.PUT)
    public JsonResult updateInfo(@RequestBody Client client){
        return clientService.updateInfo(client);
    }

    /**
     * 导出客户信息列表
     * @param bookId
     * @param status
     * @param keyword email or clintName
     *
     * @param response
     */
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    @RequiresPermissions("/resource/export")
    public void exportClient(@RequestParam(value = "bookId")int bookId,
                             @RequestParam(value = "pressId", defaultValue = "0", required = false) int pressId,
                             @RequestParam(value = "status")int status,
                             @RequestParam(value = "keyword", defaultValue = "", required = false)String keyword,
                             HttpServletRequest request,HttpServletResponse response) throws IOException{
        resService.exportClient(bookId, pressId, status, keyword, request, response);
    }

    /**
     * 获取资源状态
     * @param resId
     * @return
     * @author liukailong
     * @date 2016/9/30
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public JsonResultId status(@RequestParam(value = "resId") int resId){
        Assert.isTrue(Constant.ZERO < resId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));
        return resService.status(resId);
    }

    /**
     * 资源明细
     * @param id
     * @return
     * @author liukailong
     * @date 2016/9/30
     */
    @RequestMapping(value = "/resDetail", method = RequestMethod.GET)
    public JsonResultData resDetail(@RequestParam(value = "id") int id){
        Assert.isTrue(Constant.ZERO < id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "主键"));
        return resService.resDetail(id);
    }

    /**
     * 微信用户明细
     * @param openId
     * @return
     * @author liukailong
     * @date 2016/10/11
     */
    @RequestMapping(value = "/openDetail", method = RequestMethod.GET)
    public JsonResultData openDetail(@RequestParam(value = "openId") String openId){
        Assert.isTrue(openId != null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "openId"));
        return clientService.clientDetail(openId);
    }

    /**
     * 资源编辑是否可以下载
     * @return
     * @author hesufang zml
     * @date 2017.1.20
     */
    @RequestMapping(value = "/updateFlag", method = RequestMethod.PUT)
	public JsonResult updateFlag(@RequestBody UploadFlagVo uploadFlagVo){
        return resService.updateFlag(uploadFlagVo.getDownLoad(), uploadFlagVo.getId());
    }

    /**
     * 由智学云id拿到office文件播放地址
     */
    @RequestMapping(value = "/officeToHtml", method = RequestMethod.GET)
    public JsonResultData officeToHtml(long id){
        String token = userCenterService.getToken();
        String url = host + "/resource/player/document?resourceId="+id+"&token="+token;
        try {
            String result = HttpTookit.doGet(url, null, "utf-8", true);
            result = result.replace("\r\n","");
            log.debug("office文件播放地址:"+result);
            JSONObject jsonObject =JSON.parseObject(result);
            String code = jsonObject.getString("code");
            if(!"0".equals(code)){
                throw new ServerException("获取智学云office文件播放地址失败,状态码"+code);
            }else{
                JSONObject data = jsonObject.getJSONObject("data");
                String officeUrl = data.getString("url");
                return new JsonResultData(true, "",officeUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取office文件播放地址失败!");
        }

        return new JsonResultData(false, "");
    }

    /**
     * 二维码列表资源改名
     * @param vo
     * @return
     */
    @RequestMapping(value = "/updateResName", method = RequestMethod.PUT)
    public JsonResult updateResName(@RequestBody ResNameVo vo) {
        Assert.notNull(vo.getId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));
        Assert.notNull(vo.getName(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源名称"));

        Res res = resService.selectByPrimaryKey(vo.getId());


        FileRes fr = new FileRes();
        fr.setUuid(res.getFileUuid());
        FileRes fileRes = fileResService.selectOne(fr);
        fileRes.setName(vo.getName());

        fileResService.updateByPrimaryKeySelective(fileRes);
        /**
         * 把图书资源关系传给智学云
         */
        BookQr bookQr = bookQrService.selectByPrimaryKey(res.getQrId());
        if(bookQr != null){
            userCenterService.bindBookRes(bookQr.getBookId(),bookQr.getQrType(),res.getId());
        }
        //主动触发云端压缩
        resService.compress(res.getQrId());

        return new JsonResult(true,"修改成功");
    }

    /**
     * 二维码列表资源调整书序
     * @param vo
     * @return
     */
    @RequestMapping(value = "/setIndex", method = RequestMethod.PUT)
    public JsonResult setIndex(@RequestBody ResIndexVo vo) {
        Assert.notNull(vo.getQrId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        Assert.notNull(vo.getResId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));
        Assert.isTrue(vo.getFlag()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "调整flag"));


        return resService.setIndex(vo);
    }

    /**
     * 二维码列表资源历史记录初始化
     * @return
     */
    @RequestMapping(value = "/initdata", method = RequestMethod.GET)
    public JsonResult initdata() {

        return resService.initdata();
    }

    /**
     *  初始化图书资源关系
     * @return
     */
    @Async
    @RequestMapping(value = "/initBookResource", method = RequestMethod.GET)
    public void initBookResource(Integer bookId) {
        List<BookResourceDto> list = lBookResourceService.queryAllBookResource(bookId);
        System.out.println("初始化图书资源关系,list条数" + list.size());
        if(list != null && list.size()>0){
            int i = 0;
            for(BookResourceDto dto : list){
                lBookResourceService.initData(dto,i);
                i++;
                //异步处理1000条数据后休息等待30秒
                if(i%1000 == 0){
                    sleep(30 * 1000);
                }
            }
        }
    }
}
