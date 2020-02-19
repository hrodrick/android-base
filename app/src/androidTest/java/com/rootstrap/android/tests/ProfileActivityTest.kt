package com.rootstrap.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.rootstrap.android.R
import com.rootstrap.android.network.managers.SessionManager
import com.rootstrap.android.ui.activity.main.ProfileActivity
import com.rootstrap.android.ui.activity.main.SignUpActivity
import com.rootstrap.android.utils.BaseTests
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProfileActivityTest : BaseTests() {

    private lateinit var activity: ProfileActivity
    private lateinit var scenario: ActivityScenario<ProfileActivity>

    @Before
    override fun before() {
        super.before()
        setServerDispatch(logoutDispatcher())
        setupSession()
        scenario = ActivityScenario.launch(ProfileActivity::class.java)
        scenario.onActivity { activity -> this.activity = activity }
    }

    @Test
    fun profileUiTest() {
        stringMatches(
            R.id.welcome_text_view,
            activity.getString(R.string.welcome_message, SessionManager.user?.firstName)
        )
        onView(withId(R.id.sign_out_button)).perform(click())
        Assert.assertEquals(null, SessionManager.user)

        // Check if this activity was successful launched
        activity.runOnUiThread {
            val current = currentActivity()
            Assert.assertEquals(SignUpActivity::class.java.name, current::class.java.name)
        }
    }

    private fun logoutDispatcher(): Dispatcher {
        return object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return if (request.path!!.contains("users/sign_out"))
                    mockServer.successfulResponse()
                else
                    mockServer.notFoundResponse()
            }
        }
    }

    @After
    override fun after() {
        super.after()
    }
}
