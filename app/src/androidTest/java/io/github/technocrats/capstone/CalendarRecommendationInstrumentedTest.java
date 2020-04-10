package io.github.technocrats.capstone;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CalendarRecommendationInstrumentedTest {
    @Rule
    public ActivityTestRule<CalendarRecommendation> activityTestRule =
            new ActivityTestRule<>(CalendarRecommendation.class);

    @Test
    public void clickToSelectSuccessful() {
        onView(withId(R.id.btnRecommendationDate)).perform(click());
    }
}
