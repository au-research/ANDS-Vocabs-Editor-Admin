/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.bean;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import au.org.ands.vocabs.editor.admin.utils.ToolProperties;

/** Bean class for getting access to information about the
 *  application version. */
@Named
@SessionScoped
public class ApplicationVersionBean implements Serializable {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 4687489633102317934L;

    /** Access to the tool properties. */
    private Properties props;

    /** Version number .*/
    private String version;

    /** Version timestamp .*/
    private String versionTimestamp;

    /** Build date .*/
    private String buildDate;

    /** Get the application version number.
     *  @return the version number
     */
    public String getVersion() {
        return version;
    }

    /** Get the version timestamp.
     *  @return the version timestamp
     */
    public String getVersionTimestamp() {
        return versionTimestamp;
    }

    /** Get the build date.
     *  @return the build date
     */
    public String getBuildDate() {
        return buildDate;
    }

    /** Bean initialization. Load the version property data.
     */
    @PostConstruct
    public void initialize() {
        props = ToolProperties.getProperties();
        version = props.getProperty("EditorAdmin.version");
        // If needed in future, get these too:
        versionTimestamp = props.getProperty("EditorAdmin.versionTimestamp");
        buildDate = props.getProperty("EditorAdmin.buildDate");
    }
}
