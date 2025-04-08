package com.cr999.cn.common.enums;

/**
 * 文件描述
 *
 * @author wangqiang
 * @date 2020年08月24日 17:10
 */
public enum ResultEnum {
    SUCCESS(200,"成功"),
    REPEAT_SUBMIT(556,"您操作太频繁，请稍后再试！"),
    PARAMETER_VALID_ERROR(500100,"参数校验异常：%s"),
    PARAMETER_EMPTY_ERROR(500100,"参数不能为空"),
    DELETE_DATA_ERROR(500101,"删除数据失败"),
    UPDATE_DATA_ERROR(500103,"修改数据失败"),
    ADD_DATA_ERROR(500117,"添加数据失败"),
    DATA_EXIST(500104,"数据已存在"),
    DATA_NOT_EXIST(500105,"数据不存在"),
    PARAMETER_CHECKOUT_ERROR(500106, "参数校验异常：%s 为空"),
    STOP_STATE(500107,"已停用"),
    DATA_ABNORMAL(500110,"数据库异常"),
    REDIS_SAVE_ERROR(500111,"redis保存失败"),
    TOKEN_NULL_ERROR(500112,"token不能为空"),
    TOKEN_EXPIRED_ERROR(500113,"token已过期"),
    GENERATION_TOKEN_ERROR(500114,"生成token失败"),
    CHECK_TOKEN_ERROR(500115,"校验token失败"),
    REFRESH_TOKEN_ERROR(500116,"刷新token失败，请重新登录"),
    GET_TOKEN_CONTENT_ERROR(500117,"获取token内容失败"),
    LOGIN_FAILED_VALIDATION_FAILED(500117,"登录失败，验证码输入错误"),
    LOGIN_FAILED_VALIDATION_EXPIRED(500118,"登录失败，验证码已过期"),
    LOGIN_FAILED_ACCOUNT_NOT_EXIST(500119,"登录失败，用户不存在"),
    GET_CURRENT_LOIN_PERSON_ERROR(500120,"获取当前登录人失败"),
    UNAUTHORIZED_ERROR(401,"无权限访问,资源未授权"),
    VALUE_INPUT_ERROR(500121,"operaterInd值传入错误"),
    ACCOUNT_DOES_NOT_EXIST(500122,"帐号不存在"),






    ERROR(500200,"操作失败");

    private final Integer code;
    private final String msg;


    ResultEnum(Integer code, String msg){
        this.code=code;
        this.msg=msg;
    }
    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
