package com.example.wecareto_project.ui.screens
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.wecareto_project.data.SafetyAnswer

//Opens the device dialer
//It never places a phone call automatically
@Composable
fun SafetySupport(
    immediateDanger: SafetyAnswer?,
    suicideConcern: SafetyAnswer?
) {
    val context = LocalContext.current
    //Opens the phone app without calling
    fun openDialer(number: String) {
        try {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", number, null)
            )
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No phone dialer is available on this device.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (_: SecurityException) {
            Toast.makeText(
                context,
                "Unable to open the phone dialer.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Emergency guidance
        if (immediateDanger == SafetyAnswer.YES || immediateDanger == SafetyAnswer.NOT_SURE) {
            Text(
                "Immediate danger or unsure about safety? " +
                        "Call 911 now if emergency help is needed. " +
                        "WeCareTO cannot dispatch responders."
            )
            OutlinedButton(
                onClick = {
                    openDialer("911")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open dialer for 911")
            }
        }
        //Suicide related crisis support
        if (suicideConcern == SafetyAnswer.YES) {
            Text(
                "For thoughts of suicide or a suicide related " +
                        "crisis in Canada, call or text 988. " +
                        "If there is immediate danger, call 911."
            )
            OutlinedButton(
                onClick = {
                    openDialer("988")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open dialer for 988")
            }
        }
        //Information if questions were skipped
        if (
            immediateDanger == null ||
            suicideConcern == null
        ) {
            Text(
                "These safety questions are optional. " +
                        "If you are unsure, you can still call 911 " +
                        "for an immediate emergency or 988 " +
                        "for suicide-related support."
            )
        }
    }
}