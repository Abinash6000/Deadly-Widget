package com.project.deadlywidget

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import com.project.deadlywidget.ui.theme.DeadlyWidgetTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeadlyWidgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DOBScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DOBScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val dobManager = remember { DOBManager(context) }
    var dob by remember { mutableStateOf(dobManager.getDOB()) }
    
    // State for live countdown
    var age by remember { mutableStateOf(0) }
    var countdownText by remember { mutableStateOf("") }

    // Update countdown every second
    LaunchedEffect(dob) {
        while (true) {
            dob?.let { d ->
                val now = LocalDateTime.now()
                val today = LocalDate.now()
                
                val period = Period.between(d, today)
                age = period.years
                
                var nextBirthday = d.withYear(today.year)
                if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
                    nextBirthday = nextBirthday.plusYears(1)
                }
                
                val nextBirthdayDateTime = nextBirthday.atStartOfDay()
                val duration = Duration.between(now, nextBirthdayDateTime)
                
                val days = duration.toDays()
                val hours = duration.toHours() % 24
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                
                countdownText = "${days}d ${hours}h ${minutes}m ${seconds}s left"
            }
            delay(1000)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (dob != null) {
            Text(
                text = "Age: $age", 
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 48.sp
            )
            Text(
                text = "Next age in:", 
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = countdownText, 
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        } else {
            Text(text = "Please set your Date of Birth", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Button(onClick = {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(context, { _, y, m, d ->
                val date = LocalDate.of(y, m + 1, d)
                dobManager.saveDOB(date)
                dob = date
                
                scope.launch {
                    AgeGlanceWidget().updateAll(context)
                }
            }, year, month, day).show()
        }) {
            Text(if (dob == null) "Select Date of Birth" else "Change Date of Birth")
        }
    }
}
