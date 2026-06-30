package com.project.deadlywidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

class AgeGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dobManager = DOBManager(context)
        val dob = dobManager.getDOB()

        provideContent {
            AgeWidgetContent(dob)
        }
    }

    @Composable
    private fun AgeWidgetContent(dob: LocalDate?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = Color.White, night = Color.Black))
                .padding(4.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            if (dob == null) {
                Text(
                    text = "Set DOB in App",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ColorProvider(day = Color.Black, night = Color.White)
                    )
                )
            } else {
                val now = LocalDateTime.now()
                val today = LocalDate.now()
                
                val period = Period.between(dob, today)
                val age = period.years
                
                var nextBirthday = dob.withYear(today.year)
                if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                    nextBirthday = nextBirthday.plusYears(1)
                }
                
                val nextBirthdayDateTime = nextBirthday.atStartOfDay()
                val duration = Duration.between(now, nextBirthdayDateTime)
                
                val days = duration.toDays()
                val hours = duration.toHours() % 24
                val minutes = duration.toMinutes() % 60

                Text(
                    text = "Age: $age",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColorProvider(day = Color.Black, night = Color.White)
                    )
                )

                Text(
                    text = "Next age in:",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(day = Color.Black, night = Color.White)
                    )
                )
                
                Text(
                    text = "${days}d ${hours}h ${minutes}m left",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColorProvider(day = Color.DarkGray, night = Color.LightGray)
                    )
                )
            }
        }
    }
}
