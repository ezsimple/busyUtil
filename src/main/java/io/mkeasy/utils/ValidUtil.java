package io.mkeasy.utils;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
public class ValidUtil extends ValidateUtil {
	
	// page auth -> db column auth
	public static String convAuth(String auth) {
		if("management".equalsIgnoreCase(auth)
				|| "sales".equalsIgnoreCase(auth)
				|| "logistics".equalsIgnoreCase(auth)
				|| "accounting".equalsIgnoreCase(auth)) 
			return auth.toUpperCase();
		log.debug("#ERROR# convAuth : {} is UNKNOWN", auth);
		return "UNKNOWN";
	}
	
	public static JSONObject required(String type, String s) {
		boolean ret = _required(s);
		String message = StringUtils.upperCase(type)+" : This is required.";
		if(StringUtils.equals(type, "pass_confirm")) {
			message = "PASSWORD CONFIRM : This is required.";
		} else {
			message = StringUtils.upperCase(type)+" : This is required.";
		}

		if(!ret) log.debug("#ERROR# required : type={}, s={}",type,s);
		return _makeResult(ret, type, message);
	}
	
	public static JSONObject authCheck(String s) {
		boolean ret = _auth(s);
		final String message = "GROUP is required.";
		if(!ret) log.debug("#ERROR# authCheck : s={}",s);
		return _makeResult(ret, "auth", message);
	}

	public static JSONObject maxLength(String type, String s, int max) {
		boolean ret = _maxLength(s, max);
		final String message = "You must enter no more than "+max+" characters.";
		if(!ret) log.debug("#ERROR# maxLength : type={}, s={}, max={}",type,s,max);
		return _makeResult(ret, type, message);
	}
	
	public static JSONObject minLength(String type, String s, int min) {
		boolean ret = _minLength(s, min);
		final String message = "You must enter at least "+min+" characters.";
		if(!ret) log.debug("#ERROR# minLength : type={}, s={}, min={}",type,s,min);
		return _makeResult(ret, type, message);
	}
	
	public static JSONObject minmaxLength(String type, String s, int min, int max) {
		boolean ret = _minmaxLength(s, min, max);
		final String message = StringUtils.upperCase(type) + " : You must enter at least "+min+"characters and less than "+max+" characters.";
		if(!ret) log.debug("#ERROR# minmaxLength : type={}, s={}, min={}, max={}",type,s,min,max);
		return _makeResult(ret, type, message);
	}
	
	public static JSONObject idFormat(String id) {
		boolean ret = _idFormat(id);
		final String message = "ID: Must be a combination of 8 to 14 English lowercase letters + numbers";
		if(!ret) log.debug("#ERROR# idFormat : id={}",id);
		return _makeResult(ret, "id", message);
	}
	
	public static JSONObject nameFormat(String name) {
		boolean ret = _nameFormat(name);
		final String message = "Be within 2~20 characters, not special characters.";
		if(!ret) log.debug("#ERROR# nameFormat : name={}",name);
		return _makeResult(ret, "name", message);
	}

	public static JSONObject nameFormat(String name, int max) {
		boolean ret = _nameFormat(name, max);
		final String message = "Be within 2~"+max+" characters, not special characters.";
		if(!ret) log.debug("#ERROR# nameFormat : name={}, max={}",name, max);
		return _makeResult(ret, "name", message);
	}

	public static JSONObject codeNameFormat(String name) {
		boolean ret = _codeNameFormat(name);
		final String message = "Code name can only be a few special characters in English.";
		if(!ret) log.debug("#ERROR# codeNameFormat : name={}",name);
		return _makeResult(ret, "name", message);
	}

	public static JSONObject upperCodeNameFormat(String name) {
		boolean ret = _UpperCodeNameFormat(name);
		final String message = "Code name can only uppercase English and Digits.";
		if(!ret) log.debug("#ERROR# upperCodeNameFormat : name={}",name);
		return _makeResult(ret, "name", message);
	}

	public static JSONObject emailFormat(String email) {
		boolean ret = _emailFormat(email);
		final String message = "EMAIL : Not in email format.";
		if(!ret) log.debug("#ERROR# emailFormat : email={}",email);
		return _makeResult(ret, "email", message);
	}

	public static JSONObject webUrlFormat(String url) {
		boolean ret = _webUrl(url);
		final String message = "Not Web Address format.";
		if(!ret) log.debug("#ERROR# webUrlFormat : url={}",url);
		return _makeResult(ret, "web_url", message);
	}

	public static JSONObject phoneFormat(String type, String s) {
		boolean ret = _phoneFormat(s);
		final String message = StringUtils.upperCase(type) + " : Not valid Phone number format.";
		if(!ret) log.debug("#ERROR# phoneFormat : type={}, s={}",type, s);
		return _makeResult(ret, type, message);
	}
	
	public static JSONObject passwordFormat(String password) {
		boolean ret = _passwordFormat(password);
		// final String message = "The password must be a combination of at least 8 characters in English and lowercase letters plus numbers plus special characters.";
		final String message = "PASSWORD : Combinations of 8 to 14 characters, including uppercase, lowercase, and numbers in English";
		if(!ret) log.debug("#ERROR# passwordFormat : password={}",password);
		return _makeResult(ret, "password", message);
	}
	
	public static JSONObject passwordConfirm(String password, String pass_confirm) {
		boolean ret = StringUtils.equals(password, pass_confirm);
		final String message = "PASSWORD CONFIRM : Password does not match.";
		if(!ret) log.debug("#ERROR# passwordConfirm : password={}, pass_confirm={}",password,pass_confirm);
		return _makeResult(ret, "pass_confirm", message);
	}

	public static JSONObject decimalFormat(String type, String s) {
		boolean ret = _decimalFormat(s);
		final String message = "Not in numeric format.";
		if(!ret) log.debug("#ERROR# decimalFormat : type={}, s={}",type,s);
		return _makeResult(ret, type, message);
	}

	/*
	public static JSONObject idUnique(String q) throws Exception {
		boolean ret = _idUnique(q);
		final String message = "이미 존재하는 아이디 입니다.";
		return _makeResult(ret, message);
	}

	public static JSONObject emailUnique(String q) {
		boolean ret = _emailUnique(q);
		final String message = "이미 존재하는 이메일 입니다.";
		return _makeResult(ret, message);
	}
	*/

	public static JSONObject existSpecialChar(String type, String q) {
		boolean ret = _existSpecialChar(q);
		final String message = "Special characters exist.";
		if(!ret) log.debug("#ERROR# existSpecialChar : type={}, s={}",type,q);
		return _makeResult(ret, type, message);
	}


}
