package com.hatim.chisel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hatim.chisel.bean.TaskType;

//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class ChiselApplicationTests {

//	@Test
//	void contextLoads() {
//	}

	public static void main(String[] args) throws Exception {
		File file = new File("D:\\sts-workspace_1\\java\\properties\\CHISEL\\TASKMAPPER");
		if(!file.exists()) {
			file.createNewFile();
		}
//		TaskType t = new TaskType();
////		List<TaskType> ttt= new ArrayList<TaskType>();
//		t.setId(1l);
//		t.setTaskType("dummy1");
//		t.setClassName("com.hatim");
//		t.setComment("myComment");
//		t.setIsEnabled(true);
//		ttt.add(t);
//		FileOutputStream fos = new FileOutputStream(file);
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		
//		oos.writeObject(ttt);
//		oos.close();
//		fos.close();
//		
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ArrayList<TaskType> nttt = (ArrayList<TaskType>) ois.readObject();
		
		for(TaskType tr : nttt)
		{
			System.out.println(tr);
		}
		ois.close();
		fis.close();
//		HashMap<String, TaskType> taskList = new java.util.HashMap<String, TaskType>();
//		taskList.put("dummy1",t);
//		Iterator it = taskList.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry pair = (Map.Entry) it.next();
//			System.out.println(pair.getKey() + " = " + ((TaskType) pair.getValue()).getIsEnabled());
//			((TaskType) pair.getValue()).setIsEnabled(false);
//		}
//		
//		Iterator it2 = taskList.entrySet().iterator();
//		while (it2.hasNext()) {
//			Map.Entry pair = (Map.Entry) it2.next();
//			System.out.println(pair.getKey() + " = " + ((TaskType) pair.getValue()).getIsEnabled());
//			((TaskType) pair.getValue()).setIsEnabled(false);
//		}
		
	}
}
