package com.cr999.cn.test;

import com.cr999.cn.entity.SystemParameter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  File Name:
 *  File Function Description
 *  File Flow Description
 *  Version: V1.0
 * </p>
 *
 * @Author wangqiang
 *         <p>
 *         <li>Create Date：2025/3/26-18:24</li>
 *         <li>Revise Records</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Revise Author: wangqiang </li>
 *         <li>Revise Date: 2025/3/26-18:24</li>
 *         <li>Revise Content: </li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class TestMain {

	public static void main(String[] args) {
		String a="ew.paramNameValuePairs.MPGENVAL7";
		int i = a.indexOf("ew");
		System.out.println(i);
		String sentence = "ew.paramNameValuePairs.MPGENVAL7";
		int index = sentence.lastIndexOf("."); // 7

		System.out.println(index);
	}

}