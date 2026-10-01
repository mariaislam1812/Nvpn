package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.NVpnCopyrightFooter
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.VipGold

data class BlueprintSection(
    val title: String,
    val subtitleBn: String,
    val icon: ImageVector,
    val guideBullets: List<String>,
    val codeTitle: String,
    val codeSnippet: String
)

@Composable
fun BlueprintAndVpsGuideScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTopicIndex by rememberSaveable { mutableIntStateOf(0) }

    val sections = listOf(
        BlueprintSection(
            title = "1. Architecture & Packages",
            subtitleBn = "প্রজেক্টের আর্কিটেকচার, ফোল্ডার স্ট্রাকচার এবং প্যাকেজ তালিকা (Android Native + Flutter / React Native)",
            icon = Icons.Default.Architecture,
            guideBullets = listOf(
                "• Clean Architecture (Presentation -> Domain -> Data -> Native VPN Engine Bridge)",
                "• Android Native: android.net.VpnService + com.wireguard.android:tunnel + Room DB + TrafficStats",
                "• Flutter Stack: wireguard_flutter (^0.1.3), openvpn_flutter (^1.3.2), flutter_riverpod, google_mobile_ads, in_app_purchase / purchases_flutter (RevenueCat)",
                "• React Native Stack: react-native-wireguard-vpn, react-native-google-mobile-ads, react-native-purchases"
            ),
            codeTitle = "Folder Structure & Dependencies (pubspec.yaml / build.gradle)",
            codeSnippet = """
                # N VPN Clean Architecture Structure
                lib/ (or app/src/main/java/com/nvpn/)
                ├── core/
                │   ├── theme/          # Dark Mode (#070B14) & Neon (#00F5D4)
                │   ├── security/       # AES-256-GCM & Curve25519 Key Generator
                │   └── kill_switch/    # VpnService.Builder.setBlocking(true)
                ├── data/
                │   ├── models/         # VpnServer, TunnelStats, VipPlan
                │   ├── local_db/       # Room / Hive (Server Cache, Favorites)
                │   └── api/            # Node Health & Dynamic WireGuard Peer API
                ├── domain/
                │   └── smart_connect/  # Ping + Server Load Optimal Scoring Engine
                └── presentation/
                    ├── home/           # One-Tap Orb, Speed Graph, IP Telemetry
                    ├── servers/        # Global Nodes + Custom VPS Importer
                    └── monetization/   # AdMob Rewarded + Play/AppStore VIP IAP
            """.trimIndent()
        ),
        BlueprintSection(
            title = "2. Core VPN & Kill Switch Code",
            subtitleBn = "মূল কানেক্টিভিটি কোড (WireGuard / OpenVPN টানেল, Smart Connect এবং Kill Switch)",
            icon = Icons.Default.Terminal,
            guideBullets = listOf(
                "• One-Tap Connect: Uses Android VpnService / iOS NEPacketTunnelProvider (NetworkExtension) to establish TUN0 virtual interface.",
                "• Smart Connect Score: Score = PingMs + (LoadPercent * 0.4). Automatically selects lowest-score node.",
                "• Kill Switch: Android Builder.setBlocking(true) + Always-On VPN lockdown blocks all sockets outside TUN0."
            ),
            codeTitle = "WireGuard + Kill Switch Core Controller Example",
            codeSnippet = """
                // 1. Android Native VpnService Tunnel + Kill Switch
                val builder = Builder()
                    .setSession("N VPN - Fast. Safe. Unlimited.")
                    .setMtu(1420)
                    .addAddress("10.66.12.104", 24)
                    .addDnsServer("1.1.1.1")
                    .addRoute("0.0.0.0", 0)
                if (killSwitchEnabled) {
                    builder.setBlocking(true) // Locks non-VPN traffic if tunnel drops
                }
                val tunInterface = builder.establish()

                // 2. Flutter WireGuard Bridge Example
                final wireguard = WireGuardFlutter.instance;
                await wireguard.initialize(interfaceName: 'wg_nvpn0');
                await wireguard.startVpn(
                  serverAddress: '${'$'}{server.ip}:51820',
                  wgQuickConfig: generatedWgConfig,
                  providerBundleIdentifier: 'com.nazmul.nvpn.WGExtension',
                );
            """.trimIndent()
        ),
        BlueprintSection(
            title = "3. Ubuntu VPS Server Setup",
            subtitleBn = "সার্ভার ম্যানেজমেন্ট: কীভাবে একটি Ubuntu VPS সার্ভার কনফিগার করবেন এবং অ্যাপে যুক্ত করবেন",
            icon = Icons.Default.Dns,
            guideBullets = listOf(
                "• Step 1: Deploy an Ubuntu 24.04 LTS VPS (DigitalOcean, Vultr, Hetzner, Linode, or AWS Lightsail) with 10Gbps port.",
                "• Step 2: Run the bash script below to install WireGuard, enable BBR TCP congestion control for high speed, and disable syslog/disk logging.",
                "• Step 3: Copy the Server IP, Port (51820), and PublicKey into N VPN's 'Servers -> + VPS' button!"
            ),
            codeTitle = "Ubuntu 24.04 WireGuard High-Speed No-Logs VPS Script",
            codeSnippet = """
                #!/bin/bash
                # N VPN — Automated Ubuntu 24.04 WireGuard + BBR Setup
                apt update && apt install -y wireguard iptables qrencode

                # 1. Enable Google BBR & IPv4 Forwarding for Maximum Speed
                cat <<EOF >> /etc/sysctl.conf
                net.ipv4.ip_forward=1
                net.core.default_qdisc=fq
                net.ipv4.tcp_congestion_control=bbr
                EOF
                sysctl -p

                # 2. Generate Server Curve25519 Keys in RAM
                umask 077
                wg genkey | tee /etc/wireguard/server_private.key | wg pubkey > /etc/wireguard/server_public.key

                # 3. Create /etc/wireguard/wg0.conf (Port 51820)
                cat <<EOF > /etc/wireguard/wg0.conf
                [Interface]
                Address = 10.8.0.1/24
                ListenPort = 51820
                PrivateKey = $(cat /etc/wireguard/server_private.key)
                PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
                PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -o eth0 -j MASQUERADE
                EOF

                systemctl enable --now wg-quick@wg0
                echo "N VPN Server PublicKey: $(cat /etc/wireguard/server_public.key)"
            """.trimIndent()
        ),
        BlueprintSection(
            title = "4. AdMob & VIP Subscription",
            subtitleBn = "মনিটাইজেশন: ফ্রি সার্ভারে Google AdMob (Banner + Rewarded) এবং VIP সার্ভারে In-App Purchase",
            icon = Icons.Default.MonetizationOn,
            guideBullets = listOf(
                "• Free Tier: Display a persistent 320x50 Adaptive Banner Ad on the Home screen and trigger a Rewarded Video Ad to grant +2 Hours of temporary VIP server access.",
                "• VIP Subscription: Integrate Google Play Billing / Apple StoreKit via RevenueCat (purchases_flutter / BillingClient) for Monthly ($4.99) & Yearly ($29.99) auto-renewable plans.",
                "• Server Access Control: Backend signs short-lived JWT tokens verifying VIP entitlement before issuing VIP WireGuard peer keys."
            ),
            codeTitle = "AdMob Rewarded Ad + VIP Entitlement Code",
            codeSnippet = """
                // Rewarded Ad to Unlock VIP Server for 2 Hours
                RewardedAd.load(
                  adUnitId: AdHelper.rewardedAdUnitId,
                  request: const AdRequest(),
                  rewardedAdLoadCallback: RewardedAdLoadCallback(
                    onAdLoaded: (ad) {
                      ad.show(onUserEarnedReward: (_, reward) {
                        vpnController.grantTemporaryVipAccess(Duration(hours: 2));
                      });
                    },
                    onAdFailedToLoad: (err) => debugPrint(err.message),
                  ),
                );

                // RevenueCat / Google Play Billing VIP Purchase
                CustomerInfo info = await Purchases.purchasePackage(vipAnnualPackage);
                if (info.entitlements.all["vip_pro"]?.isActive == true) {
                  vpnController.enableUnlimitedVipMode();
                }
            """.trimIndent()
        ),
        BlueprintSection(
            title = "5. Encryption & No-Logs Policy",
            subtitleBn = "সিকিউরিটি: ডাটা এনক্রিপশন (ChaCha20-Poly1305 / AES-256-GCM) এবং Strict No-Logs Policy",
            icon = Icons.Default.Security,
            guideBullets = listOf(
                "• Transport Encryption: WireGuard uses ChaCha20-Poly1305 with Curve25519 ECDH; OpenVPN uses AES-256-GCM with TLS 1.3 Perfect Forward Secrecy.",
                "• RAM-Disk (tmpfs) VPS Nodes: Mount /var/log to tmpfs and mask systemd-journald so zero connection timestamps, DNS queries, or client IPs ever touch a hard drive.",
                "• DNS Leak Prevention: Force all port 53 traffic through the VPN tunnel interface to 1.1.1.1 / 9.9.9.9."
            ),
            codeTitle = "Zero-Logs Server Hardening Commands",
            codeSnippet = """
                # Disable Persistent Logs on VPS for Strict No-Logs Compliance
                systemctl stop rsyslog systemd-journald
                systemctl disable rsyslog
                mount -t tmpfs -o size=64m tmpfs /var/log

                # Rotate Ephemeral WireGuard Session Keys Every 3 Minutes
                # (Handled automatically by WireGuard Noise_IKpsk2 handshake)
            """.trimIndent()
        )
    )

    val activeSection = sections[selectedTopicIndex]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("blueprint_vps_guide_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Intro Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "N VPN — Complete Technical Roadmap & VPS Guide",
                        style = MaterialTheme.typography.titleLarge,
                        color = CyberEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Android ও iOS প্ল্যাটফর্মের জন্য সম্পূর্ণ আর্কিটেকচার, কানেক্টিভিটি কোড, VPS সার্ভার সেটআপ এবং AdMob/VIP মনিটাইজেশন গাইডলাইন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryWhite
                    )
                }
            }
        }

        // Topic Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(sections) { index, section ->
                    val selected = selectedTopicIndex == index
                    FilterChip(
                        selected = selected,
                        onClick = { selectedTopicIndex = index },
                        label = {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberEmerald.copy(alpha = 0.2f),
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
                        modifier = Modifier.testTag("blueprint_topic_chip_$index")
                    )
                }
            }
        }

        // Selected Section Details & Copyable Code Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = activeSection.icon,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = activeSection.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimaryWhite
                            )
                            Text(
                                text = activeSection.subtitleBn,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    activeSection.guideBullets.forEach { bullet ->
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondarySlate,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Code Block with Copy Button
                    Surface(
                        color = ObsidianBackground,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GlassBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeSection.codeTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = VipGold
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        clipboard?.setPrimaryClip(
                                            ClipData.newPlainText(activeSection.codeTitle, activeSection.codeSnippet)
                                        )
                                        Toast.makeText(context, "Copied code snippet!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("copy_blueprint_code_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = CyberEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeSection.codeSnippet,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = CyberEmerald
                            )
                        }
                    }
                }
            }
        }

        // About Us & Brand Identity Card with Mandatory Footer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_us_section_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberEmerald
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About N VPN",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "N VPN — Fast. Safe. Unlimited.\nDesigned for next-generation privacy, high-speed streaming, low-ping gaming, and military-grade WireGuard® & OpenVPN encryption with a strict Zero-Logs Policy.\n\nGitHub Repository: github.com/mariaislam1812/Nvpn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondarySlate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NVpnCopyrightFooter()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
