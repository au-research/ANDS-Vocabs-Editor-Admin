/** See the file "LICENSE" for the full license governing this code. */
package au.org.ands.vocabs.editor.admin.bean;

import javax.faces.bean.SessionScoped;
import javax.inject.Named;

/** Bean class for login information. */
@Named
@SessionScoped
public class LoginBean {

    /** Username. */
    private String username;

    /** Password. */
    private String password;

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

}
