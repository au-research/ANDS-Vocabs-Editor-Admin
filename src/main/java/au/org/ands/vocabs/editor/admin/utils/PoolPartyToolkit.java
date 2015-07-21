/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.utils;

import java.lang.invoke.MethodHandles;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.ands.vocabs.editor.admin.bean.LoginBean;
import au.org.ands.vocabs.editor.admin.model.PoolPartyProject;
import au.org.ands.vocabs.editor.admin.model.PoolPartyRequest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/** Toolkit for accessing PoolParty. */
public final class PoolPartyToolkit {

    /** The LOGGER for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            MethodHandles.lookup().lookupClass());

    /** Access to the tool properties. */
    private static final Properties PROPS =
            ToolProperties.getProperties();

    /** Private constructor for a utility class. */
    private PoolPartyToolkit() {
    }

    /** Get the PoolParty projects available to the user.
     * @param loginBean The bean containing the user's PoolParty username
     * and password.
     * @return The user's PoolParty projects as a HashMap from id to
     *         PoolPartyProject. */
    public static PoolPartyProject[] getProjects(
            final LoginBean loginBean) {
        String remoteUrl = PROPS.getProperty("PoolParty.remoteUrl")
                + "api/projects/";

        LOGGER.debug("Getting metadata from " + remoteUrl);

        Client client = ClientBuilder.newClient();
        // Need to register the Jackson provider in order
        // to deserialize the JSON returned by PoolParty.
        client.register(JacksonJaxbJsonProvider.class);

        WebTarget target = client.target(remoteUrl);
        HttpAuthenticationFeature feature =
                HttpAuthenticationFeature.basic(loginBean.getUsername(),
                        loginBean.getPassword());
        target.register(feature);

        Invocation.Builder invocationBuilder =
                target.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();

        LOGGER.debug("getProjects response code: " + response.getStatus());
        if (response.getStatus() >= Status.BAD_REQUEST.getStatusCode()) {
            // Login failed.
            return null;
        }

        PoolPartyProject[] projectsArray =
                response.readEntity(new
                        GenericType<PoolPartyProject[]>() { });
        LOGGER.debug("projectsList length = " + projectsArray.length);
        return projectsArray;
    }

    /** Run a query against a project.
     * @param loginBean The bean containing the user's PoolParty username
     * and password.
     * @param uriSupplement The PoolParty uriSupplement.
     * @param query The SPARQL query to run.
     * @return The results of running the query. */
    public static String runQuery(
            final LoginBean loginBean,
            final String uriSupplement,
            final String query) {
        String remoteUrl = PROPS.getProperty("PoolParty.remoteUrl")
                + uriSupplement;

        LOGGER.debug("Running query: " + remoteUrl);

        Client client = ClientBuilder.newClient();
        // Need to register the Jackson provider in order
        // to deserialize the JSON returned by PoolParty.
//        client.register(JacksonJaxbJsonProvider.class);

        WebTarget target = client.target(remoteUrl);
        HttpAuthenticationFeature feature =
                HttpAuthenticationFeature.basic(loginBean.getUsername(),
                        loginBean.getPassword());
        target.register(feature);

        Invocation.Builder invocationBuilder =
                target.request(MediaType.APPLICATION_JSON);

        Response response = invocationBuilder.get();

        LOGGER.debug("getProjects response code: " + response.getStatus());
        if (response.getStatus() >= Status.BAD_REQUEST.getStatusCode()) {
            // Login failed.
            return null;
        }

        String responseString =
                response.readEntity(String.class);
        LOGGER.debug("runQuery response length = " + responseString.length());
        return responseString;
    }

    /** Process the user's request.
     * @param loginBean The session bean with the request details.
     * @return the The name of the action that goes to the home page.
     */
    public static String processRequest(final LoginBean loginBean) {
        LOGGER.debug("Called processRequest");
        boolean[] selectedPoolPartyProjects =
                loginBean.getSelectedPoolPartyProjects();
        PoolPartyProject[] poolPartyProjects =
                loginBean.getPoolPartyProjects();
        boolean[] selectedPoolPartyRequests =
                loginBean.getSelectedPoolPartyRequests();
        PoolPartyRequest[] poolPartyRequests =
                loginBean.getPoolPartyRequests();
        String allResults = "";

        for (int projectIndex = 0;
                projectIndex < selectedPoolPartyProjects.length;
                projectIndex++) {
            if (!selectedPoolPartyProjects[projectIndex]) {
                continue;
            }
            PoolPartyProject project = poolPartyProjects[projectIndex];
            String uriSupplement = project.getUriSupplement();
            for (int requestIndex = 0;
                    requestIndex < selectedPoolPartyRequests.length;
                    requestIndex++) {
                if (!selectedPoolPartyRequests[requestIndex]) {
                    continue;
                }
                String type = poolPartyRequests[requestIndex].getType();
                String sparql = poolPartyRequests[requestIndex].getSparql();
                if (type.equals(ToolConstants.QUERY_TYPE)) {
                    allResults += runQuery(loginBean,
                        uriSupplement, sparql);
                }
            }
        }
        loginBean.setLastResults(allResults);
        return "welcome";
    }

}
