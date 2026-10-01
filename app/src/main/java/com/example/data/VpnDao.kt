package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isCustomVps DESC, pingMs ASC")
    fun getAllServers(): Flow<List<VpnServerEntity>>

    @Query("SELECT COUNT(*) FROM vpn_servers")
    suspend fun getServerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServerEntity): Long

    @Update
    suspend fun updateServer(server: VpnServerEntity)

    @Query("UPDATE vpn_servers SET isFavorite = :isFavorite WHERE id = :serverId")
    suspend fun updateFavorite(serverId: Int, isFavorite: Boolean)

    @Query("UPDATE vpn_servers SET pingMs = :pingMs, loadPercent = :loadPercent WHERE id = :serverId")
    suspend fun updateServerTelemetry(serverId: Int, pingMs: Int, loadPercent: Int)

    @Query("DELETE FROM vpn_servers WHERE id = :serverId AND isCustomVps = 1")
    suspend fun deleteCustomServer(serverId: Int)

    @Query("SELECT * FROM connection_logs ORDER BY timestamp DESC LIMIT 25")
    fun getRecentLogs(): Flow<List<ConnectionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConnectionLogEntity)

    @Query("DELETE FROM connection_logs")
    suspend fun clearAllLogs()
}
