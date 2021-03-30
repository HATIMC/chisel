package com.hatim.chisel.util;

import java.io.Serializable;
import java.util.HashMap;

import com.hatim.chisel.bean.TaskType;

public class TaskMapperList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -477492856678914447L;
	private HashMap<String, TaskType> map = new HashMap<String, TaskType>();

	public TaskType get(String key) {
		return map.get(key);
	}

	public TaskType put(String key, TaskType value) {
		if (map.containsKey(key)) {
			// implement the logic you need here.
			// You might want to return `value` to indicate
			// that no changes applied
			return value;
		} else {
			return map.put(key, value);
		}
	}
	public HashMap<String, TaskType> getMap(){
		return map;
	}
	public TaskType remove(String taskType) {
		return map.remove(taskType);
	}
	public void setMap(HashMap<String, TaskType> x) {
		map = x;
	}
}