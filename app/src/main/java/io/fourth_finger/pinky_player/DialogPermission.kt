package io.fourth_finger.pinky_player

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * A dialog explaining why permission is need to the user.
 * It has a button to take the user to the app settings and
 * a button to close the dialog.
 */
class DialogPermission : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val resources = it.resources
            val builder = AlertDialog.Builder(it)
            builder.setMessage(resources.getString(R.string.permission_needed))
                .setPositiveButton(resources.getString(R.string.settings)) { dialog, id ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts(
                            "package",
                            it.packageName,
                            null
                        )
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, id ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}