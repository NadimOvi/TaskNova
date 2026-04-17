package com.nadim.tasknova.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY createdAt ASC")
    fun getConversations(userId: String): Flow<List<ConversationEntity>>

    // Get last 10 messages for AI context
    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 10")
    suspend fun getRecentMessages(userId: String): List<ConversationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ConversationEntity)

    @Query("DELETE FROM conversations WHERE userId = :userId")
    suspend fun clearConversation(userId: String)
}