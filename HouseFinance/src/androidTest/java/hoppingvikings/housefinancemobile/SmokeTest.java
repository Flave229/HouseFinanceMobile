package hoppingvikings.housefinancemobile;

import android.Manifest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hoppingvikings.housefinancemobile.UserInterface.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SmokeTest
{
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule externalStoragePermission = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void ExampleSomeTest()
    {
        onView(withId(R.id.sign_in_button)).perform(click());
    }
}