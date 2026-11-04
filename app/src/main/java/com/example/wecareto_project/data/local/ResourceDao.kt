package com.example.wecareto_project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ResourceDao {
    @Query("SELECT * FROM resources ORDER BY name COLLATE NOCASE")
    abstract fun observeResources(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :id LIMIT 1")
    abstract suspend fun findById(id: String): ResourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(resources: List<ResourceEntity>)

    @Query("DELETE FROM resources WHERE source = :source")
    abstract suspend fun deleteSource(source: String)

    @Transaction
    open suspend fun replaceSource(source: String, resources: List<ResourceEntity>) {
        require(resources.all { it.source == source })
        deleteSource(source)
        upsert(resources)
    }
}
