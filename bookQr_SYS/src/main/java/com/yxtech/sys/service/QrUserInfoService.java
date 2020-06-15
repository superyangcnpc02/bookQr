package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.sys.dao.QrUserInfoMapper;
import com.yxtech.sys.domain.QrUserInfo;
import com.yxtech.utils.qr.HttpTookit;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukailong on 2016/09/21.
 */

@Service
public class QrUserInfoService extends BaseService<QrUserInfo> {


    private QrUserInfoMapper qrUserInfoMapper;

    public QrUserInfoMapper getQrUserInfoMapper() {
        return qrUserInfoMapper;
    }


    @Resource(name = "qrUserInfoMapper")
    public void setQrUserInfoMapper(QrUserInfoMapper qrUserInfoMapper) {
        setMapper(qrUserInfoMapper);
        this.qrUserInfoMapper = qrUserInfoMapper;
    }

    /**
     * 记录微信用户扫码信息
     * @author liukailong 20160921
     * @param id
     * @param code
     */
    public String qrUserInfoSave(Integer id,String code,String activation){
        String access_token = "";
        String openid = "";
        String unionid = "";
        //通过code换取网页授权access_token
        final String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ Constant.WEIXIN_APPID+"&secret="+Constant.WEIXIN_SECRET+"&code="+ code +"&grant_type=authorization_code";
        String tokenResult= HttpTookit.doGet(getTokenUrl, null, "utf-8", true);
        System.out.println("tokenResult=="+tokenResult);
//              {  "access_token": "WBvAxxPXba9lft5sHgX4RnwW39nj877uzaZOoSDsIVt7dWGjiryKFHk075USUY-bQXAXx75TPMpbyaQG4X2aQ7NzFlj9PEbIv61wkeQaRrc",
//                    "expires_in": 7200,
//                    "refresh_token": "MQyn6NmLF8-SUNZf78tUfAmZMHXSe3InosPdC5LMeBPvat10PwbicpCgcPlDDoHFOZB8xJ451ksdzXub1h9DCVaaAXaphBxtJA4fVKBV_ec",
//                    "openid": "oUgl9wVGX_7DxiJ7_44fPLOuQ5bE",
//                    "scope": "snsapi_userinfo" }
//            错误时微信会返回JSON数据包如下（示例为Code无效错误）:    {"errcode":40029,"errmsg":"invalid code"}
        JSONObject tokenObj = JSONObject.fromObject(tokenResult);
        if (tokenObj.has("errcode")) {
            int errcode = tokenObj.getInt("errcode");
            String errmsg = tokenObj.getString("errmsg");
            throw new ServiceException("获取微信access_token失败,errcode:"+errcode+" errmsg:"+errmsg);
            //return "";
        }
        if (tokenObj.has("openid")) {
            openid = tokenObj.getString("openid");
        }
        if (tokenObj.has("unionid")) {
            unionid = tokenObj.getString("unionid");
        }
        if (tokenObj.has("access_token")) {
            access_token = tokenObj.getString("access_token");
        }
        //拉取用户信息(需scope为 snsapi_userinfo)
        final String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
        String userResult= HttpTookit.doGet(userUrl, null, "utf-8", true);
//              {  "openid": "oUgl9wVGX_7DxiJ7_44fPLOuQ5bE",
//                    "nickname": "无独无偶",
//                    "sex": 1,
//                    "language": "zh_CN",
//                    "city": "安阳",
//                    "province": "河南",
//                    "country": "中国",
//                    "headimgurl": "http://wx.qlogo.cn/mmopen/iaRlzG8zy7BvRuhHyibrXPWUpibyfV6huJVqwEDgibS5Zh773kRFbUeZaVNgITM0N8MIicDRSkr1bmiaSlAianHbhaLXg/0",
//                    "privilege": [] }
//            错误时微信会返回JSON数据包如下（示例为openid无效）:   {"errcode":40003,"errmsg":" invalid openid "}
        System.out.println("userResult=="+userResult);
        JSONObject userObj = JSONObject.fromObject(userResult);
        if (userObj.has("errcode")) {
            int errcode = userObj.getInt("errcode");
            String errmsg = userObj.getString("errmsg");
            throw new ServiceException("获取微信用户信息失败,errcode:"+errcode+" errmsg:"+errmsg);
            //return "";
        }
        String nickname = "";
        if (userObj.has("nickname")) {
            nickname = userObj.getString("nickname");
            System.out.println("转码前=" + nickname);
            try {
                nickname = URLEncoder.encode(nickname, "utf-8");
                System.out.println("转码后=" + nickname);
            } catch (UnsupportedEncodingException e) {
                nickname = "无法转码的昵称";
                log.error("编码成utf-8失败!", e);
            }
        }
        String sex = "";
        if (userObj.has("sex")) {
            sex = userObj.getString("sex");
        }
        String language = "";
        if (userObj.has("language")) {
            language = userObj.getString("language");
        }
        String city = "";
        if (userObj.has("city")) {
            city = userObj.getString("city");
        }
        String province = "";
        if (userObj.has("province")) {
            province = userObj.getString("province");
        }
        String country = "";
        if (userObj.has("country")) {
            country = userObj.getString("country");
        }
        String headimgurl = "";
        if (userObj.has("headimgurl")) {
            headimgurl = userObj.getString("headimgurl");
        }
        String privilege = "";
        if (userObj.has("privilege")) {
            privilege = userObj.getString("privilege");
        }
        QrUserInfo userInfo = new QrUserInfo();
        if (!StringUtils.isBlank(activation)) {//刮刮乐
            userInfo.setActivation(activation);
            //查看表中activation是否存在
            List<QrUserInfo> list = qrUserInfoMapper.select(userInfo);
            if (list != null && list.size() != 0 && !StringUtils.isBlank(activation)) {
                return openid + "," + unionid;
            } else {
                userInfo.setQrId(id);
                userInfo.setOpenid(openid);
                userInfo.setNickname(nickname);
                userInfo.setSex(sex);
                userInfo.setProvince(province);
                userInfo.setCity(city);
                userInfo.setCountry(country);
                userInfo.setHeadimgurl(headimgurl);
                userInfo.setPrivilege(privilege);
                userInfo.setUnionid(unionid);
                userInfo.setActivation(activation);
                userInfo.setCreateTime(new Date());
                userInfo.setType(1);
                if (!StringUtils.isBlank(openid)) {
                    qrUserInfoMapper.insertSelective(userInfo);
                }
            }
        } else {
            //如果数据库有该openid下载该资源且是今天的数据,则数量+1;否则重新插入一条
            Map map = new HashMap<>();
            map.put("qrId", id);
            map.put("openId", openid);
            List<QrUserInfo> list = qrUserInfoMapper.getListByMap(map);
            System.out.println("------------------查询数据库是否存在数据listSize:" + list.size());
            if (null != list && list.size() > 0) {
                QrUserInfo user = list.get(0);
                user.setNum(user.getNum() + 1);
                System.out.println("--------------进入更新逻辑num---------------" + user.getNum());
                qrUserInfoMapper.updateByPrimaryKeySelective(user);
            } else {
                System.out.println("-------------进入新增逻辑-------------");
                userInfo.setQrId(id);
                userInfo.setOpenid(openid);
                userInfo.setNickname(nickname);
                userInfo.setSex(sex);
                userInfo.setProvince(province);
                userInfo.setCity(city);
                userInfo.setCountry(country);
                userInfo.setHeadimgurl(headimgurl);
                userInfo.setPrivilege(privilege);
                userInfo.setUnionid(unionid);
                userInfo.setActivation(activation);
                userInfo.setCreateTime(new Date());
                userInfo.setType(1);
                if (!StringUtils.isBlank(openid)) {
                    qrUserInfoMapper.insertSelective(userInfo);
                }
            }
        }

        return openid + "," + unionid;
    }

