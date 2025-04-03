package com.cr999.cn.common;

import com.cr999.cn.common.enums.ResultEnum;
import lombok.Data;

/**
 * Created by @author songjhh on 2018/8/31
 */
@Data
public class DataResponse<D> extends BaseResponse {

	private D data;

	public DataResponse() {
	}

	public DataResponse(int code, String message) {
		super(code);
		this.setMessage(message);
	}

	public DataResponse(int code, String message, D data) {
		super(code, message);
		this.setData(data);
	}

	public DataResponse(ResultEnum resultEnum, D data) {
		super(resultEnum.getCode(),resultEnum.getMsg());
		this.setData(data);
	}
	public DataResponse(ResultEnum resultEnum) {
		super(resultEnum.getCode(),resultEnum.getMsg());
	}


}
