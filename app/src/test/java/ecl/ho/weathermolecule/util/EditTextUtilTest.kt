/*
 *   Created by Eric Ho on 12/4/20 12:29 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 12:28 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.util

import android.content.Context
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class EditTextUtilTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun validate_letterOrDigitFilter() {
        val filter = EditTextUtil.LetterOrDigitFilter
        val dummyEditText = EditText(context)
        dummyEditText.filters = arrayOf(filter)
        val invalidString = "++{;.;.["
        dummyEditText.setText(invalidString)
        assertTrue(dummyEditText.text.toString().isEmpty())
        val validString = "abd 123"
        dummyEditText.setText(validString)
        assertTrue(dummyEditText.text.toString() == validString)
    }

    @Test
    fun validate_letterFilter() {
        val filter = EditTextUtil.LetterFilter
        val dummyEditText = EditText(context)
        dummyEditText.filters = arrayOf(filter)
        val invalidString = "++{;.;.[1234"
        dummyEditText.setText(invalidString)
        assertTrue(dummyEditText.text.toString().isEmpty())
        val validString = "abd"
        dummyEditText.setText(validString)
        assertTrue(dummyEditText.text.toString() == validString)
    }

}