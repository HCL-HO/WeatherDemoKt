/*
 *   Created by Eric Ho on 12/4/20 3:47 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 3:47 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import ecl.ho.weathermolecule.ui.MainActivity
import ecl.ho.weathermolecule.ui.recentsearch.RecentSearchActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Rule
    @JvmField
    val mainActivityRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun onClickHistoryIcon_navToSearchHistory() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.details_history_btn)).perform(click())
        intended(hasComponent(RecentSearchActivity::class.java.name))
        activityScenario.close()
    }
}