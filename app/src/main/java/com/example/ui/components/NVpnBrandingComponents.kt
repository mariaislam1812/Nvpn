package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SpeedSample
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
import com.example.vpn.TrafficSpeedMonitor
import kotlin.math.max

/**
 * Custom Vector/Canvas Logo for N VPN:
 * Features a stylized 'N' inside a Cyber Shield & Lock silhouette with aerodynamic speed streaks.
 */
@Composable
fun NVpnShieldLogo(
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    isConnected: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val primaryColor = if (isConnected) CyberEmerald else ElectricCyan
    val secondaryColor = if (isConnected) ElectricCyan else CyberEmerald

    Canvas(
        modifier = modifier
            .size(size)
            .testTag("nvpn_shield_logo")
    ) {
        val w = this.size.width
        val h = this.size.height

        // Subtle ambient halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.28f * glowPulse),
                    Color.Transparent
                )
            ),
            radius = w * 0.54f,
            center = Offset(w / 2f, h / 2f)
        )

        // Lock Shackle Arch at top of Shield
        val shackleWidth = w * 0.34f
        val shackleLeft = (w - shackleWidth) / 2f
        drawRoundRect(
            color = secondaryColor.copy(alpha = 0.85f),
            topLeft = Offset(shackleLeft, h * 0.05f),
            size = Size(shackleWidth, h * 0.26f),
            cornerRadius = CornerRadius(w * 0.17f, w * 0.17f),
            style = Stroke(width = w * 0.065f)
        )

        // Outer Cyber Shield Path
        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            lineTo(w * 0.88f, h * 0.26f)
            lineTo(w * 0.84f, h * 0.62f)
            quadraticTo(w * 0.78f, h * 0.84f, w * 0.50f, h * 0.95f)
            quadraticTo(w * 0.22f, h * 0.84f, w * 0.16f, h * 0.62f)
            lineTo(w * 0.12f, h * 0.26f)
            close()
        }

        // Fill Shield with Deep Obsidian Gradient
        drawPath(
            path = shieldPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0F1D36),
                    ObsidianBackground
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        // Shield Glowing Border
        drawPath(
            path = shieldPath,
            brush = Brush.linearGradient(
                colors = listOf(CyberEmerald, ElectricCyan)
            ),
            style = Stroke(
                width = w * 0.055f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Speed Motion Streaks on Left & Right
        drawLine(
            color = ElectricCyan.copy(alpha = 0.75f),
            start = Offset(w * 0.04f, h * 0.44f),
            end = Offset(w * 0.22f, h * 0.44f),
            strokeWidth = w * 0.04f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = CyberEmerald.copy(alpha = 0.75f),
            start = Offset(w * 0.78f, h * 0.64f),
            end = Offset(w * 0.96f, h * 0.64f),
            strokeWidth = w * 0.04f,
            cap = StrokeCap.Round
        )

        // Stylized High-Speed 'N' Monogram inside the Shield
        val nPath = Path().apply {
            moveTo(w * 0.34f, h * 0.71f)
            lineTo(w * 0.34f, h * 0.36f)
            lineTo(w * 0.66f, h * 0.71f)
            lineTo(w * 0.66f, h * 0.36f)
        }

        drawPath(
            path = nPath,
            brush = Brush.linearGradient(
                colors = listOf(TextPrimaryWhite, CyberEmerald, ElectricCyan)
            ),
            style = Stroke(
                width = w * 0.09f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Brand Header displaying the N VPN Logo, "N VPN" Title beside it,
 * and the mandatory "Fast. Safe. Unlimited." tagline directly underneath.
 */
@Composable
fun NVpnBrandHeader(
    isConnected: Boolean,
    hasVipAccess: Boolean,
    activeProtocolBadge: String,
    onVipBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("nvpn_brand_header"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NVpnShieldLogo(
                size = 50.dp,
                isConnected = isConnected
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextPrimaryWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = CyberEmerald.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = activeProtocolBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = R.string.app_tagline),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 0.8.sp
                    ),
                    color = ElectricCyan,
                    modifier = Modifier.testTag("nvpn_tagline_text")
                )
            }
        }

        // VIP / Pro Status Pill
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onVipBadgeClick)
                .testTag("header_vip_button"),
            color = if (hasVipAccess) VipGold.copy(alpha = 0.18f) else SlateCardElevated,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                1.dp,
                if (hasVipAccess) VipGold else GlassBorderColor
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = "VIP Access",
                    tint = VipGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (hasVipAccess) "VIP PRO" else "GET VIP",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hasVipAccess) VipGold else TextPrimaryWhite
                )
            }
        }
    }
}

