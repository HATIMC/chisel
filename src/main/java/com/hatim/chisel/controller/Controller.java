package com.hatim.chisel.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hatim.chisel.util.Utils;

@RestController
@CrossOrigin
@RequestMapping("/chisel")
@ComponentScan("com.hatim.chisel.controller")
public class Controller {

	Logger logger = Logger.getLogger(this.getClass());

	@PostMapping("/{jarName}/{pathTaskType}/{methodName}/{pathVar}")
	public Object doRun(@PathVariable String jarName, @PathVariable String pathTaskType,@PathVariable String methodName,
			@PathVariable String pathVar, @RequestBody String jsonRequestBody,
			@RequestHeader Map<String, String> httpHeaders) {
		return Utils.doRun(jarName,methodName, pathTaskType, pathVar, jsonRequestBody, httpHeaders);
	}

	@GetMapping("/test")
	public String test() {
		return "CHISEL is UP";
	}

	@GetMapping("/admin/util/config/jar/loadjar/{jarName}")
	public String loadJar(@PathVariable String jarName) {
		return Utils.loadJar(jarName);
	}

	@PostMapping("/admin/util/config/jar/loadjar/jarList")
	public String loadJars(@RequestBody String jsonRequestBody) {
		return Utils.loadJars(jsonRequestBody);
	}
	
	@PostMapping("/admin/util/config/jar/loadjar/upload")
	public @ResponseBody String uploadJars(
            @RequestParam("file") MultipartFile[] jarFile){
        return Utils.uploadJar(jarFile);
    }
	
	@GetMapping("/admin/util/config/tasktype/en_di/{en_di}/{pathTaskType}")
	public String endiTaskType(
			@PathVariable String en_di,
			@PathVariable String pathTaskType){
		System.out.println("en_di: "+ en_di + ";  taskType: "+pathTaskType);
        	return Utils.endiTaskType(en_di,pathTaskType);
    }
	
	@GetMapping("/admin/util/config/tasktype/en_di/bulk/{en_di}")
	public String endiTaskType(
			@PathVariable String en_di){
        	return Utils.endi(en_di);
    }
	
	@PostMapping("/admin/util/config/tasktype/{add_del_edit}")
	public String add_delTaskType(
			@RequestHeader Map<String, String> httpHeaders,@PathVariable String add_del_edit){
        	return Utils.add_del_editTaskType(httpHeaders,add_del_edit);
    }
	
	@GetMapping("/admin/util/config/tasktype/get/alltasktypes")
	public Object getAllTaskTypes(){
        	return Utils.getAllTaskTypes();
    }

	@GetMapping("/admin/util/config/runtime/get/allruntimes")
	public Object getAllRuntimes() {
		return Utils.getAllRuntimes();
	}
	
	@GetMapping("/admin/util/config/runtime/undeploy/{runtime}")
	public String undeploy(@PathVariable String runtime) {
		return Utils.undeployRuntime(runtime);
	}
	
	@GetMapping("/admin/util/shutdown")
	public void shutdown() {
		Utils.shutdown();
	}
}