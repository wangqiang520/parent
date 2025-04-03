package com.cr999.cn.com.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cr999.cn.com.biz.componet.RedisUtil;
import com.cr999.cn.com.biz.exception.CustomException;
import com.cr999.cn.com.biz.mapper.SystemParameterMapper;
import com.cr999.cn.com.biz.service.SystemParameterService;
import com.cr999.cn.common.ConstantEnum;
import com.cr999.cn.common.enums.ResultEnum;
import com.cr999.cn.entity.SystemParameter;
import com.cr999.cn.vo.SystemParameterVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  File Name: SystemParameterServiceImpl
 *  File Function Description: 系统参数实现类
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/26-14:52</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/26-14:52</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Service
public class SystemParameterServiceImpl extends ServiceImpl<SystemParameterMapper, SystemParameter> implements SystemParameterService {

	@Autowired
	private SystemParameterMapper systemParameterMapper;

	@Autowired
	RedisUtil redisUtil;


	@Override
	public String getParmValue(String mainKey, String subKey) {
		if (StringUtils.isBlank(mainKey) || StringUtils.isBlank(subKey)) {
			throw new CustomException("mainKey和subKey参数，不能为空！");
		}
		List<SystemParameter> systemParameters = lstSystemParameter(mainKey, subKey,0);
		if(CollectionUtils.isEmpty(systemParameters)){
			throw new CustomException("mainKey和subKey参数，未找到值！");
		}else{
			return systemParameters.get(0).getParmValue();
		}
	}

	@Override
	public List<SystemParameter> lstSystemParameter(String mainKey, String subKey,int recursiveFrequency) {

		//如果递归次数小于0，就返回一个空的集合
		if(recursiveFrequency<0){
			//防止递归次数过深，
			return new ArrayList<SystemParameter>();
		}

		String systemParameterKey= ConstantEnum.SYSTEM_PARAMETER_KEY.getValue();
		String systemParameterLockKey= ConstantEnum.SYSTEM_PARAMETER_LOCK_KEY.getValue();
		//设置系统参数过期时间
		Long systemParmeterExpireDate = Long.parseLong(ConstantEnum.SYSTEM_PARAMETER_EXPIRE_DATE.getValue());

		if(!(StringUtils.isBlank(mainKey)==StringUtils.isBlank(subKey))){
			//todo 要么mainKey和subKey必输，要么就不输，不能单个参数有值
			throw new CustomException("mainKey和subKey参数，不能单个有值！");
		}

		//1、查询redis中是否有值，如果有取redis数据
		List<SystemParameter> lstSystemParameter = (List<SystemParameter>) redisUtil.get(systemParameterKey);
		if (CollectionUtils.isNotEmpty(lstSystemParameter)) {
			lstSystemParameter=filterSystemParameter(mainKey,subKey,lstSystemParameter);
			return lstSystemParameter;
		}

		//2、redis中无值，加一把锁，只有得到锁的请求，才能查询表是否有数据，如果数据为空，保存一个list.size()=0的对象到redis,目地是防止缓存穿透
		String threadName = Thread.currentThread().getName();
		//获取分布式锁
		boolean locked = redisUtil.setIfAbsent(systemParameterLockKey, threadName,systemParmeterExpireDate);
		try {
			if(locked){
				//获取到锁,再次检查缓存
				lstSystemParameter = (List<SystemParameter>) redisUtil.get(systemParameterKey);
				if(CollectionUtils.isNotEmpty(lstSystemParameter)){
					lstSystemParameter=filterSystemParameter(mainKey,subKey,lstSystemParameter);
				}else{
					//查询所有系统参数数据，保存到redis中，
					lstSystemParameter = systemParameterMapper.selectList(new QueryWrapper<SystemParameter>());
					if(CollectionUtils.isEmpty(lstSystemParameter)){
						//系统参数表无数据，插入一条空对象到reids中，并设置过期时间
						redisUtil.set(systemParameterKey,lstSystemParameter);
						redisUtil.expire(systemParameterKey,systemParmeterExpireDate);
					}else{
						redisUtil.set(systemParameterKey,lstSystemParameter);
					}

					//再次从redis中取数据,并过滤数据，
					lstSystemParameter = (List<SystemParameter>) redisUtil.get(systemParameterKey);
					if (CollectionUtils.isNotEmpty(lstSystemParameter)) {
						lstSystemParameter=filterSystemParameter(mainKey,subKey,lstSystemParameter);
					}
				}
				return lstSystemParameter;
			}else{
				//未获取到锁，递归调用方法
				Thread.sleep(1000);
				return lstSystemParameter(mainKey,subKey,recursiveFrequency-1);
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new CustomException("初始化系统参数失败："+e.getMessage());
		}finally {
			if (locked) {
				String lockedValue = (String) redisUtil.get(systemParameterLockKey);
				if (threadName.equals(lockedValue)) {
					//获取锁对应的值，如果值是一样的，就删除此锁
					redisUtil.del(systemParameterLockKey);
				}
			}
		}
	}

	@Override
	public void addUpdateSystemParameter(SystemParameterVo vo) {
		if(vo==null){
			throw new CustomException(ResultEnum.PARAMETER_EMPTY_ERROR);
		}
		if(vo.getOperaterInd()==""){

		}
	}

	public List<SystemParameter> filterSystemParameter(String mainKey, String subKey,List<SystemParameter> lstSystemParameter) {

		if (StringUtils.isNotBlank(mainKey) && StringUtils.isNotBlank(subKey)) {
			//只返回mainKey，subKey的数据
			lstSystemParameter = lstSystemParameter.stream().filter(v -> {
				return v.getMainKey().equals(mainKey) && v.getSubKey().equals(subKey);
			}).collect(Collectors.toList());
			return lstSystemParameter;

		}else if (StringUtils.isBlank(mainKey) && StringUtils.isBlank(subKey)) {
			//mainKey和subKey为空，代表查询全表数据
			return lstSystemParameter;

		}else {
			throw new CustomException("mainKey和subKey参数，不能单个有值！");
		}
	}
}