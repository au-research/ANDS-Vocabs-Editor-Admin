/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.ands.vocabs.editor.admin.model.PoolPartyRequest;
import au.org.ands.vocabs.editor.admin.model.PoolPartyRequests;

/** Bean class for the catalogue of available requests. */
// Previously, this was @ApplicationScoped. But that scope is too long;
// the bean is even persisted across Tomcat shutdown/startup, so changes
// to requests.xml are never picked up. By making the bean @SessionScoped,
// requests.xml is read for each session.
@Named
@SessionScoped
public class RequestsCatalogueBean implements Serializable {

    // If needed, uncomment the following. NB: this has to be static,
    // otherwise it gets lost during serialization, and therefore
    // "disappears" after a page navigation.
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = -8211762082373733753L;

    /** Base name of the requests catalogue file.
     *  Possible future work: make this a setting in editorconfig.properties.
     */
    private static final String REQUESTS_CATALOGUE_FILE = "requests.xml";

    /** PoolParty projects available to this user. */
    private PoolPartyRequest[] poolPartyRequests;

    /** Get the user's PoolParty requests. The array is sorted by title.
     *  @return the poolPartyRequests
     */
    public PoolPartyRequest[] getPoolPartyRequests() {
        return poolPartyRequests;
    }

    /**
     * Set the user's PoolParty requests. The array will be sorted by title.
     * @param thePoolPartyRequests the poolPartyRequests to set
     */
    public void setPoolPartyRequests(
            final PoolPartyRequest[] thePoolPartyRequests) {
        poolPartyRequests = thePoolPartyRequests;
    }

    /** Bean initialization. Load the request definitions from
     * requests.xml.
     */
    @PostConstruct
    public void initialize() {
        LOGGER.debug("In RequestsCatalogueBean initialize");
        InputStream input = MethodHandles.lookup().lookupClass().
                getClassLoader().getResourceAsStream(REQUESTS_CATALOGUE_FILE);
        if (input == null) {
            LOGGER.error("Can't find Tool requests catalogue file");
            throw new RuntimeException(
                    "Can't find Tool requests catalogue file.");
        }
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(PoolPartyRequests.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            poolPartyRequests =
                    ((PoolPartyRequests) jaxbUnmarshaller.unmarshal(input)).
                    getRequests();
        } catch (JAXBException e) {
            LOGGER.error("Exception while parsing requests catalogue", e);
            return;
        }
        try {
            input.close();
        } catch (IOException e) {
            LOGGER.error("Exception while closing requests catalogue file", e);
            return;
        }
        LOGGER.debug("Requests read: " + poolPartyRequests.length);
    }

}
