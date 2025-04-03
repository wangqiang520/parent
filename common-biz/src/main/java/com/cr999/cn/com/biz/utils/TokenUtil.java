
package com.cr999.cn.com.biz.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.enums.ResultEnum;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: TokenUtils   
 * @Description: 使用token验证用户是否登录
 * @author: 王强
 * @date: 2020年4月29日 上午2:24:09     
 */
public class TokenUtil {
	private Logger logger= LoggerFactory.getLogger(TokenUtil.class);
	private String DATA="data";
    @Autowired
    RedisUtil redisUtil;


/**
     * @Title: token 
     * @Description: 生成Token
     * @param data 数据
     * @param maxTime 过期时间
     * @return String
     * @throws  
     */
    public String getToken (String data,long maxTime){
        String token = null;
        try {
        	//过期时间
        	long expirDate=Long.parseLong(ConstantEnum.EXPIRE_DATE.getValue());
        	expirDate=maxTime==0?expirDate:maxTime;
            Date date = new Date(System.currentTimeMillis()+expirDate);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            //设置头部信息
            Map<String,Object> header = new HashMap<String,Object>();
            header.put("type","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            JWTCreator.Builder builder = JWT.create();
            token = JWT.create()
                    .withHeader(header)
                    .withClaim(DATA,data)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            logger.error(e.getMessage());
            logger.error(ResultEnum.GENERATION_TOKEN_ERROR.getMsg());
            return  token;
        }
        return token;
    }

/**
     * @Title: checkToken 
     * @Description:校验Token
     * @param token
     * @return boolean
     * @throws  
     */
    public boolean checkToken(String token){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR);
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
        	logger.error(e.getMessage());
            logger.error(ResultEnum.CHECK_TOKEN_ERROR.getMsg());
            return  false;
        }
    }

/**
     * @Title: encodeTokenValue 
     * @Description: 解密Token里的值
     * @param token
     * @return String
     * @throws  
     */


    public String encodeTokenValue(String token) {
    	String tokenValue=null;
    	 try {
             Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
             JWTVerifier verifier = JWT.require(algorithm).build();
             DecodedJWT jwt=verifier.verify(token);
             tokenValue=jwt.getClaim(DATA).asString();
             return tokenValue;
         }catch (Exception e){
         	logger.error(e.getMessage());
            logger.error(ResultEnum.CHECK_TOKEN_ERROR.getMsg());
            return tokenValue;
         }
    }
    
    public void main(String[] args) {
      String username ="zhangsan";
        String token = getToken(username,200000L);
        System.out.println(token);
        boolean b = checkToken(token);
        System.out.println(b);
       String encode= encodeTokenValue(token);

        token="eyJ0eXAiOiJKV1QiLCJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJkYXRhIjoiemhhbmdzYW4iLCJleHAiOjE2Mzc1MDAyNTR9.quFBxJVciQrVb69MR4J1b6bSdmvcB0z7qedKZUxXdDg";

        System.out.println(checkToken(token));
        System.out.println(encodeTokenValue(token));

    }

    public String createToken(String data,long maxTime,String userId){
        if(StringUtils.isNotBlank(data) || StringUtils.isNotBlank(userId)){
            throw new CustomException(ResultEnum.GENERATION_TOKEN_ERROR);
        }
        String token=null;
        try{
            long expirDate=Long.parseLong(ConstantEnum.EXPIRE_DATE.getValue());
            expirDate=maxTime==0?expirDate:maxTime;
            long expirTime=System.currentTimeMillis()+expirDate*1000;
            Date date = new Date(expirTime);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(ConstantEnum.SECRET_KEY.getValue());
            //设置头部信息
            Map<String,Object> header = new HashMap<String,Object>();
            header.put("type","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            JWTCreator.Builder builder = JWT.create();
            token = JWT.create()
                    .withHeader(header) //头部信息
                    .withClaim(DATA,data)//业务数据
                    .withExpiresAt(date)//到期时间
                    .sign(algorithm);//加密算法
            redisUtil.set(token,data,expirDate*2);
            }catch (Exception e){
                logger.error(e.getMessage());
                logger.error(ResultEnum.GENERATION_TOKEN_ERROR.getMsg());
                e.printStackTrace();
        }

        return token;
    }


    public String refreshToken(String token){
        if(StringUtils.isBlank(token)) {
            throw new CustomException(ResultEnum.TOKEN_NULL_ERROR);
        }
        //判断key是否存在
        if(redisUtil.hasKey(token)){

        }else{

        }
        return null;
    }

}

