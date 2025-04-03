package com.cr999.cn.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 *  File Name: SystemParameter
 *  File Function Description
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/26-14:42</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/26-14:42</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Data
@TableName(value = "t_system_parameter")
public class SystemParameter extends BaseEntity{

	/**
	 * 主key
	 */
	private String mainKey;

	/**
	 * 子key
	 */
	private String subKey;

	/**
	 * 参数描述
	 */
	private String parmDesc;

	/**
	 * 参数值
	 */
	private String parmValue;

	/**
	 * 参数备注
	 */
	private String parmRemark;

	/**
	 * 模块
	 */
	private String module;
}