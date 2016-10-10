/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.utils;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import au.org.ands.vocabs.editor.admin.bean.LoginBean;
import au.org.ands.vocabs.editor.admin.model.PoolPartyProject;
import au.org.ands.vocabs.editor.admin.model.PoolPartyRequest;
import au.org.ands.vocabs.editor.admin.model.RequestResponse;

/** Toolkit for accessing PoolParty. */
public final class PoolPartyToolkit {

    /** The LOGGER for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            MethodHandles.lookup().lookupClass());

    /** Access to the tool properties. */
    private static final Properties PROPS =
            ToolProperties.getProperties();

    /** Regular expression defining the placeholder to use in queries
     * and updates, to be replaced with
     * the IRI of the named graph that contains the project's
     * thesaurus data. The replacement text will include angle brackets;
     * therefore, do not include them in the template. Sample uses within
     * a query:
     * <pre>
     * SELECT ?s FROM #THESAURUS# WHERE { ?s ... }
     * SELECT ?s FROM #THESAURUS/deprecated# WHERE { ?s ... }
     * </pre>
     * */
    private static final String PROJECT_THESAURUS_DATA_GRAPH =
            "#THESAURUS(/[^#]+)?#";

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
                + "sparql/" + uriSupplement;

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

        Form queryForm = new Form();
        queryForm.param("query", query);
        // Seem to need to set this "content-type" here.
        // Not enough to set the Content-Type using request() below.
        queryForm.param("content-type", MediaType.APPLICATION_XML);

        Invocation.Builder invocationBuilder =
                target.request();

        Response response = invocationBuilder.post(Entity.entity(queryForm,
                MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        LOGGER.debug("runQuery response code: " + response.getStatus());
        if (response.getStatus() >= Status.BAD_REQUEST.getStatusCode()) {
            // Query failed.
            return null;
        }

        String responseSparql =
                response.readEntity(String.class);

        return responseSparql;
    }

    /** Run an update against a project.
     * @param loginBean The bean containing the user's PoolParty username
     * and password.
     * @param projectID The PoolParty project ID.
     * @param update The SPARQL update to run.
     * @return The results of running the query. */
    public static String runUpdate(
            final LoginBean loginBean,
            final String projectID,
            final String update) {
        String remoteUrl = PROPS.getProperty("PoolParty.remoteUrl")
                + "api/projects/" + projectID + "/update";

        LOGGER.debug("Running update: " + remoteUrl);

        Client client = ClientBuilder.newClient();
        // Need to register the Jackson provider in order
        // to deserialize the JSON returned by PoolParty.
//        client.register(JacksonJaxbJsonProvider.class);

        WebTarget target = client.target(remoteUrl);
        HttpAuthenticationFeature feature =
                HttpAuthenticationFeature.basic(loginBean.getUsername(),
                        loginBean.getPassword());
        target.register(feature);

        // Seem to need to set this "content-type" here.
        // Not enough to set the Content-Type using request() below.

        Invocation.Builder invocationBuilder =
                target.request(MediaType.TEXT_PLAIN_TYPE);

        Response response = invocationBuilder.post(Entity.entity(update,
                MediaType.TEXT_PLAIN));

        LOGGER.debug("runUpdate response code: " + response.getStatus());
        if (response.getStatus() >= Status.BAD_REQUEST.getStatusCode()) {
            // Update failed.
            return null;
        }

        String responseString =
                response.readEntity(String.class);

        if (responseString.isEmpty()) {
            // Give back something, rather than nothing.
            responseString = "OK";
        }
        return responseString;
    }

    /** Process the user's request.
     * @param loginBean The session bean with the request details.
     * @return the The name of the action that goes to the logged-in page.
     */
    public static String processRequest(final LoginBean loginBean) {
        LOGGER.debug("Called processRequest");
        Boolean[] selectedPoolPartyProjects =
                loginBean.getSelectedPoolPartyProjects();
        PoolPartyProject[] poolPartyProjects =
                loginBean.getPoolPartyProjects();
        Boolean[] selectedPoolPartyRequests =
                loginBean.getSelectedPoolPartyRequests();
        PoolPartyRequest[] poolPartyRequests =
                loginBean.getPoolPartyRequests();
        ArrayList<RequestResponse> allResults =
                new ArrayList<RequestResponse>();

        for (int projectIndex = 0;
                projectIndex < selectedPoolPartyProjects.length;
                projectIndex++) {
            if (!selectedPoolPartyProjects[projectIndex]) {
                continue;
            }
            PoolPartyProject project = poolPartyProjects[projectIndex];
            String projectThesaurusGraph = getThesaurusGraph(project);
            for (int requestIndex = 0;
                    requestIndex < selectedPoolPartyRequests.length;
                    requestIndex++) {
                if (!selectedPoolPartyRequests[requestIndex]) {
                    continue;
                }
                PoolPartyRequest request = poolPartyRequests[requestIndex];
                String type = request.getType();
                String sparql = request.getSparql();
                // Replace occurrences of the thesaurus data graph placeholder
                // within the template.
                sparql = sparql.replaceAll(PROJECT_THESAURUS_DATA_GRAPH,
                        projectThesaurusGraph);
                LOGGER.debug("processRequest: user: " + loginBean.getUsername()
                        + ", project ID: " + project.getId()
                        + ", request: " + request.getTitle());
                if (type.equals(ToolConstants.QUERY_TYPE)) {
                    String uriSupplement = project.getUriSupplement();
                    String result = runQuery(loginBean,
                            uriSupplement, sparql);
                    RequestResponse requestResponse = new RequestResponse();
                    requestResponse.setProject(project);
                    requestResponse.setRequest(request);
                    requestResponse.setType(ToolConstants.QUERY_TYPE);
                    requestResponse.setSuccessful(result != null);
                    requestResponse.setSparqlResult(result);
                    allResults.add(requestResponse);
                } else if (type.equals(ToolConstants.UPDATE_TYPE)) {
                    String projectID = project.getId();
                    String result = runUpdate(loginBean,
                            projectID, sparql);
                    RequestResponse requestResponse = new RequestResponse();
                    requestResponse.setProject(project);
                    requestResponse.setRequest(request);
                    requestResponse.setType(ToolConstants.UPDATE_TYPE);
                    requestResponse.setSuccessful(result != null);
                    requestResponse.setUpdateResult(result);
                    allResults.add(requestResponse);
                }
            }
        }
        loginBean.setLastResults(allResults);
        return ToolConstants.WELCOME_ACTION;
    }

    /** Get the IRI of the named graph containing the project's thesaurus data,
     * with a substitution element $1 for a suffix.
     * The result has surrounding angle brackets.
     * The result of this method is intended to be used as the second parameter
     * to the method {@link String#replaceAll(String, String)}, where
     * the value of the second parameter contains one capturing group.
     * @param project The PoolParty project definition.
     * @return The IRI of the named graph, as a String containing $1.
     *   Example: <code>&lt;http://path.to.api/1234/thesaurus$1&gt;</code>
     */
    private static String getThesaurusGraph(final PoolPartyProject project) {
        return "<" + project.getUri() + "/thesaurus" + "$1" + ">";
    }

}
