package com.cr999.cn.common;

import lombok.val;

public enum ConstantEnum {
    EXPIRE_DATE("60","过期日期(单位：秒)"),
    SECRET_KEY ("ZCfasfhuaUUHufguGuwu2020BQWE","token密钥"),
    USER_TOKEN_PREFIX("token","Token前缀"),
    USER_TOKEN_PREFIX_("token_","Token前缀"),
    //180秒内，只能发送5条短信
    SEND_SMS_COUNT_EXPIRE_DATE("180","限制短信发送次数过期时间(单位：秒)"),
    SEND_SMS_COUNT("5","限制短信发送次数"),
    VERIFICATION_CODE_EXPIRE_DATE("60","短信验证码过期时间(单位：秒)"),
    EXCLUDE_PATH_PATTERNS("/v1/user/login,/v1/user/register,/err*," +
            "/*.html,/webjars/**,/swagger-resources/*/*,/swagger*","登录拦截，排除路径");




    private final String value;
    private final String msg;

    ConstantEnum(String value, String msg) {
        this.value = value;
        this.msg = msg;
    }
    public String getValue() {
        return value;
    }
    public String getMsg() {
        return msg;
    }
}
