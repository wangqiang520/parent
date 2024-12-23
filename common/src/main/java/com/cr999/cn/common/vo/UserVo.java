package com.cr999.cn.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 文件描述：
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/9 16:53 
 */
@Data
public class UserVo {

    /**
     * 用户id
     */
    private String id;
    /**
     * 用户昵称
     */
    @NotBlank(message="用户昵称不能为空！")
    private String nickName;
    /**
     * 用户名（真实姓名）
     */
    @NotBlank(message="用户名（真实姓名）不能为空！")
    private String userName;
    /**
     * 账号
     */
    @NotBlank(message="账号不能为空！")
    private String account;
    /**
     * 密码
     */
    @NotBlank(message="密码不能为空！")
    private String passWord;
    /**
     * 性别(0:男，1女)
     */
    private short sex;
    /**
     * 用户角色（0正常用户）
     */
    private short userRole;
    /**
     * 学历
     */
    private String education;
    /**
     *
     */

    @NotBlank(message="手机号码不能为空！")
    private String mobile;
    /**
     * 身份证号码
     */
    @NotBlank(message="身份证号码不能为空！")
    private String identityCard;
    /**
     * 头像
     */
    private String face;
    /**
     * 头像 200x200x80
     */
    private String face200;
    /**
     * 状态（0:在职，1：离职）
     */
    private Integer status;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 登记时间
     */
    private Date entryDate;
    /**
     * 短信验证码
     */
    private String verificationCode;
    /**
     * 是否要较验验证码
     */
    private Boolean checkVerificationCode;

}
