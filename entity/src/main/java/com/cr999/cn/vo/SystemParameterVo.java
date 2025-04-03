package com.cr999.cn.vo;

import com.cr999.cn.entity.SystemParameter;
import lombok.Data;

/**
 * <p>
 *  File Name: SystemParameterVo
 *  File Function Description
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/31-14:53</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/31-14:53</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Data
public class SystemParameterVo extends SystemParameter {
	/**
	 * 操作方式A--add，M--modify，D--delete
	 */
	String operaterInd;
}