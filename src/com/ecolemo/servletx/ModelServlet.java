package com.ecolemo.servletx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.QuerySet;
import com.ecolemo.jangorm.util.DataMap;
import com.ecolemo.jangorm.util.Paginator;

public abstract class ModelServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;
       
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataMap params = getContentParameterMap(request);

		Paginator paginator = new Paginator(getObjects(request).count(), params.parseInt("pageNumber", 1));
		request.setAttribute("paginator", paginator);
		request.setAttribute("objects", paginator.paginate(getObjects(request)));
		render(request, response);
	}

	protected abstract QuerySet<? extends Model> getObjects(HttpServletRequest req);

	public void newForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		render(request, response);
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataMap params = getContentParameterMap(request);
		getObjects(request).create(params);
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}

	public void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("item", getObjects(request).getByID(getResourceID(request)));
		render(request, response);
	}

	public void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Model object = getObjects(request).getByID(getResourceID(request));
		DataMap params = getContentParameterMap(request);
		object.setAll(params);
		object.save();
		
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getObjects(request).getByID(getResourceID(request)).delete();
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}

}