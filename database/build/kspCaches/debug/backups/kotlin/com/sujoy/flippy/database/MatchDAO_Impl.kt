package com.sujoy.flippy.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MatchDAO_Impl(
  __db: RoomDatabase,
) : MatchDAO {
  private val __db: RoomDatabase

  private val __insertAdapterOfMatchHistory: EntityInsertAdapter<MatchHistory>
  init {
    this.__db = __db
    this.__insertAdapterOfMatchHistory = object : EntityInsertAdapter<MatchHistory>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `Match History` (`id`,`playerId`,`score`,`difficulty`,`gameDuration`,`timestamp`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MatchHistory) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.playerId)
        statement.bindLong(3, entity.score.toLong())
        statement.bindText(4, entity.difficulty)
        statement.bindLong(5, entity.gameDuration)
        statement.bindLong(6, entity.timestamp)
      }
    }
  }

  public override suspend fun insertMatch(match: MatchHistory): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMatchHistory.insert(_connection, match)
  }

  public override fun getTopThreeScores(playerId: String): Flow<List<MatchHistory>> {
    val _sql: String = "SELECT * FROM `Match History` WHERE playerId = ? ORDER BY score DESC, gameDuration ASC LIMIT 3"
    return createFlow(__db, false, arrayOf("Match History")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, playerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPlayerId: Int = getColumnIndexOrThrow(_stmt, "playerId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfGameDuration: Int = getColumnIndexOrThrow(_stmt, "gameDuration")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<MatchHistory> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatchHistory
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpPlayerId: String
          _tmpPlayerId = _stmt.getText(_columnIndexOfPlayerId)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpGameDuration: Long
          _tmpGameDuration = _stmt.getLong(_columnIndexOfGameDuration)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = MatchHistory(_tmpId,_tmpPlayerId,_tmpScore,_tmpDifficulty,_tmpGameDuration,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMatchHistoryForId(playerId: String): List<MatchHistory> {
    val _sql: String = "SELECT * FROM `Match History` WHERE playerId = ? ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, playerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPlayerId: Int = getColumnIndexOrThrow(_stmt, "playerId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfGameDuration: Int = getColumnIndexOrThrow(_stmt, "gameDuration")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<MatchHistory> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatchHistory
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpPlayerId: String
          _tmpPlayerId = _stmt.getText(_columnIndexOfPlayerId)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpGameDuration: Long
          _tmpGameDuration = _stmt.getLong(_columnIndexOfGameDuration)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = MatchHistory(_tmpId,_tmpPlayerId,_tmpScore,_tmpDifficulty,_tmpGameDuration,_tmpTimestamp)
          _result.add(_item)
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
