package com.example

import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NVpnDatabase
import com.example.data.VpnConnectionState
import com.example.data.VpnRepository
import com.example.ui.NVpnTab
import com.example.ui.VpnViewModel
import com.example.ui.components.NVpnBrandHeader
import com.example.ui.screens.BlueprintAndVpsGuideScreen
import com.example.ui.screens.HomeConnectScreen
import com.example.ui.screens.SecurityAndProtocolScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.NVpnTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondarySlate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = NVpnDatabase.getInstance(applicationContext)
        val repository = VpnRepository(database.vpnDao())

        setContent {
            NVpnTheme {
                val vpnViewModel: VpnViewModel = viewModel(
                    factory = VpnViewModel.provideFactory(repository)
                )
                NVpnAppRoot(viewModel = vpnViewModel)
            }
        }
    }
}

@Composable
fun NVpnAppRoot(
    viewModel: VpnViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredServers by viewModel.filteredServers.collectAsStateWithLifecycle()
    val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()

    // Android VpnService permission launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        viewModel.toggleConnection(context)
    }

    val handleConnectAction: () -> Unit = remember(context, uiState.connectionState) {
        {
            if (uiState.connectionState == VpnConnectionState.DISCONNECTED) {
                val prepareIntent = try {
                    VpnService.prepare(context)
                } catch (_: Exception) {
                    null
                }
                if (prepareIntent != null) {
                    try {
                        vpnPermissionLauncher.launch(prepareIntent)
                    } catch (_: Exception) {
                        viewModel.toggleConnection(context)
                    }
                } else {
                    viewModel.toggleConnection(context)
                }
            } else {
                viewModel.toggleConnection(context)
            }
        }
    }

    // BackHandler for secondary tabs to return to Home Connect tab
    if (uiState.currentTab != NVpnTab.CONNECT) {
        BackHandler {
            viewModel.selectTab(NVpnTab.CONNECT)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        val isExpandedScreen = maxWidth >= 680.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = ObsidianBackground,
            topBar = {
                NVpnBrandHeader(
                    isConnected = uiState.connectionState == VpnConnectionState.CONNECTED,
                    hasVipAccess = uiState.hasVipAccess,
                    activeProtocolBadge = uiState.selectedProtocol.displayName,
                    onVipBadgeClick = { viewModel.selectTab(NVpnTab.SECURITY) }
                )
            },
            bottomBar = {
                if (!isExpandedScreen) {
                    NVpnBottomNavigationBar(
                        currentTab = uiState.currentTab,
                        onSelectTab = viewModel::selectTab
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen) {
                    NVpnSideNavigationRail(
                        currentTab = uiState.currentTab,
                        onSelectTab = viewModel::selectTab
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 640.dp)
                    ) {
                        when (uiState.currentTab) {
                            NVpnTab.CONNECT -> {
                                HomeConnectScreen(
                                    uiState = uiState,
                                    onToggleConnect = handleConnectAction,
                                    onSmartConnectClick = { viewModel.runSmartConnect(context) },
                                    onOpenServerList = { viewModel.selectTab(NVpnTab.SERVERS) },
                                    onOpenSecuritySettings = { viewModel.selectTab(NVpnTab.SECURITY) },
                                    onWatchRewardedAdPass = { viewModel.grantRewardedAdVipPass() }
                                )
                            }
                            NVpnTab.SERVERS -> {
                                ServerListScreen(
                                    uiState = uiState,
                                    servers = filteredServers,
                                    onSearchQueryChange = viewModel::updateSearchQuery,
                                    onSelectFilter = viewModel::selectFilter,
                                    onSelectServer = { server ->
                                        viewModel.selectServer(
                                            context = context,
                                            server = server,
                                            onRequireVipUnlock = {
                                                viewModel.selectTab(NVpnTab.SECURITY)
                                            }
                                        )
                                    },
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onDeleteCustomServer = viewModel::deleteCustomServer,
                                    onSmartConnectClick = { viewModel.runSmartConnect(context) },
                                    onRefreshPingsClick = viewModel::refreshAllServerPings,
                                    onAddCustomVps = viewModel::addCustomVpsServer,
                                    onUnlockVipPlan = viewModel::unlockVipSubscription,
                                    onWatchRewardedAdPass = viewModel::grantRewardedAdVipPass
                                )
                            }
                            NVpnTab.SECURITY -> {
                                SecurityAndProtocolScreen(
                                    uiState = uiState,
                                    recentLogs = recentLogs,
                                    configPreviewText = viewModel.getActiveConfigPreview(),
                                    onSelectProtocol = viewModel::selectProtocol,
                                    onToggleKillSwitch = viewModel::setKillSwitchEnabled,
                                    onSimulateKillSwitchDrop = {
                                        viewModel.simulateKillSwitchProtectionTest(context)
                                    },
                                    onToggleNoLogsMode = viewModel::setNoLogsModeEnabled,
                                    onToggleSplitTunneling = viewModel::setSplitTunnelingEnabled,
                                    onSelectDnsServer = viewModel::setDnsServer,
                                    onUnlockVipPlan = viewModel::unlockVipSubscription,
                                    onWatchRewardedAdPass = viewModel::grantRewardedAdVipPass
                                )
                            }
                            NVpnTab.BLUEPRINT -> {
                                BlueprintAndVpsGuideScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NVpnBottomNavigationBar(
    currentTab: NVpnTab,
    onSelectTab: (NVpnTab) -> Unit
) {
    NavigationBar(
        containerColor = ObsidianSurface,
        contentColor = TextPrimaryWhite,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("nvpn_bottom_nav_bar")
    ) {
        NVpnTab.entries.forEach { tab ->
            val selected = currentTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    val icon = when (tab) {
                        NVpnTab.CONNECT -> if (selected) Icons.Default.PowerSettingsNew else Icons.Outlined.PowerSettingsNew
                        NVpnTab.SERVERS -> if (selected) Icons.Default.Public else Icons.Outlined.Public
                        NVpnTab.SECURITY -> if (selected) Icons.Default.Security else Icons.Outlined.Security
                        NVpnTab.BLUEPRINT -> if (selected) Icons.Default.MenuBook else Icons.Outlined.MenuBook
                    }
                    Icon(imageVector = icon, contentDescription = tab.label)
                },
                label = {
                    Text(
                        text = tab.label,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ObsidianBackground,
                    selectedTextColor = CyberEmerald,
                    indicatorColor = CyberEmerald,
                    unselectedIconColor = TextSecondarySlate,
                    unselectedTextColor = TextSecondarySlate
                ),
                modifier = Modifier.testTag("nav_tab_${tab.route}")
            )
        }
    }
}

@Composable
private fun NVpnSideNavigationRail(
    currentTab: NVpnTab,
    onSelectTab: (NVpnTab) -> Unit
) {
    NavigationRail(
        containerColor = ObsidianSurface,
        contentColor = TextPrimaryWhite,
        modifier = Modifier.testTag("nvpn_side_nav_rail")
    ) {
        NVpnTab.entries.forEach { tab ->
            val selected = currentTab == tab
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    val icon = when (tab) {
                        NVpnTab.CONNECT -> if (selected) Icons.Default.PowerSettingsNew else Icons.Outlined.PowerSettingsNew
                        NVpnTab.SERVERS -> if (selected) Icons.Default.Public else Icons.Outlined.Public
                        NVpnTab.SECURITY -> if (selected) Icons.Default.Security else Icons.Outlined.Security
                        NVpnTab.BLUEPRINT -> if (selected) Icons.Default.MenuBook else Icons.Outlined.MenuBook
                    }
                    Icon(imageVector = icon, contentDescription = tab.label)
                },
                label = {
                    Text(
                        text = tab.label,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ObsidianBackground,
                    selectedTextColor = CyberEmerald,
                    indicatorColor = CyberEmerald,
                    unselectedIconColor = TextSecondarySlate,
                    unselectedTextColor = TextSecondarySlate
                )
            )
        }
    }
}
