package br.edu.utfpr.menfin.ui.shared.utils

import android.util.Patterns
import androidx.annotation.StringRes
import br.edu.utfpr.menfin.R

data class FormField(
    val value: String = "",
    @StringRes
    val errorMessageCode: Int? = null
)

class FormFieldUtils {

    companion object {
        fun isValid(fields: List<FormField>): Boolean {
            return fields.none { field -> field.errorMessageCode != null }
        }

        fun validateFieldRequired(value: String): Int? = if (value.isBlank()) {
            R.string.error_field_required
        } else {
            null
        }

        fun validatePasswordComplexity(password: String): Int? {
            if (password.isBlank()) return null

            val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex()
            return if (!password.matches(passwordPattern)) {
                R.string.error_password_complexity
            } else {
                null
            }
        }

        fun runValidations(value: String, vararg validators: (String) -> Int?): Int? {
            for (validator in validators) {
                val errorResult = validator(value)
                if (errorResult != null) {
                    return errorResult
                }
            }
            return null
        }

        fun validateMinLength(minLength: Int): (String) -> Int? {
            return { value ->
                if (value.isBlank()) {
                    null
                }
                else if (value.length < minLength) {
                    R.string.error_min_length
                } else {
                    null
                }
            }
        }

        fun validateEmailFormat(email: String): Int? {
            if (email.isBlank()) return null

            return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                R.string.error_invalid_email
            } else {
                null
            }
        }

        fun validateLettersOnly(text: String): Int? {
            if (text.isBlank()) return null

            val pattern = "^\\p{L}+$".toRegex()
            return if (!text.matches(pattern)) {
                R.string.error_letters_only
            } else {
                null
            }
        }
    }
}