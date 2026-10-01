package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VpnServerEntity::class, ConnectionLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NVpnDatabase : RoomDatabase() {
    abstract fun vpnDao(): VpnDao

    companion object {
        @Volatile
        private var INSTANCE: NVpnDatabase? = null

        fun getInstance(context: Context): NVpnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NVpnDatabase::class.java,
                    "nvpn_core.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun defaultGlobalServers(): List<VpnServerEntity> = listOf(
            VpnServerEntity(
                countryName = "Singapore",
                cityName = "Marina Bay Core #1",
                countryCode = "SG",
                flagEmoji = "🇸🇬",
                ipAddress = "103.253.41.18",
                virtualTunnelIp = "10.66.12.104",
                hostname = "sg-core01.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 24,
                loadPercent = 28,
                downloadCapacityMbps = 10000,
                isVip = false,
                isSmartRecommended = true,
                isFavorite = true,
                wireGuardPublicKey = "sgNVpn88xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF99aBc="
            ),
            VpnServerEntity(
                countryName = "Bangladesh",
                cityName = "Dhaka BDIX Ultra Edge",
                countryCode = "BD",
                flagEmoji = "🇧🇩",
                ipAddress = "103.108.140.22",
                virtualTunnelIp = "10.66.88.19",
                hostname = "bd-dhaka01.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 14,
                loadPercent = 34,
                downloadCapacityMbps = 10000,
                isVip = false,
                isSmartRecommended = true,
                isFavorite = true,
                wireGuardPublicKey = "bdNVpn51xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF77zXy="
            ),
            VpnServerEntity(
                countryName = "Germany",
                cityName = "Frankfurt DE-CIX #4",
                countryCode = "DE",
                flagEmoji = "🇩🇪",
                ipAddress = "185.220.101.45",
                virtualTunnelIp = "10.66.40.82",
                hostname = "de-fra04.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 46,
                loadPercent = 22,
                downloadCapacityMbps = 10000,
                isVip = false,
                isSmartRecommended = true,
                isFavorite = false,
                wireGuardPublicKey = "deNVpn44xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF12kLm="
            ),
            VpnServerEntity(
                countryName = "United States",
                cityName = "New York Financial VIP",
                countryCode = "US",
                flagEmoji = "🇺🇸",
                ipAddress = "198.51.100.89",
                virtualTunnelIp = "10.66.101.55",
                hostname = "us-nyc-vip1.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 68,
                loadPercent = 18,
                downloadCapacityMbps = 25000,
                isVip = true,
                isSmartRecommended = false,
                isFavorite = true,
                wireGuardPublicKey = "usNVpn99xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF34pQr="
            ),
            VpnServerEntity(
                countryName = "Japan",
                cityName = "Tokyo Shibuya 10Gbps",
                countryCode = "JP",
                flagEmoji = "🇯🇵",
                ipAddress = "139.162.72.114",
                virtualTunnelIp = "10.66.54.201",
                hostname = "jp-tyo-vip2.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 39,
                loadPercent = 19,
                downloadCapacityMbps = 25000,
                isVip = true,
                isSmartRecommended = true,
                isFavorite = false,
                wireGuardPublicKey = "jpNVpn77xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF56tUv="
            ),
            VpnServerEntity(
                countryName = "United Kingdom",
                cityName = "London Docklands Hub",
                countryCode = "GB",
                flagEmoji = "🇬🇧",
                ipAddress = "178.62.19.204",
                virtualTunnelIp = "10.66.44.12",
                hostname = "uk-lon02.nvpn.net",
                port = 1194,
                protocolId = VpnProtocol.OPENVPN_UDP.id,
                pingMs = 54,
                loadPercent = 41,
                downloadCapacityMbps = 10000,
                isVip = false,
                isSmartRecommended = false,
                isFavorite = false,
                wireGuardPublicKey = "ukNVpn22xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF89wXz="
            ),
            VpnServerEntity(
                countryName = "Switzerland",
                cityName = "Zurich Bunker Multi-Hop",
                countryCode = "CH",
                flagEmoji = "🇨🇭",
                ipAddress = "185.159.157.11",
                virtualTunnelIp = "10.66.77.9",
                hostname = "ch-zrh-bunker.nvpn.net",
                port = 443,
                protocolId = VpnProtocol.OPENVPN_TCP.id,
                pingMs = 52,
                loadPercent = 14,
                downloadCapacityMbps = 25000,
                isVip = true,
                isSmartRecommended = false,
                isFavorite = true,
                wireGuardPublicKey = "chNVpn11xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF90cVb="
            ),
            VpnServerEntity(
                countryName = "Netherlands",
                cityName = "Amsterdam P2P Core",
                countryCode = "NL",
                flagEmoji = "🇳🇱",
                ipAddress = "95.179.136.42",
                virtualTunnelIp = "10.66.31.78",
                hostname = "nl-ams01.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 49,
                loadPercent = 37,
                downloadCapacityMbps = 10000,
                isVip = false,
                isSmartRecommended = false,
                isFavorite = false,
                wireGuardPublicKey = "nlNVpn33xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF45nMq="
            ),
            VpnServerEntity(
                countryName = "India",
                cityName = "Mumbai Low-Ping Gaming",
                countryCode = "IN",
                flagEmoji = "🇮🇳",
                ipAddress = "139.59.24.188",
                virtualTunnelIp = "10.66.91.33",
                hostname = "in-bom-vip.nvpn.net",
                port = 51820,
                protocolId = VpnProtocol.WIREGUARD.id,
                pingMs = 28,
                loadPercent = 25,
                downloadCapacityMbps = 25000,
                isVip = true,
                isSmartRecommended = true,
                isFavorite = false,
                wireGuardPublicKey = "inNVpn66xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF67jKl="
            ),
            VpnServerEntity(
                countryName = "Australia",
                cityName = "Sydney Southern Cross",
                countryCode = "AU",
                flagEmoji = "🇦🇺",
                ipAddress = "45.76.112.90",
                virtualTunnelIp = "10.66.61.142",
                hostname = "au-syd01.nvpn.net",
                port = 500,
                protocolId = VpnProtocol.IKEV2.id,
                pingMs = 88,
                loadPercent = 16,
                downloadCapacityMbps = 15000,
                isVip = true,
                isSmartRecommended = false,
                isFavorite = false,
                wireGuardPublicKey = "auNVpn00xK2mQp8vL4zR7tY1wE5uI3oP6aS0dF23hGf="
            )
        )
    }
}
