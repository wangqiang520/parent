package com.cr999.cn.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 文件描述：
 * 基础实体类
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/4 21:27 
 */
@Data
public class BaseEntity {

    @TableId("id")
    private String id;

    @Version
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createUserId;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateUserId;

    public BaseEntity() {
    }
}
