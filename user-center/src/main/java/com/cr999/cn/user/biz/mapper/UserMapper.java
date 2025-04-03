package com.cr999.cn.user.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cr999.cn.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 文件描述：
 * 用户Mapper
 * @author wangqiang
 * @date
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

	User abc(@Param("params") Map<String, Object> params);
}