    /**
     * 记录微信用户扫描 权限二维码
     * @author cuihao 20170801
     * @param id
     * @param code
     */
    public String authQrUserInfoSave(Integer id,String code,String authkey){
        System.out.println("2 获取微信用户openid,begin="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        String access_token = "";
        String openid = "";
        String unionid = "";
        //通过code换取网页授权access_token
        final String getTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ Constant.WEIXIN_APPID+"&secret="+Constant.WEIXIN_SECRET+"&code="+ code +"&grant_type=authorization_code";
        String tokenResult= HttpTookit.doGet(getTokenUrl, null, "utf-8", true);
        System.out.println("tokenResult=="+tokenResult);
//               { "access_token": "WBvAxxPXba9lft5sHgX4RnwW39nj877uzaZOoSDsIVt7dWGjiryKFHk075USUY-bQXAXx75TPMpbyaQG4X2aQ7NzFlj9PEbIv61wkeQaRrc",
//                    "expires_in": 7200,
//                    "refresh_token": "MQyn6NmLF8-SUNZf78tUfAmZMHXSe3InosPdC5LMeBPvat10PwbicpCgcPlDDoHFOZB8xJ451ksdzXub1h9DCVaaAXaphBxtJA4fVKBV_ec",
//                    "openid": "oUgl9wVGX_7DxiJ7_44fPLOuQ5bE",
//                    "scope": "snsapi_userinfo" }
//            错误时微信会返回JSON数据包如下（示例为Code无效错误）:    {"errcode":40029,"errmsg":"invalid code"}
        try {
            JSONObject tokenObj = JSONObject.fromObject(tokenResult);
            if(tokenObj.has("errcode")){
                return "";
            }
            if(tokenObj.has("openid")){
                openid = tokenObj.getString("openid");
            }
            if(tokenObj.has("unionid")){
                unionid = tokenObj.getString("unionid");
            }
            if(tokenObj.has("access_token")){
                access_token = tokenObj.getString("access_token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //拉取用户信息(需scope为 snsapi_userinfo)
        final String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
        String userResult= HttpTookit.doGet(userUrl, null, "utf-8", true);
//               { "openid": "oUgl9wVGX_7DxiJ7_44fPLOuQ5bE",
//                    "nickname": "无独无偶",
//                    "sex": 1,
//                    "language": "zh_CN",
//                    "city": "安阳",
//                    "province": "河南",
//                    "country": "中国",
//                    "headimgurl": "http://wx.qlogo.cn/mmopen/iaRlzG8zy7BvRuhHyibrXPWUpibyfV6huJVqwEDgibS5Zh773kRFbUeZaVNgITM0N8MIicDRSkr1bmiaSlAianHbhaLXg/0",
//                    "privilege": []  }
//            错误时微信会返回JSON数据包如下（示例为openid无效）:   {"errcode":40003,"errmsg":" invalid openid "}
        System.out.println("userResult=="+userResult);
        try {
            JSONObject userObj = JSONObject.fromObject(userResult);
            if(userObj.has("errcode")){
                return "";
            }
            String nickname = "";
            if(userObj.has("nickname")){
                nickname = userObj.getString("nickname");
                System.out.println("转码前="+nickname);
                try {
                    nickname = URLEncoder.encode(nickname, "utf-8");
                    System.out.println("转码后="+nickname);
                } catch (UnsupportedEncodingException e) {
                    nickname = "无法转码的昵称";
                    log.error("编码成utf-8失败!",e);
                }
            }
            String sex = "";
            if(userObj.has("sex")){
                sex = userObj.getString("sex");
            }
            String language = "";
            if(userObj.has("language")){
                language = userObj.getString("language");
            }
            String city = "";
            if(userObj.has("city")){
                city = userObj.getString("city");
            }
            String province = "";
            if(userObj.has("province")){
                province = userObj.getString("province");
            }
            String country = "";
            if(userObj.has("country")){
                country = userObj.getString("country");
            }
            String headimgurl = "";
            if(userObj.has("headimgurl")){
                headimgurl = userObj.getString("headimgurl");
            }
            String privilege = "";
            if(userObj.has("privilege")){
                privilege = userObj.getString("privilege");
            }
            QrUserInfo userInfo = new QrUserInfo();
            if(!StringUtils.isBlank(authkey)){//权限二维码
                userInfo.setAuthkey(authkey);
                //查看表中activation是否存在
                List<QrUserInfo> list = qrUserInfoMapper.select(userInfo);
                if(list!=null && list.size()!=0 && !StringUtils.isBlank(authkey)){
                    System.out.println("2 获取微信用户openid,end="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                    return openid + "," + unionid;
                }else{
                    userInfo.setQrId(id);
                    userInfo.setOpenid(openid);
                    userInfo.setNickname(nickname);
                    userInfo.setSex(sex);
                    userInfo.setProvince(province);
                    userInfo.setCity(city);
                    userInfo.setCountry(country);
                    userInfo.setHeadimgurl(headimgurl);
                    userInfo.setPrivilege(privilege);
                    userInfo.setUnionid(unionid);
                    userInfo.setAuthkey(authkey);
                    userInfo.setCreateTime(new Date());
                    if(!StringUtils.isBlank(openid)){
                        qrUserInfoMapper.insertSelective(userInfo);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("2 获取微信用户openid,end="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        return openid + "," + unionid;
    }

    /**
     * 查询该微信用户是否扫码
     * 1未扫码，2已扫码
     * @param type 1 微信 2 qq
     * @param qrId
     * @param openId
     * @return
     */
    public int getRightStatus(int type, int qrId, String openId){
        /*
         * 2019-02-21
         * 因为编辑把权限二维码贴错了，判断code为075446-03的权限时，只要扫过075446-02,也让他访问075446-03的
         * 075446-03的authId为 627
         * 075446-02的authId为 532
         */
        if(qrId == 627){
            List<QrUserInfo> qrUserInfos = qrUserInfoMapper.getByCondition(type,qrId,openId);
            if(CollectionUtils.isEmpty(qrUserInfos)){
                return 1;   // 无扫码记录
            }
            return 2;
        }else{
            Example example = new Example(QrUserInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("qrId", qrId);
            criteria.andEqualTo("openid", openId);
            criteria.andIsNotNull("authkey");
            criteria.andEqualTo("type", type);
            int count = qrUserInfoMapper.selectCountByExample(example);
            if (count == 0 ) {
                return 1;   // 无扫码记录
            }
            return 2;
        }

    }


    /**
     * QQ SDK v2.0 获取QQ扫码用户信息 返回二维码 QrUserInfo 对象
     * @return
     */
    public QrUserInfo getQrUserInfoByQQ(Integer id, String code, String qqAppId, String qqAppKey, String qqCallBackUrl){
        QrUserInfo qrUserInfo = new QrUserInfo();
        qrUserInfo.setType(2);
        qrUserInfo.setQrId(id);
        try {
            String getTokenUrl = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + qqAppId + "&client_secret=" + qqAppKey + "&code="+code+"&redirect_uri=" + qqCallBackUrl;
            String tokenResult= HttpTookit.doGet(getTokenUrl, null, "utf-8", true);
            Map<String, String> tokenMap = queryStringToMap(tokenResult);
            if (tokenMap == null) {
                return null;
            }
            String refresh_token = tokenMap.get("refresh_token");
            String auto_access_token = "https://graph.qq.com/oauth2.0/token?grant_type=refresh_token&client_id=" + qqAppId + "&client_secret=" + qqAppKey + "&refresh_token=" + refresh_token;
            String autoTokenResult = HttpTookit.doGet(auto_access_token, null, "utf-8", true);
            Map<String, String> autoTokenResultMap = queryStringToMap(autoTokenResult);
            if (autoTokenResultMap == null) {
                return null;
            }
            String access_token = autoTokenResultMap.get("access_token");
            String openIdUrl = "https://graph.qq.com/oauth2.0/me?access_token=" + access_token;
            String openIdResult = HttpTookit.doGet(openIdUrl, null, "utf-8", true);
            openIdResult = openIdResult.substring(openIdResult.indexOf("{"), openIdResult.indexOf("}")+1);
            JSONObject openObj = JSONObject.fromObject(openIdResult);
            String openId = openObj.getString("openid");
            String userInfo = "https://graph.qq.com/user/get_user_info?access_token=" + access_token + "&oauth_consumer_key=" + qqAppId + "&openid=" + openId;
            String user = HttpTookit.doGet(userInfo, null, "utf-8", true);
            user = user.replace("\r\n", "");
            JSONObject userBean = JSONObject.fromObject(user);
            qrUserInfo.setOpenid(openId);
            qrUserInfo.setNickname(userBean.getString("nickname"));
            String gender = userBean.getString("gender");
            if ("男".equals(gender)) {
                qrUserInfo.setSex("1");
            }else if("女".equals(gender)) {
                qrUserInfo.setSex("2");
            }else {
                qrUserInfo.setSex("0");
            }
            qrUserInfo.setProvince(userBean.getString("province"));
            qrUserInfo.setCity(userBean.getString("city"));
            qrUserInfo.setCountry("");
            qrUserInfo.setHeadimgurl(userBean.getString("figureurl_qq_2"));
            qrUserInfo.setCreateTime(new Date());
            return qrUserInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qrUserInfo;
    }

    public Map<String, String> queryStringToMap(String queryString){
        if (!queryString.contains("&")) {
            return null;
        }
        Map<String, String> qm = new HashMap<>();
        if (!StringUtils.isEmpty(queryString)) {
            String[] qma = queryString.split("&");
            for (String s : qma) {
                if (!StringUtils.isEmpty(s)) {
                    String key = s.substring(0, s.indexOf("="));
                    String value = s.substring(s.indexOf("=")+1);
                    qm.put(key, value);
                }
            }
        }
        return qm;
    }

    /**
     * 保存QQ扫码后的用户信息
     * @param code
     * @return
     */
    public String saveQQUserInfo(Integer id, String code, String qqAppId, String qqAppKey, String qqCallBackUrl) {
        // 从这堆参数中拿到QQ登录成功后的用户信息
        QrUserInfo qrUserInfo = getQrUserInfoByQQ(id, code, qqAppId, qqAppKey, qqCallBackUrl);
        if (qrUserInfo != null) {
            qrUserInfoMapper.insertSelective(qrUserInfo);
            return qrUserInfo.getOpenid();
        }else {
            return null;
        }
    }

    public String saveQQUserInfo(Integer id, String code, String qqAppId, String qqAppKey, String qqCallBackUrl, String authKey) {
        // 从这堆参数中拿到QQ登录成功后的用户信息
        QrUserInfo qrUserInfo = getQrUserInfoByQQ(id, code, qqAppId, qqAppKey, qqCallBackUrl);
        QrUserInfo userInfo = new QrUserInfo();
        userInfo.setAuthkey(authKey);
        List<QrUserInfo> qrUserInfoList = qrUserInfoMapper.select(userInfo);
        if(qrUserInfoList!=null && qrUserInfoList.size()!=0 && !StringUtils.isBlank(authKey)){
            return qrUserInfo.getOpenid();
        }else{
            if (qrUserInfo != null) {
                qrUserInfo.setAuthkey(authKey);
                qrUserInfoMapper.insertSelective(qrUserInfo);
                return qrUserInfo.getOpenid();
            }
        }
        return qrUserInfo.getOpenid();
    }
}
