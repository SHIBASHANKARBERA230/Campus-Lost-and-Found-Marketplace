package com.example.campuslostfound.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserEntity: EntityInsertAdapter<UserEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserEntity = object : EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `users` (`id`,`name`,`email`,`phone`,`passwordHash`,`passwordSalt`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.email)
        statement.bindText(4, entity.phone)
        statement.bindText(5, entity.passwordHash)
        statement.bindText(6, entity.passwordSalt)
      }
    }
  }

  public override suspend fun insertUser(user: UserEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfUserEntity.insertAndReturnId(_connection, user)
    _result
  }

  public override suspend fun getUserByEmail(email: String): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE email = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, email)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfPasswordSalt: Int = getColumnIndexOrThrow(_stmt, "passwordSalt")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpPasswordSalt: String
          _tmpPasswordSalt = _stmt.getText(_columnIndexOfPasswordSalt)
          _result = UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpPhone,_tmpPasswordHash,_tmpPasswordSalt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun emailExists(email: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE email = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, email)
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

  public override suspend fun getUserById(userId: Int): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, userId.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfPasswordSalt: Int = getColumnIndexOrThrow(_stmt, "passwordSalt")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpPasswordHash: String
          _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          val _tmpPasswordSalt: String
          _tmpPasswordSalt = _stmt.getText(_columnIndexOfPasswordSalt)
          _result = UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpPhone,_tmpPasswordHash,_tmpPasswordSalt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
