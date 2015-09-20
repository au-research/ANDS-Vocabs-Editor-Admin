/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** Wrapper for the array of request definitions. */
@XmlRootElement(name = "requests")
public class PoolPartyRequests implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 8055422418468102241L;

    /** Array of the request elements. */
    @XmlElement(name = "request")
    private PoolPartyRequest[] requests;

    /** Get the SPARQL text.
     * @return the title
     */
    public final PoolPartyRequest[] getRequests() {
        return requests;
    }
}
