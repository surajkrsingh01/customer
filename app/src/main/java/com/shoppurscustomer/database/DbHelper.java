package com.shoppurscustomer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shweta on 6/9/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";
    private static final String DATABASE_NAME = Constants.APP_NAME+".db";
    private Context context;
    private static final String CART_TABLE = "MyCart";
    private static  final String shopId ="shopId";
    private static  final String shopName ="shopName";
    private static  final String itemId ="itemId";
    private static  final String itemBarCode ="itemBarCode";
    private static  final String custId ="custId";
    private static  final String custName="custName";
    private static  final String itemName ="itemName";
    private static  final String itemSize ="itemSize";
    private static  final String itemQty ="itemQty";
    private static  final String itemPrice ="itemPrice";
    private static  final String itemColor ="itemColor";
    private static  final String totalAmount="totalAmount";
    private static final String UPDATED_AT = "updatedAt";
    private static final String CREATED_AT = "createdAt";

    private static final String FAVORITE_TABLE = "MyFavotie";
    private static final String shopCode = "shopCode";

    public static final String CREATE_CART_TABLE = "create table "+CART_TABLE +
            "("+itemId+" TEXT NOT NULL, " +
            " "+shopId+" TEXT NOT NULL, " +
            " "+custId+" TEXT NOT NULL, " +
            " "+itemBarCode+" TEXT, " +
            " "+shopName+" TEXT, " +
            " "+custName+" TEXT, " +
            " "+itemName+" TEXT, " +
            " "+itemQty+" TEXT, " +
            " "+itemPrice+" TEXT, " +
            " "+itemSize+" TEXT, " +
            " "+itemColor+" TEXT, " +
            " "+totalAmount+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_FAVORITE_SHOP = "create table "+FAVORITE_TABLE +
            "("+shopCode+" TEXT NOT NULL)";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 4);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_FAVORITE_SHOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // code to add the new contact
    public void add_to_Cart(CartItem cartItem) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(itemName, cartItem.getItemName());
        values.put(itemId, cartItem.getProdCode());
        values.put(itemBarCode, cartItem.getBarcode());
        values.put(itemSize, cartItem.getSize());
        values.put(itemPrice, cartItem.getPrice());
        values.put(itemQty, cartItem.getQuantity());
        values.put(custId, cartItem.getCustCode());
        values.put(custName, cartItem.getCustName());
        values.put(shopId, cartItem.getShopCode());
        values.put(shopName, cartItem.getShopName());
        values.put(totalAmount, cartItem.getTotalAmout());
        values.put(CREATED_AT, cartItem.getCreatedDate());
        values.put(UPDATED_AT, cartItem.getUpdatedDate());

        //Log.d("Added itemId", cartItem.getProdCode());
        //Log.d("Added shopId", cartItem.getShopCode());

        // Inserting Row
        db.insert(CART_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to add the new contact
    public void add_to_Cart(List<CartItem> cartItems) {

        SQLiteDatabase db = this.getWritableDatabase();
        for(CartItem cartItem: cartItems) {
            ContentValues values = new ContentValues();
            values.put(itemName, cartItem.getItemName());
            values.put(itemId, cartItem.getProdCode());
            values.put(itemBarCode, cartItem.getBarcode());
            values.put(itemSize, cartItem.getSize());
            values.put(itemPrice, cartItem.getPrice());
            values.put(itemQty, cartItem.getQuantity());
            values.put(custId, cartItem.getCustCode());
            values.put(custName, cartItem.getCustName());
            values.put(shopId, cartItem.getShopCode());
            values.put(shopName, cartItem.getShopName());
            values.put(totalAmount, cartItem.getTotalAmout());
            values.put(CREATED_AT, cartItem.getCreatedDate());
            values.put(UPDATED_AT, cartItem.getUpdatedDate());

            // Inserting Row
            db.insert(CART_TABLE, null, values);
        }
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    CartItem getCartItem(String itemId, String shopId) {
        SQLiteDatabase db = this.getReadableDatabase();
        CartItem cartItem = new CartItem();
        Cursor cursor = db.query(CART_TABLE, new String[] { itemId,
                        itemName, itemPrice, itemQty }, itemId + "=?" + " AND " + shopId + "=?",
                new String[] { itemId, shopId}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            cartItem.setItemName(cursor.getString(cursor.getColumnIndex(itemName)));
            cartItem.setPrice(cursor.getFloat(cursor.getColumnIndex(itemPrice)));
            cartItem.setQuantity(cursor.getInt(cursor.getColumnIndex(itemQty)));
            cartItem.setSize(cursor.getFloat(cursor.getColumnIndex(itemSize)));
            cartItem.setTotalAmout(cursor.getFloat(cursor.getColumnIndex(totalAmount)));
            cartItem.setProdCode(cursor.getString(cursor.getColumnIndex(itemId)));
            cartItem.setBarcode(cursor.getString(cursor.getColumnIndex(itemBarCode)));
            cartItem.setCustCode(cursor.getString(cursor.getColumnIndex(custId)));
            cartItem.setCustName(cursor.getString(cursor.getColumnIndex(custName)));
            cartItem.setShopCode(cursor.getString(cursor.getColumnIndex(shopId)));
            cartItem.setShopName(cursor.getString(cursor.getColumnIndex(shopName)));
            cartItem.setCreatedDate(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
            cartItem.setUpdatedDate(cursor.getString(cursor.getColumnIndex(UPDATED_AT)));
        }
        return cartItem;
    }

    public boolean isProductInCart(String itemCode, String shopCode){
        //Log.d("checking itemId", itemCode);
        //Log.d("checking shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { itemId,
                        itemName, itemPrice, itemQty }, itemId + "=?" + " AND " + shopId + "=?",
                new String[] { itemCode, shopCode}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            return true;
        }
        return false;
    }

    public int getProductQuantity(String itemCode, String shopCode){
        Log.d("getting itemId", itemCode);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { itemQty }, itemId + "=?" + " AND " + shopId + "=?",
                new String[] { itemCode, shopCode}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int qty = cursor.getInt(cursor.getColumnIndex(itemQty));
            return qty;
        }
        return 0;
    }

    // code to get all contacts in a list view
    public List<CartItem> getAllCartItems() {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + CART_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setItemName(cursor.getString(cursor.getColumnIndex(itemName)));
                cartItem.setPrice(cursor.getFloat(cursor.getColumnIndex(itemPrice)));
                cartItem.setQuantity(cursor.getInt(cursor.getColumnIndex(itemQty)));
                cartItem.setSize(cursor.getFloat(cursor.getColumnIndex(itemSize)));
                cartItem.setTotalAmout(cursor.getFloat(cursor.getColumnIndex(totalAmount)));
                cartItem.setProdCode(cursor.getString(cursor.getColumnIndex(itemId)));
                cartItem.setBarcode(cursor.getString(cursor.getColumnIndex(itemBarCode)));
                cartItem.setCustCode(cursor.getString(cursor.getColumnIndex(custId)));
                cartItem.setCustName(cursor.getString(cursor.getColumnIndex(custName)));
                cartItem.setShopCode(cursor.getString(cursor.getColumnIndex(shopId)));
                cartItem.setShopName(cursor.getString(cursor.getColumnIndex(shopName)));
                cartItem.setCreatedDate(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
                cartItem.setUpdatedDate(cursor.getString(cursor.getColumnIndex(UPDATED_AT)));

                // Adding Cart to list
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
        }

        // return contact list
        return cartItems;
    }

    // code to update the single contact
    public int updateCart(CartItem cartItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d("Updated itemId", cartItem.getProdCode());
        //Log.d("Updated shopId", cartItem.getShopCode());

        ContentValues values = new ContentValues();
        values.put(itemSize, cartItem.getSize());
        values.put(itemQty, cartItem.getQuantity());
        values.put(totalAmount, cartItem.getTotalAmout());

        // updating row
        return db.update(CART_TABLE, values, itemId + " = ?" + " AND " + shopId + "=?",
                new String[] { String.valueOf(cartItem.getProdCode()),  String.valueOf(cartItem.getShopCode()) });
    }

    // Deleting single contact
    public void deleteCart(CartItem cartItem) {
        Log.d("Deleted itemId", cartItem.getProdCode());
        Log.d("Deleted shopId", cartItem.getShopCode());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CART_TABLE, itemId + " = ?" + " AND " + shopId + "=?",
                new String[] { String.valueOf(cartItem.getProdCode()),  String.valueOf(cartItem.getShopCode()) });
        db.close();
    }

    // Deleting single contact
    public void deleteAllCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ CART_TABLE);
        db.close();
    }

    // Getting contacts Count
    public int getCartCount() {
        String countQuery = "SELECT  * FROM " + CART_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getShopCount(){
        int count =0;
        String countQuery = "SELECT  count(distinct shopId) as counter FROM " +  CART_TABLE;
        Log.d("countQuery", countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("counter"));
            cursor.close();
        }
        return count;
    }

    public float getTotalAmount(String shpcode){
        float totalAmount=0;
        String countQuery = "SELECT  sum(totalAmount) as totalAmount FROM " +  CART_TABLE +" where shopId = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{shpcode});
        if(cursor.moveToFirst()) {
            totalAmount = cursor.getFloat(cursor.getColumnIndex("totalAmount"));
            cursor.close();
        }
        return totalAmount;
    }

    public int getTotalQuantity(String shpcode){
        int totalQty=0;
        String countQuery = "SELECT  sum(itemQty) as totalQty FROM " +  CART_TABLE +" where shopId = ?";
        Log.d("countQuery ", countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{ shpcode });
        if(cursor.moveToFirst()) {
            totalQty = cursor.getInt(cursor.getColumnIndex("totalQty"));
            cursor.close();
        }
        return totalQty;
    }


    public void add_to_Favorite(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(shopCode, code);
        Log.d(TAG, code+" ");
        double val = db.insert(FAVORITE_TABLE, null, values);
        Log.d(TAG, val+" ");
        db.close(); // Closing database connection
    }
    public void add_to_Favorite(List<String> mList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i =0;i<mList.size();i++) {
            ContentValues values = new ContentValues();
            values.put(shopCode, mList.get(i));
            double val = db.insert(FAVORITE_TABLE, null, values);
        }
        db.close(); // Closing database connection
    }
    // Deleting single contact
    public void remove_from_Favorite(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, code+" ");
        db.delete(FAVORITE_TABLE, shopCode + " = ?", new String[] { code });
        db.close();
    }
    public boolean isShopFavorited(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FAVORITE_TABLE, new String[] { shopCode }, shopCode + "=?",
                new String[] { code }, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            return true;
        }
        return false;
    }

    public void deleteAllFavorite() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ FAVORITE_TABLE);
        db.close();
    }
}