package pt.vigie.model;

/**
 * Created by paulofernandes on 05/09/16.
 */
public class RecProfile {

    private static RecProfile ourInstance = new RecProfile();

    private String mName;
    private int mSampling;
    private int mMin;
    private int mNOkMin;
    private int mMax;
    private int mNOkMax;

    public static RecProfile getInstance() {
        if(ourInstance==null)
            ourInstance = new RecProfile();
        return ourInstance;
    }


    private RecProfile(){}


    public void setProfile(int sampling, int min, int minNOk, int max, int maxNOk)
    {
        mName = "Default";
        mSampling = sampling;
        mMin = min;
        mNOkMin = minNOk;
        mMax = max;
        mNOkMax = maxNOk;
    }
    public void setProfile(String name, int sampling, int min, int minNOk, int max, int maxNOk)
    {
        mName = name;
        mSampling = sampling;
        mMin = min;
        mNOkMin = minNOk;
        mMax = max;
        mNOkMax = maxNOk;
    }

    /////////////////////////////
    ////////////// GETTERS
    public String getName()
    {
        return mName;
    }
    public int getSampling()
    {
        return mSampling;
    }
    public int getMin()
    {
        return mMin;
    }
    public int getMinNOk()
    {
        return mNOkMin;
    }
    public int getMax()
    {
        return mMax;
    }
    public int getMaxNOk()
    {
        return mNOkMax;
    }


}
