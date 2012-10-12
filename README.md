# servletx
servletx is Java servlet-based web framework

JavaEE already has a great web framework, Servlet & JSP. servletx respect and utilize Servlet & JSP, it just provides helper classes.

## Design principles

- No another controller class. Use servlet as controller. 
- No another template engine. JSP & JSTL is most powerful.
- JSP inheritance rather than layout like SiteMesh or Tiles.
- Should work well with eclipse WTP.
- No XML
- Do not hide details, just reduce verbosity of Java syntax.
- Keep It Simple Stupid! less than 20 classes.
- Let model layer vacant. Use hibernate or other. However, I recommend jangorm.
- Use Servlet APIs for URI dispatching.
- No magic

## Usage
### Controller
    @WebServlet(urlPatterns={ "/dealer", "/dealer/*"})
    public class DealerServlet extends DispatchServlet {
	    private static final long serialVersionUID = 1L;

    	protected void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		request.setAttribute("objects", Dealer.objects(Dealer.class).iterator());
    		render(request, response, "/view/dealer/index.jsp");
    	}
	
    	protected void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		DataMap params = getContentParameterMap(request);
    		Dealer.objects(Dealer.class).create(params);
    		render(response, "OK");
    	}
    }

### View
