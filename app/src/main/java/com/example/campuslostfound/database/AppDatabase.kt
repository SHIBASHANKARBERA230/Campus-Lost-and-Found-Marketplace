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
        NotificationEntity::class,
        ClaimEntity::class,
        FavoriteEntity::class,
        ReportEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun userDao(): UserDao

    abstract fun notificationDao(): NotificationDao

    abstract fun claimDao(): ClaimDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun reportDao(): ReportDao


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null


        // =============================================
        // Migration 1 → 2
        // Adds users table
        // =============================================

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


        // =============================================
        // Migration 2 → 3
        // Adds userId to items
        // =============================================

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


        // =============================================
        // Migration 3 → 4
        // Adds imageUri
        // =============================================

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


        // =============================================
        // Migration 4 → 5
        // Adds notifications
        // =============================================

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


        // =============================================
        // Migration 5 → 6
        // Adds claims
        // Adds notificationType
        // =============================================

        private val MIGRATION_5_6 =
            object : Migration(5, 6) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    // -----------------------------
                    // Create claims table
                    // -----------------------------

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS claims (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            itemId INTEGER NOT NULL,
                            claimantUserId INTEGER NOT NULL,
                            ownerUserId INTEGER NOT NULL,
                            reason TEXT NOT NULL,
                            additionalDetails TEXT NOT NULL,
                            status TEXT NOT NULL,
                            createdAt INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )


                    // -----------------------------
                    // Add notification type
                    // -----------------------------

                    database.execSQL(
                        """
                        ALTER TABLE notifications
                        ADD COLUMN notificationType TEXT NOT NULL
                        DEFAULT 'ITEM_MATCH'
                        """.trimIndent()
                    )
                }
            }


        // =============================================
        // Migration 6 → 7
        // Adds favorites table
        // =============================================

        private val MIGRATION_6_7 =
            object : Migration(6, 7) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS favorites (
                            userId INTEGER NOT NULL,
                            itemId INTEGER NOT NULL,
                            createdAt INTEGER NOT NULL,
                            PRIMARY KEY(userId, itemId)
                        )
                        """.trimIndent()
                    )
                }
            }


        // =============================================
        // Migration 7 → 8
        // Adds reports table
        // =============================================

        private val MIGRATION_7_8 =
            object : Migration(7, 8) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS reports (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            itemId INTEGER NOT NULL,
                            reporterUserId INTEGER NOT NULL,
                            ownerUserId INTEGER NOT NULL,
                            reason TEXT NOT NULL,
                            details TEXT NOT NULL,
                            status TEXT NOT NULL,
                            createdAt INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }


        // =============================================
        // Migration 8 → 9
        // Adds isAdmin to users
        // =============================================

        private val MIGRATION_8_9 =
            object : Migration(8, 9) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        ALTER TABLE users
                        ADD COLUMN isAdmin INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )
                }
            }


        // =============================================
        // GET DATABASE
        // =============================================

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
                                MIGRATION_4_5,
                                MIGRATION_5_6,
                                MIGRATION_6_7,
                                MIGRATION_7_8,
                                MIGRATION_8_9
                            )
                            .build()
                            .also {
                                INSTANCE = it
                            }
                }
        }
    }
}