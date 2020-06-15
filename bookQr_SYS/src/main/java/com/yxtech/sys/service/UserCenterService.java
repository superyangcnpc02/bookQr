package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.bookQr.CheckMessVo;
import com.yxtech.sys.vo.bookQr.CheckTeacherVo;
import com.yxtech.sys.vo.user.UserCenterInfo;
import com.yxtech.utils.excel.ImportExcelUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.mail.MailSender;
import com.yxtech.utils.password.Base64;
import com.yxtech.utils.qr.HttpTookit;
import com.yxtech.utils.runCode.RandomImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lyj on 2015/10/24.
 */
@Service
public class UserCenterService extends BaseService<UserCenter> {
    public final static Logger log = LoggerFactory.getLogger(UserCenterService.class);
    //private final static String userId = "59e95a4689eeb92f380f4ab2";
    private final static String email = "yiyuedu@izhixue.cn";
    private final static String password = "601f1889667efaebb33b8c12572835da3f027f78";
    //private final static String secret = "aAfw9gNhoOo4zfF1";
    private UserCenterMapper userCenterMapper;

    @Resource(name = "userCenterMapper")
    public void setUserCenterMapper(UserCenterMapper userCenterMapper) {
        setMapper(userCenterMapper);
        this.userCenterMapper = userCenterMapper;
    }

    @Autowired
    private QrUserInfoMapper qrUserInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResMapper resMapper;
    @Autowired
    private FileResMapper fileResMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientAttrMapper clientAttrMapper;
    @Autowired
    private BookQrService bookQrService;
    @Resource(name = "incrementRedisTemplate")
    protected RedisTemplate stringRedisTemplate;
    @Autowired
    private LBookResourceService lBookResourceService;
    @Autowired
    private MailSender mailSender;
    @Value("#{configProperties['zhixueyun.host']}")
    private String baseUrl;
    @Value("#{configProperties['zhixueyun.userId']}")
    private String userId;
    @Value("#{configProperties['zhixueyun.secret']}")
    private String secret;
    @Value("#{configProperties['zhixueyun.email']}")
    private String zhixueEmail;
    /**
     * 获取token
     */
    public String getToken() {
        String sign = Base64.getBase64(secret + System.currentTimeMillis()/1000);
        final String url = baseUrl + "/token/create?userId=" + userId + "&sign=" + sign;
        String tokenResult = HttpTookit.doGet(url, null, "utf-8", true);
        System.out.println("tokenResult==" + tokenResult);
        JSONObject object = JSON.parseObject(tokenResult);
        int code = object.getInteger("code");
        if (code != 0) {
            throw new ServiceException("获取社里用户中心token失败!" + tokenResult);
        } else {
            String token = object.getString("token");
            return token;
        }
    }

