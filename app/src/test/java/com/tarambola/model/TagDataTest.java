package com.tarambola.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by paulofernandes on 26/07/16.
 */
public class TagDataTest {
    TagData tagData;

    @Before
    public void setUp(){
        tagData = new TagData();
        short[] temps = {272, 274, 272, 275, 275, 275, 276, 278, 280, 282, 279};

        tagData.setActivationEnergy(83);
        tagData.setTemps(temps);

    }

    @After
    public void tearDown(){tagData =null;}

    @Test
    public void calculateKineticTest()
    {
        assertTrue(tagData.getKineticTemp()>27);
    }
}
