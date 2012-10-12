package com.ecolemo.servletx;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecolemo.jangorm.util.DataMap;

public abstract class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dispatch(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dispatch(req, resp);
	}

	protected void dispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			if (req.getPathInfo() == null) {
				index(req, resp);
				return;
			}
			Method method = getClass().getDeclaredMethod(req.getPathInfo().substring(1), HttpServletRequest.class, HttpServletResponse.class);
			method.setAccessible(true);
			method.invoke(this, req, resp);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	protected abstract void index(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException;
	
	public DataMap getContentParameterMap(HttpServletRequest request) {
		DataMap params = new DataMap();
		
		if (request == null) {
			return params;
		}
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap == null) {
			return params;
		}
		
		for (String key: parameterMap.keySet()) {
			String[] values = request.getParameterMap().get(key);
			if (values.length == 1) params.put(key, values[0]);
			else params.put(key, values);
		}
		
		return params;
	}

	protected void render(HttpServletRequest request, HttpServletResponse response, String forward) throws ServletException, IOException {
		request.getRequestDispatcher(forward).forward(request, response);
	}

	protected void render(HttpServletResponse response, String text) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println(text);
		writer.close();
	}
	
}
