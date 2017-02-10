package org.msh.pharmadex.util;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Одиссей on 23.06.2016.
 */
public class Scrooge {
	public static boolean FieldEquals(Object obj1, Object obj2, String fieldName) {
		Class class1 = obj1.getClass();
		Class class2 = obj2.getClass();
		if (fieldName == null) return false;
		if ((class1 == null) && (class2 == null)) return true;
		if ((class1 == null) && (class2 != null)) return false;
		if ((class1 != null) && (class2 == null)) return false;
		Field f1;
		try {
			f1 = class1.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			f1 = null;
		}
		Field f2;
		try {
			f2 = class1.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			f2 = null;
		}
		if ((f1 == null) && (f2 == null)) return true;
		if ((f1 == null) && (f2 != null)) return false;
		if ((f1 != null) && (f2 == null)) return false;
		String val1 = null;
		String val2 = null;
		try {
			val1 = (String) f1.get(obj1);
		} catch (IllegalAccessException e) {
			val1 = null;
		}
		try {
			val2 = (String) f2.get(obj1);
		} catch (IllegalAccessException e) {
			val2 = null;
		}
		if ((val1 == null) && (val2 == null)) return true;
		if ((val1 == null) && (val2 != null)) return false;
		if ((val1 != null) && (val2 == null)) return false;
		return val1.equalsIgnoreCase(val2);
	}

	public static boolean copyData(Object src, Object dstObject) {
		Object srcObject = src;
		Method[] gettersAndSetters = srcObject.getClass().getMethods();
		for (int i = 0; i < gettersAndSetters.length; i++) {
			try {
				String methodName = gettersAndSetters[i].getName();
				int mod = gettersAndSetters[i].getModifiers();
				if (mod == 1) {
					if (methodName.startsWith("get")) {
						if (!("getID".equalsIgnoreCase(methodName) || "getHandler".equalsIgnoreCase(methodName))) {
							Method seter = dstObject.getClass().getMethod(methodName.replaceFirst("get", "set"), gettersAndSetters[i].getReturnType());
							if (gettersAndSetters[i].invoke(srcObject, null) != null)
								seter.invoke(dstObject, gettersAndSetters[i].invoke(srcObject, null));
						}
					} else if (methodName.startsWith("is")) {
						dstObject.getClass().getMethod(methodName.replaceFirst("is", "set"), gettersAndSetters[i].getReturnType()).invoke(dstObject, gettersAndSetters[i].invoke(srcObject, null));
					}
				}

			} catch (NoSuchMethodException e) {
				// TODO: handle exception
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static void setBeanParam(String paramName, Long paramValue){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String prodAppId=null;
		Flash flash = facesContext.getExternalContext().getFlash();
		if (flash!=null) {
			flash.put(paramName,paramValue);
		}
	}

	public static Long beanParam(String parameter){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String prodAppId=null;
		if (facesContext.getExternalContext().getFlash()!=null) {
			Object obj = facesContext.getExternalContext().getFlash().get(parameter);
			if (obj!=null){
				if (obj.getClass().getName().toLowerCase().endsWith("long"))
					return (Long) obj;
				else if (obj.getClass().getName().toLowerCase().endsWith("string")){
					prodAppId = (String) obj;
				}
			}}
		if (prodAppId==null){
			if (facesContext.getExternalContext().getRequestParameterMap()!=null)
				prodAppId = facesContext.getExternalContext().getRequestParameterMap().get(parameter);
		}
		if (prodAppId!=null){
			return Long.parseLong(prodAppId);
		}
		return null;
	}

	public static String beanStrParam(String parameter){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String result=null;
		if (facesContext.getExternalContext().getFlash()!=null) {
			Object obj = facesContext.getExternalContext().getFlash().get(parameter);
			if (obj!=null){
				if (obj.getClass().getName().toLowerCase().endsWith("long"))
					return (String) obj;
				else if (obj.getClass().getName().toLowerCase().endsWith("string")){
					result = (String) obj;
				}
			}}
		if (result==null){
			if (facesContext.getExternalContext().getRequestParameterMap()!=null)
				result = facesContext.getExternalContext().getRequestParameterMap().get(parameter);
		}
		if (result!=null){
			return result;
		}
		return null;
	}

	public static void setStrBeanParam(String paramName, String paramValue){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String prodAppId=null;
		Flash flash = facesContext.getExternalContext().getFlash();
		if (flash!=null) {
			flash.put(paramName,paramValue);
		}
	}

	/**
	 * go to Home page
	 */
	public static void goToHome() {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String url = facesContext.getExternalContext().getRequestContextPath();
			facesContext.getExternalContext().redirect(url + "/home.faces");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
