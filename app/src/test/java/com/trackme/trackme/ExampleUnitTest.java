package com.trackme.trackme;

import android.content.Context;
import android.view.View;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void bmi_is_correct(){
        FitnessLevel fitnessLevel = new FitnessLevel();
        Float weight = Float.valueOf(70);
        Float height = Float.valueOf(180);

        double test = fitnessLevel.calculateBMI(weight,height);
        assert (test == 21.6);
    }

    @Test
    public void testPassword(){
        String test1 = "qwertyuiop123";
        MainActivity mainActivity = new MainActivity();
        assert mainActivity.passwordSecurityChecker(test1,test1);
    }
}