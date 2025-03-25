package com.cr999.cn.com.biz.componet;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description TODO
 * @Author wangqiang
 * @Date 2021/11/30 11:38
 * @Version 1.0
 **/
@Component
public class MyBatisMetaObjectHandlerConfig implements MetaObjectHandler {

    @Autowired
    BaseContext baseContext;
    @Override
    public void insertFill(MetaObject metaObject) {
        String userId;
        try {
            userId = baseContext.getUserId();
        } catch (Exception e) {
            userId = "-1";
        }
        if (metaObject.hasSetter("createUserId")) {
            this.setFieldValByName("createUserId", userId, metaObject);
        }

        if (metaObject.hasSetter("createTime")) {
            this.setFieldValByName("createTime", new Date(), metaObject);
        }

        if (metaObject.hasSetter("updateUserId")) {
            this.setFieldValByName("updateUserId", userId, metaObject);
        }

        if (metaObject.hasSetter("updateTime")) {
            this.setFieldValByName("updateTime", new Date(), metaObject);
        }

        if (metaObject.hasSetter("version")) {
            this.setFieldValByName("version", 1, metaObject);
        }

        if (metaObject.hasSetter("deleted")) {
            this.setFieldValByName("deleted", 0, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        String userId;
        try {
            userId = baseContext.getUserId();
        } catch (Exception e) {
            userId = "-1";
        }
        if (metaObject.hasSetter("updateUserId")) {
            try {
                this.setFieldValByName("updateUserId", userId, metaObject);
            } catch (Exception var3) {
            }
        }

        if (metaObject.hasSetter("updateTime")) {
            this.setFieldValByName("updateTime", new Date(), metaObject);
        }

        if (metaObject.hasSetter("version")) {
            this.setFieldValByName("version", (int)getFieldValByName("version", metaObject)+1, metaObject);
        }

    }
}
