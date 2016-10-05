/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.utils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.ands.vocabs.editor.admin.bean.LoginBean;

/** Authentication filter for secure pages.
 *  Based on code given in an answer at
 *  <a target="_blank"
 *  href="http://stackoverflow.com/questions/8480100/">Stack
 *  Overflow</a>. The urlPatterns given for the filter
 *  are for the login page and the welcome (i.e., logged-in) page.
 *  Other pages (e.g., CSS, JS) are not filtered.
 *  */
@WebFilter(//servletNames = {"Faces Servlet"},
           urlPatterns = {"/faces/login.xhtml", "/faces/welcome.xhtml"})
public final class AuthorizationFilter implements Filter {

    /** The LOGGER for this class. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MethodHandles.lookup().lookupClass());

    /** The LoginBean for the session. Injected. This appears to be magic,
     * as this filter class is instantiated per-servlet, not per request.
     * Injection works here because loginBean is not in fact an
     * instance of LoginBean, but of a generated subclass which
     * acts as a proxy. Invocations of its methods are implemented
     * by the proxy class as wrappers around the implementations in
     * the LoginBean class.
     * The wrapper first selects the appropriate bean object by getting
     * it from the session (because LoginBean is session-scoped). */
    @Inject
    private LoginBean loginBean;

    // AJAX doesn't (yet) come through this filter. If this changes,
    // uncomment the following.
    // /** Redirection XML for AJAX requests. */
    // private static final String AJAX_REDIRECT_XML =
    //         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    //                 + "<partial-response><redirect url=\"%s\">"
    //                 + "</redirect></partial-response>";

    @Override
    public void init(final FilterConfig arg0) throws ServletException {
        LOGGER.debug("In AuthorizationFilter.init()");
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res,
            final FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // Note parameter to getSession(): make a new session if the old
        // one has expired.
        HttpSession session = request.getSession(true);
        String loginURL = request.getContextPath() + "/faces/login.xhtml";

        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url += "?" + queryString;
        }
        LOGGER.debug("In doFilter: request URL = " + url);
        LOGGER.debug("In doFilter: session: " + session);

        // Because loginBean is injected, it is (almost certainly) not
        // null, even if there was no session; If there was no session,
        // it is a non-null proxy object which does not point to
        // an existing instance of the LoginBean class.
        // In that case, invoking loginBean.isLoggedIn() will cause a new
        // instance of LoginBean to be created; and isLoggedIn() will
        // (since the object is brand new) then return false.
        boolean loggedIn = (session != null)
                && (loginBean != null)
                && (loginBean.isLoggedIn());

        // Do logging here, rather than before the previous statement,
        // as this reference to loginBean invokes loginBean.toString(),
        // which would have had the same sort of side-effect as mentioned
        // in the previous comment -- and it's not good for debugging
        // statements to have side effects!
        LOGGER.debug("In doFilter: loginBean: " + loginBean);

        boolean loginRequest = request.getRequestURI().equals(loginURL);
        boolean resourceRequest = request.getRequestURI()
                .startsWith(request.getContextPath()
                        + ResourceHandler.RESOURCE_IDENTIFIER + "/");
        // AJAX doesn't (yet) come through this filter. If this changes,
        // uncomment the following.
        // boolean ajaxRequest = "partial/ajax"
        //         .equals(request.getHeader("Faces-Request"));

        LOGGER.debug("In doFilter: loginRequest: " + loginRequest
                + "; resourceRequest: " + resourceRequest
                + "; loggedIn: " + loggedIn);
        if (loggedIn || loginRequest || resourceRequest) {
            if (!resourceRequest) {
                // Prevent browser from caching restricted resources.
                // See also:
                // http://stackoverflow.com/q/4194207/
                // HTTP 1.1.
                response.setHeader("Cache-Control",
                                   "no-cache, no-store, must-revalidate");
                // HTTP 1.0.
                response.setHeader("Pragma", "no-cache");
                // Proxies.
                response.setDateHeader("Expires", 0);
            }

            // Continue the request.
            chain.doFilter(request, response);
        // AJAX doesn't (yet) come through this filter. If this changes,
        // uncomment the following.
        // } else if (ajaxRequest) {
        //     // Return special XML response instructing
        //     // JSF AJAX to send a redirect.
        //     response.setContentType("text/xml");
        //     response.setCharacterEncoding("UTF-8");
        //     response.getWriter().printf(AJAX_REDIRECT_XML, loginURL);
        } else {
            // Perform standard synchronous redirect.
            response.sendRedirect(loginURL);
        }
    }

    @Override
    public void destroy() {
        LOGGER.debug("In AuthorizationFilter.destroy()");
    }

}
