package com.cr999.cn.user.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr999.cn.com.biz.componet.BaseContext;
import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.componet.TokenUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.DataResponse;
import com.cr999.cn.common.ResultEnum;
import com.cr999.cn.common.vo.UserBaseVo;
import com.cr999.cn.common.vo.UserVo;
import com.cr999.cn.entity.user.User;
import com.cr999.cn.user.biz.mapper.UserMapper;
import com.cr999.cn.user.biz.service.TokenService;
import com.cr999.cn.user.biz.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件描述：
 *
 * @author wangqiang
 * @version 1.0
 * @date 2021/4/5 0:20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    @Value("${sms.verification.switch}")
    boolean smsVerificationSwitch;

    @Autowired
    TokenService tokenService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    BaseContext baseContext;

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
    public void register(UserVo vo) {
        if(vo==null){
            throw new CustomException(ResultEnum.PARAMETER_EMPTY_ERROR.getMsg(),
                    ResultEnum.PARAMETER_EMPTY_ERROR.getCode());
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
        throw new CustomException("用户已离职！",ResultEnum.STOP_STATE.getCode());
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
            String mobile = userBaseVo.getMobile();
            if (StringUtils.isBlank(mobile)) {
                throw new CustomException("手机号码为空，不能发送短信验证码！");
            }
            //短信验证码key
            String verificationCodeKey = "userId:" + userId + ":" + mobile + "verificationCodeKey";
            //短信发送次数key
            String sendSmsCountKey = "userId:" + userId + ":" + mobile + "count";
            //限制短信发送次数
            Integer sendSmsCount = Integer.valueOf(ConstantEnum.SEND_SMS_COUNT.getValue());
            //限制短信发送次数过期时间
            Long sendSmsCountExpireDate = Long.parseLong(ConstantEnum.SEND_SMS_COUNT_EXPIRE_DATE.getValue());
            //获取短信已经发送的次数
            Integer SMSCount = (Integer) redisUtil.get(sendSmsCountKey);
            //判断本次登录，是属于获取验证码，还是较验验证码
            if (vo.getCheckVerificationCode()) {
                //较验验证
                String verificationCode = vo.getVerificationCode();
                if (StringUtils.isBlank(verificationCode)) {
                    throw new CustomException(ResultEnum.PARAMETER_CHECKOUT_ERROR.getMsg().replace("%s", "VerificationCode"),
                            ResultEnum.PARAMETER_CHECKOUT_ERROR.getCode());
                }
                //获取保存到redis中的验证码
                String verificationCodeValue = (String) redisUtil.get(verificationCodeKey);
                if (StringUtils.isNotBlank(verificationCodeValue)) {
                    if (verificationCodeValue.equals(verificationCode)) {
                        //登录成功
                        //生成token
                        //String token = tokenService.createToken(JSONObject.toJSONString(user), ConstantEnum.EXPIRE_DATE.getValue(), user.getId());

                        return tokenUtil.createToken(JSONObject.toJSONString(userBaseVo));
                    }
                    throw new CustomException(ResultEnum.LOGIN_FAILED_VALIDATION_FAILED.getMsg(),
                            ResultEnum.LOGIN_FAILED_VALIDATION_FAILED.getCode());
                } else {
                    throw new CustomException(ResultEnum.LOGIN_FAILED_VALIDATION_EXPIRED.getMsg(),
                            ResultEnum.LOGIN_FAILED_VALIDATION_EXPIRED.getCode());
                }
            } else {
                //获取验证码
                //1、判断手机号码发送验证码的次数，如果达到一定的次数，提示客户N分钟之后再来登录
                //4、保存短信发送次数
                if (SMSCount != null) {
                    if (SMSCount >= sendSmsCount) {
                        throw new CustomException("登录失败，短信验证次数已用完，" + sendSmsCountExpireDate + "秒内，连续发送短信" + sendSmsCount + "条，请稍后再尝试");
                    }
                    //获取剩余的过期时间
                    long expire = redisUtil.getExpire(sendSmsCountKey);
                    redisUtil.set(sendSmsCountKey, SMSCount + 1, expire);
                } else {
                    //如果为空，说明短时间内没有发送验证
                    redisUtil.set(sendSmsCountKey, 1, sendSmsCountExpireDate);
                }
                //2、判断手机号码发送的验证码有效期是否已过期，如果已过期，重新生成新的，反之发送旧的验证
                Integer verificationCode = (Integer) redisUtil.get(verificationCodeKey);
                Long verificationCodeExpireDate = Long.parseLong(ConstantEnum.VERIFICATION_CODE_EXPIRE_DATE.getValue());
                if (verificationCode != null) {
                    //更新验证码失效时间
                    redisUtil.expire(verificationCodeKey, verificationCodeExpireDate);
                } else {
                    //重新生成新的验证码，保存到redis中
                    int randomNumber = (int) ((Math.random() * 9 + 1) * 100000);
                    redisUtil.set(verificationCodeKey, randomNumber, verificationCodeExpireDate);
                }
                //3、调用短信平台发送验证码todo
                return null;
            }
        } else {
            //String token = tokenService.createToken(JSONObject.toJSONString(user), ConstantEnum.EXPIRE_DATE.getValue(), user.getId());
            return tokenUtil.createToken(JSONObject.toJSONString(userBaseVo));
        }
    }


}