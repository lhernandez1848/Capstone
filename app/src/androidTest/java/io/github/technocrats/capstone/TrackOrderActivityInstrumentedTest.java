package io.github.technocrats.capstone;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TrackOrderActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<TrackOrderActivity> activityTestRule =
            new ActivityTestRule<>(TrackOrderActivity.class);


    @Test
    public void searchOrder_validOrderNumber_ExpectedSuccess() throws InterruptedException {

        // arrange
        String orderNumberTest = "5054012930";
        String expectedResult = "1 order(s) found.";

        // act
        onView(withId(R.id.etOrderNumber)).perform(typeText(orderNumberTest), closeSoftKeyboard());
        onView(withId(R.id.btnTrackOrder)).perform(click());

        // assert
        Thread.sleep(2000);
        onView(withId(R.id.tvResult)).check(matches(withText(expectedResult)));
    }

    @Test
    public void searchOrder_invalidOrderNumber_ExpectedFail() throws InterruptedException {

        // arrange
        String orderNumberTest = "123456789";
        String expectedResult = "No orders found.";

        // act
        onView(withId(R.id.etOrderNumber)).perform(typeText(orderNumberTest), closeSoftKeyboard());
        onView(withId(R.id.btnTrackOrder)).perform(click());

        // assert
        Thread.sleep(2000);
        onView(withId(R.id.tvResult)).check(matches(withText(expectedResult)));
    }

}
