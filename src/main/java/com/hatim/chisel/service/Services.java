package com.hatim.chisel.service;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Service;

@Service
public class Services {

	static {
		PropertyConfigurator.configure("D:\\sts-workspace_1\\java\\properties\\log4j.properties");
	}
	String s = "Hello ";

	public String main(String name) {
		return s + name;
	}

}
