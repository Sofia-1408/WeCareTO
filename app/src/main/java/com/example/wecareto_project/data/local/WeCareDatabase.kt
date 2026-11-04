package com.example.wecareto_project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResourceEntity::class], version = 1, exportSchema = true)
abstract class WeCareDatabase : RoomDatabase() {
    abstract fun resourceDao(): ResourceDao
}
