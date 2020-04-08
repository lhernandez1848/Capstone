package io.github.technocrats.capstone;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ProductSearchInstrumentedTest {

    @Rule
    public ActivityTestRule<CreateOrderActivity> activityTestRule =
            new ActivityTestRule<>(CreateOrderActivity.class);

    @Test
    public void displayExpandableList_Test() {
        onView(withId(R.id.rdbSelect)).perform(click());
        onView(withId(R.id.expandableListView)).check(matches(isDisplayed()));
    }

    @Test
    public void displaySearchLayout_Test() {
        onView(withId(R.id.rdbSearch)).perform(click());
        onView(withId(R.id.grpSearch)).check(matches(isDisplayed()));
    }

    // The following test requires that animations are disabled in the target device
    // Settings > Developer Options >
    // -- Window animation scale
    // -- Transition animation scale
    // -- Animator duration scale
    @Test
    public void displaySearchResultError_Test(){
        onView(withId(R.id.rdbSearch)).perform(click());
        onView(withId(R.id.btnSearchProduct)).perform(click());
        onView(withId(R.id.tvProductNameError)).check(matches(isDisplayed()));
    }

}
