package com.gity.kliksewa.helper

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.gity.kliksewa.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.NumberFormat
import java.util.Locale

object CommonUtils {
    fun showMessages(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Comprehensive Snackbar method with multiple options
    fun showSnackBar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        actionColor: Int? = null,
        onActionClick: (() -> Unit)? = null
    ): Snackbar {
        val snackbar = Snackbar.make(view, message, duration)

        // Add action if provided
        actionText?.let { text ->
            snackbar.setAction(text) {
                onActionClick?.invoke()
            }

            // Set custom action color if provided
            actionColor?.let { color ->
                snackbar.setActionTextColor(color)
            }
        }

        snackbar.show()
        return snackbar
    }

    // Overloaded method for simple Snackbar without action
    fun showSnackBar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT
    ): Snackbar {
        return showSnackBar(view, message, duration, null, null, null)
    }

    // Error Snackbar with red color
    fun showErrorSnackBar(
        view: View,
        errorMessage: String,
        actionText: String? = "Retry",
        onRetry: (() -> Unit)? = null
    ): Snackbar {
        return showSnackBar(
            view = view,
            message = errorMessage,
            duration = Snackbar.LENGTH_LONG,
            actionText = actionText,
            onActionClick = onRetry
        )
    }

    // Success Snackbar with green color
    fun showSuccessSnackBar(
        view: View,
        successMessage: String
    ): Snackbar {
        return showSnackBar(
            view = view,
            message = successMessage,
            duration = Snackbar.LENGTH_SHORT,
            actionText = "OK",
        )
    }

    //    Show Loading
    fun showLoading(isLoading: Boolean, view: View) {
        if (isLoading) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    //    Material Alert Dialog Builder
    fun materialAlertDialog(
        message: String,
        title: String,
        context: Context,
        onPositiveClick: () -> Unit,
    ) {
        MaterialAlertDialogBuilder(context, R.style.CustomAlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ ->
                onPositiveClick()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Content Resolver
     fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }

    fun getFormattedPrice(
        pricePerHour: Double?,
        pricePerDay: Double?,
        pricePerWeek: Double?,
        pricePerMonth: Double?
    ): String {
        // Urutan prioritas: day > hour > week > month
        val selectedPrice = when {
            pricePerDay != null && pricePerDay > 0 -> Pair(pricePerDay, "Per Day")
            pricePerHour != null && pricePerHour > 0 -> Pair(pricePerHour, "Per Hour")
            pricePerWeek != null && pricePerWeek > 0 -> Pair(pricePerWeek, "Per Week")
            pricePerMonth != null && pricePerMonth > 0 -> Pair(pricePerMonth, "Per Month")
            else -> null
        }

        return if (selectedPrice != null) {
            val (price, type) = selectedPrice
            "Rp${String.format("%,.0f", price)} / $type"
        } else {
            "Harga tidak tersedia"
        }
    }

     fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }


}