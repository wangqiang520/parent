package com.cr999.cn.user.controller;

import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.common.DataResponse;
import com.cr999.cn.common.enums.ResultEnum;
import com.cr999.cn.entity.SystemParameter;
import com.cr999.cn.vo.SystemParameterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  File Name: SystemParameterController
 *  File Function Description 系统参数控制层
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/4/3-16:11</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/4/3-16:11</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */

@Api(value ="系统参数控制层")
@RestController
@RequestMapping("/v1/systemParameter")
public class SystemParameterController {

	@Autowired
	SystemParameterService systemParameterService;

	@ApiOperation(value = "系统参数新增维护", notes = "系统参数新增维护", httpMethod = "POST")
	@PostMapping("/addUpdateSystemParameter")
	public DataResponse addUpdateSystemParameter(@RequestBody SystemParameterVo vo){
		SystemParameter systemParameter = systemParameterService.addUpdateSystemParameter(vo);
		return new DataResponse(ResultEnum.SUCCESS,systemParameter);
	}
}