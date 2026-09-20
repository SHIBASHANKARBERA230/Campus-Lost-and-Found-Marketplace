package com.example.campuslostfound.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class NotificationDao_Impl(
  __db: RoomDatabase,
) : NotificationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfNotificationEntity: EntityInsertAdapter<NotificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfNotificationEntity = object : EntityInsertAdapter<NotificationEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `notifications` (`id`,`userId`,`title`,`message`,`itemId`,`isRead`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NotificationEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.userId.toLong())
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.message)
        statement.bindLong(5, entity.itemId.toLong())
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun insertNotification(notification: NotificationEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, notification)
  }

  public override suspend fun getNotifications(userId: Int): List<NotificationEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM notifications
        |        WHERE userId = ?
        |        ORDER BY createdAt DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfItemId: Int = getColumnIndexOrThrow(_stmt, "itemId")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpItemId: Int
          _tmpItemId = _stmt.getLong(_columnIndexOfItemId).toInt()
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = NotificationEntity(_tmpId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpItemId,_tmpIsRead,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUnreadCount(userId: Int): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM notifications
        |        WHERE userId = ?
        |        AND isRead = 0
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun notificationExists(userId: Int, itemId: Int): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM notifications
        |        WHERE userId = ?
        |        AND itemId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, itemId.toLong())
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAsRead(notificationId: Int) {
    val _sql: String = """
        |
        |        UPDATE notifications
        |        SET isRead = 1
        |        WHERE id = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, notificationId.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAllAsRead(userId: Int) {
    val _sql: String = """
        |
        |        UPDATE notifications
        |        SET isRead = 1
        |        WHERE userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllNotifications(userId: Int) {
    val _sql: String = """
        |
        |        DELETE FROM notifications
        |        WHERE userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
