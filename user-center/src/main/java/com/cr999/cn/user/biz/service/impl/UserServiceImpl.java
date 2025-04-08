package com.cr999.cn.user.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr999.cn.com.biz.componet.BaseContext;
import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.componet.TokenUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.com.biz.interceptor.MySqlLimitAddInterceptor;
import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.enums.ResultEnum;
import com.cr999.cn.vo.UserBaseVo;
import com.cr999.cn.vo.UserVo;
import com.cr999.cn.entity.User;
import com.cr999.cn.user.biz.mapper.UserMapper;
import com.cr999.cn.user.biz.service.TokenService;
import com.cr999.cn.user.biz.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 文件描述：
 *
 * @author wangqiang
 * @version 1.0
 * @date 2021/4/5 0:20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    UserMapper userMapper;

    @Value("${sms.verification.switch}")
    boolean smsVerificationSwitch;  //登录短信验证是否开启

    @Autowired
    TokenService tokenService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    BaseContext baseContext;

    @Autowired
    SystemParameterService systemParameterService;

    @Override
    public List<User> getUserList(UserVo vo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        String nickName = vo.getNickName();
        String userName = vo.getUserName();
        String account = vo.getAccount();
        String mobile = vo.getMobile();
        String email = vo.getEmail();
        if (StringUtils.isNotBlank(nickName)) {
            queryWrapper.like("nick_name", nickName);
        }
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("user_name", userName);
        }
        if (StringUtils.isNotBlank(account)) {
            queryWrapper.like("account", account);
        }
        if (StringUtils.isNotBlank(mobile)) {
            queryWrapper.like("mobile", mobile);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like("email", email);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList;
    }

    @Override
    public User register(UserVo vo) {
        if(vo==null){
            throw new CustomException(ResultEnum.PARAMETER_EMPTY_ERROR);
        }
        User user=new User();
        BeanUtils.copyProperties(vo,user);
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        queryWrapper.eq("identity_card",user.getIdentityCard());
        List<User> users = userMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(users)){
            userMapper.insert(user);
        }else{
            throw new CustomException("身份证号码已存在");
        }
        return user;

    }

    @Override
    public String login(UserVo vo) {
        String account = vo.getAccount();
        String password = vo.getPassWord();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            throw new CustomException("帐号或密码不能为空！");
        }
        User user = this.getOne(new QueryWrapper<User>().eq("account", account).eq("status","0"));
        //判断帐号是否存在
        if (user == null) {
            throw new CustomException("帐号不存在！");
        }
        String pwd = user.getPassWord();
        //判断加密后的密码是否正确
        if (!password.equals(pwd)) {
            throw new CustomException("密码错误！");
        }
        return login(user,vo);
    }

    @Override
    public void logout() {
        String token = baseContext.getToken();
        String prefix = ConstantEnum.USER_TOKEN_PREFIX_.getValue();
        redisUtil.del(prefix+token);
    }

    @Override
    public void update(UserVo vo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("account",vo.getAccount());
        User user = this.getOne(queryWrapper);
        if(user==null){
            throw new CustomException("帐号不存在！",ResultEnum.DATA_NOT_EXIST.getCode());
        }
        if(user.getStatus()==1){
            throw new CustomException("用户已离职！",ResultEnum.STOP_STATE.getCode());
        }
        if(StringUtils.isNotBlank(vo.getEmail())){

        }
        //只能修改
        BeanUtils.copyProperties(vo,user);
        userMapper.updateById(user);
    }

    @Override
    public UserBaseVo currentLogin() {
        String token = baseContext.getToken();
        String prefix = ConstantEnum.USER_TOKEN_PREFIX_.getValue();
        String s = (String) redisUtil.get(prefix + token);
        UserBaseVo userBaseVo= JSONObject.parseObject(s,UserBaseVo.class);
        return userBaseVo;
    }

    public String login(User user, UserVo vo) {
        UserBaseVo userBaseVo=new UserBaseVo();
        BeanUtils.copyProperties(user,userBaseVo);
        String userId = userBaseVo.getId();
        if (smsVerificationSwitch) {
            if(vo.getCheckVerificationCode()==null){
                throw new CustomException(ResultEnum.PARAMETER_CHECKOUT_ERROR.getMsg().replace("%s", "checkVerificationCode"),ResultEnum.PARAMETER_CHECKOUT_ERROR.getCode());
            }
            String mobile = userBaseVo.getMobile();
            if (StringUtils.isBlank(mobile)) {
                throw new CustomException("手机号码为空，不能发送短信验证码！");
            }
            //短信验证码key
            String verificationCodeKey = "userId:" + userId + ":" + mobile + "verificationCodeKey";

            //判断本次登录，是属于获取验证码，还是较验验证码
            if (vo.getCheckVerificationCode()) {
                //较验验证
                String verificationCode = vo.getVerificationCode();
                if (StringUtils.isBlank(verificationCode)) {
                    throw new CustomException(ResultEnum.PARAMETER_CHECKOUT_ERROR.getMsg().replace("%s", "VerificationCode"),ResultEnum.PARAMETER_CHECKOUT_ERROR.getCode());
                }
                //获取保存到redis中的验证码
                String verificationCodeValue = (String) redisUtil.get(verificationCodeKey);
                if (StringUtils.isNotBlank(verificationCodeValue)) {
                    if (verificationCodeValue.equals(verificationCode)) {
                        //生成token
                        return tokenUtil.createToken(JSONObject.toJSONString(userBaseVo));
                    }
                    throw new CustomException(ResultEnum.LOGIN_FAILED_VALIDATION_FAILED);
                } else {
                    throw new CustomException(ResultEnum.LOGIN_FAILED_VALIDATION_EXPIRED);
                }
            } else {
                //发送短信验证码
                return sendSMSVerificationCode(vo)+"";
            }
        } else {
            return tokenUtil.createToken(JSONObject.toJSONString(userBaseVo));
        }
    }

    /**
    * @Author 19075
    *         <p>
    *         <li>2025/4/8-11:02</li>
    *         <li>Function Description</li>
    *         CN:发送短信验证码
    *         EN:
    *         <li>Flow Description</li>
    *         CN:
    *         EN:
    *         </p>
    * @param vo
    * @return
    **/
    public String sendSMSVerificationCode(UserVo vo){

        String account = vo.getAccount();

        User user = this.getOne(new QueryWrapper<User>().eq("account", account).eq("status","0"));
        //判断帐号是否存在
        if (user == null) {
            throw new CustomException(ResultEnum.ACCOUNT_DOES_NOT_EXIST);
        }
        String userId=user.getId();
        String mobile=user.getMobile();
        //短信验证码key
        String verificationCodeKey = "userId:" + userId + ":" + mobile + "verificationCodeKey";
        //短信发送次数key
        String sendSmsCountKey = "userId:" + userId + ":" + mobile + "count";

        //限制短信发送次数过期时间
        String sendSmsCountExpireDate = systemParameterService.getParmValue("SEND_SMS_COUNT_EXPIRE_DATE", "*",true);

        //获取短信已经发送的次数
        Integer SMSCount = (Integer) redisUtil.get(sendSmsCountKey);
        //限制短信发送次数过期时间
        if (SMSCount != null) {
            //限制短信发送次数
            String sendSmsCount = systemParameterService.getParmValue("SEND_SMS_COUNT", "*",true);
            if (SMSCount >= Integer.valueOf(sendSmsCount)) {
                throw new CustomException("登录失败，短信验证次数已用完，" + sendSmsCountExpireDate + "秒内，已连续发送短信" + sendSmsCount + "条，请稍后再尝试");
            }
            //自增1
            redisUtil.incr(sendSmsCountKey,1);

        } else {
            redisUtil.set(sendSmsCountKey,0, Long.parseLong(sendSmsCountExpireDate));
        }

        //2、判断手机号码发送的验证码有效期是否已过期，如果已过期，重新生成新的，反之发送旧的验证
        String verificationCode = (String) redisUtil.get(verificationCodeKey);
        //验证码过期时间
        String verificationCodeExpireDate = systemParameterService.getParmValue("VERIFICATION_CODE_EXPIRE_DATE", "*",true);
        if (verificationCode != null && SMSCount != null) {
            //更新验证码失效时间
            redisUtil.expire(verificationCodeKey, Long.parseLong(verificationCodeExpireDate));
        } else {
            //生成6位数的随机验证码
            Integer randomNum = ThreadLocalRandom.current().nextInt(100000, 999999); // 100000 ~ 999999
            verificationCode= String.valueOf(randomNum);
            logger.info("随机生成的验证码：" + randomNum);
            //保存验证码到redis中
            redisUtil.set(verificationCodeKey, verificationCode, Long.parseLong(verificationCodeExpireDate));
        }
        //调用短信平台发送验证码 todo
        return verificationCode;
    }


}
