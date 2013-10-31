/**
 * 
 */
package com.xwtec.yycoin.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.xwtec.util.db.DBException;
import com.xwtec.util.db.DBTool;
import com.xwtec.util.tool.Constant;

/**
 * @author liuzixiang
 * 
 */
public class CollectionInfo {

	/**
	 * 查询名称
	 * 
	 * @param collectionID
	 *            名称
	 * @return 数据库中是否存在这个名称
	 */
	public int queryByCollectionID(String collectionID) {
		Log.v("=======queryByCollectionID========", "collectionID = "
				+ collectionID);
		int recodeCount = 0;
		Cursor cursor = null;
		try {
			cursor = DBTool.getInstance().query(Constant.TABLE_NAME, null,
					"collectionID = '" + collectionID + "'", null, null, null,
					null);
			if (null != cursor) {
				recodeCount = cursor.getCount();
			}
		} catch (DBException e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return recodeCount;
	}

	/**
	 * 查询名称
	 * 
	 * @param collectionID
	 *            名称
	 * @return 数据库中是否存在这个名称
	 */
	public Map<String, String> queryByCollectionByID(String collectionID) {
		Log.v("=======queryByCollectionID========", "collectionID = "
				+ collectionID);
		Map<String, String> map = new HashMap<String, String>();
		Cursor cursor = null;
		try {
			cursor = DBTool.getInstance().query(Constant.TABLE_NAME,
					new String[] { "saveData", "content" },
					"collectionID = '" + collectionID + "'", null, null, null,
					null);
			if (null != cursor) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					map.put("content", cursor.getString(1));
					map.put("saveData", cursor.getString(0));
					cursor.moveToNext();
				}
			}
		} catch (DBException e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return map;
	}

	public List<Map<String, String>> queryAllCollectionInfo() {
		int recodeCount = 0;
		Log.v("=======queryAllCollectionInfo========", "....");
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		try {
			cursor = DBTool.getInstance().query(
					Constant.TABLE_NAME,
					new String[] { "collectionID", "content", "title",
							"saveData", "purl", "curl", "jg", "gg", "zl", "tc",
							"productno", "image" }, null, null, null, null,
					null);
			if (null != cursor) {
				recodeCount = cursor.getCount();
			}
			if (recodeCount > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("collectionID", cursor.getString(0));
					map.put("content", cursor.getString(1));
					map.put("title", cursor.getString(2));
					map.put("saveData", cursor.getString(3));
					map.put("purl", cursor.getString(4));
					map.put("curl", cursor.getString(5));
					map.put("jg", cursor.getString(6));
					map.put("gg", cursor.getString(7));
					map.put("zl", cursor.getString(8));
					map.put("tc", cursor.getString(9));
					map.put("productno", cursor.getString(10));
					map.put("image", cursor.getString(11));
					list.add(map);
					cursor.moveToNext();
				}
			}
		} catch (DBException e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 插入数据
	 * 
	 * @param collectionID
	 *            名称
	 * @param content
	 *            内容
	 */
	public void insert(String content, String collectionID, String title,
			String saveData, String purl, String curl, String jg, String gg,
			String zl, String tc, String productno, String image) {
		Log.v("=======insert========", " content = " + content
				+ " collectionID = " + collectionID);
		ContentValues v = new ContentValues();
		v.put("content", content);
		v.put("collectionID", collectionID);
		v.put("title", title);
		v.put("saveData", saveData);
		v.put("purl", purl);
		v.put("curl", curl);
		v.put("jg", jg);
		v.put("gg", gg);
		v.put("zl", zl);
		v.put("tc", tc);
		v.put("productno", productno);
		v.put("image", image);
		try {
			DBTool.getInstance().insertRecord(Constant.TABLE_NAME, v);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除该条记录
	 * 
	 * @param collectionID
	 *            收藏的ID
	 */
	public void delete(String collectionID) {
		Log.v("1111", "collectionID = " + collectionID);
		String whereClause = "" + collectionID + "=?";
		String[] whereArgs = new String[] { String.valueOf(collectionID) };
		String sql = "DELETE FROM " + Constant.TABLE_NAME
				+ " WHERE collectionID=" + collectionID;
		Log.v("111111111", "sql = " + sql);
		try {
			// DBTool.getInstance().deleteRecord(Constant.TABLE_NAME,
			// whereClause,
			// whereArgs);
			DBTool.getInstance().deleteRecodeBySql(sql);

		} catch (DBException e) {
			e.printStackTrace();
		}
	}
}
