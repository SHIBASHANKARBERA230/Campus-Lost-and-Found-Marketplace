package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ItemEntity::class,
        UserEntity::class,
        NotificationEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun userDao(): UserDao

    abstract fun notificationDao(): NotificationDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration 1 → 2
        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            email TEXT NOT NULL,
                            phone TEXT NOT NULL,
                            passwordHash TEXT NOT NULL,
                            passwordSalt TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        // Migration 2 → 3
        private val MIGRATION_2_3 =
            object : Migration(2, 3) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        ALTER TABLE items
                        ADD COLUMN userId INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )
                }
            }

        // Migration 3 → 4
        // Adds imageUri column
        private val MIGRATION_3_4 =
            object : Migration(3, 4) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        ALTER TABLE items
                        ADD COLUMN imageUri TEXT
                        """.trimIndent()
                    )
                }
            }

        // Migration 4 → 5
        // Adds notifications table
        private val MIGRATION_4_5 =
            object : Migration(4, 5) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS notifications (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            userId INTEGER NOT NULL,
                            title TEXT NOT NULL,
                            message TEXT NOT NULL,
                            itemId INTEGER NOT NULL,
                            isRead INTEGER NOT NULL,
                            createdAt INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    INSTANCE
                        ?: Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "campus_lost_found_database"
                        )
                            .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                                MIGRATION_4_5
                            )
                            .build()
                            .also {
                                INSTANCE = it
                            }
                }
        }
    }
}