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
 * @author Administrator
 * 
 */
public class ProductCarInfo {

	/**
	 * 查询名称
	 * 
	 * @param productID
	 *            名称
	 * @return 数据库中是否存在这个名称
	 */
	public int queryByProductID(String productID) {
		Log.v("=======queryByProductID========", "productCarId = " + productID);
		int recodeCount = 0;
		Cursor cursor = null;
		try {
			cursor = DBTool.getInstance().query(Constant.PRODUCT_CAR,
					new String[] { "count" },
					"productCarId = '" + productID + "'", null, null, null,
					null);
			if (null != cursor) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					recodeCount = Integer.parseInt(cursor.getString(0));
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
		return recodeCount;
	}

	public List<Map<String, String>> queryAllProductInfo() {
		int recodeCount = 0;
		Log.v("=======queryAllProductInfo========", "....");
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		try {
			cursor = DBTool.getInstance().query(
					Constant.PRODUCT_CAR,
					new String[] { "productCarId", "title", "jg", "cz", "gg",
							"zl", "tc", "count", "productno", "image" }, null,
					null, null, null, null);
			if (null != cursor) {
				recodeCount = cursor.getCount();
			}
			if (recodeCount > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("productCarId", cursor.getString(0));
					map.put("title", cursor.getString(1));
					map.put("jg", cursor.getString(2));
					map.put("cz", cursor.getString(3));
					map.put("gg", cursor.getString(4));
					map.put("zl", cursor.getString(5));
					map.put("tc", cursor.getString(6));
					map.put("count", cursor.getString(7));
					map.put("productno", cursor.getString(8));
					map.put("image", cursor.getString(9));
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

	public void update(String productno, int count) {
		ContentValues v = new ContentValues();
		v.put("count", count);
		try {
			DBTool.getInstance().updateRecord(Constant.PRODUCT_CAR, v,
					" productno = ?", new String[] { productno });
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param ID
	 *            名称
	 * @param content
	 *            内容
	 */
	public void insert(String ID, String title, String jg, String cz,
			String gg, String zl, String tc, int count, String productno,
			String image) {
		Log.v("=======insert========", " ID = " + ID);
		ContentValues v = new ContentValues();
		v.put("productCarId", ID);
		v.put("title", title);
		v.put("jg", jg);
		v.put("cz", cz);
		v.put("gg", gg);
		v.put("zl", zl);
		v.put("tc", tc);
		v.put("count", count);
		v.put("productno", productno);
		v.put("image", image);
		try {
			DBTool.getInstance().insertRecord(Constant.PRODUCT_CAR, v);
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除该条记录
	 * 
	 * @param ID
	 *            收藏的ID
	 */
	public void delete(String ID) {
		String sql = "DELETE FROM " + Constant.PRODUCT_CAR
				+ " WHERE productCarId=" + ID;
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
