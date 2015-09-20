/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.model;

import java.io.Serializable;

/** Details of one possible request to PoolParty. */
public class PoolPartyRequest implements Comparable<PoolPartyRequest>,
    Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 7603871400368111397L;

    /** The request type. Either "Query" or "Update".*/
    private String type;

    /** The request title. */
    private String title;

    /** The SPARQL text .*/
    private String sparql;

    /** Get the request title.
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /** Set the request title.
     * @param aTitle the title to set
     */
    public final void setTitle(final String aTitle) {
        title = aTitle;
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

    /** Get the SPARQL text.
     * @return the title
     */
    public final String getSparql() {
        return sparql;
    }

    /** Set the SPARQL text.
     * @param aSparql the SPARQL text to set
     */
    public final void setSparql(final String aSparql) {
        sparql = aSparql;
    }

    /** Ordering is by type, then by title. */
    @Override
    public final int compareTo(final PoolPartyRequest otherRequest) {
        int typeComparison = type.compareTo(otherRequest.type);
        if (typeComparison != 0) {
            return typeComparison;
        }
        return title.compareTo(otherRequest.getTitle());
    }
}
