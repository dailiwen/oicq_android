package com.example.dai.oicq_android.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.PrintWriter;
import java.util.Date;

/**
 * 账号实体类
 * @author dailiwen
 * @date 2018/06/22
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Account {
	private String id;
	private String loginName;
	private String password;
	private Date registerTime;
	private Date lastLoginTime;
	private PrintWriter writer;

	public Account() {
	}

	public Account(String id, String loginName, String password, Date registerTime, Date lastLoginTime) {
		this.id = id;
		this.loginName = loginName;
		this.password = password;
		this.registerTime = registerTime;
		this.lastLoginTime = lastLoginTime;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
}
