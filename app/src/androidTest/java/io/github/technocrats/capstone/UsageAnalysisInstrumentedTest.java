package io.github.technocrats.capstone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UsageAnalysisInstrumentedTest {

    @Rule
    public ActivityTestRule<UsageAnalysisActivity> activityTestRule =
            new ActivityTestRule<>(UsageAnalysisActivity.class);

    @Test
    public void display_Categories_Spinner_Test() {
        onView(withId(R.id.radio_bar)).perform(click());
        onView(withId(R.id.categories_spinner)).check(matches(isDisplayed()));
    }

    @Test
    public void display_Subcategories_Spinner_Test() {
        onView(withId(R.id.radio_line)).perform(click());
        onView(withId(R.id.subcategories_spinner)).check(matches(isDisplayed()));
    }
}
