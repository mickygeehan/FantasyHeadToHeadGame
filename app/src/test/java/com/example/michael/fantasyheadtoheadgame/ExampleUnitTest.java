package com.example.michael.fantasyheadtoheadgame;

import com.example.michael.fantasyheadtoheadgame.ActivityScreens.Login;
import com.example.michael.fantasyheadtoheadgame.ActivityScreens.UserTeamScreen;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.SecurityMethods;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    RequestResponseParser parse = new RequestResponseParser();
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    
    //Test security methods
    @Test
    public void testSqlPreventionValid(){
        assertThat(SecurityMethods.isCleanInput("this isnt"), is(false));
    }

    @Test
    public void testSqlPreventionInvalid(){
        assertThat(SecurityMethods.isCleanInput("Thisisokay"), is(true));
    }
    
    //Test Common utility methods
    @Test
    public void checkValidIPValid(){
        assertThat(CommonUtilityMethods.validIP("192.168.1.103"), is(true));
    }

    @Test
    public void checkValidIP2Invalid(){
        assertThat(CommonUtilityMethods.validIP("192 168 1.1"), is(false));
    }

    @Test
    public void checkValidIP2Invalid2(){
        assertThat(CommonUtilityMethods.validIP("192d.168 1.1"), is(false));
    }
    
    
    //Parser
    @Test
    public void checkDoubleParser(){
        double check = parse.parseBudget("15");
        assertTrue(check == 15);
        
        check = parse.parseBudget(" ");
        assertTrue(check == -100);
    }


}