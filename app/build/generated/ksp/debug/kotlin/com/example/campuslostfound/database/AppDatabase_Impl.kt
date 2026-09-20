package com.example.campuslostfound.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _itemDao: Lazy<ItemDao> = lazy {
    ItemDao_Impl(this)
  }

  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }

  private val _notificationDao: Lazy<NotificationDao> = lazy {
    NotificationDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(5, "4169827e67d2b69dc299cdcc347751b2", "d98596456c4756a680a2ff93494eff1a") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `type` TEXT NOT NULL, `location` TEXT NOT NULL, `date` TEXT NOT NULL, `status` TEXT NOT NULL, `imageUri` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `phone` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `passwordSalt` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `itemId` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4169827e67d2b69dc299cdcc347751b2')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `items`")
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `notifications`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsItems.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("description", TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("category", TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("type", TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("location", TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("date", TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("status", TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("imageUri", TableInfo.Column("imageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesItems: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoItems: TableInfo = TableInfo("items", _columnsItems, _foreignKeysItems, _indicesItems)
        val _existingItems: TableInfo = read(connection, "items")
        if (!_infoItems.equals(_existingItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |items(com.example.campuslostfound.database.ItemEntity).
              | Expected:
              |""".trimMargin() + _infoItems + """
              |
              | Found:
              |""".trimMargin() + _existingItems)
        }
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("passwordHash", TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("passwordSalt", TableInfo.Column("passwordSalt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.example.campuslostfound.database.UserEntity).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsNotifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotifications.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("userId", TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("message", TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("itemId", TableInfo.Column("itemId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNotifications: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoNotifications: TableInfo = TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications)
        val _existingNotifications: TableInfo = read(connection, "notifications")
        if (!_infoNotifications.equals(_existingNotifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notifications(com.example.campuslostfound.database.NotificationEntity).
              | Expected:
              |""".trimMargin() + _infoNotifications + """
              |
              | Found:
              |""".trimMargin() + _existingNotifications)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "items", "users", "notifications")
  }

  public override fun clearAllTables() {
    super.performClear(false, "items", "users", "notifications")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ItemDao::class, ItemDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserDao::class, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NotificationDao::class, NotificationDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun itemDao(): ItemDao = _itemDao.value

  public override fun userDao(): UserDao = _userDao.value

  public override fun notificationDao(): NotificationDao = _notificationDao.value
}
