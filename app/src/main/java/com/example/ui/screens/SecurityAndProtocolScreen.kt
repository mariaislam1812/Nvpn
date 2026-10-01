package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ConnectionLogEntity
import com.example.data.VpnProtocol
import com.example.ui.NVpnUiState
import com.example.ui.components.NVpnCopyrightFooter
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

@Composable
fun SecurityAndProtocolScreen(
    uiState: NVpnUiState,
    recentLogs: List<ConnectionLogEntity>,
    configPreviewText: String,
    onSelectProtocol: (VpnProtocol) -> Unit,
    onToggleKillSwitch: (Boolean) -> Unit,
    onSimulateKillSwitchDrop: () -> Unit,
    onToggleNoLogsMode: (Boolean) -> Unit,
    onToggleSplitTunneling: (Boolean) -> Unit,
    onSelectDnsServer: (String) -> Unit,
    onUnlockVipPlan: (String) -> Unit,
    onWatchRewardedAdPass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfigPreviewDialog by rememberSaveable { mutableStateOf(false) }

    val dnsOptions = listOf(
        "1.1.1.1 (Cloudflare Zero-Log)",
        "9.9.9.9 (Quad9 Malware Shield)",
        "8.8.8.8 (Google Anycast DNS)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("security_protocol_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Tunnel Protocol Selection (WireGuard / OpenVPN / IKEv2)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberEmerald
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VPN Tunnel Protocol",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        OutlinedButton(
                            onClick = { showConfigPreviewDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("inspect_tunnel_config_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "View .conf",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    VpnProtocol.entries.forEach { protocol ->
                        val isSelected = uiState.selectedProtocol == protocol
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectProtocol(protocol) }
                                .testTag("protocol_option_${protocol.id.lowercase()}"),
                            color = if (isSelected) SlateCardElevated else ObsidianBackground.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CyberEmerald else GlassBorderColor
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) {
                                        Icons.Default.RadioButtonChecked
                                    } else {
                                        Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) CyberEmerald else TextMutedSlate
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = protocol.displayName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = TextPrimaryWhite
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = ElectricCyan.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(5.dp)
                                        ) {
                                            Text(
                                                text = protocol.badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ElectricCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = protocol.encryptionSpec,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald
                                    )
                                    Text(
                                        text = protocol.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondarySlate
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Kill Switch & Leak Protection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(
                    1.dp,
                    if (uiState.isKillSwitchTriggeredBlock) AlertCrimson else GlassBorderColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (uiState.isKillSwitchEnabled) CyberEmerald else TextMutedSlate
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.kill_switch_title),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryWhite
                                )
                                Text(
                                    text = stringResource(id = R.string.kill_switch_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondarySlate
                                )
                            }
                        }
                        Switch(
                            checked = uiState.isKillSwitchEnabled,
                            onCheckedChange = onToggleKillSwitch,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBackground,
                                checkedTrackColor = CyberEmerald
                            ),
                            modifier = Modifier.testTag("kill_switch_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onSimulateKillSwitchDrop,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AlertCrimson.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCrimson),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_kill_switch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GppBad,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Kill Switch Emergency Traffic Lock",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Strict No-Logs RAM-Disk Policy Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.no_logs_title),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryWhite
                                )
                                Text(
                                    text = stringResource(id = R.string.no_logs_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondarySlate
                                )
                            }
                        }
                        Switch(
                            checked = uiState.isNoLogsRamModeEnabled,
                            onCheckedChange = onToggleNoLogsMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBackground,
                                checkedTrackColor = ElectricCyan
                            ),
                            modifier = Modifier.testTag("no_logs_mode_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Split Tunneling Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Split Tunneling (LAN & Local Banking Bypass)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryWhite
                            )
                            Text(
                                text = "Allow trusted local apps to connect directly while encrypting all other traffic",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondarySlate
                            )
                        }
                        Switch(
                            checked = uiState.isSplitTunnelingEnabled,
                            onCheckedChange = onToggleSplitTunneling,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBackground,
                                checkedTrackColor = CyberEmerald
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // DNS Resolver Selection
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Encrypted DNS-over-HTTPS (DoH) Resolver",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        dnsOptions.forEach { dns ->
                            val selected = uiState.selectedDnsServer == dns
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectDnsServer(dns) },
                                label = { Text(dns, style = MaterialTheme.typography.labelMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberEmerald.copy(alpha = 0.16f),
                                    selectedLabelColor = CyberEmerald,
                                    containerColor = ObsidianBackground
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // 3. VIP Subscription (IAP) & Google AdMob Monetization Center
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vip_subscription_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.5.dp, VipGold.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = VipGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "N VPN VIP Subscription & AdMob",
                                style = MaterialTheme.typography.titleLarge,
                                color = VipGold
                            )
                            Text(
                                text = "Current Plan: ${uiState.activeVipPlanName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimaryWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "• Unlock all 25Gbps VIP Multi-Hop Servers (US, Japan, Switzerland, India)\n• 100% Ad-Free Experience (Removes Banner & Interstitial Ads)\n• Up to 10 Simultaneous Devices (Android & iOS)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondarySlate
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onUnlockVipPlan("VIP Monthly ($4.99/mo)") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SlateCardSurface,
                                contentColor = VipGold
                            ),
                            border = BorderStroke(1.dp, VipGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("vip_monthly_button")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MONTHLY", style = MaterialTheme.typography.labelSmall)
                                Text("$4.99 / mo", style = MaterialTheme.typography.titleSmall)
                            }
                        }

                        Button(
                            onClick = { onUnlockVipPlan("VIP Annual Pro ($29.99/yr)") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VipGold,
                                contentColor = ObsidianBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("vip_annual_button")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ANNUAL (SAVE 50%)", style = MaterialTheme.typography.labelSmall)
                                Text("$29.99 / yr", style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onWatchRewardedAdPass,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberEmerald),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Free Tier: Watch AdMob Rewarded Ad (+2 Hours VIP Access)")
                    }
                }
            }
        }

        // 4.Mandatory Copyright & Contact Footer
        item {
            NVpnCopyrightFooter()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showConfigPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showConfigPreviewDialog = false },
            containerColor = SlateCardSurface,
            titleContentColor = TextPrimaryWhite,
            title = {
                Text(
                    text = "${uiState.selectedProtocol.displayName} Tunnel Profile",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Surface(
                    color = ObsidianBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = configPreviewText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = CyberEmerald,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("N VPN Config", configPreviewText))
                        Toast.makeText(context, "Tunnel config copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showConfigPreviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberEmerald,
                        contentColor = ObsidianBackground
                    )
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Config")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigPreviewDialog = false }) {
                    Text("Close", color = TextSecondarySlate)
                }
            }
        )
    }
}
