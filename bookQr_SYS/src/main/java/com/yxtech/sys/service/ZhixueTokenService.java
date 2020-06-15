package com.yxtech.sys.service;

import com.yxtech.common.advice.ServiceException;
import com.yxtech.utils.qr.HttpTookit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lichagnfeng on 2017/4/20.
 */
@Service
public class ZhixueTokenService {

    @Value("#{configProperties['getTokenUrl']}")
    private String getTokenUrl;
    public final static Logger log = LoggerFactory.getLogger(ZhixueTokenService.class);
    @Autowired
    private UserCenterService userCenterService;

    /**
     * 用于存储递增数据
     */
    @Resource(name = "incrementRedisTemplate")
    protected RedisTemplate incrementRedisTemplate;

    public ZhixueTokenService() {
    }

    public synchronized String getToken() {
        return userCenterService.getToken();
    }

    public synchronized String getToken1() {
        ValueOperations<String, String> valueOperations = this.incrementRedisTemplate.opsForValue();
        String token = (String)valueOperations.get("zhixueToken");
        return StringUtils.isEmpty(token) ? this.getZhixueToken() : token;
    }

    public String getZhixueToken() {
        String tokenResult = HttpTookit.doGet(this.getTokenUrl, (String)null, "utf-8", true);
        log.debug("tokenResult:" + tokenResult);
        if (!StringUtils.isEmpty(tokenResult) && tokenResult.matches("\\\"[A-Z0-9]+\\\"[\\r\\n]?[\\n]?")) {
            String token = tokenResult.replace("\"", "").replace("\r\n", "");
            ValueOperations<String, String> valueOperations = this.incrementRedisTemplate.opsForValue();
            valueOperations.set("zhixueToken", token, 50L, TimeUnit.MINUTES);
            return token;
        } else {
            throw new ServiceException("token result is error:" + tokenResult);
        }
    }
}
