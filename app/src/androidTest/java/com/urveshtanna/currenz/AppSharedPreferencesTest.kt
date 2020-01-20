package com.urveshtanna.currenz

import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences
import com.urveshtanna.currenz.ui.selection.Constants
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner


/**
 * Unit test for {@link com.urveshtanna.currenz.domain.local.sharedPreferences.AppSharedPreferences}
 */
@RunWith(MockitoJUnitRunner::class)
class AppSharedPreferencesTest {

    private val spMockBuilder: SPMockBuilder = SPMockBuilder()
    private var appSharedPreferences: AppSharedPreferences? = null;
    private var appSharedPreferencesDefault: AppSharedPreferences? = null;
    private var TEST_USER_SYMBOL = "JPG";
    private var TEST_DEFAULT_SYMBOL = Constants.DEFAULT_CURRENCY;

    @Before
    fun setUp() {
        appSharedPreferences = AppSharedPreferences(spMockBuilder.createContext())
        appSharedPreferencesDefault = AppSharedPreferences(spMockBuilder.createContext())
    }

    /**
     * This test is check if user selected symbol is saved and retrevied correctly
     */
    @Test
    fun changingDefaultSymbol() {
        appSharedPreferences?.setUserPreferredCurrency(TEST_USER_SYMBOL)
        Assert.assertEquals(appSharedPreferences?.getUserPreferredCurrency(), TEST_USER_SYMBOL)
    }

    /**
     * This test is check if without changing user symbol the default symbol is returned or not
     */
    @Test
    fun checkingDefaultSymbol() {
        Assert.assertEquals(appSharedPreferences?.getUserPreferredCurrency(), TEST_DEFAULT_SYMBOL)
    }

}