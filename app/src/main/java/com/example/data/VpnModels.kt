package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VpnProtocol(
    val id: String,
    val displayName: String,
    val defaultPort: Int,
    val encryptionSpec: String,
    val badge: String,
    val description: String
) {
    WIREGUARD(
        id = "WIREGUARD",
        displayName = "WireGuard®",
        defaultPort = 51820,
        encryptionSpec = "ChaCha20-Poly1305 · Curve25519",
        badge = "ULTRA FAST",
        description = "Next-gen kernel-grade UDP tunnel with instant handshake and minimal battery impact."
    ),
    OPENVPN_UDP(
        id = "OPENVPN_UDP",
        displayName = "OpenVPN (UDP)",
        defaultPort = 1194,
        encryptionSpec = "AES-256-GCM · TLS 1.3 · SHA-512",
        badge = "BALANCED",
        description = "Battle-tested open-source TLS tunnel optimized for low-latency streaming and gaming."
    ),
    OPENVPN_TCP(
        id = "OPENVPN_TCP",
        displayName = "OpenVPN (TCP 443)",
        defaultPort = 443,
        encryptionSpec = "AES-256-GCM · Stealth HTTPS Port 443",
        badge = "STEALTH BYPASS",
        description = "Disguises VPN traffic over standard HTTPS port 443 to bypass strict ISP & campus firewalls."
    ),
    IKEV2(
        id = "IKEV2",
        displayName = "IKEv2 / IPsec",
        defaultPort = 500,
        encryptionSpec = "AES-256-CBC · MOBIKE Seamless Handover",
        badge = "MOBILE STABLE",
        description = "Automatically switches between Wi-Fi and 5G/LTE cellular networks without dropping tunnel."
    );

    companion object {
        fun fromId(id: String): VpnProtocol = entries.find { it.id == id } ?: WIREGUARD
    }
}

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class ServerFilterType(val label: String) {
    ALL("All Nodes"),
    SMART("Smart Optimal"),
    FREE("Free Tier"),
    VIP("VIP 10Gbps"),
    CUSTOM("My VPS"),
    FAVORITES("Saved")
}

@Entity(tableName = "vpn_servers")
data class VpnServerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val countryName: String,
    val cityName: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val virtualTunnelIp: String,
    val hostname: String,
    val port: Int = 51820,
    val protocolId: String = VpnProtocol.WIREGUARD.id,
    val pingMs: Int,
    val loadPercent: Int,
    val downloadCapacityMbps: Int = 1000,
    val isVip: Boolean = false,
    val isSmartRecommended: Boolean = false,
    val isFavorite: Boolean = false,
    val isCustomVps: Boolean = false,
    val wireGuardPublicKey: String = "nVpnWgPubKey9xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF="
)

@Entity(tableName = "connection_logs")
data class ConnectionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverName: String,
    val flagEmoji: String,
    val protocolName: String,
    val virtualIp: String,
    val durationSeconds: Long,
    val downloadedBytes: Long,
    val uploadedBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)

data class SpeedSample(
    val timestampMs: Long,
    val downloadBps: Long,
    val uploadBps: Long
)

data class PublicIpInfo(
    val ip: String,
    val isp: String,
    val country: String,
    val city: String,
    val isLiveNetwork: Boolean
)
