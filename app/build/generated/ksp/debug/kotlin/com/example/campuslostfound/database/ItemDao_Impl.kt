package com.example.campuslostfound.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ItemDao_Impl(
  __db: RoomDatabase,
) : ItemDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfItemEntity: EntityInsertAdapter<ItemEntity>

  private val __updateAdapterOfItemEntity: EntityDeleteOrUpdateAdapter<ItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfItemEntity = object : EntityInsertAdapter<ItemEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `items` (`id`,`userId`,`name`,`description`,`category`,`type`,`location`,`date`,`status`,`imageUri`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ItemEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.userId.toLong())
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.type)
        statement.bindText(7, entity.location)
        statement.bindText(8, entity.date)
        statement.bindText(9, entity.status)
        val _tmpImageUri: String? = entity.imageUri
        if (_tmpImageUri == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpImageUri)
        }
      }
    }
    this.__updateAdapterOfItemEntity = object : EntityDeleteOrUpdateAdapter<ItemEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `items` SET `id` = ?,`userId` = ?,`name` = ?,`description` = ?,`category` = ?,`type` = ?,`location` = ?,`date` = ?,`status` = ?,`imageUri` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ItemEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.userId.toLong())
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.type)
        statement.bindText(7, entity.location)
        statement.bindText(8, entity.date)
        statement.bindText(9, entity.status)
        val _tmpImageUri: String? = entity.imageUri
        if (_tmpImageUri == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpImageUri)
        }
        statement.bindLong(11, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertItem(item: ItemEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfItemEntity.insertAndReturnId(_connection, item)
    _result
  }

  public override suspend fun updateItem(item: ItemEntity): Int = performSuspending(__db, false, true) { _connection ->
    var _result: Int = 0
    _result += __updateAdapterOfItemEntity.handle(_connection, item)
    _result
  }

  public override suspend fun getItemById(itemId: Int): ItemEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE id = ?
        |        LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, itemId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: ItemEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _result = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllItems(): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLostItems(): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE type = 'LOST'
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFoundItems(): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE type = 'FOUND'
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun searchItems(query: String): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE name LIKE '%' || ? || '%'
        |        OR description LIKE '%' || ? || '%'
        |        OR category LIKE '%' || ? || '%'
        |        OR location LIKE '%' || ? || '%'
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        _argIndex = 3
        _stmt.bindText(_argIndex, query)
        _argIndex = 4
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getItemsByCategory(category: String): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE category = ?
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getItemsByTypeAndCategory(type: String, category: String): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE type = ?
        |        AND category = ?
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getItemByOwner(itemId: Int, userId: Int): ItemEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE id = ?
        |        AND userId = ?
        |        LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, itemId.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, userId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: ItemEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _result = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getItemsByUser(userId: Int): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE userId = ?
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findMatchingItems(
    oppositeType: String,
    category: String,
    name: String,
    userId: Int,
  ): List<ItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM items
        |        WHERE type = ?
        |        AND category = ?
        |        AND userId != ?
        |        AND (
        |            name LIKE '%' || ? || '%'
        |            OR ? LIKE '%' || name || '%'
        |        )
        |        ORDER BY id DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, oppositeType)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        _argIndex = 3
        _stmt.bindLong(_argIndex, userId.toLong())
        _argIndex = 4
        _stmt.bindText(_argIndex, name)
        _argIndex = 5
        _stmt.bindText(_argIndex, name)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _result: MutableList<ItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ItemEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: Int
          _tmpUserId = _stmt.getLong(_columnIndexOfUserId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          _item = ItemEntity(_tmpId,_tmpUserId,_tmpName,_tmpDescription,_tmpCategory,_tmpType,_tmpLocation,_tmpDate,_tmpStatus,_tmpImageUri)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteItemByOwner(itemId: Int, userId: Int): Int {
    val _sql: String = """
        |
        |        DELETE FROM items
        |        WHERE id = ?
        |        AND userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, itemId.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, userId.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateItemByOwner(
    itemId: Int,
    userId: Int,
    name: String,
    description: String,
    category: String,
    location: String,
    date: String,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE items
        |        SET name = ?,
        |            description = ?,
        |            category = ?,
        |            location = ?,
        |            date = ?
        |        WHERE id = ?
        |        AND userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, name)
        _argIndex = 2
        _stmt.bindText(_argIndex, description)
        _argIndex = 3
        _stmt.bindText(_argIndex, category)
        _argIndex = 4
        _stmt.bindText(_argIndex, location)
        _argIndex = 5
        _stmt.bindText(_argIndex, date)
        _argIndex = 6
        _stmt.bindLong(_argIndex, itemId.toLong())
        _argIndex = 7
        _stmt.bindLong(_argIndex, userId.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateItemStatusByOwner(
    itemId: Int,
    userId: Int,
    status: String,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE items
        |        SET status = ?
        |        WHERE id = ?
        |        AND userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, itemId.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, userId.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
