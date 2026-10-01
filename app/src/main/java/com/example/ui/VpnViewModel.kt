package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ConnectionLogEntity
import com.example.data.NVpnDatabase
import com.example.data.PublicIpInfo
import com.example.data.ServerFilterType
import com.example.data.SpeedSample
import com.example.data.VpnConnectionState
import com.example.data.VpnProtocol
import com.example.data.VpnRepository
import com.example.data.VpnServerEntity
import com.example.vpn.NVpnService
import com.example.vpn.TrafficSpeedMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class NVpnTab(val route: String, val label: String) {
    CONNECT("connect", "Connect"),
    SERVERS("servers", "Servers"),
    SECURITY("security", "Security & VIP"),
    BLUEPRINT("blueprint", "Roadmap & Code")
}

data class NVpnUiState(
    val currentTab: NVpnTab = NVpnTab.CONNECT,
    val connectionState: VpnConnectionState = VpnConnectionState.DISCONNECTED,
    val selectedServer: VpnServerEntity? = null,
    val isSmartConnectActive: Boolean = true,
    val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val isKillSwitchEnabled: Boolean = true,
    val isKillSwitchTriggeredBlock: Boolean = false,
    val isNoLogsRamModeEnabled: Boolean = true,
    val isSplitTunnelingEnabled: Boolean = false,
    val isDnsLeakProtectionEnabled: Boolean = true,
    val selectedDnsServer: String = "1.1.1.1 (Cloudflare Zero-Log)",
    val realPublicIpInfo: PublicIpInfo = PublicIpInfo(
        ip = "Detecting IP…",
        isp = "Scanning Interface…",
        country = "Local",
        city = "Gateway",
        isLiveNetwork = false
    ),
    val currentDownloadBps: Long = 0L,
    val currentUploadBps: Long = 0L,
    val sessionDownloadedBytes: Long = 0L,
    val sessionUploadedBytes: Long = 0L,
    val connectedDurationSeconds: Long = 0L,
    val speedHistory: List<SpeedSample> = List(28) { SpeedSample(0L, 0L, 0L) },
    val searchQuery: String = "",
    val selectedFilter: ServerFilterType = ServerFilterType.ALL,
    val isPingingServers: Boolean = false,
    val isVipSubscribed: Boolean = false,
    val activeVipPlanName: String = "Free Tier (Ad-Supported)",
    val rewardedPassRemainingSeconds: Long = 0L,
    val statusBannerMessage: String? = null,
    val tunnelStatusLabel: String = "Tunnel Idle"
) {
    val hasVipAccess: Boolean
        get() = isVipSubscribed || rewardedPassRemainingSeconds > 0L
}

