/*
 *   Created by Eric Ho on 12/4/20 10:20 AM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 10:20 AM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.util

import android.text.InputFilter
import java.lang.StringBuilder

class EditTextUtil {
    companion object {
        val LetterOrDigitFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val result = StringBuilder()
            for (i in start until end) {
                source?.let {
                    if (it[i].isLetterOrDigit() || it[i].isWhitespace()) {
                        result.append(it[i])
                    }
                }
            }
            result.toString()
        }

        val LetterFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val result = StringBuilder()
            for (i in start until end) {
                source?.let {
                    if (it[i].isLetter()) {
                        result.append(it[i])
                    }
                }
            }
            result.toString()
        }
    }
}
