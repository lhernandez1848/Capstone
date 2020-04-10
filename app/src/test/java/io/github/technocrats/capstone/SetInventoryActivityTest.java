package io.github.technocrats.capstone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetInventoryActivityTest {

    @Test
    public void formatDate_invalidMonth_ExpectedNull(){

        // arrange
        int testDay = 1;
        int testMonth = 13; // invalid month
        int testYear = 2020;
        String expectedResult = "";


        // act
        String actualResult = SetInventoryActivity.formatDate(testDay, testMonth, testYear);

        // assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void formatDate_zeroParameters_ExpectedNull(){

        // arrange
        int testDay = 0;
        int testMonth = 0; // invalid month
        int testYear = 0;
        String expectedResult = "";


        // act
        String actualResult = SetInventoryActivity.formatDate(testDay, testMonth, testYear);

        // assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void formatDate_validDate_ExpectedSuccess(){

        // arrange
        int testDay = 12;
        int testMonth = 3;
        int testYear = 2020;
        String expectedResult = "12-Mar-2020";


        // act
        String actualResult = SetInventoryActivity.formatDate(testDay, testMonth, testYear);

        // assert
        assertEquals(expectedResult, actualResult);
    }



}
