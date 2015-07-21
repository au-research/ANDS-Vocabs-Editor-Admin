/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Details of one PoolParty project. */
@XmlRootElement
/* This annotation means that properties other than the ones defined
 * here are ignored during parsing. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolPartyProject implements Comparable<PoolPartyProject>,
    Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 2522160638005424651L;

    /** The project id. */
    private String id;

    /** The project title. */
    private String title;

    /** The project uriSupplement. */
    private String uriSupplement;

    /**
     * @return the id
     */
    public final String getId() {
        return id;
    }

    /** Set the project id.
     * @param anId the id to set
     */
    public final void setId(final String anId) {
        id = anId;
    }

    /** Get the project title.
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /** Set the project title.
     * @param aTitle the title to set
     */
    public final void setTitle(final String aTitle) {
        title = aTitle;
    }

    /** Get the project uriSupplement.
     * @return the uriSupplement
     */
    public final String getUriSupplement() {
        return uriSupplement;
    }

    /** Set the project uriSupplement.
     * @param aUriSupplement the uriSupplement to set
     */
    public final void setUriSupplement(final String aUriSupplement) {
        uriSupplement = aUriSupplement;
    }

    /** Ordering is by title. */
    @Override
    public final int compareTo(final PoolPartyProject otherProject) {
        return title.compareTo(otherProject.getTitle());
    }
}