    /**
     * 接入社里用户中心
     * @param type 1 微信 2 qq
     * @param threePartId
     */
    public String userCenter(Integer type, String threePartId,HttpServletRequest request) throws Exception{
        String phone="";
        System.out.println("3接入出版社用户中心,begin="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        String openid = "";
        String unionid = "";
        if(type == 1){
            openid = threePartId.split(",")[0];
            unionid = threePartId.split(",")[1];
        }else{
            openid = threePartId;
        }
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("获取token失败!");
        }

        //2.微信登录验证
        final String weiLoginUrl = baseUrl + "/sessions/wechat/create?token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("openid", openid);
        if (type == 1) {
            params.put("unionid", unionid);//微信unionid
            params.put("type", "wechat");
        }else if(type == 2){
            params.put("unionid", openid);//qq unionid就是 openid
            params.put("type", "qq");
        }

        String weResult = HttpTookit.doPostJson(weiLoginUrl, params);
        System.out.println("params==" + JSON.toJSONString(params));
        System.out.println("weResult==" + weResult);
        JSONObject weObject = JSON.parseObject(weResult);
        int weCode = weObject.getInteger("code");
        if (weCode == 10003) {//{"code":10003,"message":"User not find"}
            //中心用户不存在,先注册中心,在注册本地
            Example example = new Example(QrUserInfo.class);
            example.setOrderByClause("id desc");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("openid", openid);
            List<QrUserInfo> qrUserInfoList = qrUserInfoMapper.selectByExample(example);
            if (qrUserInfoList != null && qrUserInfoList.size() != 0) {
                QrUserInfo qrUserInfo = qrUserInfoList.get(0);
                String nickname = qrUserInfo.getNickname();
                String headimgurl = qrUserInfo.getHeadimgurl();
                String sex = qrUserInfo.getSex().trim();
                String gender = "x";
                if ("1".equals(sex)) {
                    gender = "m";
                } else if ("2".equals(sex)) {
                    gender = "f";
                }
                final String registerUrl = baseUrl + "/users/create?token=" + token;
                Map<String, String> paramsR = new HashMap<>();
                paramsR.put("name", nickname);//昵称
                paramsR.put("avatar", headimgurl);
                paramsR.put("gender", gender);//'m'男性, 'f'女性, 'x'保密
                if (type == 1) {
                    paramsR.put("type", "wechat");//登录类型
                    paramsR.put("openid", openid);//微信openid
                    paramsR.put("unionid", unionid);//微信unionid
                } else if (type == 2) {
                    paramsR.put("type", "qq");//登录类型
                    paramsR.put("openid", openid);//微信openid
                }
                paramsR.put("site", "tupmbook.com");//站点
                String ip = WebUtil.getIpAddr(request);
                System.out.println("------------------------------ip==" + ip);
                paramsR.put("ip", ip);//站点
                String registerResult = HttpTookit.doPostJson(registerUrl, paramsR);//{"code":0,"message":"Successfully","userId":"59ed480889eeb92f380f4ab3"}
                System.out.println("registerResult==" + registerResult);
                JSONObject registerObject = JSON.parseObject(registerResult);
                int registerCode = registerObject.getInteger("code");
                if (registerCode == 0) {
                    String userId = registerObject.getString("userId");

                    UserCenter userCenterEP = new UserCenter();
                    userCenterEP.setUserId(userId);
                    userCenterEP.setOpenid(openid);
                    if (this.selectCount(userCenterEP) == 0) {
                        //注册本地
                        UserCenter userCenter = new UserCenter();
                        userCenter.setOpenid(openid);
                        userCenter.setUserId(userId);
                        userCenter.setCreateTime(new Date());
                        userCenterMapper.insertSelective(userCenter);
                    }
                } else {
                    System.err.println("-------paramsR=="+JSON.toJSONString(paramsR));
                    throw new ServiceException("用户中心创建用户失败,状态码"+registerCode);
                }

            }

        } else if (weCode == 0) {
            //中心用户存在,验证本地用户,不存在则插入
            String userId = weObject.getString("userId");
            String mobile = weObject.getString("mobile");
            UserCenter userCenterEP = new UserCenter();
            userCenterEP.setUserId(userId);
            userCenterEP.setOpenid(openid);
            if (this.selectCount(userCenterEP) == 0) {
                //注册本地
                UserCenter userCenter = new UserCenter();
                userCenter.setOpenid(openid);
                userCenter.setUserId(userId);
                userCenter.setCreateTime(new Date());
                userCenterMapper.insertSelective(userCenter);
            }
            if(!StringUtils.isEmpty(mobile)){
                phone = mobile;
                Client clientEP = new Client();
                clientEP.setPhone(mobile);
                if (clientMapper.selectCount(clientEP) == 0) {
                    Client client = new Client();
                    client.setName("name");
                    client.setPhone(mobile);
                    client.setOpenId(openid);
                    clientMapper.insertSelective(client);
                }
            }
        } else {
            throw new ServiceException("微信登录错误,错误码" + weCode);
        }
        System.out.println("3接入出版社用户中心,end="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        return phone;
    }

    /**
     * 校验系统用户在社里用户中心是否存在(编辑、总编、管理员)
     *    <code key="10003" value="找不到用户"/>
     *    <code key="10007" value="密码错误"/>
     *    <code key="0" value="验证成功!"/>
     */
    public UserCenterInfo checkIsExist(String email, String password) {
        UserCenterInfo vo = new UserCenterInfo();
        vo.setCode(-1);
        try {
            // 1.获取token
            String token = getToken();
            if ("".equals(token)) {
                throw new ServiceException("checkIsExist,获取token失败!");
            }
            //2.用户登录验证
            final String loginUrl = baseUrl + "/sessions/create?token=" + token;
            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            String uResult = HttpTookit.doPostJson(loginUrl, params);
            System.out.println("checkIsExist Result==" + uResult);
            JSONObject uObject = JSON.parseObject(uResult);
            int weCode = uObject.getInteger("code");
            String userId = uObject.getString("userId");
            vo.setCode(weCode);
            vo.setUserId(userId);
        } catch (Exception e) {
            System.err.println("checkIsExist失败!");
            e.printStackTrace();
        }

        return vo;
    }

    /**
     * 新增系统用户时,接入社里用户中心(编辑、总编、管理员)
     * 共有下面三个接口需要调用,分别是:(默认密码都是123456)
     * 1.新增是管理员 /user/add
     * 2.激活是管理员 /user/active
     * 3.审核通过是管理员 /user/examine
     */
    public void addNativ(Integer id, HttpServletRequest request) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return;
        }
        // 1.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("addNativ,获取token失败!");
        }

        //2.用户登录验证
        String pwd = Constant.PASSWORD;
        final String loginUrl = baseUrl + "/sessions/create?token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("password", pwd);

        String uResult = HttpTookit.doPostJson(loginUrl, params);
        System.out.println("params==" + JSON.toJSONString(params));
        System.out.println("uResult==" + uResult);
        JSONObject uObject = JSON.parseObject(uResult);
        int weCode = uObject.getInteger("code");
        if (weCode == 10003) {//{"code":10003,"message":"User not find"}
            //中心用户不存在,先注册中心,在注册本地
            final String registerUrl = baseUrl + "/users/create?token=" + token;
            Map<String, String> paramsR = new HashMap<>();
            paramsR.put("name", user.getUserName());//昵称
            paramsR.put("email", user.getEmail());  //邮箱
            paramsR.put("password", pwd);//密码
            //paramsR.put("avatar", headimgurl); 头像
            paramsR.put("type", "none");//登录类型
            paramsR.put("site", "tupmbook.com");//站点
            String ip = WebUtil.getIpAddr(request);
            System.out.println("------------------------------ip==" + ip);
            paramsR.put("ip", ip);//站点
            String registerResult = HttpTookit.doPostJson(registerUrl, paramsR);//{"code":0,"message":"Successfully","userId":"59ed480889eeb92f380f4ab3"}
            System.out.println("paramsR=="+JSON.toJSONString(paramsR));
            System.out.println("registerResult==" + registerResult);
            JSONObject registerObject = JSON.parseObject(registerResult);
            int registerCode = registerObject.getInteger("code");
            if (registerCode == 0) {
                String userId = registerObject.getString("userId");
                //更改本地
                user.setUserId(userId);
                userMapper.updateByPrimaryKeySelective(user);
            } else {
                throw new ServiceException("用户中心创建用户失败,状态码" + registerCode);
            }

        } else if (weCode == 0) {
            //中心用户存在,验证本地用户,不存在则插入
            String userId = uObject.getString("userId");
            if (!userId.equals(user.getUserId())) {
                user.setUserId(userId);
                userMapper.updateByPrimaryKeySelective(user);
            }
        } else if(weCode == 10007) {
            System.out.println("用户:"+user.getEmail()+"已经在用户中心注册过，请直接登录吧！");
        } else {
            throw new ServiceException("系统用户登录错误,错误码" + weCode);
        }
    }

    /**
     * 编辑系统用户时,接入社里用户中心(编辑、总编、管理员)
     * 共有下面三个接口需要调用,分别是:(默认密码都是123456)
     * 1.修改用户 /user/edit   修改昵称和头像
     * 2.修改密码 /user/password  需传明文密码
     * 3.重设密码 /user/resetPassword  传123456
     */
    public void updateNativ(Integer id, HttpServletRequest request) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return;
        }
        // 1.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("aupdateNativ,获取token失败!");
        }
        //2.向用户中心发起更新请求，用户保存到智学云用户中心。
        final String updateUrl = baseUrl + "/users/update?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("id", user.getUserId());//用户id
        paramsR.put("name", user.getUserName());//昵称
        paramsR.put("email", user.getEmail());  //邮箱
        paramsR.put("password", user.getPassword());//密码
        //paramsR.put("avatar", headimgurl); 头像
        paramsR.put("type", "none");//登录类型
        paramsR.put("site", "tupmbook.com");//站点
        String ip = WebUtil.getIpAddr(request);
        System.out.println("------------------------------ip==" + ip);
        paramsR.put("ip", ip);//站点
        String updateResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","userId":"59ed480889eeb92f380f4ab3"}
        log.debug("paramsR==" + JSON.toJSONString(paramsR));
        log.debug("updateResult==" + updateResult);
        JSONObject registerObject = JSON.parseObject(updateResult);
        int registerCode = registerObject.getInteger("code");
        if (registerCode != 0) {
            throw new ServiceException("用户中心更新用户失败,状态码" + registerCode);
        }

    }

    /**
     * 调用社里接口,批量生成序列号
     *
     * @param type 1:权限二维码 2:刮刮卡二维码
     */
    public List<String> getSerializedUuid(int type,int num,String bookCode) {
        List<String> list = new ArrayList<>();
        // 1.获取token
        String token = getToken();
        if("".equals(token)){
            return list;
        }

        //2.生成批次号
        final String batchUrl = baseUrl + "/license/create?token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("bookId", bookCode);
        params.put("count", String.valueOf(num));
        params.put("validitySecond", "3153600000");//100年
        params.put("userId", userId);
        params.put("type", "yyd");//平台标识:文泉云盘传yyd
        if(type == 1){
            params.put("category", "qx");//序列号分类:文泉云盘专用：qx、ggk
        }else{
            params.put("category", "ggk");//序列号分类:文泉云盘专用：qx、ggk
        }


        String batchResult = HttpTookit.doPostJson(batchUrl, params);
        System.out.println("batchResult==" + batchResult);
        JSONObject batchObject = JSON.parseObject(batchResult);
        int batchCode = batchObject.getInteger("code");
        if (batchCode == 0) {
            String batchId = batchObject.getString("batchId");
            //3.根据批次号获取序列号
            final String SerializedUrl = baseUrl + "/license/bybatch?batchId=" + batchId + "&token=" + token;
            String SerializedResult = HttpTookit.doGet(SerializedUrl, null, "utf-8", true);
            System.out.println("SerializedResult==" + SerializedResult);
            JSONObject SerializedObject = JSON.parseObject(SerializedResult);
            int SerializedCode = SerializedObject.getInteger("code");
            if (SerializedCode == 0) {
                JSONArray licenses = SerializedObject.getJSONArray("licenses");
                for (int i = 0; i < licenses.size(); i++) {
                    JSONObject license = (JSONObject) licenses.get(i);
                    String _id = license.getString("code");
//                    String _id = license.getString("_id");
                    System.out.println("序列号=" + _id);
                    list.add(_id);
                }
            }
        }

        return list;
    }

    /**
     * 校验序列号在用户中心是否存在,不存在即非法
     * @param license
     */
    public void checkLicense(String license) {
        System.out.println("1.校验序列号是否非法,begin="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        // 1.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("校验序列号时,获取token失败!");
        }
        //2.检查序列号
        final String checkUrl = baseUrl + "/license/check?license=" + license + "&token=" + token;
        String checkResult = HttpTookit.doGet(checkUrl, null, "utf-8", true);
        System.out.println("checkResult==" + checkResult);
        JSONObject checkObject = JSON.parseObject(checkResult);
        int checkCode = checkObject.getInteger("code");
        System.out.println(checkCode);
        if (checkCode == 10010) {
            throw new ServiceException("序列号非法!");
        }
        System.out.println("1.校验序列号是否非法,end="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
    }

    /**
     * 激活序列号
     *
     * @param license
     */
    public void activeSerializedUuid(String license, String openid) {
        String scanUserId = "";
        UserCenter userCenterEP = new UserCenter();
        userCenterEP.setOpenid(openid);
        List<UserCenter> list = userCenterMapper.select(userCenterEP);
        if (list != null && list.size() != 0) {
            UserCenter userCenter = list.get(0);
            scanUserId = userCenter.getUserId();
        }

        // 1.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("激活序列号时,获取token失败!");
        }
        //2.激活序列号
        final String activeUrl = baseUrl + "/license/activate?token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("license", license);
        params.put("userId", scanUserId);

        String activeResult = HttpTookit.doPostJson(activeUrl, params);
        System.out.println("activeResult==" + activeResult);
        JSONObject activeObject = JSON.parseObject(activeResult);
        int activeCode = activeObject.getInteger("code");
        if (activeCode != 0 && activeCode != 10009) {
            if(activeCode == 10010){
                throw new ServiceException("激活序列号失败,序列号非法!");
            }else{
                throw new ServiceException("激活序列号失败,错误代码" + activeCode);
            }
        }
    }


    /**
     * 获取token
     */
    public String getNewToken() {
        String sign = Base64.getBase64(secret + System.currentTimeMillis()/1000);
        final String url = baseUrl + "/token/create?userId=" + userId + "&sign=" + sign;
        log.debug("Math.ceil(System.currentTimeMillis()/1000)="+System.currentTimeMillis()/1000);
        log.debug("getNewToken url="+url);
        String tokenResult = HttpTookit.doGet(url, null, "utf-8", true);
        System.out.println("tokenResult==" + tokenResult);
        JSONObject object = JSON.parseObject(tokenResult);
        int code = object.getInteger("code");
        if (code != 0) {
            System.err.println("获取社里用户中心token失败!");
            return "";
        } else {
            String token = object.getString("token");
            return token;
        }
    }

    /**
     * 向智学云平台绑定图书和资源关系
     * 1.导入资源生成二维码（批量新增资源）(resource/import)
     * 2.二维码下新增资源（单个新增资源）(resource/push)
     * 3.二维码下替换资源（替换资源）(resource/replace)
     * 4.二维码下资源改名（修改资源）(resource/updateResName)
     */
    public void bindBookRes(int bookId,int qrType,int resId) {
        bindBookRes(bookId,qrType,resId,null);
    }

    /**
     * 向智学云平台删除图书和资源关系
     * 1.删除图书（已激活的图书删除 ）(/book/delete,/book/removeMany)
     * 2.删除二维码（批量删除资源）(qrcode/delete)
     * 3.删除二维码下面资源（删除资源）(resource/delete)
     */
    public void deleteBookRes(int resId) {
        deleteBookRes(resId,null);
    }

    /**
     * 向智学云平台校验手机号是否是唯一的
     */
    public void checkMobile(String mobile) {
        // 1.获取token
        String token = this.getNewToken();
        if ("".equals(token)) {
            throw new ServiceException("checkMobile,获取token失败!");
        }
        //2.向智学云平台校验手机号是否是信任的
        final String checkMobileUrl = baseUrl + "/users/check/mobile?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("mobile", mobile);//用户手机号
        String checkResult = HttpTookit.doPostJson(checkMobileUrl, paramsR);//{"code":0,"message":"Successfully","status":"1"//0 不存在手机号，1存在手机号}
        log.debug("----------------paramsR==" + JSON.toJSONString(paramsR));
        log.debug("----------------checkResult==" + checkResult);
        JSONObject checkObject = JSON.parseObject(checkResult);
        int checkCode = checkObject.getInteger("code");
        if (checkCode == 0) {
            int status = checkObject.getInteger("status");
            //0 不存在手机号，1存在手机号
            if(status == 1){
                throw new ServiceException("手机号已存在!");
            }
        } else {
            log.error("checkMobile失败!");
            throw new ServiceException("checkMobile失败!");
        }
    }

    /**
     * 向智学云平台校验手机号是否是信任的
     */
    public CheckTeacherVo checkIfTrusted(String mobile) {
        Integer status = 2;
        String email = "";
        // 1.获取token
        String token = this.getNewToken();
        if ("".equals(token)) {
            throw new ServiceException("checkIfTrusted,获取token失败!");
        }
        //2.向智学云平台校验手机号是否是信任的
        final String updateUrl = baseUrl + "/teacher/bymobile?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("mobile", mobile);//用户手机号
        String checkResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","data":{}}
        log.debug("----------------paramsR==" + JSON.toJSONString(paramsR));
        log.debug("----------------checkResult==" + checkResult);
        JSONObject checkObject = JSON.parseObject(checkResult);
        int checkCode = checkObject.getInteger("code");
        if (checkCode == 0) {
            //-1拒绝；0待审核；1通过；2不存在
            status = checkObject.getInteger("status");
            if (status == 1) {
                JSONObject teacherInfo = checkObject.getJSONObject("teacherInfo");
                email = teacherInfo.getString("Email");
            }
        } else {
            log.error("checkIfTrusted失败!");
            throw new ServiceException("checkIfTrusted失败!");
        }

        return new CheckTeacherVo(status,email);
    }

    /**
     * 根据教师手机号发送验证码
     */
    public String sendMess(String mobile) {
        //生成手机验证码6位数字
        String code = RandomImage.generateTextCode(RandomImage.TYPE_NUM_ONLY, 6, null);
        //将该手机验证码存入redis
        stringRedisTemplate.opsForValue().set(mobile,code,300, TimeUnit.SECONDS);
        this.sendSmsCode(mobile,code);

        return code;
    }

    /**
     * desc:发送手机验证码<br>
     * date: 2019/3/8
     * @author wangxiaofeng
     * @param phone
     * @param code
     * @return
     */
    public void sendSmsCode(String phone,String code){
        // 1.获取token
        String token = this.getNewToken();
        if ("".equals(token)) {
            throw new ServiceException("sendSmsCode,获取token失败!");
        }
        //2.向智学云平台校验手机号是否是信任的
        final String updateUrl = baseUrl + "/sms?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("phone", phone);//用户手机号
        paramsR.put("code", code);//验证码
        paramsR.put("type", "1");//固定值
        paramsR.put("sms_sign", "yunpan");//向服务端申请过自定义签名的可用
        String sendSmsCodeResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"发送成功","data":{}}
        log.debug("----------------paramsR==" + JSON.toJSONString(paramsR));
        log.debug("----------------sendSmsCodeResult==" + sendSmsCodeResult);
        JSONObject checkObject = JSON.parseObject(sendSmsCodeResult);
        int checkCode = checkObject.getInteger("code");
        if (checkCode == 0) {
            log.debug("发送短信成功!");
        }else{
            log.debug("发送短信失败!");
            throw new ServiceException("发送短信失败!---------paramsR==" + JSON.toJSONString(paramsR)+"----------------sendSmsCodeResult==" + sendSmsCodeResult);
        }
    }

    /**
     * 校验教师手机号验证码是否正确
     */
    public int checkMess(String mobile,String code,String openid,String unionid) {
        //1符合 2不符合
        int result=2;
        //判断验证码是否相同
        if(!stringRedisTemplate.hasKey(mobile)){
            throw new ServiceException("验证码已过期，请重新获取");
        }
        String rdsCode=(String) stringRedisTemplate.opsForValue().get(mobile);
        if(code.equals(rdsCode)){
            result = 1;
            //1.修改本地用户手机号
            Client clientEP = new Client();
            clientEP.setOpenId(openid);
            if(clientMapper.selectCount(clientEP) == 0){
                clientEP.setName("");
                clientEP.setPhone(mobile);
                clientMapper.insertSelective(clientEP);
            }else{
                Example example = new Example(Client.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("openId",openid);

                Client client = new Client();
                client.setPhone(mobile);
                clientMapper.updateByExampleSelective(client,example);
            }
            //2.修改智学云用户手机号
            this.updateUserMobile(openid,unionid,mobile);
        }

        return result;
    }

    /**
     * 校验教师手机号验证码是否正确
     */
    public JsonResultData checkMessForAuto(String mobile,String code,String openid,String unionid,String mail,String school,int qrcodeId) throws Exception {

        int result = this.checkMess(mobile,code,openid,unionid);

        //2 更新client
        Example clientExample = new Example(Client.class);
        clientExample.createCriteria().andEqualTo("openId", openid);
        List<Client> oldClientList = clientMapper.selectByExample(clientExample);
        if (oldClientList != null && oldClientList.size() > 0) {
            Client client = oldClientList.get(Constant.ZERO);
            client.setEmail(mail);
            client.setSchool(school);
            clientMapper.updateByPrimaryKeySelective(client);

            //2 给申请人发送邮件
            BookQr bookQr = bookQrService.selectByPrimaryKey(qrcodeId);
            bookQrService.sendEmailToClient(bookQr, client);

            Integer clientId = client.getId();
            Integer bookId = bookQr.getBookId();
            Example qrSub = new Example(ClientAttr.class);
            qrSub.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("qrId", qrcodeId).andEqualTo("type", Constant.TWO);
            List<ClientAttr> clientAttSubList = clientAttrMapper.selectByExample(qrSub);
            if (clientAttSubList == null || clientAttSubList.size() == 0) {
                ClientAttr clientAttr = new ClientAttr();
                clientAttr.setClientId(clientId);
                clientAttr.setEmail(client.getEmail());
                clientAttr.setBookId(bookId);
                clientAttr.setQrId(Integer.valueOf(qrcodeId));
                clientAttr.setStatus(2);
                clientAttr.setType(2);
                clientAttr.setType(bookQr.getQrType());
                clientAttr.setCreateTime(new Date());
                clientAttrMapper.insertSelective(clientAttr);
            }
        }

        //添加方法，调/users/change/profile（用户属性修改）
        changeProfile(openid,mail,school);

        return new JsonResultData<>(new CheckMessVo(result));
    }

    /**
     *  修改用户属性
     * @param openid
     * @param mail
     * @param school
     */
    public void changeProfile(String openid, String mail, String school) {
        try {
            Example example = new Example(UserCenter.class);
            example.setOrderByClause("id DESC");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("openid",openid);
            List<UserCenter> userCenterList = userCenterMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(userCenterList)) {
                log.error("根据openid：" + openid + "到数据库查询用户不存在!");
                return;
            }
            UserCenter userCenter = userCenterList.get(0);

            // 2.获取token
            String token = getToken();
            if ("".equals(token)) {
                throw new ServiceException("修改用户属性,获取token失败!");
            }
            //3.发请求
            final String url = baseUrl + "/users/change/profile?token=" + token;
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userCenter.getUserId());//用户id
            params.put("ex_email", mail);//邮箱
            params.put("school", school);//所在学校

            String result = HttpTookit.doPostJson(url, params);
            JSONObject jsonObject = JSON.parseObject(result);
            int code = jsonObject.getInteger("code");
            if (code != 0) {
                log.error("用户属性修改失败：" + jsonObject);
            }
        }catch (Exception e){
            log.error("用户属性修改异常：",e);
        }
    }

    /**
     * 用户扫描课件二维码，验证手机号后需要修改用户信息
     */
    private void updateUserMobile(String openid, String unionid ,String mobile) {
        // 1.获取userId
        Example example = new Example(UserCenter.class);
        example.setOrderByClause("id DESC");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openid",openid);
        List<UserCenter> userCenterList = userCenterMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userCenterList)) {
            log.error("根据openid：" + openid + "到数据库查询用户不存在!");
            return;
        }
        UserCenter userCenter = userCenterList.get(0);

        // 2.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("updateUser,获取token失败!");
        }
        //3.向用户中心发起更新请求，用户保存到智学云用户中心。
        final String updateUrl = baseUrl + "/users/bind/mobile?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("id", userCenter.getUserId());//用户id
        paramsR.put("mobile", mobile);  //手机号
        if(!StringUtils.isEmpty(unionid)){
            paramsR.put("type", "wechat");//微信类型
            paramsR.put("unionid", unionid);//微信unionid
        }else{
            paramsR.put("type", "qq");//qq类型
            paramsR.put("openid", openid);//微信openid
        }
        String updateResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","userId":"59ed480889eeb92f380f4ab3"}
        log.debug("paramsR==" + JSON.toJSONString(paramsR));
        log.debug("updateResult==" + updateResult);
        JSONObject registerObject = JSON.parseObject(updateResult);
        int registerCode = registerObject.getInteger("code");
        if (registerCode != 0) {
            String message = registerObject.getString("message");
            throw new ServiceException("用户中心更新用户失败,状态码:" + registerCode+" 提示信息:"+message);
        }

    }

    /**
     * 用户扫描课件二维码，验证邮箱后需要修改用户信息
     */
    public void updateUserEmail(String openid, String email) {
        // 1.获取userId
        UserCenter userCenterEP = new UserCenter();
        userCenterEP.setOpenid(openid);
        List<UserCenter> userCenterList = userCenterMapper.select(userCenterEP);
        if (CollectionUtils.isEmpty(userCenterList)) {
            log.error("根据openid：" + openid + "到数据库查询用户不存在!");
            return;
        }
        UserCenter userCenter = userCenterList.get(0);

        // 2.获取token
        String token = getToken();
        if ("".equals(token)) {
            throw new ServiceException("updateUser,获取token失败!");
        }
        //3.向用户中心发起更新请求，用户保存到智学云用户中心。
        final String updateUrl = baseUrl + "/users/update?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("id", userCenter.getUserId());//用户id
        paramsR.put("name", "");//昵称
        paramsR.put("email", email);  //邮箱
        String updateResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","userId":"59ed480889eeb92f380f4ab3"}
        log.debug("paramsR==" + JSON.toJSONString(paramsR));
        log.debug("updateResult==" + updateResult);
        JSONObject registerObject = JSON.parseObject(updateResult);
        int registerCode = registerObject.getInteger("code");
        if (registerCode != 0) {
            throw new ServiceException("用户中心更新用户失败,状态码" + registerCode);
        }

    }

    /**
     * 向智学云平台添加教师信息
     */
    public void addTeacherInfo(Client client) {
        try {
            // 1.获取token
            String token = this.getNewToken();
            if ("".equals(token)) {
                throw new ServiceException("addTeacherInfo,获取token失败!");
            }
            //2.向智学云平台发起更新请求，绑定图书和资源关系
            final String addUrl = baseUrl + "/teacher/add?token=" + token;
            Map<String, String> paramsR = new HashMap<>();
            paramsR.put("Mobile", client.getPhone());//用户手机号
            paramsR.put("Email", client.getEmail());//用户邮箱
            paramsR.put("InfoFrom", "wqyuedu");//来源(在用户中心注册的userid)
            paramsR.put("TeacherName", client.getName());//教师名称
            paramsR.put("Sex", "");//性别
            paramsR.put("Provence", "");//省
            paramsR.put("City", client.getSeat());//市
            paramsR.put("University", client.getSchool());//学校
            paramsR.put("College", client.getDepart());//院系
            paramsR.put("Major", client.getMajor());//专业
            paramsR.put("Level", "");//学历(博士、研究生、本科、大专)
            paramsR.put("Course", client.getLesson());//所教课程
            paramsR.put("PositionName", "");//职位、职称

            String addResult = HttpTookit.doPostJson(addUrl, paramsR);//{"code":0,"message":"Successfully","data":{}}
            log.debug("----------------paramsR==" + JSON.toJSONString(paramsR));
            log.debug("----------------addResult==" + addResult);
            JSONObject addObject = JSON.parseObject(addResult);
            int addCode = addObject.getInteger("code");
            if (addCode == 0) {
                log.debug("addTeacherInfo 成功!");
            }else{
                log.error("addTeacherInfo 失败!");
            }

        } catch (Exception e) {
            log.error("addTeacherInfo 失败!");
            e.printStackTrace();
        }

    }



    /**
     * 获取excel列表
     * @param in
     * @return
     */
    public static List<UserCenterVo> getData(InputStream in){
        LinkedHashMap<String, String> fieldMap = Maps.newLinkedHashMap();
        fieldMap.put("序号", "id");
        fieldMap.put("链接", "link");
        return ImportExcelUtil.excelToList(in, UserCenterVo.class, fieldMap);
    }

    /**
     *  向智学云平台绑定图书和资源关系
     * @param bookId
     * @param qrType
     * @param resId
     * @param lBookResId
     */
    public void bindBookRes(int bookId,int qrType,int resId,Integer lBookResId) {
        LBookResource lBookResource = getLBookResource(bookId,qrType,resId,lBookResId);
        lBookResource.setErrorType(1);//新增
        if(lBookResId == null){//旧资源取消任务执行
            lBookResourceService.updateStatusByResId(resId);
        }
        try {
            Book book = bookMapper.selectByPrimaryKey(bookId);
            Res res = resMapper.selectByPrimaryKey(resId);
            FileRes fileResEP = new FileRes();
            fileResEP.setUuid(res.getFileUuid());
            FileRes fileRes = fileResMapper.selectOne(fileResEP);

            String code = book.getCode();
            String name = fileRes.getName();
            if ( book == null || res == null || fileRes==null) {
                lBookResource.setErrorMessage("数据为空，无法继续执行");
                lBookResourceService.insertOrUpdate(lBookResource);
                System.err.println("----------------错误空数据--------------");
                return;
            }
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name) || "0".equals(name)) {
                System.err.println("----------------正常空数据--------------");
                return;
            }
            // 1.获取token
            String token = this.getNewToken();
            if ("".equals(token)) {
                lBookResource.setErrorMessage("获取token失败，无法继续执行");
                lBookResourceService.insertOrUpdate(lBookResource);
                throw new ServiceException("bindBookRes,获取token失败!");
            }
            //2.向智学云平台发起更新请求，绑定图书和资源关系
            final String updateUrl = baseUrl + "/book/resource_add?token=" + token;
            Map<String, String> paramsR = new HashMap<>();
            paramsR.put("bookId", book.getCode());//图书code
            paramsR.put("rid", String.valueOf(resId));//资源id
            paramsR.put("name", fileRes.getName());//资源名称
            paramsR.put("type", String.valueOf(qrType));  //资源类型:1课件 2扩展 3刮刮卡
            paramsR.put("resourceId", String.valueOf(fileRes.getZhixueid()));//资源zhixueid
            if(!"mp4".equals(fileRes.getSuffix())){
                paramsR.put("viewurl", fileRes.getViewurl());//非视频传预览路径
            }else{
                paramsR.put("viewurl", "");//视频传空
            }
            System.out.println("------------------paramsR=="+JSON.toJSONString(paramsR));
            String bindResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","data":{}}
            System.out.println("bindResult==" + bindResult);
            if(StringUtils.isEmpty(bindResult)){
                sendToZhixueyun(502,"智学云接口异常",JSON.toJSONString(paramsR));
                throw new ServiceException("访问智学云接口异常!");
            }
            JSONObject bindObject = JSON.parseObject(bindResult);
            int bindCode = bindObject.getInteger("code");
            lBookResource.setErrorCode(bindCode);
            lBookResource.setErrorMessage(bindObject.getString("message"));
            if (bindCode != 0) {
                lBookResourceService.insertOrUpdate(lBookResource);
                System.err.println("bindBookRes失败!");
                sendToZhixueyun(bindCode,bindObject.getString("message"),JSON.toJSONString(paramsR));
            }else {
                //成功执行，将状态改为成功
                if(lBookResId != null){
                    lBookResourceService.insertOrUpdate(lBookResource);
                }
            }
        }catch (Exception e){
            lBookResource.setErrorMessage(e.getMessage());
            lBookResourceService.insertOrUpdate(lBookResource);
            e.printStackTrace();
        }
    }

    /**
     *  向智学云平台删除图书和资源关系
     * @param resId
     * @param lBookResId
     */
    public void deleteBookRes(int resId,Integer lBookResId) {
        LBookResource lBookResource = getLBookResource(null,null,resId,lBookResId);
        lBookResource.setErrorType(2);//删除
        if(lBookResId == null){//旧资源取消任务执行
            lBookResourceService.updateStatusByResId(resId);
        }
        try {
            // 1.获取token
            String token = this.getNewToken();
            if ("".equals(token)) {
                lBookResource.setErrorMessage("获取token失败，无法继续执行");
                lBookResourceService.insertOrUpdate(lBookResource);
                throw new ServiceException("deleteBookRes,获取token失败!");
            }
            //2.向智学云平台发起更新请求，绑定图书和资源关系
            final String updateUrl = baseUrl + "/book/resource_del?token=" + token;
            Map<String, String> paramsR = new HashMap<>();
            paramsR.put("rid", String.valueOf(resId));//资源id
            log.debug("------------------paramsR=="+JSON.toJSONString(paramsR));
            String delResult = HttpTookit.doPostJson(updateUrl, paramsR);//{"code":0,"message":"Successfully","data":{}}
            log.debug("deleteBookRes Result==" + delResult);
            JSONObject delObject = JSON.parseObject(delResult);
            int delCode = delObject.getInteger("code");
            lBookResource.setErrorCode(delCode);
            lBookResource.setErrorMessage(delObject.getString("message"));
            if (delCode != 0) {
                lBookResourceService.insertOrUpdate(lBookResource);
                System.err.println("deleteBookRes失败!");
                sendToZhixueyun(delCode,delObject.getString("message"),JSON.toJSONString(paramsR));
            }else {
                //成功执行，将状态改为成功
                if(lBookResId != null){
                    lBookResourceService.insertOrUpdate(lBookResource);
                }
            }

        } catch (ServiceException e) {
            System.err.println("deleteBookRes失败!");
            e.printStackTrace();
        }catch (Exception e){
            lBookResource.setErrorMessage("系统异常");
            lBookResourceService.insertOrUpdate(lBookResource);
            e.printStackTrace();
        }
    }

    private LBookResource getLBookResource(Integer bookId, Integer qrType, Integer resId, Integer lBookResId) {
        LBookResource lBookResource = new LBookResource();
        lBookResource.setBookId(bookId);
        lBookResource.setQrType(qrType);
        lBookResource.setResId(resId);
        lBookResource.setId(lBookResId);
        return lBookResource;
    }

    /**
     * 当同步失败时发邮件给智学云
     * @param code
     * @param message
     * @param param
     */
    public void sendToZhixueyun(int code,String message,String param){
        String context = "错误码:"+code + " message:"+message + " 参数列表:"+param;
        String subject = "图书资源同步接口调用出错";
        String[] toMail = new String[]{zhixueEmail};
        mailSender.sendRichTextMail(context, subject, toMail);
    }

}
