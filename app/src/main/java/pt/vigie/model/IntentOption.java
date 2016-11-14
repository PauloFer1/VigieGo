package pt.vigie.model;

/**
 * Created by paulofernandes on 05/09/16.
 */
public class IntentOption {

    public static enum Operations {FINISH_RECORDING, READ_TEMPS, SHOW_TEMPS, NOTHING, RECOVER_AAR, START_RECORDING, SHORT_READ};
    private Operations mOption;

    private static IntentOption ourInstance = new IntentOption();

    public static IntentOption getInstance() {
        if(ourInstance==null)
            ourInstance = new IntentOption();
        return ourInstance;
    }

    private IntentOption() {
        mOption = Operations.SHORT_READ;
    }

    //////////////////////////////
    ////////////////  GETTERS
    public Operations getOption()
    {
        return(mOption);
    }

    //////////////////////////////
    ////////////////  SETTERS
    public void setOption(Operations option){
        mOption = option;
    }
}
