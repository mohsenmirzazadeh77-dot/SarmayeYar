package com.sarmayeyar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.sarmayeyar.app.ui.*
import com.sarmayeyar.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarmayeYarTheme {
                App(vm)
            }
        }
    }
}

@Composable
private fun App(vm: MainViewModel) {
    val assets by vm.assets.collectAsState()
    val history by vm.history.collectAsState()
    val prices by vm.prices.collectAsState()
    val busy by vm.busy.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                listOf("داشبورد", "دارایی‌ها", "تحلیل", "نمودار", "سناریو", "تنظیمات").forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = {},
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            Modifier.fillMaxSize().padding(padding)
        ) {
            when (tab) {
                0 -> DashboardScreen(assets, prices, vm::refreshPrices, busy)
                1 -> AssetsScreen(assets, vm::addAsset, vm::deleteAsset)
                2 -> AnalysisScreen(assets)
                3 -> HistoryScreen(history)
                4 -> ScenarioScreen(assets)
                else -> SettingsScreen(vm::backup)
            }
        }
    }
}
