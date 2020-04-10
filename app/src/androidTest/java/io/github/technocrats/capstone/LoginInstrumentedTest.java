package io.github.technocrats.capstone;

import androidx.test.espresso.action.TypeTextAction;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void usernameEmpty() throws InterruptedException {
        String TOAST_TEXT = "Username cannot be empty";
        onView(withId(R.id.txtUsername)).perform(typeText(""));
        onView(withId(R.id.txtPassword)).perform(new TypeTextAction("123456")).perform(closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(700);
        onView(withText(TOAST_TEXT)).inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void passwordEmpty() throws InterruptedException {
        String TOAST_TEXT = "Password cannot be empty";
        onView(withId(R.id.txtUsername)).perform(new TypeTextAction("lis"));
        onView(withId(R.id.txtPassword)).perform(new TypeTextAction("")).perform(closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(700);
        onView(withText(TOAST_TEXT)).inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void loginSuccess() throws InterruptedException {
        onView(withId(R.id.txtUsername)).perform(typeText("lis")).perform(closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("123456")).perform(closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(2000);
        assertTrue(activityTestRule.getActivity().isFinishing());
    }

    @Test
    public void loginFailure() throws InterruptedException {
        onView(withId(R.id.txtUsername)).perform(typeText("lis")).perform(closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(typeText("password")).perform(closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(2000);
        assertFalse(activityTestRule.getActivity().isFinishing());
    }
}
