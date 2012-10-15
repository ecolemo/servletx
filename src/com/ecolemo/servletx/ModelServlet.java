package com.ecolemo.servletx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.util.DataMap;
import com.ecolemo.jangorm.util.Paginator;

public abstract class ModelServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;
       
	public abstract Class<? extends Model> modelClass();
	
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataMap params = getContentParameterMap(request);

		Paginator paginator = new Paginator(Model.objects(modelClass()).count(), params.parseInt("pageNumber", 1));
		request.setAttribute("paginator", paginator);
		request.setAttribute("objects", paginator.paginate(Model.objects(modelClass())));
		render(request, response);
	}

	public void newForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		render(request, response);
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataMap params = getContentParameterMap(request);
		Model.objects(modelClass()).create(params);
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}

	public void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("item", Model.objects(modelClass()).getByID(getResourceID(request)));
		render(request, response);
	}

	public void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Model dealer = Model.objects(modelClass()).getByID(getResourceID(request));
		DataMap params = getContentParameterMap(request);
		dealer.setAll(params);
		dealer.save();
		
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Model.objects(modelClass()).getByID(getResourceID(request)).delete();
		response.sendRedirect(request.getContextPath() + request.getServletPath());
	}

}