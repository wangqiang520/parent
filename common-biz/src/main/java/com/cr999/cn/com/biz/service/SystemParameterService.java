package com.cr999.cn.com.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cr999.cn.entity.SystemParameter;
import com.cr999.cn.vo.SystemParameterVo;

import java.util.List;

/**
 * <p>
 *  File Name: SystemParameterService
 *  File Function Description
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/26-14:51</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/26-14:51</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public interface SystemParameterService extends IService<SystemParameter> {

	/**
	* @Author 19075
	*         <p>
	*         <li>2025/3/26-15:56</li>
	*         <li>Function Description</li>
	*         CN:获取系统参数值
	*         EN:
	*         <li>Flow Description</li>
	*         CN:
	*         EN:
	*         </p>
	* @param mainKey
	* @param subKey
	* @param cacheRead 是否从缓存读取（true=读缓存，false=读数据库）
	* @return
	**/
	String getParmValue(String mainKey,String subKey,boolean cacheRead);

	/**
	* @Author 19075
	*         <p>
	*         <li>2025/3/26-16:11</li>
	*         <li>Function Description</li>
	*         CN:获取系统参数list
	*         EN:
	*         <li>Flow Description</li>
	*         CN:
	*         EN:
	*         </p>
	* @param mainKey
	* @param subKey
	 * @param recursiveFrequency 递归次数
	* @return
	**/
	List<SystemParameter> lstSystemParameter(String mainKey,String subKey,int recursiveFrequency);

	/**
	* @Author 19075
	*         <p>
	*         <li>2025/3/31-15:00</li>
	*         <li>Function Description</li>
	*         CN:添加更新系统参数
	*         EN:
	*         <li>Flow Description</li>
	*         CN:
	*         EN:
	*         </p>
	* @param vo
	* @return
	**/
	SystemParameter addUpdateSystemParameter(SystemParameterVo vo);

}
