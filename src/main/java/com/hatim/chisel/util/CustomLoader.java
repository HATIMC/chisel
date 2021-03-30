package com.hatim.chisel.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomLoader {

	private static URLClassLoader myClassLoader = null;

//	public static ClassLoader loadClass() throws Exception {
//		List<File> jars = Arrays.asList(new File(Utils.sysProperties.getProperty("CHISEL_JAR_DIR")).listFiles());
//		URL[] urls = new URL[jars.size()];
//		for (int i = 0; i < jars.size(); i++) {
//			try {
//				urls[i] = jars.get(i).toURI().toURL();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		if (myClassLoader == null) {
//			myClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
//		} else {
//			myClassLoader = new URLClassLoader(urls, myClassLoader);
//		}
//		return myClassLoader;
//	}

	public static ClassLoader loadClass(File classFile) throws Exception {
		URL[] urls = new URL[1];
		try {
			urls[0] = classFile.toURI().toURL();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (myClassLoader == null) {
			myClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
		} else {
			myClassLoader = new URLClassLoader(urls, myClassLoader);
		}
		return myClassLoader;
	}

	public static ClassLoader getClassLoader() {
		return myClassLoader;
	}

	public static URLClassLoader loadClass_autoLoadJars(File jar) {
		URL[] urls = new URL[1];
		try {
			urls[0] = jar.toURI().toURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		URLClassLoader myClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
		return myClassLoader;
	}
}
