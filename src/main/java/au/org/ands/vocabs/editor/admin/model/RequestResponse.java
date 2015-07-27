/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.ands.vocabs.editor.admin.utils.ToolProperties;

/** Response of one request to PoolParty, either of a query or
 * an update. */
public class RequestResponse implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 5295284562748569981L;

    /** The class object for this class. */
    private static Class<?> classObject =
            MethodHandles.lookup().lookupClass();

    /** The LOGGER for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(classObject);

    /** The PoolParty project. */
    private PoolPartyProject project;

    /** The request. */
    private PoolPartyRequest request;

    /** The request type. Either "Query" or "Update".*/
    private String type;

    /** Was the query or update successful? */
    private Boolean successful = false;

    /** The SPARQL Result response, if this is a Query. */
    private String sparqlResult;

    /** The SPARQL Result response, if this is an Update. */
    private String updateResult;

    /** Get the PoolParty project.
     * @return the project
     */
    public final PoolPartyProject getProject() {
        return project;
    }

    /** Set the PoolParty project.
     * @param aProject the PoolParty project to set
     */
    public final void setProject(final PoolPartyProject aProject) {
        project = aProject;
    }

    /** Get the request.
     * @return the request
     */
    public final PoolPartyRequest getRequest() {
        return request;
    }

    /** Set the request.
     * @param aRequest the request to set
     */
    public final void setRequest(final PoolPartyRequest aRequest) {
        request = aRequest;
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

    /** Get the successful value.
     * @return the successful value
     */
    public final boolean isSuccessful() {
        return successful;
    }

    /** Set the successful value.
     * @param aSuccessful the successful value to set
     */
    public final void setSuccessful(final Boolean aSuccessful) {
        successful = aSuccessful;
    }

    /** Get the SPARQL Result response, if this was from a query.
     * @return the SPARQL Result response
     */
    public final String getSparqlResult() {
        return sparqlResult;
    }

    /** Set the SPARQL Result response, if this was from a query.
     * @param aSparqlResult the SPARQL Result to set
     */
    public final void setSparqlResult(final String aSparqlResult) {
        sparqlResult = aSparqlResult;
    }

    /** Get the SPARQL Update response, if this was from an update.
     * @return the Update response
     */
    public final String getUpdateResult() {
        return updateResult;
    }

    /** Set the SPARQL Update response, if this was from an update.
     * @param anUpdateResult the Update response to set
     */
    public final void setUpdateResult(final String anUpdateResult) {
        updateResult = anUpdateResult;
    }

    /** File representing the XSLT script. */
    private static String stylesheet =
            ToolProperties.getProperty("SPARQLResults.xsl");

    /** TransformerFactory used when running the XSLT script. */
    private static TransformerFactory tFactory =
            TransformerFactory.newInstance();

    /** Get the SPARQL Result response, if this was from a query.
     *  The return value is the response converted to
     *  an XHTML fragment suitable for embedding in a web page.
     * @return the SPARQL Result response, as an XHTML fragment
     */
    public final String getSparqlResultAsXHTML() {
        if (!successful || sparqlResult == null) {
            // The query returned an error status, or (somehow)
            // there is no result to return.
            return "Error";
        }
        try {
            StringReader reader = new StringReader(sparqlResult);
            StringWriter writer = new StringWriter();
            InputStream input = classObject.getClassLoader().
                    getResourceAsStream(stylesheet);
            if (input == null) {
                throw new RuntimeException("Can't find SPARQL Results XSL.");
            }
            StreamSource stylesource = new StreamSource(input);
            Transformer transformer = tFactory.newTransformer(stylesource);
            transformer.transform(new StreamSource(reader),
                    new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            LOGGER.error("Exception in getSparqlResultAsXHTML: ", e);
        }
        return "Error in getSparqlResultAsXHTML";
    }

    /** Get the SPARQL Result response, if this was from a query.
     *  The return value is the response converted to
     *  an XHTML fragment suitable for embedding in a web page.
     *  Thanks to
     *  http://stackoverflow.com/questions/9391838/
     *  how-to-provide-a-file-download-from-a-jsf-backing-bean
     */
    public final void sparqlResultDownload() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.responseReset();
            if (!successful || sparqlResult == null) {
                // The query returned an error status, or (somehow)
                // there is no result to return.
                return;
            }
            ec.setResponseContentType("application/sparql-results+xml");
            ec.setResponseContentLength(sparqlResult.length());
            ec.setResponseHeader("Content-Disposition",
                    "attachment; filename=\"" + "SPARQLResult-"
                            + project.getId()
                            + ".xml" + "\"");
            OutputStream output = ec.getResponseOutputStream();
            output.write(sparqlResult.getBytes(Charset.forName("UTF-8")));
            fc.responseComplete();
       } catch (IOException e) {
            LOGGER.error("Exception in sparqlResultDownload: ", e);
        }
    }

}
