package com.cr999.cn.user.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.ResultEnum;
import com.cr999.cn.common.utils.EncryptionUtils;
import com.cr999.cn.entity.user.User;
import com.cr999.cn.user.biz.service.TokenService;
import com.cr999.cn.user.biz.service.UserService;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    private Logger logger= LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserService userService;
    String prefix = ConstantEnum.USER_TOKEN_PREFIX_.getValue();



    @Override
    public String createToken(String data, String expirDate, String userId) {
        Long expirTime=System.currentTimeMillis()/1000+Long.parseLong(expirDate);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("expirTime",expirTime);
        map.put("data",data);
        map.put("userId",userId);
        String jsonString = JSONObject.toJSONString(map);
        String token = EncryptionUtils.encodeAes(jsonString, ConstantEnum.SECRET_KEY.getValue());
        //redisUtil.set(prefix+token,data,Long.parseLong(ConstantEnum.EXPIRE_DATE.getValue()));
        return token;
    }

    @Override
    public boolean checkToken(String token) {
        try {
            if(StringUtils.isBlank(token)) {
                throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
            }
            Map<String,Object> map=getTokenContent(token);
            Long expirTime= (Long) map.get("expirTime");
            if(expirTime==null){
                throw new CustomException("token格式错误！");
            }
            long currentTime = System.currentTimeMillis()/1000;
            return currentTime <= expirTime;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String refreshToken(String token) {
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        Map<String,Object> map=getTokenContent(token);
        Long expirTime= (Long) map.get("expirTime");
        expirTime=expirTime+Long.parseLong(ConstantEnum.EXPIRE_DATE.getValue());
        long currentTime = System.currentTimeMillis()/1000;
        String result=null;
        if(currentTime<expirTime){
            //生成新的token
            String data=(String) map.get("data");
            String userId=(String) map.get("userId");
            result=createToken(data,ConstantEnum.EXPIRE_DATE.getValue(),userId);
            return result;
        }
        log.info(ResultEnum.REFRESH_TOKEN_ERROR.getMsg());
        return null;
    }


    @Override
    public Map<String,Object> getTokenContent(String token){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        try {
            String s = EncryptionUtils.decodeAes(token, ConstantEnum.SECRET_KEY.getValue());
            Map map = JSONObject.parseObject(s, Map.class);
            return map;
        }catch (Exception e){
            throw new CustomException("token 解密失败！");
        }
    }

    @Override
    public String createToken2(User user){
        String data=JSONObject.toJSONString(user);
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


    @Override
    public boolean checkToken2(String token){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
            logger.error(ResultEnum.TOKEN_EXPIRED_ERROR.getMsg());
        }
        return  false;
    }

    @Override
    public String refreshToken2(String token,String account){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR.getMsg(),ResultEnum.TOKEN_NULL_ERROR.getCode());
        }
        //判断key是否存在
        if(redisUtil.hasKey(prefix+token)){
            User user = userService.getOne(new QueryWrapper<User>().eq("account", account));
            return createToken2(user);
        }else{
            logger.error(ResultEnum.REFRESH_TOKEN_ERROR.getMsg());
            //throw new CustomException(ResultEnum.REFRESH_TOKEN_ERROR.getMsg(),ResultEnum.REFRESH_TOKEN_ERROR.getCode());
            return null;
        }
    }

    @Override
    public User getTokenContent2(String token) {
        User user=null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt=verifier.verify(token);
            String tokenValue=jwt.getClaim("data").asString();
            user = JSONObject.parseObject(tokenValue, User.class);
        }catch (Exception e){
            logger.error(e.getMessage());
            logger.error(ResultEnum.GET_TOKEN_CONTENT_ERROR.getMsg());
            e.printStackTrace();
        }
        return user;
    }
}
