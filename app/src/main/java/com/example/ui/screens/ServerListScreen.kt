package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ServerFilterType
import com.example.data.VpnProtocol
import com.example.data.VpnServerEntity
import com.example.ui.NVpnUiState
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.VipGold
import com.example.ui.theme.WarningAmber

@Composable
fun ServerListScreen(
    uiState: NVpnUiState,
    servers: List<VpnServerEntity>,
    onSearchQueryChange: (String) -> Unit,
    onSelectFilter: (ServerFilterType) -> Unit,
    onSelectServer: (VpnServerEntity) -> Unit,
    onToggleFavorite: (VpnServerEntity) -> Unit,
    onDeleteCustomServer: (VpnServerEntity) -> Unit,
    onSmartConnectClick: () -> Unit,
    onRefreshPingsClick: () -> Unit,
    onAddCustomVps: (String, String, String, String, VpnProtocol, String) -> Unit,
    onUnlockVipPlan: (String) -> Unit,
    onWatchRewardedAdPass: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddVpsDialog by rememberSaveable { mutableStateOf(false) }
    var vipPromptServer by remember { mutableStateOf<VpnServerEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("server_list_screen")
    ) {
        // Search & Add Custom VPS Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search country, city, or IP…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMutedSlate
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ElectricCyan
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = TextSecondarySlate
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberEmerald,
                    unfocusedBorderColor = GlassBorderColor,
                    focusedContainerColor = SlateCardSurface,
                    unfocusedContainerColor = SlateCardSurface,
                    focusedTextColor = TextPrimaryWhite,
                    unfocusedTextColor = TextPrimaryWhite
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("server_search_input")
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = { showAddVpsDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberEmerald,
                    contentColor = ObsidianBackground
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                modifier = Modifier.testTag("add_custom_vps_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Custom VPS",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "VPS",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ServerFilterType.entries) { filter ->
                val selected = uiState.selectedFilter == filter
                FilterChip(
                    selected = selected,
                    onClick = { onSelectFilter(filter) },
                    label = {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberEmerald.copy(alpha = 0.18f),
                        selectedLabelColor = CyberEmerald,
                        containerColor = SlateCardSurface,
                        labelColor = TextSecondarySlate
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = GlassBorderColor,
                        selectedBorderColor = CyberEmerald
                    ),
                    modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Smart Connect + Real Ping Test Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onSmartConnectClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateCardElevated,
                    contentColor = CyberEmerald
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("server_list_smart_connect_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Smart Connect",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onRefreshPingsClick,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("refresh_server_pings_button")
            ) {
                if (uiState.isPingingServers) {
                    CircularProgressIndicator(
                        color = ElectricCyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "Test Ping",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ping Test",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Server List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(servers, key = { it.id }) { server ->
                val isSelected = uiState.selectedServer?.id == server.id
                ServerNodeCard(
                    server = server,
                    isSelected = isSelected,
                    hasVipAccess = uiState.hasVipAccess,
                    onClick = {
                        if (server.isVip && !uiState.hasVipAccess) {
                            vipPromptServer = server
                        } else {
                            onSelectServer(server)
                        }
                    },
                    onToggleFavorite = { onToggleFavorite(server) },
                    onDeleteCustom = { onDeleteCustomServer(server) }
                )
            }
        }
    }

    // Add Custom VPS Server Modal Dialog
    if (showAddVpsDialog) {
        AddCustomVpsDialog(
            onDismiss = { showAddVpsDialog = false },
            onSave = { country, city, ip, port, protocol, pubKey ->
                onAddCustomVps(country, city, ip, port, protocol, pubKey)
                showAddVpsDialog = false
            }
        )
    }

    // VIP Unlock / Rewarded Ad Modal Dialog when tapping a VIP Server
    vipPromptServer?.let { targetServer ->
        AlertDialog(
            onDismissRequest = { vipPromptServer = null },
            containerColor = SlateCardSurface,
            titleContentColor = TextPrimaryWhite,
            textContentColor = TextSecondarySlate,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = VipGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock ${targetServer.flagEmoji} ${targetServer.countryName} VIP",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${targetServer.cityName} (${targetServer.downloadCapacityMbps / 1000} Gbps Multi-Hop Node) is a VIP Server.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Choose how to connect right now:",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan
                    )
                    Button(
                        onClick = {
                            onWatchRewardedAdPass()
                            onSelectServer(targetServer)
                            vipPromptServer = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberEmerald,
                            contentColor = ObsidianBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PlayCircleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Watch AdMob Rewarded Ad (+2h Free VIP)")
                    }
                    Button(
                        onClick = {
                            onUnlockVipPlan("VIP Annual Pro ($29.99/yr)")
                            onSelectServer(targetServer)
                            vipPromptServer = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VipGold,
                            contentColor = ObsidianBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activate VIP Pro (Unlimited + No Ads)")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { vipPromptServer = null }) {
                    Text("Cancel", color = TextSecondarySlate)
                }
            }
        )
    }
}

@Composable
private fun ServerNodeCard(
    server: VpnServerEntity,
    isSelected: Boolean,
    hasVipAccess: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteCustom: () -> Unit
) {
    val pingColor = when {
        server.pingMs <= 35 -> CyberEmerald
        server.pingMs <= 75 -> ElectricCyan
        else -> WarningAmber
    }
    val protocol = VpnProtocol.fromId(server.protocolId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("server_item_card_${server.countryCode.lowercase()}_${server.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateCardElevated else SlateCardSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) CyberEmerald else GlassBorderColor
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ObsidianBackground)
                            .border(1.dp, GlassBorderColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = server.flagEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = server.countryName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (server.isVip) {
                                Surface(
                                    color = VipGold.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, VipGold.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (hasVipAccess) "VIP UNLOCKED" else "VIP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VipGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = CyberEmerald.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (server.isCustomVps) "MY VPS" else "FREE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${server.cityName} · ${protocol.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondarySlate
                        )
                    }
                }

                // Ping & Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${server.pingMs} ms",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = pingColor
                        )
                        Text(
                            text = "${server.downloadCapacityMbps / 1000} Gbps",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedSlate
                        )
                    }

                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (server.isFavorite) VipGold else TextMutedSlate,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (server.isCustomVps) {
                        IconButton(onClick = onDeleteCustom) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Custom VPS",
                                tint = AlertCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = CyberEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speed & Load Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IP: ${server.ipAddress}:${server.port}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedSlate
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Server Load ${server.loadPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondarySlate
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { (server.loadPercent / 100f).coerceIn(0.05f, 1f) },
                        color = pingColor,
                        trackColor = ObsidianBackground,
                        modifier = Modifier
                            .width(68.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCustomVpsDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, VpnProtocol, String) -> Unit
) {
    var country by rememberSaveable { mutableStateOf("Singapore") }
    var city by rememberSaveable { mutableStateOf("My Ubuntu WireGuard VPS") }
    var ipAddress by rememberSaveable { mutableStateOf("167.71.204.58") }
    var port by rememberSaveable { mutableStateOf("51820") }
    var selectedProtocol by remember { mutableStateOf(VpnProtocol.WIREGUARD) }
    var wgPubKey by rememberSaveable { mutableStateOf("wgServerPubKeyCurve25519Base64Key=") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateCardSurface,
        titleContentColor = TextPrimaryWhite,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = CyberEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connect Your Own VPS Server", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter your Ubuntu WireGuard or OpenVPN VPS credentials to add it to N VPN:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondarySlate
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country (e.g. Singapore, Bangladesh)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Node Label / City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ipAddress,
                        onValueChange = { ipAddress = it },
                        label = { Text("VPS IP Address") },
                        singleLine = true,
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Port") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VpnProtocol.entries.take(2).forEach { proto ->
                        FilterChip(
                            selected = selectedProtocol == proto,
                            onClick = {
                                selectedProtocol = proto
                                port = proto.defaultPort.toString()
                            },
                            label = { Text(proto.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = wgPubKey,
                    onValueChange = { wgPubKey = it },
                    label = { Text("WireGuard Server PublicKey") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ipAddress.isNotBlank()) {
                        onSave(country, city, ipAddress, port, selectedProtocol, wgPubKey)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberEmerald,
                    contentColor = ObsidianBackground
                )
            ) {
                Text("Save & Connect VPS")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondarySlate)
            }
        }
    )
}
