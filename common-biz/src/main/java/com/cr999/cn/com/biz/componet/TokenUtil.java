package com.cr999.cn.com.biz.componet;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.ResultEnum;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author wangqiang
 * @Date 2021/11/28 17:52
 * @Version 1.0
 **/
@Component
public class TokenUtil {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisUtil redisUtil;
    String prefix = ConstantEnum.USER_TOKEN_PREFIX_.getValue();

    public String createToken(String data){
        if(StringUtils.isBlank(data)){
            throw new CustomException(ResultEnum.GENERATION_TOKEN_ERROR.getMsg(),ResultEnum.GENERATION_TOKEN_ERROR.getCode());
        }
        String token=null;
        try{
            long expirDate=Long.parseLong(ConstantEnum.EXPIRE_DATE.getValue());
            Date date = new Date(System.currentTimeMillis()+expirDate*1000);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            //设置头部信息
            Map<String,Object> header = new HashMap<String,Object>();
            header.put("type","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header) //头部信息
                    .withClaim("data",data)//业务数据
                    .withExpiresAt(date)//到期时间
                    .sign(algorithm);//加密算法
            redisUtil.set(prefix+token,data,expirDate*2);
        }catch (Exception e){
            logger.error(e.getMessage());
            logger.error(ResultEnum.GENERATION_TOKEN_ERROR.getMsg());
            e.printStackTrace();
        }
        return token;
    }


    public boolean checkToken(String token){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        if(!redisUtil.hasKey(prefix+token)){
            throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
    }

    public String refreshToken2(String token,String account){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        if(!redisUtil.hasKey(prefix+token)){
            throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
        }
        //判断key是否存在
        String data= (String) redisUtil.get(prefix+token);
        if(StringUtils.isNotBlank(data)){
            return createToken(data);
        }
        logger.error(ResultEnum.REFRESH_TOKEN_ERROR.getMsg());
        throw new CustomException(ResultEnum.REFRESH_TOKEN_ERROR.getMsg(),ResultEnum.REFRESH_TOKEN_ERROR.getCode());

    }


    public String getTokenContent(String token) {
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        if(!redisUtil.hasKey(prefix+token)){
            throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
        }
        DecodedJWT jwt=null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            JWTVerifier verifier = JWT.require(algorithm).build();
            jwt=verifier.verify(token);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg(),ResultEnum.TOKEN_EXPIRED_ERROR.getCode());
        }

        String data=jwt.getClaim("data").asString();
        if(StringUtils.isBlank(data)){
            throw new CustomException(ResultEnum.GET_TOKEN_CONTENT_ERROR.getMsg(),ResultEnum.GET_TOKEN_CONTENT_ERROR.getCode());
        }
        return data;
    }
}
