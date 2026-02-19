package com.fitx.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.domain.model.GoalType
import com.fitx.app.domain.model.Gender
import com.fitx.app.ui.components.MetricCard
import com.fitx.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()

    FitxScreenScaffold(topBar = { ScreenTopBar("Profile & Health", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    value = form.heightCm,
                    onValueChange = { value -> viewModel.updateForm { it.copy(heightCm = value) } },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = form.weightKg,
                    onValueChange = { value -> viewModel.updateForm { it.copy(weightKg = value) } },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = form.age,
                    onValueChange = { value -> viewModel.updateForm { it.copy(age = value) } },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                EnumSelector(
                    title = "Gender",
                    values = Gender.entries,
                    selected = form.gender,
                    onSelected = { value -> viewModel.updateForm { it.copy(gender = value) } }
                )
            }
            item {
                EnumSelector(
                    title = "Activity Level",
                    values = com.fitx.app.domain.model.ActivityLevel.entries,
                    selected = form.activityLevel,
                    onSelected = { value -> viewModel.updateForm { it.copy(activityLevel = value) } }
                )
            }
            item {
                EnumSelector(
                    title = "Goal",
                    values = GoalType.entries,
                    selected = form.goalType,
                    onSelected = { value -> viewModel.updateForm { it.copy(goalType = value) } }
                )
            }
            item {
                OutlinedTextField(
                    value = form.goalWeightKg,
                    onValueChange = { value -> viewModel.updateForm { it.copy(goalWeightKg = value) } },
                    label = { Text("Goal Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(onClick = viewModel::saveProfile) {
                    Text("Save Profile")
                }
            }
            if (metrics != null) {
                item { MetricCard("BMI", "%.1f".format(metrics!!.bmi)) }
                item { MetricCard("BMR", "%.0f kcal".format(metrics!!.bmr)) }
                item { MetricCard("TDEE", "%.0f kcal".format(metrics!!.tdee)) }
                item { MetricCard("Daily Target", "${metrics!!.dailyCalorieTarget} kcal") }
                item { MetricCard("Goal Projection", "${metrics!!.projectedWeeksToGoal} weeks") }
            }
        }
    }
}
