/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.model;

import java.io.Serializable;

import au.org.ands.vocabs.editor.admin.schema.Sparql;

/** Response of one request to PoolParty, either of a query or
 * an update. */
public class RequestResponse implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 5295284562748569981L;

    /** The project index (into LoginBean.poolPartyProjects). */
    private int projectIndex;

    /** The request index (into LoginBean.poolPartyProjects). */
    private int requestIndex;

    /** The request type. Either "Query" or "Update".*/
    private String type;

    /** The SPARQL Result response, if a Query .*/
    private Sparql sparqlResult;

    /** Get the project index (into LoginBean.poolPartyProjects).
     * @return the project index
     */
    public final int getProjectIndex() {
        return projectIndex;
    }

    /** Set the project index (into LoginBean.poolPartyProjects).
     * @param aProjectIndex the project index to set
     */
    public final void setProjectIndex(final int aProjectIndex) {
        projectIndex = aProjectIndex;
    }

    /** Get the request index (into LoginBean.poolPartyRequests).
     * @return the request index
     */
    public final int getRequestIndex() {
        return requestIndex;
    }

    /** Set the request index (into LoginBean.poolPartyRequests).
     * @param aRequestIndex the request index to set
     */
    public final void setRequestIndex(final int aRequestIndex) {
        requestIndex = aRequestIndex;
    }

    /** Get the request type.
     * @return the type
     */
    public final String getType() {
        return type;
    }

    /** Set the request type.
     * @param aType the type to set
     */
    public final void setType(final String aType) {
        type = aType;
    }

    /** Get the SPARQL Result response, if this was from a query.
     * @return the title
     */
    public final Sparql getSparqlResult() {
        return sparqlResult;
    }

    /** Set the SPARQL Result response, if this was from a query.
     * @param aSparqlResult the SPARQL Result to set
     */
    public final void setSparqlResult(final Sparql aSparqlResult) {
        sparqlResult = aSparqlResult;
    }

}
