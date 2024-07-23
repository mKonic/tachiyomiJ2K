package eu.mkonic.tachiyomi.appwidget.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.appwidget.ContainerModifier
import eu.mkonic.tachiyomi.appwidget.util.stringResource
import eu.mkonic.tachiyomi.ui.main.MainActivity

@Composable
fun LockedWidget() {
    val intent = Intent(LocalContext.current, Class.forName(MainActivity.MAIN_ACTIVITY)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    Box(
        modifier = GlanceModifier
            .clickable(actionStartActivity(intent))
            .then(ContainerModifier)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.appwidget_unavailable_locked),
            style = TextStyle(
                color = ColorProvider(R.color.appwidget_on_secondary_container),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
