package org.std.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultUtils {
	
	public static Result success(Object data){
		return success(data, "操作成功", 0);
	}
	
	public static Result success(Object data, String message){
		return success(data, message, 0);
	}
	
	public static Result success(Object data, int status){
		return success(data, "操作成功", status);
	}
	
	public static Result success(Object data, String message, int status){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Result result = new Result();
		result.setMessage(message);
		result.setStatus(status);
		result.setTime(sdf.format(new Date()));
		result.setData(data);
		return result;
	}
	
	public static Result err(String message){
		return err(message, -1);
	}
	
	public static Result err(String message, int status){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Result result = new Result();
		result.setMessage(message);
		result.setStatus(status);
		result.setTime(sdf.format(new Date()));
		return result;
	}
}
