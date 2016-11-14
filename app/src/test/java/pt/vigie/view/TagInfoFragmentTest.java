package pt.vigie.view;

/**
 * Created by paulofernandes on 15/07/16.
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TagInfoFragmentTest {

    TagInfoFragment tagInfo;

    @Before
    public void setUp(){
        tagInfo = new TagInfoFragment();
    }
    @After
    public void tearDown(){
        tagInfo = null;
    }

    @Test
    public void tagInfoCreateTest() throws Exception
    {
        TagInfoFragment tagInfo = new TagInfoFragment();

    }

    /**
     * Test Record Time left set with RecTimeLeft class
     * @throws Exception
     */
    @Test
    public void setRecTimeLeftTest() throws Exception{

        TagInfoFragment.RecTimeLeft timeLeft = new TagInfoFragment.RecTimeLeft();

        timeLeft.days = 2;
        timeLeft.hours = 1;
        timeLeft.minutes = 23;
        timeLeft.seconds = 30;

        //tagInfo.setRecTimeLeft(timeLeft);

        try {
            assertEquals(2, timeLeft.days);
        }catch (AssertionError e){
            System.out.println("days failed");
            throw e;
        }
        try {
            assertEquals(1, timeLeft.hours);
        }catch (AssertionError e){
            System.out.println("hours failed");
            throw e;
        }
        try {
            assertEquals(23, timeLeft.minutes);
        }catch (AssertionError e){
            System.out.println("minutes failed");
            throw e;
        }
        try {
            assertEquals(30, timeLeft.seconds);
        }catch (AssertionError e){
            System.out.println("seconds failed");
            throw e;
        }
    }
}
