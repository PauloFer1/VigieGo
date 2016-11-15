package pt.vigie.model;

/**
 * Created by paulofernandes on 19/07/16.
 */
public class LoginSession {

    private String      mUsername;
    private String      mPassword;
    private boolean     mIsLogged = false;

    private static LoginSession ourInstance = new LoginSession();

    public static LoginSession getInstance() {
        if(ourInstance==null)
            ourInstance = new LoginSession();
        return ourInstance;
    }

    private LoginSession() {
    }

    /**
     * Get Username String
     * @return String username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Get Password String
     * @return String password
     */
    public String getPassword() {
        return(mPassword);
    }

    /**
     * If is logged?
     * @return boolean flag
     */
    public boolean isLogged(){
        return(mIsLogged);
    }

    /**
     * set username
     * @param username String
     */
    public void setUsername(String username){
        this.mUsername = username;
    }

    /**
     * Set password
     * @param password String
     */
    public void setPassword(String password){
        this.mPassword = password;
    }

    /**
     * Login, set mIslogged to true
     */
    public void login(){
        this.mIsLogged = true;
    }

    /**
     * Logout, set mIslogged to false
     */
    public void logout(){
        this.mIsLogged = false;
    }
}
