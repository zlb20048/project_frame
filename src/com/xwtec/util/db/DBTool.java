/*
 * 文 件 名:  DBTool.java
 * 描    述:  <描述>
 * 修 改 人:  y00109551
 * 修改时间:  2009-6-11
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.xwtec.util.db;

import java.util.List;

import com.xwtec.util.tool.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 封装SQLite数据库操作工具类，单实例类
 * 
 * @author y00109551
 * @version [版本号, 2009-6-11]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class DBTool {
	/**
	 * 数据库名称，一般一个应用只会对应一个数据库
	 */
	private static final String DATABASE_NAME = "musicClient.db";

	/**
	 * 版本号
	 */
	private static final int DATABASE_VERSION = 3;

	/**
	 * 单实例对象
	 */
	private static DBTool dbTool = null;

	/**
	 * 数据库helper类
	 */
	private DatabaseHelper dbHelper = null;

	/**
	 * 私有构造方法
	 * 
	 * @param context
	 *            上下文对象
	 */
	private DBTool(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	/**
	 * 初始化方法
	 * 
	 * @param context
	 *            context
	 */
	public static void init(Context context) {
		dbTool = new DBTool(context);
	}

	/**
	 * 获取实例对象
	 * 
	 * @return DBTool
	 */
	public static DBTool getInstance() {
		return dbTool;
	}

	/**
	 * 创建数据表
	 * 
	 * @param tableName
	 *            表名
	 * @param isRenew
	 *            如果要创建的表已存在，是否先删除
	 * @param sql
	 *            完整的建表SQL语句
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public void creatTable(String tableName, boolean isRenew, String sql)
			throws DBException {

		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			if (isRenew) {
				db.execSQL("DROP TABLE IF EXISTS " + tableName);
			}
			db.execSQL(sql);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 删除某数据表
	 * 
	 * @param tableName
	 *            要删除的表名
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public void dropTable(String tableName) throws DBException {
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 查询方法
	 * 
	 * @param tableName
	 *            要查询的表名
	 * @param col
	 *            要查询的字段名数组
	 * @param selection
	 *            相当于sql语句的where部分，如果想返回所有的数据，那么就直接置为null
	 * @param selectionArg
	 *            在selection部分，你有可能用到“?”,那么在selectionArgs定义的字符串会代替selection中的“?”
	 * @param groupBy
	 *            定义查询出来的数据是否分组，如果为null则说明不用分组
	 * @param having
	 *            相当于sql语句当中的having部分
	 * @param orderBy
	 *            来描述我们期望的返回值是否需要排序，如果设置为null则说明不需要排序
	 * @return Cursor 游标对象
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public Cursor query(String tableName, String[] col, String selection,
			String[] selectionArg, String groupBy, String having, String orderBy)
			throws DBException {
		Cursor cur = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			cur = db.query(tableName, col, selection, selectionArg, groupBy,
					having, orderBy);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		return cur;
	}

	/**
	 * 插入一条新纪录的方法
	 * 
	 * @param tableName
	 *            对应的数据表名
	 * @param values
	 *            列名和值对
	 * @return 成功则返回插入的行号，失败返回0
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public long insertRecord(String tableName, ContentValues values)
			throws DBException {
		long rowID = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			rowID = db.insert(tableName, null, values);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		return rowID;
	}

	/**
	 * 批量插入
	 * 
	 * @param tableName
	 *            tableName
	 * @param values
	 *            values
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public void batchInsertRecords(String tableName, List<ContentValues> values)
			throws DBException {
		if (values == null) {
			return;
		}

		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			for (ContentValues v : values) {
				db.insert(tableName, null, v);
			}
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 更新一条纪录的方法
	 * 
	 * @param tableName
	 *            对应的数据表名
	 * @param values
	 *            列名和值对
	 * @param whereClause
	 *            相当于sql语句的where部分
	 * @param whereArgs
	 *            替换whereClause中的“?”
	 * @return 更新成功返回true，失败返回false
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public boolean updateRecord(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) throws DBException {
		long rowID = -1;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			rowID = db.update(tableName, values, whereClause, whereArgs);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		return rowID > 0;
	}

	/**
	 * 外部写好的非查询的SQL语句直接执行，通常不建议直接使用该方法
	 * 
	 * @param sql
	 *            完整的非查询类的SQL语句
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public void execSQL(String sql) throws DBException {
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 删除一条纪录
	 * 
	 * @param tableName
	 *            对应的数据表名
	 * @param whereClause
	 *            相当于sql语句的where部分
	 * @param whereArgs
	 *            对应whereClause中的“?”部分
	 * @throws DBException
	 *             DBException
	 * @see [类、类#方法、类#成员]
	 */
	public void deleteRecord(String tableName, String whereClause,
			String[] whereArgs) throws DBException {
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(tableName, whereClause, whereArgs);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public void deleteRecodeBySql(String sql) throws DBException {
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void onClose() {
		dbHelper.close();
	}

	/**
	 * SQLiteDatabase的辅助类，为内部类 相关方法不用在类里实现
	 * 
	 * @author y00109551
	 * @version [版本号, 2009-6-11]
	 * @see [相关类/方法]
	 * @since [产品/模块版本]
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * 
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param db
		 *            db
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS "
					+ Constant.TABLE_NAME
					+ "(collectionID TEXT not null,content TEXT not null,"
					+ "title TEXT not null, saveData TEXT not null, "
					+ "purl TEXT not null, curl TEXT not null, jg TEXT not null,"
					+ "gg TEXT not null,productno Text not null, image TEXT,"
					+ "zl TEXT not null,tc TEXT not null )";

			String sql1 = "CREATE TABLE IF NOT EXISTS " + Constant.PRODUCT_CAR
					+ "(productCarId TEXT not null,productno Text not null,"
					+ "title TEXT not null, "
					+ "jg TEXT not null, cz TEXT not null,"
					+ "gg TEXT not null,image TEXT,"
					+ "zl TEXT not null,tc TEXT not null, " + "count INTEGER )";
			db.execSQL(sql1);
			db.execSQL(sql);
		}

		/**
		 * 
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param db
		 *            db
		 * @param oldVersion
		 *            oldVersion
		 * @param newVersion
		 *            newVersion
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
		 *      int, int)
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
