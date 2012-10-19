package com.ecolemo.servletx;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ecolemo.jangorm.util.DataMap;

public abstract class ServletAction {
	protected final HttpServletRequest request;
	protected final HttpServletResponse response;
	protected DataMap params;
	protected String resourceID;
	protected String actionPath;

	public ServletAction(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		
		params = new DataMap();
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap != null) {
			for (String key: parameterMap.keySet()) {
				String[] values = request.getParameterMap().get(key);
				if (values.length == 1) params.put(key, values[0]);
				else params.put(key, values);
			}
		}

		request.setAttribute("contextPath", request.getContextPath());
		request.setAttribute("servletPath", request.getServletPath());

		if (request.getPathInfo() == null) {
			actionPath = "index";
			request.setAttribute("actionPath", "index");
		}

		String[] pathInfo = request.getPathInfo().split("/");
		actionPath = pathInfo[1];
		request.setAttribute("methodPath", actionPath);
		
		if (pathInfo.length > 2) {
			resourceID = pathInfo[2];
			request.setAttribute("resourceID", resourceID);
		}
	}

	public void dispatch() throws ServletException, IOException {
		try {
			beforeFilter();
			
			Method method = getClass().getMethod(actionPath);
			method.setAccessible(true);
			method.invoke(this);
			
			afterFilter();
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

	public abstract void index();

	protected void afterFilter() throws ServletException, IOException {
		
	}

	protected void beforeFilter() {
		
	}

	protected void render() throws ServletException, IOException {
		request.getRequestDispatcher(getDefaultViewPath()).forward(request, response);
	}
	
	protected void render(String forward) throws ServletException, IOException {
		request.getRequestDispatcher(forward).forward(request, response);
	}

	protected void renderText(String text) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println(text);
		writer.close();
	}

	protected void renderMapJSON(Map object) {
		try {
			response.setContentType("text/javascript;charset=utf-8");
			PrintWriter writer = response.getWriter();
			ObjectMapper om = new ObjectMapper();
			om.writeValue(writer, object);
			writer.close();
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	protected String getDefaultViewPath() {
		return "/view" + request.getServletPath() + "/" + request.getAttribute("methodPath") + ".jsp";
	}
	
    public String getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void clearCookie(String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
	
}
