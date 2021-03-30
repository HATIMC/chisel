package com.hatim.chisel.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.multipart.MultipartFile;

import com.hatim.chisel.bean.ClassControllerBean;
import com.hatim.chisel.bean.TaskType;
import com.hatim.chisel.utils.ChiselUtils;

public class Utils {

	private static Properties sysProperties = new Properties();
	private static File mapper = null;
	private static Logger logger;
	private static Map<String, URLClassLoader> loaderMap = new HashMap<String, URLClassLoader>();
	private static Map<String, String> jarMap = new HashMap<String, String>();
	private static TaskMapperList taskList = new TaskMapperList();

	private Utils() {
	}

	static {
		try {
			initChisel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getClassName(String taskType) {
		return taskList.get(taskType).getClassName();

	}

	private static void initChisel() throws IOException {
		loadChiselProperties();
		initMapper();
		loadLoggerProperties();
		refreshMapper();
		autoLoadJars();
	}

	private static void loadChiselProperties() {
		try {
			if (System.getenv("CHISEL_PROPERTIES") == null || System.getenv("CHISEL_PROPERTIES").equals("")) {
				System.out.println("add CHISEL_PROPERTIES to your env");
				System.out.println("add following properties:");
				System.out.println("CHISEL_TASK_MAPPER_FILE=\r\n" + "CHISEL_JAR_DIR=\r\n" + "CHISEL_LOGGER=");
				System.exit(1);
			}
			FileInputStream fs = new FileInputStream(System.getenv("CHISEL_PROPERTIES"));
			sysProperties.load(fs);
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void loadLoggerProperties() {
		PropertyConfigurator.configure(sysProperties.getProperty("CHISEL_LOGGER"));
		logger = Logger.getLogger(Utils.class);
	}

	private static TaskType descTaskType(String taskType) {
		return taskList.get(taskType);
	}

	private static void initMapper() throws IOException {
		mapper = new File(sysProperties.getProperty("CHISEL_TASK_MAPPER_FILE"));
		if (!mapper.exists()) {
			mapper.createNewFile();
			persistTaskMapper();
		}
	}

	@SuppressWarnings("unchecked")
	public static void refreshMapper() {
		try {

			FileInputStream fis = new FileInputStream(mapper);
			ObjectInputStream ois = new ObjectInputStream(fis);
//			taskList=((TaskMapperList) ois.readObject());
			taskList.setMap((HashMap<String, TaskType>) ois.readObject());
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized static String loadJars(String jsonRequestBody) {
		try {
			Map<String, String> map = ChiselUtils.jsonToObject(jsonRequestBody, HashMap.class);
			String[] jarList = map.get("jarList").split(",");
			for (String jar : jarList) {
				if (jar.toLowerCase().endsWith("jar")) {
					File file = new File(Utils.sysProperties.getProperty("CHISEL_JAR_DIR") + File.separator + jar);
					loadJar_autoLoadJars(file.getName());
				} else {
					return "FAILURE";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "FAILURE";
		}
		return "SUCCESS";
	}

	public synchronized static String loadJar(String jarName) {
		try {
			if (jarName.toLowerCase().endsWith("jar")) {
				File file = new File(Utils.sysProperties.getProperty("CHISEL_JAR_DIR") + File.separator + jarName);
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(file);
					Enumeration<? extends ZipEntry> e = zipFile.entries();
					while (e.hasMoreElements()) {
						loadJar_autoLoadJars(file.getName());
//						CustomLoader.loadClass(file);
						zipFile.close();
						return "SUCCESS";
					}
				} catch (Exception ex) {
					zipFile.close();
					return "FAILURE";
				}
				zipFile.close();
			} else {
				return "FAILURE";
			}
		} catch (Exception e) {
			return "FAILURE";
		}
		return "SUCCESS";
	}

	/*
	 * @SuppressWarnings("rawtypes") public static Object doRun(String methodName,
	 * String pathTaskType, String pathVar, String jsonRequestBody, Map<String,
	 * String> httpHeaders) { Utils.refreshMapper(); String class1 =
	 * Utils.getClassName(pathTaskType); Class c = null; Object a = null; try { if
	 * (!(class1 == null || class1.isEmpty() || class1.equals(""))) { c =
	 * Class.forName(class1); } else { throw new
	 * ClassNotFoundException("Class not found"); } } catch (ClassNotFoundException
	 * e) { Utils.refreshMapper(); class1 = Utils.getClassName(pathTaskType); try {
	 * c = Class.forName(class1, true, CustomLoader.getClassLoader()); } catch
	 * (Exception e1) { return "{}"; } } try { a = c.newInstance(); } catch
	 * (Exception e) { e.printStackTrace(); return "{}"; }
	 * 
	 * Method method = null; try { method = a.getClass().getMethod(methodName,
	 * String.class, String.class, Map.class); } catch (SecurityException e) {
	 * e.printStackTrace(); } catch (NoSuchMethodException e) { e.printStackTrace();
	 * } try { return method.invoke(a, pathVar, jsonRequestBody, httpHeaders); }
	 * catch (IllegalArgumentException e) { e.printStackTrace(); } catch
	 * (IllegalAccessException e) { e.printStackTrace(); } catch
	 * (InvocationTargetException e) { e.printStackTrace(); } catch (Exception e) {
	 * e.printStackTrace(); } catch (Error e) { e.printStackTrace(); } catch
	 * (Throwable e) { e.printStackTrace(); } return "{}"; }
	 */

	@SuppressWarnings("rawtypes")
	public static Object doRun(String jarName, String methodName, String pathTaskType, String pathVar,
			String jsonRequestBody, Map<String, String> httpHeaders) {
		try {
		refreshMapper();
		TaskType tt = Utils.taskList.get(pathTaskType);
		String class1 = Utils.getClassName(pathTaskType);
		Class c = null;
		Object a = null;
		if (tt != null && tt.getIsEnabled()) {
			try {
				if (!(class1 == null || class1.isEmpty() || class1.equals(""))) {
					c = Class.forName(class1, true, (URLClassLoader) loaderMap.get(jarName));
				} else {
					logger.error("Class Invocation Error");
					return "[{}]";
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				a = c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return "[{}]";
			}
			Method method = null;
			try {
				method = a.getClass().getMethod(methodName, String.class, String.class, Map.class);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			try {
				return method.invoke(a, pathVar, jsonRequestBody, httpHeaders);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return "[{}]";
		} else {
			return "[{}]";
		}
		}catch (Throwable e) {
			e.printStackTrace();
			return "[{}]";
		}
	}

	public synchronized static String uploadJar(MultipartFile[] jarFiles) {
		refreshMapper();
		for (MultipartFile jarFile : jarFiles) {
			if (!jarFile.isEmpty() && jarFile.getOriginalFilename().toLowerCase().endsWith("jar")) {
				try {
					byte[] bytes = jarFile.getBytes();
					String jarFilePath = Utils.sysProperties.getProperty("CHISEL_JAR_DIR") + File.separator
							+ jarFile.getOriginalFilename();
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(jarFilePath)));
					stream.write(bytes);
					stream.close();
					if (loadJar(jarFile.getOriginalFilename()).equals("FAILURE")) {
						new File(jarFilePath).delete();
						return "FAILURE";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				return "FAILURE";
			}
		}
		return "SUCCESS";
	}

	public synchronized static String endiTaskType(String en_di, String pathTaskType) {
		boolean bool = false;
		try {
			refreshMapper();
			if (en_di.equalsIgnoreCase("enable")) {
				bool = enableTaskType(pathTaskType);
			} else if (en_di.equalsIgnoreCase("disable")) {
				bool = disableTaskType(pathTaskType);
			}
			if (bool) {
				return "SUCCESS";
			}
			return "FAILURE";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAILURE";
		}
	}

	public synchronized static String endi(String en_di) {
		boolean bool = false;
		try {
			refreshMapper();
			if (en_di.equalsIgnoreCase("enable")) {
				bool = enableAllTaskTypes();
			} else if (en_di.equalsIgnoreCase("disable")) {
				bool = disableAllTaskTypes();
			}
			if (bool) {
				return "SUCCESS";
			}
			return "FAILURE";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAILURE";
		}
	}

	private synchronized static boolean disableTaskType(String pathTaskType) {
		try {
//			refreshMapper();
			TaskType t = descTaskType(pathTaskType);
			if (t != null && (t.getIsEnabled())) {
				taskList.remove(pathTaskType);
				t.setIsEnabled(false);
				taskList.put(pathTaskType, t);
				return persistTaskMapper();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private synchronized static boolean enableTaskType(String pathTaskType) {
		try {
//			refreshMapper();
			TaskType t = descTaskType(pathTaskType);
			if (t != null && (!t.getIsEnabled())) {
				taskList.remove(pathTaskType);
				t.setIsEnabled(true);
				taskList.put(pathTaskType, t);
				return persistTaskMapper();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	private synchronized static boolean disableAllTaskTypes() {
		try {
//			refreshMapper();
			Iterator it = taskList.getMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());
				((TaskType) pair.getValue()).setIsEnabled(false);
			}
			return persistTaskMapper();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	private synchronized static boolean enableAllTaskTypes() {
		try {
//			refreshMapper();
			Iterator it = taskList.getMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());
				((TaskType) pair.getValue()).setIsEnabled(true);
			}
			return persistTaskMapper();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized static String add_del_editTaskType(Map<String, String> headers, String add_del_edit) {
		String taskType = headers.get("tasktype");
		String className = headers.get("classname");
		String comment = headers.get("comment");
		String isEnabled = headers.get("isenabled");
		String id = UUID.randomUUID().toString();
		boolean enable = false;

		if (isEnabled != null) {
			try {
				enable = Boolean.valueOf(isEnabled);
			} catch (Exception e) {
				e.printStackTrace();
				enable = false;
			}
		} else {
			enable = false;
		}

		if (add_del_edit != null && add_del_edit.equalsIgnoreCase("add")) {
			try {
				if (taskType == null || className == null || className.equals("") || taskType.equals("")) {
					return "FAILURE";
				}
				TaskType t = new TaskType();
				t.setId(id);
				t.setTaskType(taskType);
				t.setClassName(className);
				t.setComment(comment.replace("=", "->"));
				t.setIsEnabled(enable);
				taskList.put(taskType, t);
				persistTaskMapper();
				return "SUCCESS";
			} catch (Exception e) {
				e.printStackTrace();
				return "FAILURE";
			}
		} else if (add_del_edit != null && add_del_edit.equalsIgnoreCase("delete")) {
			try {
				if (taskList.get(taskType) == null) {
					return "FAILURE";
				}
				taskList.remove(taskType);
				persistTaskMapper();
				return "SUCCESS";
			} catch (Exception e) {
				e.printStackTrace();
				return "FAILURE";
			}
		}
		if (add_del_edit != null && add_del_edit.equalsIgnoreCase("edit")) {
			if (add_del_editTaskType(headers, "delete").equalsIgnoreCase("SUCCESS")) {
				if (add_del_editTaskType(headers, "add").equalsIgnoreCase("SUCCESS")) {
					return "SUCCESS";
				}
			}
			return "FAILURE";
		} else {
			return "FAILURE";
		}
	}

	public static Object getAllTaskTypes() {
		try {
			persistTaskMapper();
			refreshMapper();
			return taskList.getMap().values();
		} catch (Exception e) {
			e.printStackTrace();
			return "[{}]";
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object getAllClasses() {
		try {
			persistTaskMapper();
			refreshMapper();
			List<String> classes = new ArrayList<String>();
			Iterator it = taskList.getMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				classes.add(((TaskType) pair.getValue()).getClassName());
			}
			return classes;

		} catch (Exception e) {
			e.printStackTrace();
			return "[{}]";
		}
	}

	private static void autoLoadJars() {
		try {
			File dir = new File(sysProperties.getProperty("CHISEL_JAR_DIR"));
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jar");
				}
			});
			try {
				for (File file : files) {
					loadJar_autoLoadJars(file.getName());
//					loaderMap.put(file.getName().split("\\.")[0], myClassLoader);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static URLClassLoader loadJar_autoLoadJars(String jarName) {
		try {
			URLClassLoader myClassLoader = null;
			File file = new File(Utils.sysProperties.getProperty("CHISEL_JAR_DIR") + File.separator + jarName);
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(file);
				Enumeration<? extends ZipEntry> e = zipFile.entries();
				while (e.hasMoreElements()) {
					String runtime = file.getName().split("\\.")[0];
					myClassLoader = loaderMap.get(runtime);
					if (myClassLoader != null) {
						return myClassLoader;
					}
					myClassLoader = CustomLoader.loadClass_autoLoadJars(file);
					loaderMap.put(runtime, myClassLoader);
					jarMap.put(runtime, file.getName());
					zipFile.close();
					return myClassLoader;
				}
			} catch (Exception ex) {
				zipFile.close();
				return null;
			}
			zipFile.close();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<ClassControllerBean> getAllRuntimes() {
		ArrayList<ClassControllerBean> runtimes = new ArrayList<ClassControllerBean>();
		Long count = 1l;
		ClassControllerBean bean = null;
		for (Map.Entry me : loaderMap.entrySet()) {
			bean = new ClassControllerBean();
			bean.setId(count);
			bean.setRuntime((String) me.getKey());
			runtimes.add(bean);
			count = count + 1;
		}
		return runtimes;
	}

	public static String undeployRuntime(String runtime) {
		try {
			loaderMap.remove(runtime).close();
			if (deleteRuntime(jarMap.get(runtime))) {
				return "SUCCESS";
			}
			return "FAILURE";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAILURE";
		}
	}

	private static boolean deleteRuntime(String fileName) {
		try {
			File file = new File(Utils.sysProperties.getProperty("CHISEL_JAR_DIR") + File.separator + fileName);
			if (file.exists()) {
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.close();
				File renameFile = new File(file.getAbsolutePath()+new Date().getTime());
				if (file.renameTo(renameFile)) {
					return true;
				}
				return false;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private synchronized static boolean persistTaskMapper() {
		try {
			FileOutputStream fos = new FileOutputStream(mapper);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(taskList.getMap());
			oos.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized static void shutdown() {
		try {
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();

		}
	}
}