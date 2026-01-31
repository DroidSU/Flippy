package com.sujoy.flippy.database

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
  private val _matchDAO: Lazy<MatchDAO> = lazy {
    MatchDAO_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "df17394364ed32445e48c0fb7c3f96d2", "f686bd8667638af3435239f799faee3b") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Match History` (`id` TEXT NOT NULL, `playerId` TEXT NOT NULL, `score` INTEGER NOT NULL, `difficulty` TEXT NOT NULL, `gameDuration` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'df17394364ed32445e48c0fb7c3f96d2')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `Match History`")
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
        val _columnsMatchHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMatchHistory.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatchHistory.put("playerId", TableInfo.Column("playerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatchHistory.put("score", TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatchHistory.put("difficulty", TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatchHistory.put("gameDuration", TableInfo.Column("gameDuration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatchHistory.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMatchHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMatchHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMatchHistory: TableInfo = TableInfo("Match History", _columnsMatchHistory, _foreignKeysMatchHistory, _indicesMatchHistory)
        val _existingMatchHistory: TableInfo = read(connection, "Match History")
        if (!_infoMatchHistory.equals(_existingMatchHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |Match History(com.sujoy.flippy.database.MatchHistory).
              | Expected:
              |""".trimMargin() + _infoMatchHistory + """
              |
              | Found:
              |""".trimMargin() + _existingMatchHistory)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "Match History")
  }

  public override fun clearAllTables() {
    super.performClear(false, "Match History")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(MatchDAO::class, MatchDAO_Impl.getRequiredConverters())
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

  public override fun matchDao(): MatchDAO = _matchDAO.value
}