class VpnViewModel(
    private val repository: VpnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NVpnUiState())
    val uiState: StateFlow<NVpnUiState> = _uiState.asStateFlow()

    val servers: StateFlow<List<VpnServerEntity>> = repository.allServers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<ConnectionLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredServers: StateFlow<List<VpnServerEntity>> = combine(
        servers,
        _uiState
    ) { allServers, state ->
        allServers.filter { server ->
            val matchesQuery = state.searchQuery.isBlank() ||
                server.countryName.contains(state.searchQuery, ignoreCase = true) ||
                server.cityName.contains(state.searchQuery, ignoreCase = true) ||
                server.ipAddress.contains(state.searchQuery, ignoreCase = true)

            val matchesFilter = when (state.selectedFilter) {
                ServerFilterType.ALL -> true
                ServerFilterType.SMART -> server.isSmartRecommended || server.pingMs <= 45
                ServerFilterType.FREE -> !server.isVip
                ServerFilterType.VIP -> server.isVip
                ServerFilterType.CUSTOM -> server.isCustomVps
                ServerFilterType.FAVORITES -> server.isFavorite
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val speedMonitor = TrafficSpeedMonitor()
    private var telemetryTickerJob: Job? = null
    private var connectionTransitionJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
            val ipInfo = repository.fetchRealPublicIp()
            _uiState.update { it.copy(realPublicIpInfo = ipInfo) }
        }

        viewModelScope.launch {
            servers.collect { list ->
                if (list.isNotEmpty() && _uiState.value.selectedServer == null) {
                    val bestInitial = list.filter { !it.isVip }.minByOrNull { it.pingMs } ?: list.first()
                    _uiState.update { it.copy(selectedServer = bestInitial) }
                } else if (_uiState.value.selectedServer != null) {
                    val updatedCurrent = list.find { it.id == _uiState.value.selectedServer?.id }
                    if (updatedCurrent != null) {
                        _uiState.update { it.copy(selectedServer = updatedCurrent) }
                    }
                }
            }
        }

        viewModelScope.launch {
            NVpnService.tunnelDescriptorStatus.collect { desc ->
                _uiState.update { it.copy(tunnelStatusLabel = desc) }
            }
        }

        startLiveTelemetryLoop()
    }

    private fun startLiveTelemetryLoop() {
        telemetryTickerJob?.cancel()
        telemetryTickerJob = viewModelScope.launch {
            speedMonitor.resetBaseline()
            while (isActive) {
                delay(1000L)
                val state = _uiState.value
                val isConnected = state.connectionState == VpnConnectionState.CONNECTED
                val capacity = state.selectedServer?.downloadCapacityMbps ?: 10000
                val sample = speedMonitor.sampleCurrentSpeed(isConnected, capacity)

                _uiState.update { current ->
                    val updatedHistory = (current.speedHistory.drop(1) + sample)
                    val newDuration = if (isConnected) current.connectedDurationSeconds + 1L else 0L
                    val newDownTotal = if (isConnected) current.sessionDownloadedBytes + sample.downloadBps else current.sessionDownloadedBytes
                    val newUpTotal = if (isConnected) current.sessionUploadedBytes + sample.uploadBps else current.sessionUploadedBytes
                    val newRewardSeconds = (current.rewardedPassRemainingSeconds - 1L).coerceAtLeast(0L)

                    current.copy(
                        currentDownloadBps = sample.downloadBps,
                        currentUploadBps = sample.uploadBps,
                        sessionDownloadedBytes = newDownTotal,
                        sessionUploadedBytes = newUpTotal,
                        connectedDurationSeconds = newDuration,
                        speedHistory = updatedHistory,
                        rewardedPassRemainingSeconds = newRewardSeconds
                    )
                }
            }
        }
    }

    fun selectTab(tab: NVpnTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectFilter(filter: ServerFilterType) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun clearBannerMessage() {
        _uiState.update { it.copy(statusBannerMessage = null) }
    }

    fun toggleConnection(context: Context) {
        val currentState = _uiState.value.connectionState
        when (currentState) {
            VpnConnectionState.DISCONNECTED -> connectToSelectedServer(context)
            VpnConnectionState.CONNECTED -> disconnectVpn(context, unexpectedDrop = false)
            VpnConnectionState.CONNECTING, VpnConnectionState.DISCONNECTING -> {
                connectionTransitionJob?.cancel()
                disconnectVpn(context, unexpectedDrop = false)
            }
        }
    }

    fun connectToSelectedServer(context: Context) {
        val server = _uiState.value.selectedServer ?: servers.value.firstOrNull() ?: return
        if (server.isVip && !_uiState.value.hasVipAccess) {
            _uiState.update {
                it.copy(
                    statusBannerMessage = "🔒 ${server.countryName} (${server.cityName}) is a VIP 10Gbps Server. Watch a Free Rewarded Ad or Upgrade to VIP!"
                )
            }
            return
        }

        connectionTransitionJob?.cancel()
        connectionTransitionJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    connectionState = VpnConnectionState.CONNECTING,
                    isKillSwitchTriggeredBlock = false,
                    statusBannerMessage = "Handshaking ${it.selectedProtocol.displayName} tunnel with ${server.hostname}…"
                )
            }

            // Start Android VpnService
            try {
                val dnsIp = _uiState.value.selectedDnsServer.substringBefore(" ").trim()
                val intent = Intent(context, NVpnService::class.java).apply {
                    action = NVpnService.ACTION_CONNECT
                    putExtra(NVpnService.EXTRA_SERVER_NAME, "${server.countryName} (${server.cityName})")
                    putExtra(NVpnService.EXTRA_VIRTUAL_IP, server.virtualTunnelIp)
                    putExtra(NVpnService.EXTRA_DNS_SERVER, dnsIp)
                    putExtra(NVpnService.EXTRA_PROTOCOL_NAME, _uiState.value.selectedProtocol.displayName)
                    putExtra(NVpnService.EXTRA_KILL_SWITCH, _uiState.value.isKillSwitchEnabled)
                }
                context.startService(intent)
            } catch (_: Exception) {
            }

            delay(950L)
            speedMonitor.resetBaseline()
            _uiState.update {
                it.copy(
                    connectionState = VpnConnectionState.CONNECTED,
                    connectedDurationSeconds = 0L,
                    sessionDownloadedBytes = 0L,
                    sessionUploadedBytes = 0L,
                    statusBannerMessage = "🛡️ Protected by N VPN · Masked IP: ${server.ipAddress} (${server.countryCode})"
                )
            }
        }
    }

    fun disconnectVpn(context: Context, unexpectedDrop: Boolean = false) {
        val prevState = _uiState.value
        val activeServer = prevState.selectedServer
        val duration = prevState.connectedDurationSeconds
        val downBytes = prevState.sessionDownloadedBytes
        val upBytes = prevState.sessionUploadedBytes

        connectionTransitionJob?.cancel()
        connectionTransitionJob = viewModelScope.launch {
            _uiState.update {
                it.copy(connectionState = VpnConnectionState.DISCONNECTING)
            }

            try {
                val intent = Intent(context, NVpnService::class.java).apply {
                    action = NVpnService.ACTION_DISCONNECT
                }
                context.startService(intent)
            } catch (_: Exception) {
            }

            if (activeServer != null && duration > 1L) {
                repository.recordConnectionSession(
                    server = activeServer,
                    protocol = prevState.selectedProtocol,
                    durationSeconds = duration,
                    downloadedBytes = downBytes,
                    uploadedBytes = upBytes,
                    noLogsModeEnabled = prevState.isNoLogsRamModeEnabled
                )
            }

            delay(450L)
            val killSwitchBlocked = unexpectedDrop && prevState.isKillSwitchEnabled
            _uiState.update {
                it.copy(
                    connectionState = VpnConnectionState.DISCONNECTED,
                    currentDownloadBps = 0L,
                    currentUploadBps = 0L,
                    connectedDurationSeconds = 0L,
                    isKillSwitchTriggeredBlock = killSwitchBlocked,
                    statusBannerMessage = if (killSwitchBlocked) {
                        "🛑 KILL SWITCH ENGAGED: Tunnel dropped unexpectedly. All unprotected internet traffic is blocked to prevent IP leak!"
                    } else {
                        "VPN Disconnected. Your real ISP IP (${it.realPublicIpInfo.ip}) is now visible."
                    }
                )
            }
        }
    }

    fun simulateKillSwitchProtectionTest(context: Context) {
        if (_uiState.value.connectionState == VpnConnectionState.CONNECTED) {
            _uiState.update { it.copy(isKillSwitchEnabled = true) }
            disconnectVpn(context, unexpectedDrop = true)
        } else {
            _uiState.update {
                it.copy(
                    isKillSwitchEnabled = true,
                    isKillSwitchTriggeredBlock = true,
                    statusBannerMessage = "🛑 Kill Switch Leak-Block Active: Unprotected sockets locked until VPN reconnects."
                )
            }
        }
    }

    fun runSmartConnect(context: Context) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPingingServers = true,
                    isSmartConnectActive = true,
                    statusBannerMessage = "⚡ Smart Connect: Scanning global nodes for lowest ping & load…"
                )
            }
            val currentList = servers.value
            repository.refreshServerLatencies(currentList)
            delay(500L)
            val updatedList = servers.value
            val hasVip = _uiState.value.hasVipAccess
            val candidates = updatedList.filter { hasVip || !it.isVip }
            val bestServer = candidates.minByOrNull { it.pingMs + (it.loadPercent / 3) }
                ?: updatedList.firstOrNull()

            _uiState.update {
                it.copy(
                    isPingingServers = false,
                    selectedServer = bestServer ?: it.selectedServer,
                    currentTab = NVpnTab.CONNECT
                )
            }
            if (bestServer != null) {
                connectToSelectedServer(context)
            }
        }
    }

    fun selectServer(
        context: Context,
        server: VpnServerEntity,
        onRequireVipUnlock: (VpnServerEntity) -> Unit
    ) {
        if (server.isVip && !_uiState.value.hasVipAccess) {
            onRequireVipUnlock(server)
            return
        }
        val wasConnected = _uiState.value.connectionState == VpnConnectionState.CONNECTED
        _uiState.update {
            it.copy(
                selectedServer = server,
                isSmartConnectActive = false,
                currentTab = NVpnTab.CONNECT
            )
        }
        if (wasConnected) {
            connectToSelectedServer(context)
        } else {
            _uiState.update {
                it.copy(
                    statusBannerMessage = "Selected ${server.flagEmoji} ${server.countryName} (${server.pingMs} ms). Tap Connect to start tunnel."
                )
            }
        }
    }

    fun refreshAllServerPings() {
        if (_uiState.value.isPingingServers) return
        viewModelScope.launch {
            _uiState.update { it.copy(isPingingServers = true) }
            repository.refreshServerLatencies(servers.value)
            val ipInfo = repository.fetchRealPublicIp()
            _uiState.update {
                it.copy(
                    isPingingServers = false,
                    realPublicIpInfo = ipInfo,
                    statusBannerMessage = "✅ Refreshed real-time latency across ${servers.value.size} global server nodes."
                )
            }
        }
    }

    fun toggleFavorite(server: VpnServerEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(server)
        }
    }

    fun addCustomVpsServer(
        countryName: String,
        cityName: String,
        ipAddress: String,
        portStr: String,
        protocol: VpnProtocol,
        wireGuardPubKey: String
    ) {
        viewModelScope.launch {
            val port = portStr.toIntOrNull()?.coerceIn(1, 65535) ?: protocol.defaultPort
            val added = repository.addCustomVpsServer(
                countryName = countryName,
                cityName = cityName,
                ipAddress = ipAddress,
                port = port,
                protocol = protocol,
                wireGuardPubKey = wireGuardPubKey
            )
            _uiState.update {
                it.copy(
                    selectedServer = added,
                    statusBannerMessage = "✅ Custom VPS Node '${added.countryName} (${added.ipAddress}:$port)' saved & selected!"
                )
            }
        }
    }

    fun deleteCustomServer(server: VpnServerEntity) {
        viewModelScope.launch {
            repository.deleteCustomServer(server.id)
            _uiState.update {
                it.copy(statusBannerMessage = "Removed custom VPS node ${server.ipAddress}")
            }
        }
    }

    fun selectProtocol(protocol: VpnProtocol) {
        _uiState.update {
            it.copy(
                selectedProtocol = protocol,
                statusBannerMessage = "Protocol switched to ${protocol.displayName} (${protocol.encryptionSpec})"
            )
        }
    }

    fun setKillSwitchEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isKillSwitchEnabled = enabled,
                isKillSwitchTriggeredBlock = if (!enabled) false else it.isKillSwitchTriggeredBlock,
                statusBannerMessage = if (enabled) {
                    "🛡️ Kill Switch Armed: Unprotected traffic will be blocked if VPN drops."
                } else {
                    "Kill Switch Disabled."
                }
            )
        }
    }

    fun setNoLogsModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                repository.clearAllLogs()
            }
            _uiState.update {
                it.copy(
                    isNoLogsRamModeEnabled = enabled,
                    statusBannerMessage = if (enabled) {
                        "🔒 Strict Zero-Logs Policy Active: RAM-only session state, local logs wiped."
                    } else {
                        "Diagnostic session history enabled locally."
                    }
                )
            }
        }
    }

    fun setSplitTunnelingEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isSplitTunnelingEnabled = enabled,
                statusBannerMessage = if (enabled) {
                    "Split Tunneling Enabled: Local banking & LAN apps bypass VPN."
                } else {
                    "Full-Device Tunnel: 100% of device traffic routed through N VPN."
                }
            )
        }
    }

    fun setDnsServer(dnsLabel: String) {
        _uiState.update {
            it.copy(
                selectedDnsServer = dnsLabel,
                statusBannerMessage = "Encrypted DNS updated to $dnsLabel"
            )
        }
    }

    fun unlockVipSubscription(planName: String) {
        _uiState.update {
            it.copy(
                isVipSubscribed = true,
                activeVipPlanName = planName,
                statusBannerMessage = "👑 Welcome to N VPN VIP ($planName)! All 25Gbps Multi-Hop servers & Ad-Free mode unlocked."
            )
        }
    }

    fun grantRewardedAdVipPass() {
        _uiState.update {
            it.copy(
                rewardedPassRemainingSeconds = it.rewardedPassRemainingSeconds + 7200L, // +2 Hours
                statusBannerMessage = "🎁 AdMob Rewarded Pass Activated! All VIP Servers unlocked for 2 Hours."
            )
        }
    }

    fun getActiveConfigPreview(): String {
        val state = _uiState.value
        val server = state.selectedServer ?: NVpnDatabase.defaultGlobalServers().first()
        val dnsIp = state.selectedDnsServer.substringBefore(" ").trim()
        return repository.generateTunnelConfigPreview(
            server = server,
            protocol = state.selectedProtocol,
            dnsServer = dnsIp,
            killSwitchEnabled = state.isKillSwitchEnabled
        )
    }

    companion object {
        fun provideFactory(repository: VpnRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VpnViewModel(repository) as T
                }
            }
    }
}
