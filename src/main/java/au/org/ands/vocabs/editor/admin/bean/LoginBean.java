/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.bean;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.ands.vocabs.editor.admin.model.PoolPartyProject;
import au.org.ands.vocabs.editor.admin.model.PoolPartyRequest;
import au.org.ands.vocabs.editor.admin.model.RequestResponse;
import au.org.ands.vocabs.editor.admin.utils.PoolPartyToolkit;
import au.org.ands.vocabs.editor.admin.utils.ToolConstants;

/** Bean class for login information. */
@Named
@SessionScoped
public class LoginBean implements Serializable {

    // If needed, uncomment the following. NB: this has to be static,
    // otherwise it gets lost during serialization, and therefore
    // "disappears" after a page navigation.
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1956630088877042573L;

    /** Username. */
    private String username;

    /** Password. */
    private String password;

    /** PoolParty projects available to this user. */
    private PoolPartyProject[] poolPartyProjects;

    /** Which PoolParty projects are selected for working on. */
    private Boolean[] selectedPoolPartyProjects;

    /** Access to the RequestsCatalogueBean application bean .*/
    @Inject
    private RequestsCatalogueBean requestsCatalogueBean;

    /** PoolParty requests available to this user. */
    private PoolPartyRequest[] poolPartyRequests;

    /** Which PoolParty requests are selected for processing. */
    private Boolean[] selectedPoolPartyRequests;

    /** The last results of requests. */
    private ArrayList<RequestResponse> lastResults;

    /** Get the username.
     * @return the username
     */
    public final String getUsername() {
        return username;
    }

    /** Set the username.
     * @param aUsername the username to set
     */
    public final void setUsername(final String aUsername) {
        username = aUsername;
    }

    /** Get the password.
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /** Set the password.
     * @param aPassword the password to set
     */
    public final void setPassword(final String aPassword) {
        password = aPassword;
    }

    /** Get the user's PoolParty projects. The array is sorted by title.
     * @return the poolPartyProjects
     */
    public final PoolPartyProject[] getPoolPartyProjects() {
        return poolPartyProjects;
    }

    /** Set the user's PoolParty projects. The array will be sorted by title.
     * @param thePoolPartyProjects the poolPartyProjects to set
     */
    public final void setPoolPartyProjects(
            final PoolPartyProject[] thePoolPartyProjects) {
        poolPartyProjects = thePoolPartyProjects;
    }

    /** Get the selected PoolParty projects.
     * @return the selected projects
     */
    public final Boolean[] getSelectedPoolPartyProjects() {
        return selectedPoolPartyProjects;
    }

    /** Set the selected PoolParty projects.
     * @param aSelectedPoolPartyProjects
     *            the selected projects to set
     */
    public final void setSelectedPoolPartyProjects(
            final Boolean[] aSelectedPoolPartyProjects) {
        selectedPoolPartyProjects = aSelectedPoolPartyProjects;
    }

    /** Get the user's PoolParty requests. The array is sorted by title.
     * @return the poolPartyRequests
     */
    public final PoolPartyRequest[] getPoolPartyRequests() {
        return poolPartyRequests;
    }

    /** Set the user's PoolParty requests. The array will be sorted by title.
     * @param thePoolPartyRequests the poolPartyRequests to set
     */
    public final void setPoolPartyRequests(
            final PoolPartyRequest[] thePoolPartyRequests) {
        poolPartyRequests = thePoolPartyRequests;
    }

    /** Get the selected PoolParty requests.
     * @return the selected requests
     */
    public final Boolean[] getSelectedPoolPartyRequests() {
        return selectedPoolPartyRequests;
    }

    /** Set the selected PoolParty requests.
     * @param aSelectedPoolPartyRequests the selected requests to set
     */
    public final void setSelectedPoolPartyRequests(
            final Boolean[] aSelectedPoolPartyRequests) {
        selectedPoolPartyRequests = aSelectedPoolPartyRequests;
    }

    /** Get the last results of request processing.
     * @return the last results
     */
    public final ArrayList<RequestResponse> getLastResults() {
        return lastResults;
    }

    /** Set the last results of request processing.
     * @param aLastResults the last results to set
     */
    public final void setLastResults(
            final ArrayList<RequestResponse> aLastResults) {
        lastResults = aLastResults;
    }

    /** Bean initialization. Copy the request definitions from
     * the application bean.
     */
    @PostConstruct
    public void initialize() {
        LOGGER.debug("In LoginBean initialize");
        poolPartyRequests = requestsCatalogueBean.getPoolPartyRequests();
        selectedPoolPartyRequests = new Boolean[poolPartyRequests.length];
    }

    /** Login. Check the user credentials. If successful, poolPartyProjects is
     * initialized with the user's projects, and selectedPoolPartyProjects is
     * initialized as an array of the same size, with false in all entries.
     * @return The name of the action that goes to the home page.
     */
    public final String login() {
        poolPartyProjects = PoolPartyToolkit.getProjects(this);
        if (poolPartyProjects == null) {
            /* Invalid login. */
            return ToolConstants.HOME_ACTION;
        }
        selectedPoolPartyProjects = new Boolean[poolPartyProjects.length];
        // Reset all other properties.
        Arrays.fill(selectedPoolPartyRequests, false);
        Arrays.sort(poolPartyProjects);
        lastResults = null;
        LOGGER.debug("Login by user: " + username);
        return ToolConstants.WELCOME_ACTION;
    }

    /** Process the user's request.
     * @return The name of the resulting action.
     */
    public final String processRequest() {
        LOGGER.debug("Called LoginBean processRequest()");
        if (selectedPoolPartyProjects == null) {
            LOGGER.debug("selectedPoolPartyProjects: null");
        } else {
            LOGGER.debug("selectedPoolPartyProjects: "
                    + Arrays.toString(selectedPoolPartyProjects));
        }
        if (selectedPoolPartyRequests == null) {
            LOGGER.debug("selectedPoolPartyRequests: null");
        } else {
            LOGGER.debug("selectedPoolPartyRequests: "
                    + Arrays.toString(selectedPoolPartyRequests));
        }
        return PoolPartyToolkit.processRequest(this);
    }

    /** Logout. Invalidate the user's session.
     * @return The name of the action that goes to the home page.
     */
    public final String logout() {
        LOGGER.debug("Logout by user: " + username);
        username = "";
        password = "";
        poolPartyProjects = null;
        selectedPoolPartyProjects = null;
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        ec.invalidateSession();
        return ToolConstants.HOME_ACTION;
    }

}
