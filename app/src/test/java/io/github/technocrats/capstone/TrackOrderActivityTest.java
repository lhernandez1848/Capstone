package io.github.technocrats.capstone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackOrderActivityTest {

    @Test
    public void validateOrderNumber_orderNumberIsNull_ExpectedFalse(){

        // arrange
        Boolean expectedResult = false;
        Boolean actualResult;
        String orderNumberTest = "";

        // act
        actualResult = TrackOrderActivity.validateOrderNumber(orderNumberTest);

        // assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateOrderNumber_orderNumberContainsLetters_ExpectedFalse(){

        // arrange
        Boolean expectedResult = false;
        Boolean actualResult;
        String orderNumberTest = "ABC123456";

        // act
        actualResult = TrackOrderActivity.validateOrderNumber("");

        // assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validateOrderNumber_validOrderNumber_ExpectedTrue(){

        // arrange
        Boolean actualResult;
        String orderNumberTest = "50540129630";

        // act
        actualResult = TrackOrderActivity.validateOrderNumber(orderNumberTest);

        // assert
        assertTrue(actualResult);
    }


}