/**
 * Real-Time Speed Meter & Dual-Stream Throughput Graph Card
 */
@Composable
fun RealTimeSpeedMeterCard(
    downloadBps: Long,
    uploadBps: Long,
    sessionDownloadBytes: Long,
    sessionUploadBytes: Long,
    speedHistory: List<SpeedSample>,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val (downVal, downUnit) = TrafficSpeedMonitor.formatSpeed(downloadBps)
    val (upVal, upUnit) = TrafficSpeedMonitor.formatSpeed(uploadBps)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_speed_meter_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
        border = BorderStroke(1.dp, GlassBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Download Speed",
                            tint = CyberEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DOWNLOAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondarySlate
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = downVal,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CyberEmerald
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = downUnit,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondarySlate,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "Total: ${TrafficSpeedMonitor.formatTotalBytes(sessionDownloadBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedSlate
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(44.dp)
                        .background(GlassBorderColor)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upload Speed",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "UPLOAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondarySlate
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = upVal,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = upUnit,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondarySlate,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "Total: ${TrafficSpeedMonitor.formatTotalBytes(sessionUploadBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedSlate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-time Dual-Line Throughput Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianBackground.copy(alpha = 0.7f))
                    .border(1.dp, GlassBorderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val samples = speedHistory.ifEmpty { listOf(SpeedSample(0, 0, 0)) }
                    val maxSpeed = max(
                        samples.maxOfOrNull { max(it.downloadBps, it.uploadBps) } ?: 100_000L,
                        200_000L
                    ).toFloat()

                    // Horizontal subtle grid lines
                    for (i in 1..2) {
                        val y = height * (i / 3f)
                        drawLine(
                            color = GlassBorderColor.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (samples.size > 1) {
                        val stepX = width / (samples.size - 1).toFloat()
                        val downPath = Path()
                        val downFillPath = Path()
                        val upPath = Path()

                        samples.forEachIndexed { index, sample ->
                            val x = index * stepX
                            val downRatio = (sample.downloadBps.toFloat() / maxSpeed).coerceIn(0.04f, 0.95f)
                            val upRatio = (sample.uploadBps.toFloat() / maxSpeed).coerceIn(0.02f, 0.92f)
                            val downY = height - (downRatio * height)
                            val upY = height - (upRatio * height)

                            if (index == 0) {
                                downPath.moveTo(x, downY)
                                downFillPath.moveTo(x, height)
                                downFillPath.lineTo(x, downY)
                                upPath.moveTo(x, upY)
                            } else {
                                downPath.lineTo(x, downY)
                                downFillPath.lineTo(x, downY)
                                upPath.lineTo(x, upY)
                            }
                        }

                        downFillPath.lineTo(width, height)
                        downFillPath.close()

                        drawPath(
                            path = downFillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CyberEmerald.copy(alpha = if (isConnected) 0.26f else 0.08f),
                                    Color.Transparent
                                )
                            )
                        )

                        drawPath(
                            path = downPath,
                            color = CyberEmerald,
                            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        drawPath(
                            path = upPath,
                            color = ElectricCyan.copy(alpha = 0.85f),
                            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) CyberEmerald else TextMutedSlate)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isConnected) "LIVE STREAM TELEMETRY" else "NIC IDLE MONITOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextSecondarySlate
                    )
                }
            }
        }
    }
}

/**
 * Mandatory Brand Footer:
 * "Copyright by nazmul 2026"
 * "Contact: nazmulhasansojib51@Gmail.com"
 */
@Composable
fun NVpnCopyrightFooter(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contactEmail = "nazmulhasansojib51@Gmail.com"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nvpn_copyright_footer"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SlateCardSurface.copy(alpha = 0.85f)
        ),
        border = BorderStroke(1.dp, GlassBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CyberEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.footer_copyright),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimaryWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.footer_contact),
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("N VPN Contact", contactEmail))
                        Toast.makeText(context, "Copied: $contactEmail", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_footer_email_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Contact Email",
                        tint = TextSecondarySlate,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$contactEmail")
                                putExtra(Intent.EXTRA_SUBJECT, "N VPN Inquiry")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, contactEmail, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("send_footer_email_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Contact",
                        tint = CyberEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
