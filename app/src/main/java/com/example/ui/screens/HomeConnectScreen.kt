package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.VpnConnectionState
import com.example.ui.NVpnUiState
import com.example.ui.components.NVpnCopyrightFooter
import com.example.ui.components.RealTimeSpeedMeterCard
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.ConnectedGreen
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
fun HomeConnectScreen(
    uiState: NVpnUiState,
    onToggleConnect: () -> Unit,
    onSmartConnectClick: () -> Unit,
    onOpenServerList: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onWatchRewardedAdPass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = uiState.connectionState == VpnConnectionState.CONNECTED
    val isTransitioning = uiState.connectionState == VpnConnectionState.CONNECTING ||
        uiState.connectionState == VpnConnectionState.DISCONNECTING
    val activeServer = uiState.selectedServer

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_connect_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Notification Banner if present
        item {
            AnimatedVisibility(visible = uiState.statusBannerMessage != null) {
                Surface(
                    color = if (uiState.isKillSwitchTriggeredBlock) {
                        AlertCrimson.copy(alpha = 0.18f)
                    } else {
                        SlateCardElevated
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.isKillSwitchTriggeredBlock) AlertCrimson else CyberEmerald.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.isKillSwitchTriggeredBlock) Icons.Default.GppBad else Icons.Default.Security,
                            contentDescription = null,
                            tint = if (uiState.isKillSwitchTriggeredBlock) AlertCrimson else CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.statusBannerMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryWhite,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Hero Center Card: Global Cyber Map Backdrop + One-Tap Connect Power Orb + IP & Timer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(
                    1.5.dp,
                    when {
                        isConnected -> CyberEmerald.copy(alpha = 0.65f)
                        uiState.isKillSwitchTriggeredBlock -> AlertCrimson
                        else -> GlassBorderColor
                    }
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Subtle Global Network Map Hero Art Background
                    Image(
                        painter = painterResource(id = R.drawable.img_cyber_globe_bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = if (isConnected) 0.30f else 0.16f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(370.dp)
                    )

                    // Gradient Vignette Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(370.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ObsidianBackground.copy(alpha = 0.55f),
                                        ObsidianBackground.copy(alpha = 0.82f),
                                        SlateCardSurface
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Status Pill & Kill Switch Indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadgePill(
                                connectionState = uiState.connectionState,
                                isKillSwitchBlocked = uiState.isKillSwitchTriggeredBlock
                            )

                            Surface(
                                onClick = onOpenSecuritySettings,
                                color = if (uiState.isKillSwitchEnabled) {
                                    CyberEmerald.copy(alpha = 0.12f)
                                } else {
                                    ObsidianBackground.copy(alpha = 0.6f)
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (uiState.isKillSwitchEnabled) CyberEmerald.copy(alpha = 0.4f) else GlassBorderColor
                                ),
                                modifier = Modifier.testTag("kill_switch_status_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Kill Switch Status",
                                        tint = if (uiState.isKillSwitchEnabled) CyberEmerald else TextMutedSlate,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (uiState.isKillSwitchEnabled) "KILL SWITCH ON" else "KILL SWITCH OFF",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (uiState.isKillSwitchEnabled) CyberEmerald else TextSecondarySlate
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Session Duration Timer
                        Text(
                            text = formatDurationHms(uiState.connectedDurationSeconds),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = if (isConnected) CyberEmerald else TextPrimaryWhite,
                            modifier = Modifier.testTag("session_timer_text")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Center One-Tap Connect Power Button
                        OneTapConnectOrb(
                            connectionState = uiState.connectionState,
                            onClick = onToggleConnect
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = when (uiState.connectionState) {
                                VpnConnectionState.CONNECTED -> stringResource(id = R.string.tap_to_disconnect)
                                VpnConnectionState.CONNECTING -> stringResource(id = R.string.status_connecting)
                                VpnConnectionState.DISCONNECTING -> stringResource(id = R.string.status_disconnecting)
                                VpnConnectionState.DISCONNECTED -> stringResource(id = R.string.tap_to_connect)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isConnected) CyberEmerald else ElectricCyan
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // IP Address & Tunnel Info Row
                        Surface(
                            color = ObsidianBackground.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, GlassBorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ip_telemetry_banner")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isConnected) "MASKED PUBLIC IP" else "YOUR EXPOSED ISP IP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondarySlate
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isConnected && activeServer != null) {
                                            activeServer.ipAddress
                                        } else {
                                            uiState.realPublicIpInfo.ip
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isConnected) CyberEmerald else WarningAmber,
                                        modifier = Modifier.testTag("displayed_ip_text")
                                    )
                                    Text(
                                        text = if (isConnected && activeServer != null) {
                                            "TUN IP: ${activeServer.virtualTunnelIp} · Zero-Log Node"
                                        } else {
                                            "ISP: ${uiState.realPublicIpInfo.isp}"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMutedSlate,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "PROTOCOL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondarySlate
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.selectedProtocol.displayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "Port ${uiState.selectedProtocol.defaultPort}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMutedSlate
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Server & Smart Connect Action Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenServerList)
                            .padding(vertical = 4.dp)
                            .testTag("selected_server_selector_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SlateCardElevated)
                                    .border(1.dp, GlassBorderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeServer?.flagEmoji ?: "🌐",
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeServer?.countryName ?: "Select Server",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimaryWhite
                                    )
                                    if (uiState.isSmartConnectActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = ElectricCyan.copy(alpha = 0.16f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "SMART",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ElectricCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${activeServer?.cityName ?: "Auto Optimal"} · Load ${activeServer?.loadPercent ?: 20}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondarySlate
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                val ping = activeServer?.pingMs ?: 24
                                val pingColor = when {
                                    ping < 45 -> CyberEmerald
                                    ping < 90 -> ElectricCyan
                                    else -> WarningAmber
                                }
                                Text(
                                    text = "$ping ms",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = pingColor
                                )
                                Text(
                                    text = "CHANGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedSlate
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Change Server",
                                tint = TextSecondarySlate
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Smart Connect 1-Tap Button
                    Button(
                        onClick = onSmartConnectClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SlateCardElevated,
                            contentColor = CyberEmerald
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("smart_connect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.smart_connect),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        // Real-Time Download & Upload Speed Meter + Live Graph
        item {
            RealTimeSpeedMeterCard(
                downloadBps = uiState.currentDownloadBps,
                uploadBps = uiState.currentUploadBps,
                sessionDownloadBytes = uiState.sessionDownloadedBytes,
                sessionUploadBytes = uiState.sessionUploadedBytes,
                speedHistory = uiState.speedHistory,
                isConnected = isConnected
            )
        }

        // Monetization Strip: Google AdMob Free Tier & Rewarded VIP Unlock
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monetization_admob_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, VipGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = VipGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = if (uiState.hasVipAccess) "VIP ACTIVE" else "ADMOB REWARDED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VipGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.rewardedPassRemainingSeconds > 0) {
                                    "VIP Pass: ${uiState.rewardedPassRemainingSeconds / 60}m left"
                                } else {
                                    uiState.activeVipPlanName
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimaryWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.hasVipAccess) {
                                "All 25Gbps VIP Multi-Hop Servers & Zero-Ads Unlocked."
                            } else {
                                "Watch a short Rewarded Video Ad to unlock all VIP 25Gbps Servers for 2 Hours!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondarySlate
                        )
                    }

                    if (!uiState.isVipSubscribed) {
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = onWatchRewardedAdPass,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VipGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VipGold),
                            modifier = Modifier.testTag("watch_rewarded_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+2h VIP",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        // Mandatory Copyright & Contact Footer on Home Screen
        item {
            NVpnCopyrightFooter()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StatusBadgePill(
    connectionState: VpnConnectionState,
    isKillSwitchBlocked: Boolean
) {
    val (label, color, icon) = when {
        isKillSwitchBlocked -> Triple("KILL SWITCH LOCKED", AlertCrimson, Icons.Default.GppBad)
        connectionState == VpnConnectionState.CONNECTED -> Triple(
            stringResource(id = R.string.status_connected),
            CyberEmerald,
            Icons.Default.GppGood
        )
        connectionState == VpnConnectionState.CONNECTING -> Triple(
            stringResource(id = R.string.status_connecting),
            ElectricCyan,
            Icons.Default.Speed
        )
        connectionState == VpnConnectionState.DISCONNECTING -> Triple(
            stringResource(id = R.string.status_disconnecting),
            WarningAmber,
            Icons.Default.Speed
        )
        else -> Triple(
            stringResource(id = R.string.status_disconnected),
            WarningAmber,
            Icons.Default.Public
        )
    }

    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.testTag("connection_status_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun OneTapConnectOrb(
    connectionState: VpnConnectionState,
    onClick: () -> Unit
) {
    val isConnected = connectionState == VpnConnectionState.CONNECTED
    val isConnecting = connectionState == VpnConnectionState.CONNECTING ||
        connectionState == VpnConnectionState.DISCONNECTING

    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_scale"
    )
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    val orbColor by animateColorAsState(
        targetValue = when (connectionState) {
            VpnConnectionState.CONNECTED -> CyberEmerald
            VpnConnectionState.CONNECTING, VpnConnectionState.DISCONNECTING -> ElectricCyan
            VpnConnectionState.DISCONNECTED -> Color(0xFF334155)
        },
        label = "orb_color"
    )

    Box(
        modifier = Modifier
            .size(164.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("one_tap_connect_button"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val maxRadius = size.minDimension / 2f

            // Outer pulsing halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        orbColor.copy(alpha = if (isConnected) 0.34f else 0.16f),
                        Color.Transparent
                    )
                ),
                radius = maxRadius * radarScale,
                center = center
            )

            // Outer technical ring
            drawCircle(
                color = orbColor.copy(alpha = 0.45f),
                radius = maxRadius * 0.86f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating radar arc when connecting or connected
            if (isConnecting || isConnected) {
                rotate(degrees = sweepAngle, pivot = center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyberEmerald,
                                ElectricCyan
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 240f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Core Power Button Surface
        Surface(
            modifier = Modifier.size(118.dp),
            shape = CircleShape,
            color = ObsidianBackground,
            border = BorderStroke(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = if (isConnected) {
                        listOf(CyberEmerald, ElectricCyan)
                    } else {
                        listOf(ElectricCyan.copy(alpha = 0.7f), GlassBorderColor)
                    }
                )
            ),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "One-Tap VPN Connect",
                    tint = if (isConnected) CyberEmerald else TextPrimaryWhite,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> "ON"
                        VpnConnectionState.CONNECTING -> "SYNC"
                        VpnConnectionState.DISCONNECTING -> "STOP"
                        VpnConnectionState.DISCONNECTED -> "START"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isConnected) CyberEmerald else TextSecondarySlate
                )
            }
        }
    }
}

private fun formatDurationHms(totalSeconds: Long): String {
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
