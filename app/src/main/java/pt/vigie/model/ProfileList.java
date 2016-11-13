package pt.vigie.model;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by paulofernandes on 01/09/16.
 */
public class ProfileList implements Serializable{

    private Vector<Profile> list;

    public ProfileList(){
        list = new Vector<Profile>();
    };

    public void addProfile(Profile p){
        list.add(p);
    }
    public void addProfile(int id, String name, long measureTime, int minTemp, int maxTemp, int minNOkTemp, int maxNOkTemp, boolean startByBtn)
    {
        Profile p = new Profile(id, name, measureTime, minTemp, maxTemp, minNOkTemp, maxNOkTemp, startByBtn);

        list.add(p);
    }

    public void creatDummy(){
        addProfile(1, "Teste 1", 15, 15, 20, 3, 3, false);
        addProfile(2,"Teste 2", 10, 16, 21, 3, 3, false);
        addProfile(3,"Teste 3", 9, 17, 22, 3, 3, false);
        addProfile(4,"Teste 4", 8, 18, 23, 3, 3, false);
        addProfile(5,"Teste 5", 7, 19, 24, 3, 3, false);
    }

    public Vector<Profile> getList(){return list;};

}
