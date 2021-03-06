package com.shoppurs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.shoppurs.models.Coupon;
import com.shoppurs.models.DeliveryAddress;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyToDo;
import com.shoppurs.models.ProductColor;
import com.shoppurs.models.ProductComboDetails;
import com.shoppurs.models.ProductComboOffer;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductSize;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shweta on 6/9/2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String TAG = "DbHelper";
    private static final String DATABASE_NAME = Constants.APP_NAME+".db";

    private static final String FAVORITE_TABLE = "MyFavotie";
    public static final String PRODUCT_UNIT_TABLE = "PRODUCT_UNIT_TABLE";
    public static final String DELIVERY_ADDRESS_TABLE = "DELIVERY_ADDRESS_TABLE";
    public static final String PRODUCT_SIZE_TABLE = "PRODUCT_SIZE_TABLE";
    public static final String SHOP_DELIVERY_DETAILS_TABLE = "SHOP_DELIVERY_DETAILS_TABLE";
    public static final String PRODUCT_COLOR_TABLE = "PRODUCT_COLOR_TABLE";
    public static final String CART_TABLE = "CART_TABLE";
    public static final String PROD_FREE_OFFER_TABLE = "PROD_FREE_OFFER_TABLE";
    public static final String PROD_PRICE_TABLE = "PROD_PRICE_TABLE";
    public static final String PROD_PRICE_DETAIL_TABLE = "PROD_PRICE_DETAIL_TABLE";
    public static final String PROD_COMBO_TABLE = "PROD_COMBO_TABLE";
    public static final String PROD_COMBO_DETAIL_TABLE = "PROD_COMBO_DETAIL_TABLE";
    public static final String COUPON_TABLE = "COUPON_TABLE";
    public static final String TODOLIST_PRODUCT_TABLE = "TODOLIST_PRODUCT_TABLE";
    public static final String TODOLIST_TABLE = "TODOLIST_TABLE";


    public static final String ID = "id";
    public static final String PRODID = "prod_id";
    public static final String SHOP_CODE = "shop_code";
    public static final String TODO_NAME = "todo_name";
    public static final String DISCOUNT = "discount";
    public static final String SHOPPURS_DISCOUNT = "shoppurs_discount";
    public static final String CUST_CODE = "cust_code";
    public static final String CAT_ID = "cat_id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String PROD_SUB_CAT_ID = "PROD_SUB_CAT_ID";
    public static final String PROD_CAT_ID = "PROD_CAT_ID";
    public static final String PROD_NAME = "PROD_NAME";
    public static final String PROD_CODE = "PROD_CODE";
    public static final String PROD_BARCODE = "PROD_BARCODE";
    public static final String IS_BARCODE_AVAILABLE = "IS_BARCODE_AVAILABLE";
    public static final String PROD_DESC = "PROD_DESC";
    public static final String PROD_MRP = "PROD_MRP";
    public static final String PROD_SP = "PROD_SP";
    public static final String PROD_REORDER_LEVEL = "PROD_REORDER_LEVEL";
    public static final String PROD_QOH = "PROD_QOH";
    public static final String PROD_HSN_CODE = "PROD_HSN_CODE";
    public static final String PROD_CGST = "PROD_CGST";
    public static final String PROD_IGST = "PROD_IGST";
    public static final String PROD_SGST = "PROD_SGST";
    public static final String PROD_CGST_PER = "PROD_CGST_PER";
    public static final String PROD_IGST_PER = "PROD_IGST_PER";
    public static final String PROD_SGST_PER = "PROD_SGST_PER";
    public static final String PROD_WARRANTY = "PROD_WARRANTY";
    public static final String PROD_MFG_DATE = "PROD_MFG_DATE";
    public static final String PROD_EXPIRY_DATE = "PROD_EXPIRY_DATE";
    public static final String PROD_MFG_BY = "PROD_MFG_BY";
    public static final String PROD_IMAGE_1 = "PROD_IMAGE_1";
    public static final String PROD_IMAGE_2 = "PROD_IMAGE_2";
    public static final String PROD_IMAGE_3 = "PROD_IMAGE_3";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String TOTAL_MRP_AMOUNT = "totalMRPAmount";
    public static final String CART_TYPE = "cartType";
    public static final String  FREQUENCY = "frequency";
    public static final String  FREQUENCY_START_DATE = "frequencyStartDate";
    public static final String  FREQUENCY_END_DATE = "frequencyEndDate";
    public static final String  FREQUENCY_QTY = "frequencyQty";
    public static final String  FREQUENCY_VALUE = "frequencyValue";
    public static final String TOTAL_QTY = "totalQty";
    public static final String SIZE = "size";
    public static final String COLOR = "color";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String UPDATED_BY = "UPDATED_BY";
    public static final String COUPON_ID = "couponId";
    public static final String COUPON_NAME = "couponName";
    public static final String COUPON_PER = "couponPer";
    public static final String COUPON_MAX_AMOUNT = "couponMaxAmount";
    public static final String COUPON_AMOUNT = "couponAmount";
    public static final String DELIVERY_ADDRESS_ID = "deliveryAddressId";
    public static final String DELIVERY_HOUSE_NO = "deliveryHouse";
    public static final String DELIVERY_LANDMARK = "deliveryLandmark";
    public static final String DELIVERY_CUSTOMER_NAME = "deliveryCustomerName";
    public static final String IS_DELIVERY_DEFAULT = "isDeliveryDefault";
    public static final String DELIVERY_CUSTOMER_CODE = "deliveryCustomerCode";
    public static final String DELIVERY_CUSTOMER_MOBILE= "deliveryMobile";
    public static final String DELIVERY_ADDRESS = "deliveryAddress";
    public static final String DELIVERY_PIN = "deliveryPin";
    public static final String DELIVERY_STATE = "deliveryState";
    public static final String DELIVERY_CITY = "deliveryCity";
    public static final String DELIVERY_COUNTRY = "deliveryCountry";
    public static final String DELIVERY_CHARGES= "deliveryCharges";
    public static final String LATITUDE= "latitude";
    public static final String LONGITUDE= "longitude";
    public static final String OFFER_ID= "offerId";
    public static final String OFFER_TYPE= "offerType";
    public static final String OFFER_ITEM_COUNTER= "offerItemCounter";
    public static final String FREE_PRODUCT_POSITION= "freeProductPosition";
    public static final String COMBO_PRODUCT_IDS= "comboProductIds";
    public static final String UNIT= "unit";
    public static final String UPDATED_AT = "updatedAt";
    public static final String CREATED_AT = "createdAt";


    public static final String PROD_ID= "prodId";
    public static final String PROD_BUY_ID= "prodBuyId";
    public static final String PROD_FREE_ID= "prodFreeId";
    public static final String PROD_BUY_QTY= "prodBuyQty";
    public static final String PROD_FREE_QTY= "prodFreeQty";
    public static final String START_DATE= "startDate";
    public static final String END_DATE= "endDate";
    public static final String STATUS= "status";

    public static final String VALUE= "value";
    public static final String SIZE_ID= "sizeId";
    public static final String COLOR_NAME= "colorName";
    public static final String COLOR_VALUE= "colorValue";

    private static String RET_LAT = "retLat";
    private static String RET_LONG = "retLong";
    private static String IS_DELIVERY_AVAILABLE = "isDeliveryAvailable";
    private static String MIN_DELIVERY_AMOUNT = "minDeliveryAmount"; //charge per km
    private static String MIN_DELIVERY_TIME = "minDeliverytime";
    private static String MIN_DELIVERY_DISTANCE = "minDeliverydistance";
    private static String CHARGE_AFTER_MIN_DISTANCE = "chargesAfterMinDistance";

    public static final String CREATE_SHOP_DELIVERY_DETAILS_TABLE = "create table "+SHOP_DELIVERY_DETAILS_TABLE +
            "("+SHOP_CODE+" TEXT UNIQUE, " +
            " "+RET_LAT+" TEXT, " +
            " "+CART_TYPE+" TEXT, " +
            " "+RET_LONG+" TEXT, " +
            " "+DELIVERY_CHARGES+" TEXT, " +
            " "+IS_DELIVERY_AVAILABLE+" TEXT, " +
            " "+MIN_DELIVERY_AMOUNT+" TEXT, " +
            " "+MIN_DELIVERY_TIME+" TEXT, " +
            " "+MIN_DELIVERY_DISTANCE+" TEXT, " +
            " "+CHARGE_AFTER_MIN_DISTANCE+" TEXT)";

    public static final String CREATE_PRODUCT_UNIT_TABLE = "create table "+PRODUCT_UNIT_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+PROD_ID+" TEXT, " +
            " "+NAME+" TEXT, " +
            " "+VALUE+" TEXT, " +
            " "+STATUS+" TEXT)";

    public static final String CREATE_PRODUCT_SIZE_TABLE = "create table "+PRODUCT_SIZE_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+PROD_ID+" TEXT, " +
            " "+SIZE+" TEXT, " +
            " "+STATUS+" TEXT)";

    public static final String CREATE_PRODUCT_COLOR_TABLE = "create table "+PRODUCT_COLOR_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+PROD_ID+" TEXT, " +
            " "+SIZE_ID+" TEXT, " +
            " "+COLOR_NAME+" TEXT, " +
            " "+COLOR_VALUE+" TEXT, " +
            " "+STATUS+" TEXT)";

    public static final String CREATE_PROD_FREE_OFFER_TABLE = "create table "+PROD_FREE_OFFER_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+NAME+" TEXT NOT NULL, " +
            " "+PROD_BUY_ID+" TEXT NOT NULL, " +
            " "+PROD_FREE_ID+" TEXT NOT NULL, " +
            " "+PROD_BUY_QTY+" TEXT NOT NULL, " +
            " "+PROD_FREE_QTY+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+START_DATE+" TEXT, " +
            " "+END_DATE+" TEXT, " +
            " "+STATUS+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_PROD_PRICE_TABLE = "create table "+PROD_PRICE_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+PROD_ID+" TEXT NOT NULL, " +
            " "+NAME+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+START_DATE+" TEXT, " +
            " "+END_DATE+" TEXT, " +
            " "+STATUS+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_PROD_PRICE_DETAIL_TABLE = "create table "+PROD_PRICE_DETAIL_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+OFFER_ID+" TEXT NOT NULL, " +
            " "+TOTAL_QTY+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+PROD_SP+" TEXT, " +
            " "+STATUS+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_PROD_COMBO_TABLE = "create table "+PROD_COMBO_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+NAME+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+START_DATE+" TEXT, " +
            " "+END_DATE+" TEXT, " +
            " "+STATUS+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_PROD_COMBO_DETAIL_TABLE = "create table "+PROD_COMBO_DETAIL_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+OFFER_ID+" TEXT NOT NULL, " +
            " "+PROD_ID+" TEXT NOT NULL, " +
            " "+TOTAL_QTY+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+PROD_SP+" TEXT, " +
            " "+STATUS+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";

    public static final String CREATE_COUPON_TABLE = "create table "+COUPON_TABLE +
            "("+ID+" TEXT NOT NULL, " +
            " "+CART_TYPE+" TEXT, " +
            " "+COUPON_PER+" TEXT NOT NULL, " +
            " "+COUPON_MAX_AMOUNT+" TEXT NOT NULL, " +
            " "+COUPON_AMOUNT+" TEXT NOT NULL, " +
            " "+SHOP_CODE+" TEXT NOT NULL, " +
            " "+NAME+" TEXT NOT NULL, " +
            " "+STATUS+" TEXT, " +
            " "+START_DATE+" TEXT, " +
            " "+END_DATE+" TEXT, " +
            " "+CREATED_BY+" TEXT, " +
            " "+UPDATED_BY+" TEXT, " +
            " "+CREATED_AT+" TEXT, " +
            " "+UPDATED_AT+" TEXT)";


    public static final String CREATE_CART_TABLE = "create table "+CART_TABLE +
            "("+ID+" TEXT, " +
            " "+PRODID+" TEXT, " +
            //" "+CUST_CODE+" TEXT, " +
            " "+SHOP_CODE+" TEXT, " +
            " "+COUPON_ID+" TEXT, " +
            " "+COUPON_NAME+" TEXT, " +
            " "+COUPON_PER+" TEXT, " +
            " "+COUPON_MAX_AMOUNT+" TEXT , " +
            " "+DISCOUNT+" TEXT , " +
            " "+SHOPPURS_DISCOUNT+" TEXT , " +
            " "+OFFER_ID+" TEXT , " +
            " "+OFFER_TYPE+" TEXT , " +
            " "+DELIVERY_ADDRESS+" TEXT , " +
            " "+DELIVERY_PIN+" TEXT , " +
            " "+DELIVERY_COUNTRY+" TEXT , " +
            " "+DELIVERY_STATE+" TEXT , " +
            " "+DELIVERY_CITY+" TEXT, " +
            " "+DELIVERY_CHARGES+" TEXT, " +
            " "+LONGITUDE+" TEXT, " +
            " "+LATITUDE+" TEXT , " +
            " "+OFFER_ITEM_COUNTER+" TEXT, " +
            " "+FREE_PRODUCT_POSITION+" INTEGER, " +
            " "+COMBO_PRODUCT_IDS+" TEXT, " +
            " "+PROD_SUB_CAT_ID+" TEXT, " +
            " "+PROD_CAT_ID+" TEXT, " +
            " "+PROD_NAME+" TEXT NOT NULL, " +
            " "+PROD_CODE+" TEXT, " +
            " "+PROD_BARCODE+" TEXT, " +
            " "+PROD_DESC+" TEXT, " +
            " "+PROD_MRP+" TEXT, " +
            " "+PROD_SP+" TEXT, " +
            " "+PROD_HSN_CODE+" TEXT, " +
            " "+PROD_CGST+" TEXT, " +
            " "+PROD_IGST+" TEXT, " +
            " "+PROD_SGST+" TEXT, " +
            " "+PROD_CGST_PER+" TEXT, " +
            " "+PROD_IGST_PER+" TEXT, " +
            " "+PROD_SGST_PER+" TEXT, " +
            " "+TOTAL_QTY+" TEXT, " +
            " "+TOTAL_AMOUNT+" TEXT, " +
            " "+UNIT+" TEXT, " +
            " "+COLOR+" TEXT, " +
            " "+SIZE+" TEXT, " +

            " "+ CART_TYPE +" TEXT, " +
            " "+FREQUENCY+" TEXT, " +
            " "+FREQUENCY_START_DATE+" TEXT, " +
            " "+FREQUENCY_END_DATE+" TEXT, " +
            " "+FREQUENCY_QTY+" TEXT, " +
            " "+FREQUENCY_VALUE+" TEXT, " +

            " "+IS_BARCODE_AVAILABLE+" TEXT, " +
            " "+PROD_IMAGE_1+" TEXT, " +
            " "+PROD_IMAGE_2+" TEXT, " +
            " "+PROD_IMAGE_3+" TEXT)";


    public static final String CREATE_TODOLIST_PRODUCT_TABLE = "create table "+TODOLIST_PRODUCT_TABLE +
            "("+ID+" TEXT, " +
            " "+PRODID+" TEXT, " +
            //" "+CUST_CODE+" TEXT, " +
            " "+SHOP_CODE+" TEXT, " +
            " "+PROD_NAME+" TEXT NOT NULL, " +
            " "+PROD_CODE+" TEXT UNIQUE, " +
            " "+PROD_BARCODE+" TEXT, " +
            " "+PROD_DESC+" TEXT, " +
            " "+PROD_MRP+" TEXT, " +
            " "+PROD_SP+" TEXT, " +
            " "+PROD_HSN_CODE+" TEXT, " +
            " "+PROD_CGST+" TEXT, " +
            " "+PROD_IGST+" TEXT, " +
            " "+PROD_SGST+" TEXT, " +
            " "+PROD_CGST_PER+" TEXT, " +
            " "+PROD_IGST_PER+" TEXT, " +
            " "+PROD_SGST_PER+" TEXT, " +
            " "+TOTAL_QTY+" TEXT, " +
            " "+TOTAL_AMOUNT+" TEXT, " +
            " "+UNIT+" TEXT, " +
            " "+COLOR+" TEXT, " +
            " "+SIZE+" TEXT, " +
            " "+IS_BARCODE_AVAILABLE+" TEXT, " +
            " "+PROD_IMAGE_1+" TEXT, " +
            " "+PROD_IMAGE_2+" TEXT, " +
            " "+PROD_IMAGE_3+" TEXT)";


    public static final String CREATE_TODOLIST_TABLE = "create table "+TODOLIST_TABLE +
            "("+ID+" TEXT, " +
            " "+TODO_NAME+" TEXT, " +
            " "+SHOP_CODE+" TEXT)";


    public static final String CREATE_FAVORITE_SHOP = "create table "+FAVORITE_TABLE +
            "("+SHOP_CODE+" TEXT NOT NULL)";

    public static final String CREATE_DELIVERY_ADDRESS = "create table "+DELIVERY_ADDRESS_TABLE +
            "("+ID+" INTEGER PRIMARY KEY, " +
            " "+DELIVERY_CUSTOMER_CODE+" TEXT NOT NULL, " +
            " "+DELIVERY_CUSTOMER_NAME+" TEXT NOT NULL, " +
            " "+DELIVERY_CUSTOMER_MOBILE+" TEXT NOT NULL, " +
            " "+DELIVERY_HOUSE_NO+" TEXT, " +
            " "+DELIVERY_ADDRESS+" TEXT, " +
            " "+IS_DELIVERY_DEFAULT+" TEXT, " +
            " "+LATITUDE+" TEXT, " +
            " "+LONGITUDE+" TEXT, " +
            " "+DELIVERY_LANDMARK+" TEXT, " +
            " "+DELIVERY_CITY+" TEXT, " +
            " "+DELIVERY_STATE+" TEXT, " +
            " "+DELIVERY_COUNTRY+" TEXT, " +
            " "+DELIVERY_PIN+" TEXT)";


    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 23);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_FAVORITE_SHOP);
        db.execSQL(CREATE_DELIVERY_ADDRESS);
        db.execSQL(CREATE_PRODUCT_UNIT_TABLE);
        db.execSQL(CREATE_PRODUCT_SIZE_TABLE);
        db.execSQL(CREATE_PRODUCT_COLOR_TABLE);
        db.execSQL(CREATE_PROD_FREE_OFFER_TABLE);
        db.execSQL(CREATE_PROD_PRICE_TABLE);
        db.execSQL(CREATE_PROD_PRICE_DETAIL_TABLE);
        db.execSQL(CREATE_PROD_COMBO_TABLE);
        db.execSQL(CREATE_PROD_COMBO_DETAIL_TABLE);
        db.execSQL(CREATE_COUPON_TABLE);
        db.execSQL(CREATE_SHOP_DELIVERY_DETAILS_TABLE);
        db.execSQL(CREATE_TODOLIST_PRODUCT_TABLE);
        db.execSQL(CREATE_TODOLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+PROD_FREE_OFFER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PROD_PRICE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PROD_PRICE_DETAIL_TABLE);
        db.execSQL(CREATE_PROD_FREE_OFFER_TABLE);
        db.execSQL(CREATE_PROD_PRICE_TABLE);
        db.execSQL(CREATE_PROD_PRICE_DETAIL_TABLE);

        db.execSQL("DROP TABLE IF EXISTS "+PROD_COMBO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PROD_COMBO_DETAIL_TABLE);
        db.execSQL(CREATE_PROD_COMBO_TABLE);
        db.execSQL(CREATE_PROD_COMBO_DETAIL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CART_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+COUPON_TABLE);
        db.execSQL(CREATE_COUPON_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PRODUCT_UNIT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PRODUCT_SIZE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+PRODUCT_COLOR_TABLE);
        db.execSQL(CREATE_PRODUCT_UNIT_TABLE);
        db.execSQL(CREATE_PRODUCT_SIZE_TABLE);
        db.execSQL(CREATE_PRODUCT_COLOR_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+DELIVERY_ADDRESS_TABLE);
        db.execSQL(CREATE_DELIVERY_ADDRESS);
        db.execSQL("DROP TABLE IF EXISTS "+SHOP_DELIVERY_DETAILS_TABLE);
        db.execSQL(CREATE_SHOP_DELIVERY_DETAILS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TODOLIST_PRODUCT_TABLE);
        db.execSQL(CREATE_TODOLIST_PRODUCT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TODOLIST_TABLE);
        db.execSQL(CREATE_TODOLIST_TABLE);
    }

    public boolean addProductToCart(MyProduct item, String cartType){
        if(item.getProductPriceOffer() != null ) {
            ProductPriceOffer productPriceOffer = item.getProductPriceOffer();
            addProductPriceOffer(productPriceOffer, Utility.getTimeStamp(),Utility.getTimeStamp(), item.getShopCode());
            for(ProductPriceDetails productPriceDetails : productPriceOffer.getProductPriceDetails()){
                addProductPriceDetailOffer(productPriceDetails,  Utility.getTimeStamp(),Utility.getTimeStamp(),  item.getShopCode());
            }
        }else if(item.getProductDiscountOffer() != null ){
            ProductDiscountOffer productDiscountOffer = item.getProductDiscountOffer();
            addProductFreeOffer(productDiscountOffer,  Utility.getTimeStamp(),Utility.getTimeStamp(),  item.getShopCode());
        }

        if(item.getProductUnitList()!=null) {
            for (int i = 0; i < item.getProductUnitList().size(); i++) {
                addProductUnit(item.getProductUnitList().get(i), item.getId());
            }
        }
        if(item.getProductSizeList()!=null) {
            for (int i = 0; i < item.getProductSizeList().size(); i++) {
                addProductSize(item.getProductSizeList().get(i), item.getId());
                for (int j = 0; j < item.getProductSizeList().get(i).getProductColorList().size(); j++) {
                    addProductColor(item.getProductSizeList().get(i).getProductColorList().get(j), item.getProductSizeList().get(i).getId());
                }
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(PRODID, item.getProdId());
        //contentValues.put(CUST_CODE, item.getCustCode());
        contentValues.put(SHOP_CODE, item.getShopCode());
        contentValues.put(OFFER_ID, item.getOfferId());
        contentValues.put(OFFER_TYPE, item.getOfferType());
        contentValues.put(PROD_SUB_CAT_ID, item.getSubCatId());
        contentValues.put(PROD_CAT_ID, item.getCatId());
        contentValues.put(PROD_CODE, item.getCode());
        contentValues.put(PROD_BARCODE, item.getBarCode());
        contentValues.put(PROD_NAME, item.getName());
        contentValues.put(PROD_DESC, item.getDesc());
        contentValues.put(PROD_MRP, item.getMrp());
        contentValues.put(PROD_SP, item.getSellingPrice());
        contentValues.put(PROD_HSN_CODE, item.getProdHsnCode());
        if(cartType.equals("frequency")){
            ProductFrequency frequency = item.getFrequency();
            contentValues.put(CART_TYPE, "frequency");
            contentValues.put(FREQUENCY, frequency.getName());
            contentValues.put(FREQUENCY_START_DATE, frequency.getStartDate());
            contentValues.put(FREQUENCY_END_DATE, frequency.getEndDate());
            contentValues.put(FREQUENCY_QTY, frequency.getQyantity());
            contentValues.put(FREQUENCY_VALUE, frequency.getNoOfDays());
        }else  contentValues.put(CART_TYPE, "normal");
        if(item.getComboProductIds() != null){
            contentValues.put(PROD_CGST, item.getProdCgst());
            contentValues.put(PROD_IGST, item.getProdIgst());
            contentValues.put(PROD_SGST, item.getProdSgst());
            Log.i("dbhelper","cgst "+item.getProdCgst());
            Log.i("dbhelper","sgst "+item.getProdSgst());
        }else{
            contentValues.put(PROD_CGST_PER, item.getProdCgst());
            contentValues.put(PROD_IGST_PER, item.getProdIgst());
            contentValues.put(PROD_SGST_PER, item.getProdSgst());
            float rate = ((item.getSellingPrice() * (item.getProdCgst()+item.getProdSgst()))/(100 +
                    (item.getProdCgst()+item.getProdSgst())));
            Log.d("Rate ", ""+rate);
            contentValues.put(PROD_CGST, rate);
            contentValues.put(PROD_IGST, rate/2);
            contentValues.put(PROD_SGST, rate);
            Log.i("dbhelper","cgst "+rate);
            Log.i("dbhelper","sgst "+rate);
        }
        contentValues.put(PROD_IMAGE_1, item.getProdImage1());
        contentValues.put(PROD_IMAGE_2, item.getProdImage2());
        contentValues.put(PROD_IMAGE_3, item.getProdImage3());
        contentValues.put(IS_BARCODE_AVAILABLE, item.getIsBarcodeAvailable());
        contentValues.put(TOTAL_AMOUNT, item.getTotalAmount());
        contentValues.put(TOTAL_QTY, item.getQuantity());
        contentValues.put(OFFER_ITEM_COUNTER, item.getOfferItemCounter());
        contentValues.put(FREE_PRODUCT_POSITION, item.getFreeProductPosition());
        contentValues.put(COMBO_PRODUCT_IDS, item.getComboProductIds());

        if(TextUtils.isEmpty(item.getUnit())){
            if(item.getProductUnitList()!=null && item.getProductUnitList().size() > 0){
                ProductUnit unit = item.getProductUnitList().get(0);
                contentValues.put(UNIT, unit.getUnitValue()+" "+unit.getUnitName());
                Log.d(UNIT, unit.getUnitValue()+" "+unit.getUnitName());
            }else{
                contentValues.put(UNIT, item.getUnit());
            }

        }else{
            contentValues.put(UNIT, item.getUnit());
        }

        if(TextUtils.isEmpty(item.getSize())){
            if(item.getProductSizeList()!=null && item.getProductSizeList().size() > 0){
                ProductSize size = item.getProductSizeList().get(0);
                contentValues.put(SIZE, size.getSize());
                if(TextUtils.isEmpty(item.getColor())){
                    if(size.getProductColorList()!=null && size.getProductColorList().size() > 0){
                        ProductColor color = size.getProductColorList().get(0);
                        contentValues.put(COLOR, color.getColorName());
                    }else{
                        contentValues.put(COLOR, item.getColor());
                    }
                }else{
                    contentValues.put(COLOR, item.getColor());
                }
            }else{
                contentValues.put(SIZE, item.getSize());
                contentValues.put(COLOR, item.getColor());
            }

        }else{
            contentValues.put(SIZE, item.getSize());
            contentValues.put(COLOR, item.getColor());
        }


        try {
            Log.d(ID, item.getId());
            Log.d(SHOP_CODE, item.getShopCode());
            Log.d(OFFER_ID, item.getOfferId());
           // Log.d(OFFER_TYPE, item.getOfferType());
            Log.d(PROD_SUB_CAT_ID, item.getSubCatId());
            Log.d(PROD_CAT_ID, item.getCatId());
            Log.d(PROD_CODE, item.getCode());
         //   Log.d(PROD_BARCODE, item.getBarCode());
            Log.d(PROD_NAME, item.getName());
            Log.d(PROD_DESC, item.getDesc());
            Log.d(PROD_MRP, String.valueOf(item.getMrp()));
            Log.d(PROD_SP, String.valueOf(item.getSellingPrice()));
        //    Log.d(PROD_HSN_CODE, item.getProdHsnCode());
            Log.d(PROD_CGST, String.valueOf(item.getProdCgst()));
            Log.d(PROD_IGST, String.valueOf(item.getProdIgst()));
            Log.d(PROD_SGST, String.valueOf(item.getProdSgst()));
          //  Log.d(PROD_IMAGE_1, item.getProdImage1());
          //  Log.d(PROD_IMAGE_2, item.getProdImage2());
          //  Log.d(PROD_IMAGE_3, item.getProdImage3());
            Log.d(IS_BARCODE_AVAILABLE, item.getIsBarcodeAvailable());
            Log.d(TOTAL_AMOUNT, String.valueOf(item.getTotalAmount()));
            Log.d(TOTAL_QTY, String.valueOf(item.getQuantity()));
            Log.d(OFFER_ITEM_COUNTER, String.valueOf(item.getOfferItemCounter()));
            Log.d(FREE_PRODUCT_POSITION, String.valueOf(item.getFreeProductPosition()));
         //   Log.d(COMBO_PRODUCT_IDS, item.getComboProductIds());
            Log.d(SIZE, item.getSize());
            Log.d(COLOR, item.getColor());
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }


        // contentValues.put(UNIT, item.getUnit());
        double status = db.insert(CART_TABLE, null, contentValues);
        Log.i("DbHelper",item.getName()+" added in cart status "+status);
        return true;
    }

    public boolean addProductInTODOList(MyProduct item, String todoId){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, todoId);
        contentValues.put(PRODID, item.getProdId());
        //contentValues.put(CUST_CODE, item.getCustCode());
        contentValues.put(SHOP_CODE, item.getShopCode());
        contentValues.put(PROD_CODE, item.getCode());
        contentValues.put(PROD_BARCODE, item.getBarCode());
        contentValues.put(PROD_NAME, item.getName());
        contentValues.put(PROD_DESC, item.getDesc());
        contentValues.put(PROD_MRP, item.getMrp());
        contentValues.put(PROD_SP, item.getSellingPrice());
        contentValues.put(PROD_HSN_CODE, item.getProdHsnCode());
        contentValues.put(PROD_IMAGE_1, item.getProdImage1());
        contentValues.put(PROD_IMAGE_2, item.getProdImage2());
        contentValues.put(PROD_IMAGE_3, item.getProdImage3());
        contentValues.put(IS_BARCODE_AVAILABLE, item.getIsBarcodeAvailable());
        contentValues.put(TOTAL_AMOUNT, item.getTotalAmount());
        contentValues.put(TOTAL_QTY, item.getQuantity());

        double status = db.insert(TODOLIST_PRODUCT_TABLE, null, contentValues);
        Log.i("DbHelper",item.getName()+" added in ToDo List status "+status);
        return true;
    }

    public ArrayList<MyProduct> getToDoListProducts(String shopCode, String todoId){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+TODOLIST_PRODUCT_TABLE +" WHERE "+SHOP_CODE+" = ? AND "+ID+"=?";
        Cursor res =  db.rawQuery(query, new String[]{shopCode, todoId});
        ArrayList<MyProduct> itemList=new ArrayList<>();
        MyProduct productItem = null;
        if(res.moveToFirst()){
            do{
                productItem=new MyProduct();
                productItem.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                productItem.setId(res.getString(res.getColumnIndex(ID)));
                productItem.setProdId(res.getInt(res.getColumnIndex(PRODID)));
                productItem.setName(res.getString(res.getColumnIndex(PROD_NAME)));
                productItem.setCode(res.getString(res.getColumnIndex(PROD_CODE)));
                productItem.setBarCode(res.getString(res.getColumnIndex(PROD_BARCODE)));
                productItem.setDesc(res.getString(res.getColumnIndex(PROD_DESC)));
                productItem.setProdHsnCode(res.getString(res.getColumnIndex(PROD_HSN_CODE)));
                productItem.setProdImage1(res.getString(res.getColumnIndex(PROD_IMAGE_1)));
                productItem.setProdImage2(res.getString(res.getColumnIndex(PROD_IMAGE_2)));
                productItem.setProdImage3(res.getString(res.getColumnIndex(PROD_IMAGE_3)));
                productItem.setIsBarcodeAvailable(res.getString(res.getColumnIndex(IS_BARCODE_AVAILABLE)));
                productItem.setMrp(res.getFloat(res.getColumnIndex(PROD_MRP)));
                productItem.setSellingPrice(res.getFloat(res.getColumnIndex(PROD_SP)));
                productItem.setTotalAmount(res.getFloat(res.getColumnIndex(TOTAL_AMOUNT)));
                productItem.setQuantity(res.getInt(res.getColumnIndex(TOTAL_QTY)));
                itemList.add(productItem);
            }while (res.moveToNext());
        }

        return itemList;
    }

    public boolean removeProductFromTODoList(String id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(TODOLIST_PRODUCT_TABLE, ID+" = ? AND "+PROD_SP+" != ?"  + " AND " + SHOP_CODE + "=?",new String[]{String.valueOf(id),String.valueOf(0f),shopCode});
        Log.d(TAG, "removed product from ToDo List status "+val + "id  "+id);
        return true;
    }

    public boolean checkProdExistInToDoList(String prodId, String shopCode){
        Log.d("prodId "+prodId, "shopCode "+shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+TODOLIST_PRODUCT_TABLE+" WHERE "+ID+" = ?" + " AND " + SHOP_CODE + "=?"  + " AND " + PROD_SP + "!=? ";
        Cursor res =  db.rawQuery(query, new String[]{ prodId, shopCode, String.valueOf(0f)});
        boolean exist = false;
        if(res.moveToFirst()){
            //prodCode = res.getString(res.getColumnIndex(PROD_BARCODE));
            exist = true;

        }else{
            // prodCode =  "no";
        }
        return exist;
    }


    public boolean addTODOList(MyToDo item){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(TODO_NAME, item.getName());
        contentValues.put(SHOP_CODE, item.getShopCode());

        double status = db.insert(TODOLIST_TABLE, null, contentValues);
        Log.i("DbHelper",item.getName()+" added in ToDo List status "+status);
        return true;
    }

    public ArrayList<MyToDo> getToDoList(String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+TODOLIST_TABLE +" WHERE "+SHOP_CODE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{shopCode});
        ArrayList<MyToDo> itemList=new ArrayList<>();
        MyToDo item = null;
        if(res.moveToFirst()){
            do{
                item=new MyToDo();
                item.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                item.setId(res.getString(res.getColumnIndex(ID)));
                item.setName(res.getString(res.getColumnIndex(TODO_NAME)));
                itemList.add(item);
            }while (res.moveToNext());
        }

        return itemList;
    }

    public boolean removeTODoList(String id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(TODOLIST_TABLE, ID+" = ?"  + " AND " + SHOP_CODE + "=?",new String[]{String.valueOf(id),shopCode});
        Log.d(TAG, "removed ToDo List status "+val + "id  "+id);
        return true;
    }


    // code to add the new contact
    public void addShopDeliveryDetails(ShopDeliveryModel shopDeliveryModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SHOP_CODE, shopDeliveryModel.getShopCode());
        contentValues.put(RET_LAT, shopDeliveryModel.getRetLat());
        contentValues.put(RET_LONG, shopDeliveryModel.getRetLong());
        contentValues.put(IS_DELIVERY_AVAILABLE, shopDeliveryModel.getIsDeliveryAvailable());
        contentValues.put(MIN_DELIVERY_AMOUNT, shopDeliveryModel.getMinDeliveryAmount());
        contentValues.put(MIN_DELIVERY_TIME, shopDeliveryModel.getMinDeliverytime());
        contentValues.put(MIN_DELIVERY_DISTANCE, shopDeliveryModel.getMinDeliverydistance());
        contentValues.put(CHARGE_AFTER_MIN_DISTANCE, shopDeliveryModel.getChargesAfterMinDistance());
        double status = db.insert(SHOP_DELIVERY_DETAILS_TABLE, null, contentValues);
        Log.d(TAG, "added shopDeliveryModel status "+status);
    }

    public void updateShopDeliveryDetails(ShopDeliveryModel shopDeliveryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DELIVERY_CHARGES, shopDeliveryModel.getNetDeliveryCharge());
        double val = db.update(SHOP_DELIVERY_DETAILS_TABLE, contentValues, SHOP_CODE + " = ?",
                new String[] { String.valueOf(shopDeliveryModel.getShopCode())});

        Log.d("DELIVERY_CHARGES ", shopDeliveryModel.getNetDeliveryCharge() +"");
        Log.d("Shop Code ", shopDeliveryModel.getShopCode() +"");
        Log.d("update Status ", val +"");
    }

    public ShopDeliveryModel getShopDeliveryDetails(String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        String mSql="select * from "+SHOP_DELIVERY_DETAILS_TABLE+" WHERE "+SHOP_CODE+" = ?";
        Cursor res =  db.rawQuery(mSql, new String[]{String.valueOf(shopCode)});
        ShopDeliveryModel item = new ShopDeliveryModel();
        if(res.moveToFirst()){
                item=new ShopDeliveryModel();
                item.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                item.setRetLat(res.getDouble(res.getColumnIndex(RET_LAT)));
                item.setRetLong(res.getDouble(res.getColumnIndex(RET_LONG)));
                item.setIsDeliveryAvailable(res.getString(res.getColumnIndex(IS_DELIVERY_AVAILABLE)));
                item.setMinDeliveryAmount(res.getDouble(res.getColumnIndex(MIN_DELIVERY_AMOUNT)));
                item.setMinDeliverytime(res.getString(res.getColumnIndex(MIN_DELIVERY_TIME)));
                item.setMinDeliverydistance(res.getInt(res.getColumnIndex(MIN_DELIVERY_DISTANCE)));
                item.setChargesAfterMinDistance(res.getDouble(res.getColumnIndex(CHARGE_AFTER_MIN_DISTANCE)));
                item.setNetDeliveryCharge(res.getDouble(res.getColumnIndex(DELIVERY_CHARGES)));
        }
        return item;
    }

    public List<ShopDeliveryModel> getShopDeliveryDetails(){
        SQLiteDatabase db = this.getReadableDatabase();
        String mSql="select * from "+SHOP_DELIVERY_DETAILS_TABLE+"";
        Cursor res =  db.rawQuery(mSql, null);
        ShopDeliveryModel item = null;
        List<ShopDeliveryModel> shopDeliveryModelList = new ArrayList<>();
        if(res.moveToFirst()){
            do {
                item = new ShopDeliveryModel();
                item.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                item.setRetLat(res.getDouble(res.getColumnIndex(RET_LAT)));
                item.setRetLong(res.getDouble(res.getColumnIndex(RET_LONG)));
                item.setIsDeliveryAvailable(res.getString(res.getColumnIndex(IS_DELIVERY_AVAILABLE)));
                item.setMinDeliveryAmount(res.getDouble(res.getColumnIndex(MIN_DELIVERY_AMOUNT)));
                item.setMinDeliverytime(res.getString(res.getColumnIndex(MIN_DELIVERY_TIME)));
                item.setMinDeliverydistance(res.getInt(res.getColumnIndex(MIN_DELIVERY_DISTANCE)));
                item.setChargesAfterMinDistance(res.getDouble(res.getColumnIndex(CHARGE_AFTER_MIN_DISTANCE)));
                item.setNetDeliveryCharge(res.getDouble(res.getColumnIndex(DELIVERY_CHARGES)));
                shopDeliveryModelList.add(item);
            }while (res.moveToNext());
        }
        return shopDeliveryModelList;
    }

    public float getAllShopsDeliveryCharges(){
        float totalCharges=0;
        String countQuery = "SELECT  sum("+DELIVERY_CHARGES+") as deliveryCharges FROM " +  SHOP_DELIVERY_DETAILS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor.moveToFirst()) {
            totalCharges = cursor.getFloat(cursor.getColumnIndex(DELIVERY_CHARGES));
            cursor.close();
        }
        return totalCharges;
    }

    public boolean removeShopDeliveryDetails(String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(SHOP_DELIVERY_DETAILS_TABLE,  SHOP_CODE +"=?",new String[]{shopCode});
        Log.d(TAG, "removed Coupon from cart status "+val + "shopCode  "+shopCode);
        return true;
    }

    public int getProductQuantity(String itemId, String shopCode, String cartType){
        Log.d("getting itemId", itemId);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { TOTAL_QTY }, ID + "=?" + " AND " + SHOP_CODE + "=?"  + " AND " + CART_TYPE + "=?",
                new String[] { itemId, shopCode, cartType}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int qty = cursor.getInt(cursor.getColumnIndex(TOTAL_QTY));
            return qty;
        }
        return 0;
    }

    public int getTotalAmount(String itemId, String shopCode, String cartType){
        Log.d("getting itemId", itemId);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { TOTAL_AMOUNT }, ID + "=?" + " AND " + SHOP_CODE + "=?"   + " AND " + CART_TYPE + "=?",
                new String[] { itemId, shopCode, cartType}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int qty = cursor.getInt(cursor.getColumnIndex(TOTAL_AMOUNT));
            return qty;
        }
        return 0;
    }

    public float getProductSellingPrice(String itemId, String shopCode){
        Log.d("getting itemId", itemId);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { PROD_SP }, ID + "=?" + " AND " + SHOP_CODE + "=?",
                new String[] { itemId, shopCode}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            float sp = cursor.getFloat(cursor.getColumnIndex(PROD_SP));
            Log.d("SP ", sp+"");
            return sp;
        }
        return 0;
    }

    public int getFreeProductPosition(String itemId, String shopCode){
        Log.d("getting itemId", itemId);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { FREE_PRODUCT_POSITION }, ID + "=?" + " AND " + SHOP_CODE + "=?",
                new String[] { itemId, shopCode}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int pos = cursor.getInt(cursor.getColumnIndex(FREE_PRODUCT_POSITION));
            return pos;
        }
        return 0;
    }


    public int getOfferCounter(String itemId, String shopCode){
        Log.d("getting itemId", itemId);
        Log.d("getting shopId", shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[] { OFFER_ITEM_COUNTER }, ID + "=?" + " AND " + SHOP_CODE + "=?",
                new String[] { itemId, shopCode}, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int pos = cursor.getInt(cursor.getColumnIndex(OFFER_ITEM_COUNTER));
            return pos;
        }
        return 0;
    }


    public ArrayList<MyProduct> getCartProducts(String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+CART_TABLE +" where "+ CART_TYPE + "=?";
        Cursor res =  db.rawQuery(query, new String[]{cartType});
        ArrayList<MyProduct> itemList=new ArrayList<>();
        MyProduct productItem = null;
        if(res.moveToFirst()){
            do{
                productItem=new MyProduct();
                productItem.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                productItem.setId(res.getString(res.getColumnIndex(ID)));
                productItem.setProdId(res.getInt(res.getColumnIndex(PRODID)));
                productItem.setOfferId(res.getString(res.getColumnIndex(OFFER_ID)));
                productItem.setOfferType(res.getString(res.getColumnIndex(OFFER_TYPE)));
                productItem.setCatId(res.getString(res.getColumnIndex(PROD_CAT_ID)));
                productItem.setSubCatId(res.getString(res.getColumnIndex(PROD_SUB_CAT_ID)));
                productItem.setName(res.getString(res.getColumnIndex(PROD_NAME)));
                productItem.setCode(res.getString(res.getColumnIndex(PROD_CODE)));
                productItem.setBarCode(res.getString(res.getColumnIndex(PROD_BARCODE)));
                productItem.setDesc(res.getString(res.getColumnIndex(PROD_DESC)));
                productItem.setProdHsnCode(res.getString(res.getColumnIndex(PROD_HSN_CODE)));
                productItem.setProdCgst(res.getFloat(res.getColumnIndex(PROD_CGST_PER)));
                productItem.setProdIgst(res.getFloat(res.getColumnIndex(PROD_IGST_PER)));
                productItem.setProdSgst(res.getFloat(res.getColumnIndex(PROD_SGST_PER)));
                productItem.setProdImage1(res.getString(res.getColumnIndex(PROD_IMAGE_1)));
                productItem.setProdImage2(res.getString(res.getColumnIndex(PROD_IMAGE_2)));
                productItem.setProdImage3(res.getString(res.getColumnIndex(PROD_IMAGE_3)));
                productItem.setIsBarcodeAvailable(res.getString(res.getColumnIndex(IS_BARCODE_AVAILABLE)));
                productItem.setMrp(res.getFloat(res.getColumnIndex(PROD_MRP)));
                productItem.setSellingPrice(res.getFloat(res.getColumnIndex(PROD_SP)));
                productItem.setTotalAmount(res.getFloat(res.getColumnIndex(TOTAL_AMOUNT)));
                productItem.setQuantity(res.getInt(res.getColumnIndex(TOTAL_QTY)));
                productItem.setOfferItemCounter(res.getInt(res.getColumnIndex(OFFER_ITEM_COUNTER)));
                productItem.setFreeProductPosition(res.getInt(res.getColumnIndex(FREE_PRODUCT_POSITION)));
                productItem.setComboProductIds(res.getString(res.getColumnIndex(COMBO_PRODUCT_IDS)));
                productItem.setUnit(res.getString(res.getColumnIndex(UNIT)));
                productItem.setColor(res.getString(res.getColumnIndex(COLOR)));
                productItem.setSize(res.getString(res.getColumnIndex(SIZE)));
                productItem.setProductUnitList(getProductUnitList(db, productItem.getId()));
                productItem.setProductSizeList(getProductSizeList(db, productItem.getId()));
                if(res.getString(res.getColumnIndex(CART_TYPE)).equals("frequency")){
                    ProductFrequency frequency = new ProductFrequency();
                    frequency.setName(res.getString(res.getColumnIndex(FREQUENCY)));
                    frequency.setQyantity(res.getString(res.getColumnIndex(FREQUENCY_QTY)));
                    frequency.setStartDate(res.getString(res.getColumnIndex(FREQUENCY_START_DATE)));
                    frequency.setEndDate(res.getString(res.getColumnIndex(FREQUENCY_END_DATE)));
                    frequency.setNoOfDays(res.getString(res.getColumnIndex(FREQUENCY_VALUE)));
                 productItem.setFrequency(frequency);
                }
                if(productItem.getSellingPrice() == 0f){
                    if(itemList.size()>productItem.getFreeProductPosition())
                    itemList.add(productItem.getFreeProductPosition(),productItem);
                }else{
                    List<ProductPriceOffer> productPriceOfferList = getProductPriceOffer(""+productItem.getId(), productItem.getShopCode());
                    List<ProductDiscountOffer> productDiscountOfferList = getProductFreeOffer(""+productItem.getId(), productItem.getShopCode());
                    Log.i(TAG,"productPriceOfferList size "+productPriceOfferList.size());
                    Log.i(TAG,"productDiscountOfferList size "+productDiscountOfferList.size());

                    if(productPriceOfferList.size() > 0){
                        productItem.setProductPriceOffer(productPriceOfferList.get(0));
                        productItem.setOfferId(""+productPriceOfferList.get(0).getId());
                        productItem.setOfferType("price");
                    }else if(productDiscountOfferList.size() > 0){
                        productItem.setProductDiscountOffer(productDiscountOfferList.get(0));
                        productItem.setOfferId(""+productDiscountOfferList.get(0).getId());
                        productItem.setOfferType("free");
                    }
                    itemList.add(productItem);
                }
            }while (res.moveToNext());
        }

        return itemList;
    }

    public void updateCartData(MyProduct item, String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i("dbhelper","product "+item.getName());
        Log.i("dbhelper","qty "+item.getQuantity());
        Log.i("dbhelper","total amount "+item.getTotalAmount());
        Log.i("dbhelper","shopCode "+item.getShopCode());
        Log.i("dbhelper","SP "+item.getSellingPrice());
        Log.i("dbhelper","CGST "+item.getProdCgst());
        Log.i("dbhelper","SGST "+item.getProdSgst());
        if(cartType.equals("frequency"))
        contentValues.put(CART_TYPE, "frequency");
        else contentValues.put(CART_TYPE, "normal");
        contentValues.put(TOTAL_QTY, item.getQuantity());
        contentValues.put(TOTAL_AMOUNT, item.getTotalAmount());
        contentValues.put(PROD_SP, item.getSellingPrice());
        if(item.getComboProductIds()!=null){
            contentValues.put(PROD_CGST, item.getProdCgst());
            contentValues.put(PROD_IGST, item.getProdIgst());
            contentValues.put(PROD_SGST, item.getProdSgst());
            Log.i("dbhelper","cgst "+item.getProdCgst());
            Log.i("dbhelper","sgst "+item.getProdSgst());
        }else {
            float rate = ((item.getSellingPrice() * (item.getProdCgst() + item.getProdSgst())) / (100 +
                    (item.getProdCgst() + item.getProdSgst())));
            Log.d("Rate ", "" + rate);
            contentValues.put(PROD_CGST, rate / 2);
            contentValues.put(PROD_IGST, rate);
            contentValues.put(PROD_SGST, rate / 2);
        }

        double status = db.update(CART_TABLE, contentValues, ID + " = ?" + " AND " + SHOP_CODE + "=?" + " AND " + PROD_SP+" != ?",
                new String[] { String.valueOf(item.getId()),  String.valueOf(item.getShopCode()), String.valueOf(0)});

        Log.d("Cart updated status ", ""+status);

        //if(!item.getShopCode().equals("SHP1"))
        updateShopCouponDiscount(item, cartType);
        updateShoppursCouponDiscount(cartType);
    }

    private void updateShopCouponDiscount(MyProduct item, String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        float tAmount = getTotalAmount(item.getShopCode());
        float couponDiscount = getCouponAmount(tAmount, item.getShopCode());
        Log.i("dbhelper","after coupon total amount "+(tAmount - couponDiscount));
        Log.i("dbhelper","after coupon  discount"+couponDiscount);
        contentValues.put(DISCOUNT, couponDiscount);
        db.update(CART_TABLE, contentValues, SHOP_CODE + "=?"  + " AND " + PROD_SP+" != ?",
                new String[] { item.getShopCode(), cartType });
    }
    private void updateShoppursCouponDiscount(String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        float tAmount = getTotalPriceCart("normal");
        float couponDiscount = getCouponAmount(tAmount, "SHP1");
        Log.i("dbhelper","after coupon total amount "+(tAmount - couponDiscount));
        Log.i("dbhelper","after coupon  discount"+couponDiscount);
        contentValues.put(SHOPPURS_DISCOUNT, couponDiscount);
        db.update(CART_TABLE, contentValues, SHOP_CODE + "=?"  + " AND " + CART_TYPE+" != ?",
                new String[] {"SHP1", cartType});
    }

    public float getCouponAmount(float totalAmount, String shopCode) {
        Coupon coupon = getCouponOffer(shopCode);
        float couponDiscount=0;
        if (coupon.getPercentage() > 0) {
            couponDiscount = totalAmount * coupon.getPercentage() / 100;

            if (couponDiscount < coupon.getMaxDiscount() || coupon.getMaxDiscount() == 0)
                coupon.setAmount(couponDiscount);
            else coupon.setAmount(coupon.getMaxDiscount());
            Log.d("CouponApplied on ", totalAmount + "");
            Log.d("CouponDiscount ", "" + couponDiscount);
        }
        return coupon.getAmount();
    }

    public void manageCouponDiscount(Coupon coupon, String action){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i("dbhelper","Discount "+coupon.getAmount());
        if(action.equals("remove")) {
            if(coupon.getShopCode().equals("SHP1"))
                contentValues.put(SHOPPURS_DISCOUNT, 0);
            else
            contentValues.put(DISCOUNT, 0);
        }else {
            if(coupon.getShopCode().equals("SHP1"))
                contentValues.put(SHOPPURS_DISCOUNT, coupon.getAmount());
            else
                contentValues.put(DISCOUNT, coupon.getAmount());
        }
        db.update(CART_TABLE, contentValues, SHOP_CODE + "=?",
                new String[] { coupon.getShopCode() });
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

    public int getCartCount(String shopCode) {
        String countQuery = "SELECT  * FROM " + CART_TABLE +" where "+ SHOP_CODE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{shopCode});
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
        String countQuery = "SELECT  sum(totalAmount) as totalAmount FROM " +  CART_TABLE +" where SHOP_CODE = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{shpcode});
        if(cursor.moveToFirst()) {
            totalAmount = cursor.getFloat(cursor.getColumnIndex(TOTAL_AMOUNT));
            cursor.close();
        }
        return totalAmount;
    }

    public float getTotalShopCouponDiscount(String cartType){
        float totalDiscount=0;
        String countQuery = "SELECT  sum("+DISCOUNT+") as discount FROM " +  CART_TABLE +" where "+CART_TYPE+" = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{cartType});
        if(cursor.moveToFirst()) {
            totalDiscount = cursor.getFloat(cursor.getColumnIndex(DISCOUNT));
            cursor.close();
        }
        return totalDiscount;
    }

    public float getTotalShoppursCouponDiscount(String cartType){
        float totalDiscount=0;
        String countQuery = "SELECT "+SHOPPURS_DISCOUNT+" FROM " +  CART_TABLE +" where "+CART_TYPE+" = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{cartType});
        if(cursor.moveToFirst()) {
            totalDiscount = cursor.getFloat(cursor.getColumnIndex(SHOPPURS_DISCOUNT));
            cursor.close();
        }
        return totalDiscount;
    }

    public float getTotalShopCouponDiscount(String shopCode, String cartType){
        float totalDiscount=0;
        String countQuery = "SELECT "+DISCOUNT+" FROM " +  CART_TABLE +" where SHOP_CODE =?" +" AND "+CART_TYPE+" = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{shopCode, cartType});
        if(cursor.moveToFirst()) {
            totalDiscount = cursor.getFloat(cursor.getColumnIndex(DISCOUNT));
            cursor.close();
        }
        return totalDiscount;
    }

    public int getTotalQuantity(String shpcode){
        int totalQty=0;
        String countQuery = "SELECT  sum("+ TOTAL_QTY +") as totalQty FROM " +  CART_TABLE +" where SHOP_CODE = ?";
        Log.d("countQuery ", countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{ shpcode });
        if(cursor.moveToFirst()) {
            totalQty = cursor.getInt(cursor.getColumnIndex(TOTAL_QTY));
            cursor.close();
        }
        return totalQty;
    }


    public void add_to_Favorite(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SHOP_CODE, code);
        Log.d(TAG, code+" ");
        double val = db.insert(FAVORITE_TABLE, null, values);
        Log.d(TAG, "Favourite Added "+ val);
        db.close(); // Closing database connection
    }



    public void add_to_Favorite(List<String> mList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i =0;i<mList.size();i++) {
            ContentValues values = new ContentValues();
            values.put(SHOP_CODE, mList.get(i));
            double val = db.insert(FAVORITE_TABLE, null, values);
            Log.d(TAG, "Favourite Added "+ val);
        }
        db.close(); // Closing database connection
    }
    // Deleting single contact
    public void remove_from_Favorite(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, code+" ");
        db.delete(FAVORITE_TABLE, SHOP_CODE + " = ?", new String[] { code });
        db.close();
    }
    public boolean isShopFavorited(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FAVORITE_TABLE, new String[] { SHOP_CODE }, SHOP_CODE + "=?",
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

    public boolean addDeliveryAddress(DeliveryAddress item, String custCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(DELIVERY_CUSTOMER_NAME, item.getName());
        contentValues.put(DELIVERY_CUSTOMER_MOBILE, item.getMobile());
        contentValues.put(DELIVERY_CUSTOMER_CODE, custCode);
        contentValues.put(DELIVERY_HOUSE_NO, item.getHouseNo());
        contentValues.put(DELIVERY_ADDRESS, item.getAddress());
        contentValues.put(IS_DELIVERY_DEFAULT, item.getIsDefaultAddress());
        contentValues.put(LATITUDE, item.getDelivery_lat());
        contentValues.put(LONGITUDE, item.getDelivery_long());
        contentValues.put(DELIVERY_LANDMARK, item.getLandmark());
        contentValues.put(DELIVERY_CITY, item.getCity());
        contentValues.put(DELIVERY_STATE, item.getState());
        contentValues.put(DELIVERY_COUNTRY, item.getCountry());
        contentValues.put(DELIVERY_PIN, item.getPinCode());
        db.insert(DELIVERY_ADDRESS_TABLE, null, contentValues);
        Log.i("DbHelper","Delivery Address is added");
        return true;
    }

    public boolean addAllDeliveryAddress(List<DeliveryAddress> items, String custCode){
        for(DeliveryAddress item: items) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, item.getId());
            contentValues.put(DELIVERY_CUSTOMER_NAME, item.getName());
            contentValues.put(DELIVERY_CUSTOMER_MOBILE, item.getMobile());
            contentValues.put(DELIVERY_CUSTOMER_CODE, custCode);
            contentValues.put(DELIVERY_HOUSE_NO, item.getHouseNo());
            contentValues.put(DELIVERY_ADDRESS, item.getAddress());
            contentValues.put(IS_DELIVERY_DEFAULT, item.getIsDefaultAddress());
            contentValues.put(LATITUDE, item.getDelivery_lat());
            contentValues.put(LONGITUDE, item.getDelivery_long());
            contentValues.put(DELIVERY_LANDMARK, item.getLandmark());
            contentValues.put(DELIVERY_CITY, item.getCity());
            contentValues.put(DELIVERY_STATE, item.getState());
            contentValues.put(DELIVERY_COUNTRY, item.getCountry());
            contentValues.put(DELIVERY_PIN, item.getPinCode());
            db.insert(DELIVERY_ADDRESS_TABLE, null, contentValues);
            Log.i("DbHelper", "Delivery Address is added");
        }
        return true;
    }

    public void updateDeliveryAddress(DeliveryAddress item, String custCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DELIVERY_CUSTOMER_NAME, item.getName());
        contentValues.put(DELIVERY_CUSTOMER_MOBILE, item.getMobile());
        contentValues.put(DELIVERY_CUSTOMER_CODE, custCode);
        contentValues.put(DELIVERY_HOUSE_NO, item.getHouseNo());
        contentValues.put(DELIVERY_ADDRESS, item.getAddress());
        contentValues.put(IS_DELIVERY_DEFAULT, item.getIsDefaultAddress());
        contentValues.put(LATITUDE, item.getDelivery_lat());
        contentValues.put(LONGITUDE, item.getDelivery_long());
        contentValues.put(DELIVERY_LANDMARK, item.getLandmark());
        contentValues.put(DELIVERY_CITY, item.getCity());
        contentValues.put(DELIVERY_STATE, item.getState());
        contentValues.put(DELIVERY_COUNTRY, item.getCountry());
        contentValues.put(DELIVERY_PIN, item.getPinCode());

        Log.d(ID, String.valueOf(item.getId()));
        Log.d(DELIVERY_CUSTOMER_CODE, String.valueOf(custCode));
        db.update(DELIVERY_ADDRESS_TABLE, contentValues, ID + " = ?" + " AND " + DELIVERY_CUSTOMER_CODE + "=?",
                new String[] { String.valueOf(item.getId()),  String.valueOf(custCode)});
    }

    public List<DeliveryAddress> getallDeliveryAddress(String custCode){
        SQLiteDatabase db = this.getWritableDatabase();
        String unitSql="select * from "+DELIVERY_ADDRESS_TABLE+" WHERE "+DELIVERY_CUSTOMER_CODE+" = ?";
        Cursor res =  db.rawQuery(unitSql, new String[]{String.valueOf(custCode)});
        ArrayList<DeliveryAddress> itemList=new ArrayList<>();
        DeliveryAddress item = null;
        if(res.moveToFirst()){
            do{
                item=new DeliveryAddress();
                item.setId(res.getString(res.getColumnIndex(ID)));
                item.setName(res.getString(res.getColumnIndex(DELIVERY_CUSTOMER_NAME)));
                item.setMobile(res.getString(res.getColumnIndex(DELIVERY_CUSTOMER_MOBILE)));
                item.setHouseNo(res.getString(res.getColumnIndex(DELIVERY_HOUSE_NO)));
                item.setAddress(res.getString(res.getColumnIndex(DELIVERY_ADDRESS)));
                item.setIsDefaultAddress(res.getString(res.getColumnIndex(IS_DELIVERY_DEFAULT)));
                item.setDelivery_lat(res.getString(res.getColumnIndex(LATITUDE)));
                item.setDelivery_long(res.getString(res.getColumnIndex(LONGITUDE)));
                item.setLandmark(res.getString(res.getColumnIndex(DELIVERY_LANDMARK)));
                item.setCity(res.getString(res.getColumnIndex(DELIVERY_CITY)));
                item.setState(res.getString(res.getColumnIndex(DELIVERY_STATE)));
                item.setPinCode(res.getString(res.getColumnIndex(DELIVERY_PIN)));
                item.setCountry(res.getString(res.getColumnIndex(DELIVERY_COUNTRY)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public DeliveryAddress getdefaultDeliveryAddress(String custCode){
        SQLiteDatabase db = this.getReadableDatabase();
        String unitSql="select * from "+DELIVERY_ADDRESS_TABLE+" WHERE "+DELIVERY_CUSTOMER_CODE+" = ?" +" AND "+IS_DELIVERY_DEFAULT+" = ?";
        Cursor res =  db.rawQuery(unitSql, new String[]{String.valueOf(custCode), "Yes"});
        DeliveryAddress item = null;
        if(res.moveToFirst()){
            do{
                item=new DeliveryAddress();
                item.setId(res.getString(res.getColumnIndex(ID)));
                item.setName(res.getString(res.getColumnIndex(DELIVERY_CUSTOMER_NAME)));
                item.setMobile(res.getString(res.getColumnIndex(DELIVERY_CUSTOMER_MOBILE)));
                item.setHouseNo(res.getString(res.getColumnIndex(DELIVERY_HOUSE_NO)));
                item.setAddress(res.getString(res.getColumnIndex(DELIVERY_ADDRESS)));
                item.setIsDefaultAddress(res.getString(res.getColumnIndex(IS_DELIVERY_DEFAULT)));
                item.setDelivery_lat(res.getString(res.getColumnIndex(LATITUDE)));
                item.setDelivery_long(res.getString(res.getColumnIndex(LONGITUDE)));
                item.setLandmark(res.getString(res.getColumnIndex(DELIVERY_LANDMARK)));
                item.setCity(res.getString(res.getColumnIndex(DELIVERY_CITY)));
                item.setState(res.getString(res.getColumnIndex(DELIVERY_STATE)));
                item.setPinCode(res.getString(res.getColumnIndex(DELIVERY_PIN)));
                item.setCountry(res.getString(res.getColumnIndex(DELIVERY_COUNTRY)));
            }while (res.moveToNext());
        }
        return item;
    }

    // Deleting single contact
    public void remove_delivery_address(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, id+" ");
        db.delete(DELIVERY_ADDRESS_TABLE, ID + " = ?", new String[] { id });
        db.close();
    }

    public void deleteAllDeliveryAddress() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ DELIVERY_ADDRESS_TABLE);
        db.close();
    }


    public boolean addProductUnit(ProductUnit item, String prodId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(PROD_ID, prodId);
        contentValues.put(NAME, item.getUnitName());
        contentValues.put(VALUE, item.getUnitValue());
        contentValues.put(STATUS, item.getStatus());
        db.insert(PRODUCT_UNIT_TABLE, null, contentValues);
        Log.i("DbHelper","Unit Row is added");
        return true;
    }

    public boolean addProductSize(ProductSize item, String prodId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(PROD_ID, prodId);
        contentValues.put(SIZE, item.getSize());
        contentValues.put(STATUS, item.getStatus());
        db.insert(PRODUCT_SIZE_TABLE, null, contentValues);
        Log.i("DbHelper","Size Row is added");
        return true;
    }

    public boolean addProductColor(ProductColor item, String prodId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(PROD_ID, prodId);
        contentValues.put(SIZE_ID, item.getSizeId());
        contentValues.put(COLOR_NAME, item.getColorName());
        contentValues.put(COLOR_VALUE, item.getColorValue());
        contentValues.put(STATUS, item.getStatus());
        db.insert(PRODUCT_COLOR_TABLE, null, contentValues);
        Log.i("DbHelper","Color Row is added");
        return true;
    }

    public List<ProductUnit> getProductUnitList(SQLiteDatabase db,String prodId){
        String unitSql="select * from "+PRODUCT_UNIT_TABLE+" WHERE "+PROD_ID+" = ? AND "+STATUS+" = 'N'";
        Cursor res =  db.rawQuery(unitSql, new String[]{prodId});
        ArrayList<ProductUnit> itemList=new ArrayList<>();
        ProductUnit item = null;
        if(res.moveToFirst()){
            do{
                item=new ProductUnit();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setUnitName(res.getString(res.getColumnIndex(NAME)));
                item.setUnitValue(res.getString(res.getColumnIndex(VALUE)));
                item.setStatus(res.getString(res.getColumnIndex(STATUS)));
                itemList.add(item);
            }while (res.moveToNext());
        }

        return itemList;
    }

    public List<ProductSize> getProductSizeList(SQLiteDatabase db,String prodId){
        String unitSql="select * from "+PRODUCT_SIZE_TABLE+" WHERE "+PROD_ID+" = ? AND "+STATUS+" = 'N'";
        Cursor res =  db.rawQuery(unitSql, new String[]{prodId});
        ArrayList<ProductSize> itemList=new ArrayList<>();
        ProductSize item = null;
        if(res.moveToFirst()){
            do{
                item=new ProductSize();
                item.setId(res.getString(res.getColumnIndex(ID)));
                item.setSize(res.getString(res.getColumnIndex(SIZE)));
                item.setStatus(res.getString(res.getColumnIndex(STATUS)));
                item.setProductColorList(getProductColorList(db,item.getId()));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public List<ProductColor> getProductColorList(SQLiteDatabase db,String sizeId){
        String unitSql="select * from "+PRODUCT_COLOR_TABLE+" WHERE "+SIZE_ID+" = ? AND "+STATUS+" = 'N'";
        Cursor res =  db.rawQuery(unitSql, new String[]{sizeId});
        ArrayList<ProductColor> itemList=new ArrayList<>();
        ProductColor item = null;
        if(res.moveToFirst()){
            do{
                item=new ProductColor();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setSizeId(res.getInt(res.getColumnIndex(SIZE_ID)));
                item.setColorName(res.getString(res.getColumnIndex(COLOR_NAME)));
                item.setColorValue(res.getString(res.getColumnIndex(COLOR_VALUE)));
                item.setStatus(res.getString(res.getColumnIndex(STATUS)));
                itemList.add(item);
            }while (res.moveToNext());
        }
        return itemList;
    }

    public boolean addProductFreeOffer(ProductDiscountOffer item, String createdAt, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(SHOP_CODE, shopCode);
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(PROD_BUY_ID, item.getProdBuyId());
        contentValues.put(PROD_FREE_ID, item.getProdFreeId());
        contentValues.put(PROD_BUY_QTY, item.getProdBuyQty());
        contentValues.put(PROD_FREE_QTY, item.getProdFreeQty());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        db.insert(PROD_FREE_OFFER_TABLE, null, contentValues);
        Log.i("DbHelper","addProductFreeOffer Added");
        return true;
    }


    public boolean addProductPriceOffer(ProductPriceOffer item, String createdAt, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(PROD_ID, item.getProdId());
        contentValues.put(SHOP_CODE, shopCode);
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        double val = db.insert(PROD_PRICE_TABLE, null, contentValues);
        Log.i("DbHelper","addProductPriceOffer Added "+val);
        return true;
    }

    public boolean removePriceProductFromCart(String id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(PROD_PRICE_TABLE, PROD_ID+" = ?"  + " AND " + SHOP_CODE + "=?" ,new String[]{String.valueOf(id), shopCode});
        Log.d(TAG, "removed price product from cart status "+val + "id  "+id);
        return true;
    }



    public boolean addProductPriceDetailOffer(ProductPriceDetails item, String createdAt, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(OFFER_ID, item.getPcodPcoId());
        contentValues.put(SHOP_CODE, shopCode);
        contentValues.put(TOTAL_QTY, item.getPcodProdQty());
        contentValues.put(PROD_SP, item.getPcodPrice());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        double val = db.insert(PROD_PRICE_DETAIL_TABLE, null, contentValues);
        Log.i("DbHelper","ProductPriceOffer Details Added "+val);
        return true;
    }

    public boolean removePriceProductDetailsFromCart(String id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(PROD_PRICE_DETAIL_TABLE, ID+" = ?"  + " AND " + SHOP_CODE + "=?",new String[]{String.valueOf(id), shopCode});
        Log.d(TAG, "removed price product Details from cart status "+val + "id  "+id);
        return true;
    }

    public boolean addProductComboOffer(ProductComboOffer item, String createdAt, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(SHOP_CODE, shopCode);
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        db.insert(PROD_COMBO_TABLE, null, contentValues);
        Log.i("DbHelper","Row is added");
        return true;
    }

    public boolean addProductComboDetailOffer(ProductComboDetails item, String createdAt, String updatedAt,  String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(SHOP_CODE, shopCode);
        contentValues.put(OFFER_ID, item.getPcodPcoId());
        contentValues.put(PROD_ID, item.getPcodProdId());
        contentValues.put(TOTAL_QTY, item.getPcodProdQty());
        contentValues.put(PROD_SP, item.getPcodPrice());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(CREATED_AT, createdAt);
        contentValues.put(UPDATED_AT, updatedAt);
        db.insert(PROD_COMBO_DETAIL_TABLE, null, contentValues);
        Log.i("DbHelper","Row is added");
        return true;
    }
    public boolean removeCouponFromCart(String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(COUPON_TABLE,  SHOP_CODE +"=?",new String[]{shopCode});
        Log.d(TAG, "removed Coupon from cart status "+val + "shopCode  "+shopCode);
        return true;
    }


    public boolean manageCouponOffer(Coupon item ,String action){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(SHOP_CODE, item.getShopCode());
        contentValues.put(COUPON_PER, item.getPercentage());
        contentValues.put(NAME, item.getName());
        contentValues.put(COUPON_MAX_AMOUNT, item.getMaxDiscount());
        contentValues.put(COUPON_AMOUNT, item.getAmount());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        if(action.equals("add")) {
            long status = db.insert(COUPON_TABLE, null, contentValues);
            Log.i("DbHelper","Coupon is added status "+status);
        }
        else {
            int status = db.update(COUPON_TABLE, contentValues, SHOP_CODE+"=? AND "+ID+"=?", new String[]{item.getShopCode(), String.valueOf(item.getId())});
            Log.i("DbHelper","Coupon is updated status "+status);
        }
        return true;
    }


   /* public boolean checkProdExistInCart(String barCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+CART_TABLE+" WHERE "+PROD_BARCODE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{barCode});
        boolean exist = false;
        if(res.moveToFirst()){
            //prodCode = res.getString(res.getColumnIndex(PROD_BARCODE));
            exist = true;

        }else{
            // prodCode =  "no";
        }
        return exist;
    }*/

    public boolean checkProdExistInCart(String prodId, String shopCode){
        Log.d("prodId "+prodId, "shopCode "+shopCode);
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+CART_TABLE+" WHERE "+ID+" = ?" + " AND " + SHOP_CODE + "=?"  + " AND " + PROD_SP + "!=? ";
        Cursor res =  db.rawQuery(query, new String[]{ prodId, shopCode, String.valueOf(0f)});
        boolean exist = false;
        if(res.moveToFirst()){
            //prodCode = res.getString(res.getColumnIndex(PROD_BARCODE));
            exist = true;

        }else{
            // prodCode =  "no";
        }
        return exist;
    }

    public float getTotalPriceCart(String shopCode, String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+TOTAL_AMOUNT+") as totalAmount from "+CART_TABLE +" where SHOP_CODE = ?" +" AND "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{ shopCode, cartType });
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTotalPriceCart(String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+TOTAL_AMOUNT+") as totalAmount from "+CART_TABLE +" where "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{cartType});
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }



    public float getTotalMrpPriceCart(String shopCode, String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+PROD_MRP+" * "+TOTAL_QTY+") as totalAmount from "+CART_TABLE +" where SHOP_CODE = ?" +" AND "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{shopCode, cartType});
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTotalMrpPriceCart(String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+PROD_MRP+" * "+TOTAL_QTY+") as totalAmount from "+CART_TABLE +" where "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{cartType});
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTotalTaxesart(String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum(("+PROD_CGST+" + "+PROD_SGST+")*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE +" where "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{cartType});
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTotalTaxesart(String shopCode, String cartType){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum(("+PROD_CGST+" + "+PROD_SGST+")*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE  +" where SHOP_CODE = ?"  +" AND "+CART_TYPE+" = ?";
        Cursor res =  db.rawQuery(query, new String[]{ shopCode, cartType });
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTaxesCart(String taxType){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(taxType.equals("cgst")){
            query="select sum("+PROD_CGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE;
        }else if(taxType.equals("sgst")){
            query="select sum("+PROD_SGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE;
        }else{
            query="select sum("+PROD_IGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE;
        }
        Cursor res =  db.rawQuery(query, null);
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public float getTaxesCart(String taxType, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(taxType.equals("cgst")){
            query="select sum("+PROD_CGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE +" where SHOP_CODE = ?";
        }else if(taxType.equals("sgst")){
            query="select sum("+PROD_SGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE  +" where SHOP_CODE = ?";
        }else{
            query="select sum("+PROD_IGST+"*"+TOTAL_QTY+") as totalAmount from "+CART_TABLE +" where SHOP_CODE = ?";
        }
        Cursor res =  db.rawQuery(query, new String[]{shopCode});
        float amount = 0f;
        if(res.moveToFirst()){
            do{
                amount = amount + res.getFloat(res.getColumnIndex(TOTAL_AMOUNT));
            }while (res.moveToNext());

        }

        return amount;
    }

    public int getTotalQuantityCart(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+TOTAL_QTY+") as totalQty from "+CART_TABLE;
        Cursor res =  db.rawQuery(query, null);
        int qty = 0;
        if(res.moveToFirst()){
            do{
                qty = qty + res.getInt(res.getColumnIndex("totalQty"));
            }while (res.moveToNext());

        }

        return qty;
    }

    public float getTotalTaxValue(String columnName){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+columnName+" * "+PROD_MRP+"/100) as totalTax from "+CART_TABLE;
        Cursor res =  db.rawQuery(query, null);
        float tax = 0f;
        if(res.moveToFirst()){
            do{
                tax = tax + res.getFloat(res.getColumnIndex("totalTax"));
            }while (res.moveToNext());

        }

        return tax;
    }

    public float getTotalDisValue(String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum(("+PROD_MRP+" - "+PROD_SP+")* " +TOTAL_QTY+") as totalDis from "+CART_TABLE  +" where SHOP_CODE = ?";
        Cursor res =  db.rawQuery(query,  new String[]{ shopCode });
        float dis = 0f;
        if(res.moveToFirst()){
            do{
                dis = dis + res.getFloat(res.getColumnIndex("totalDis"));
            }while (res.moveToNext());

        }

        return dis;
    }

    public float getTotalDisValue(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select sum("+PROD_MRP+" - "+PROD_SP+") as totalDis from "+CART_TABLE;
        Cursor res =  db.rawQuery(query, null);
        float dis = 0f;
        if(res.moveToFirst()){
            do{
                dis = dis + res.getFloat(res.getColumnIndex("totalDis"));
            }while (res.moveToNext());

        }

        return dis;
    }


    public List<ProductPriceOffer> getProductPriceOffer(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+PROD_PRICE_TABLE;
        final String detailQuery="select * from "+PROD_PRICE_DETAIL_TABLE+" where "+OFFER_ID+" IN(?)";
        Cursor res =  db.rawQuery(query, null);
        Cursor detailsRes =  null;
        ArrayList<ProductPriceOffer> itemList=new ArrayList<>();
        ArrayList<ProductPriceDetails> productPriceDetailsList=null;
        ProductPriceDetails productPriceDetails = null;
        ProductPriceOffer productPriceOffer = null;
        if(res.moveToFirst()){
            do{
                productPriceOffer = new ProductPriceOffer();
                productPriceOffer.setId(res.getInt(res.getColumnIndex(ID)));
                productPriceOffer.setProdId(res.getInt(res.getColumnIndex(PROD_ID)));
                productPriceOffer.setOfferName(res.getString(res.getColumnIndex(NAME)));
                productPriceOffer.setStatus(res.getString(res.getColumnIndex(STATUS)));
                productPriceOffer.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                productPriceOffer.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                productPriceDetailsList=new ArrayList<>();
                detailsRes =  db.rawQuery(detailQuery, new String[]{String.valueOf(productPriceOffer.getId())});
                if(detailsRes.moveToFirst()){
                    do{
                        productPriceDetails = new ProductPriceDetails();
                        productPriceDetails.setId(detailsRes.getInt(detailsRes.getColumnIndex(ID)));
                        productPriceDetails.setPcodPcoId(detailsRes.getInt(detailsRes.getColumnIndex(OFFER_ID)));
                        productPriceDetails.setPcodProdQty(detailsRes.getInt(detailsRes.getColumnIndex(TOTAL_QTY)));
                        productPriceDetails.setPcodPrice(detailsRes.getFloat(detailsRes.getColumnIndex(PROD_SP)));
                        productPriceDetails.setStatus(detailsRes.getString(detailsRes.getColumnIndex(STATUS)));
                        productPriceDetailsList.add(productPriceDetails);
                    }while (detailsRes.moveToNext());
                }
                productPriceOffer.setProductPriceDetails(productPriceDetailsList);
                itemList.add(productPriceOffer);

            }while (res.moveToNext());
        }

        return itemList;
    }

    public List<ProductPriceOffer> getProductPriceOffer(String prodId, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+PROD_PRICE_TABLE+" where "+PROD_ID+" IN(?)"  + " AND " + SHOP_CODE + "=?";
        final String detailQuery="select * from "+PROD_PRICE_DETAIL_TABLE+" where "+OFFER_ID+" IN(?)";
        Cursor res =  db.rawQuery(query, new String[]{prodId, shopCode});
        Cursor detailsRes =  null;
        ArrayList<ProductPriceOffer> itemList=new ArrayList<>();
        ArrayList<ProductPriceDetails> productPriceDetailsList=null;
        ProductPriceOffer productPriceOffer = null;
        ProductPriceDetails productPriceDetails = null;
        if(res.moveToFirst()){
            do{
                productPriceOffer = new ProductPriceOffer();
                productPriceOffer.setId(res.getInt(res.getColumnIndex(ID)));
                productPriceOffer.setProdId(res.getInt(res.getColumnIndex(PROD_ID)));
                productPriceOffer.setOfferName(res.getString(res.getColumnIndex(NAME)));
                productPriceOffer.setStatus(res.getString(res.getColumnIndex(STATUS)));
                productPriceOffer.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                productPriceOffer.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                productPriceDetailsList=new ArrayList<>();
                detailsRes =  db.rawQuery(detailQuery, new String[]{String.valueOf(productPriceOffer.getId())});
                if(detailsRes.moveToFirst()){
                    do{
                        productPriceDetails = new ProductPriceDetails();
                        productPriceDetails.setId(detailsRes.getInt(detailsRes.getColumnIndex(ID)));
                        productPriceDetails.setPcodPcoId(detailsRes.getInt(detailsRes.getColumnIndex(OFFER_ID)));
                        productPriceDetails.setPcodProdQty(detailsRes.getInt(detailsRes.getColumnIndex(TOTAL_QTY)));
                        productPriceDetails.setPcodPrice(detailsRes.getFloat(detailsRes.getColumnIndex(PROD_SP)));
                        productPriceDetails.setStatus(detailsRes.getString(detailsRes.getColumnIndex(STATUS)));
                        productPriceDetailsList.add(productPriceDetails);
                    }while (detailsRes.moveToNext());
                }
                productPriceOffer.setProductPriceDetails(productPriceDetailsList);
                itemList.add(productPriceOffer);

            }while (res.moveToNext());
        }

        return itemList;
    }

    public List<ProductComboOffer> getProductComboOffer(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+PROD_COMBO_TABLE;
        final String detailQuery="select * from "+PROD_COMBO_DETAIL_TABLE+" where "+OFFER_ID+" IN(?)";
        Cursor res =  db.rawQuery(query, null);
        Cursor detailsRes =  null;
        ArrayList<ProductComboOffer> itemList=new ArrayList<>();
        ArrayList<ProductComboDetails> productComboOfferDetails=null;
        ProductComboDetails productComboDetails = null;
        ProductComboOffer productComboOffer = null;
        if(res.moveToFirst()){
            do{
                productComboOffer = new ProductComboOffer();
                productComboOffer.setId(res.getInt(res.getColumnIndex(ID)));
                productComboOffer.setOfferName(res.getString(res.getColumnIndex(NAME)));
                productComboOffer.setStatus(res.getString(res.getColumnIndex(STATUS)));
                productComboOffer.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                productComboOffer.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                productComboOfferDetails=new ArrayList<>();
                detailsRes =  db.rawQuery(detailQuery, new String[]{String.valueOf(productComboOffer.getId())});
                if(detailsRes.moveToFirst()){
                    do{
                        productComboDetails = new ProductComboDetails();
                        productComboDetails.setId(detailsRes.getInt(detailsRes.getColumnIndex(ID)));
                        productComboDetails.setPcodPcoId(detailsRes.getInt(detailsRes.getColumnIndex(OFFER_ID)));
                        productComboDetails.setPcodProdId(detailsRes.getInt(detailsRes.getColumnIndex(PROD_ID)));
                        productComboDetails.setPcodProdQty(detailsRes.getInt(detailsRes.getColumnIndex(TOTAL_QTY)));
                        productComboDetails.setPcodPrice(detailsRes.getFloat(detailsRes.getColumnIndex(PROD_SP)));
                        productComboDetails.setStatus(detailsRes.getString(detailsRes.getColumnIndex(STATUS)));
                        productComboOfferDetails.add(productComboDetails);
                    }while (detailsRes.moveToNext());
                }
                productComboOffer.setProductComboOfferDetails(productComboOfferDetails);
                itemList.add(productComboOffer);

            }while (res.moveToNext());
        }

        return itemList;
    }

    public List<Object> getProductFreeOffer(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+PROD_FREE_OFFER_TABLE;
        Cursor res =  db.rawQuery(query, null);
        List<Object> productDiscountOfferList = new ArrayList<>();
        ProductDiscountOffer productDiscountOffer = null;
        if(res.moveToFirst()){
            do{
                productDiscountOffer = new ProductDiscountOffer();
                productDiscountOffer.setId(res.getInt(res.getColumnIndex(ID)));
                productDiscountOffer.setOfferName(res.getString(res.getColumnIndex(NAME)));
                productDiscountOffer.setProdBuyId(res.getInt(res.getColumnIndex(PROD_BUY_ID)));
                productDiscountOffer.setProdFreeId(res.getInt(res.getColumnIndex(PROD_FREE_ID)));
                productDiscountOffer.setProdBuyQty(res.getInt(res.getColumnIndex(PROD_BUY_QTY)));
                productDiscountOffer.setProdFreeQty(res.getInt(res.getColumnIndex(PROD_FREE_QTY)));
                productDiscountOffer.setStatus(res.getString(res.getColumnIndex(STATUS)));
                productDiscountOffer.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                productDiscountOffer.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                productDiscountOfferList.add(productDiscountOffer);
            }while (res.moveToNext());

        }

        return productDiscountOfferList;
    }


    public List<ProductDiscountOffer> getProductFreeOffer(String prodId, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+PROD_FREE_OFFER_TABLE +" where "+PROD_BUY_ID+" = ?"   + " AND " + SHOP_CODE + "=?";
        Cursor res =  db.rawQuery(query, new String[]{prodId, shopCode});
        List<ProductDiscountOffer> productDiscountOfferList = new ArrayList<>();
        ProductDiscountOffer productDiscountOffer = null;
        if(res.moveToFirst()){
            do{
                productDiscountOffer = new ProductDiscountOffer();
                productDiscountOffer.setId(res.getInt(res.getColumnIndex(ID)));
                productDiscountOffer.setOfferName(res.getString(res.getColumnIndex(NAME)));
                productDiscountOffer.setProdBuyId(res.getInt(res.getColumnIndex(PROD_BUY_ID)));
                productDiscountOffer.setProdFreeId(res.getInt(res.getColumnIndex(PROD_FREE_ID)));
                productDiscountOffer.setProdBuyQty(res.getInt(res.getColumnIndex(PROD_BUY_QTY)));
                productDiscountOffer.setProdFreeQty(res.getInt(res.getColumnIndex(PROD_FREE_QTY)));
                productDiscountOffer.setStatus(res.getString(res.getColumnIndex(STATUS)));
                productDiscountOffer.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                productDiscountOffer.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                productDiscountOfferList.add(productDiscountOffer);
            }while (res.moveToNext());

        }

        return productDiscountOfferList;
    }

    public Coupon getCouponOffer(String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+COUPON_TABLE +" where " + SHOP_CODE + "=?";
        Cursor res =  db.rawQuery(query, new String[]{shopCode});
        Coupon item = new Coupon();
        if(res.moveToFirst()){
            item = new Coupon();
            item.setId(res.getInt(res.getColumnIndex(ID)));
            item.setName(res.getString(res.getColumnIndex(NAME)));
            item.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
            item.setMaxDiscount(res.getFloat(res.getColumnIndex(COUPON_MAX_AMOUNT)));
            item.setAmount(res.getFloat(res.getColumnIndex(COUPON_AMOUNT)));
            item.setPercentage(res.getFloat(res.getColumnIndex(COUPON_PER)));
            item.setStatus(res.getString(res.getColumnIndex(STATUS)));
            item.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
            item.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
        }

        return item;
    }

    public List<Coupon> getCouponOffer(){
        SQLiteDatabase db = this.getReadableDatabase();
        final String query="select * from "+COUPON_TABLE;
        Cursor res =  db.rawQuery(query,new String[]{});
        List<Coupon> itemList = new ArrayList<>();
        Coupon item = new Coupon();
        if(res.moveToFirst()){
            do{
                item = new Coupon();
                item.setId(res.getInt(res.getColumnIndex(ID)));
                item.setShopCode(res.getString(res.getColumnIndex(SHOP_CODE)));
                item.setName(res.getString(res.getColumnIndex(NAME)));
                item.setMaxDiscount(res.getFloat(res.getColumnIndex(COUPON_MAX_AMOUNT)));
                item.setAmount(res.getFloat(res.getColumnIndex(COUPON_AMOUNT)));
                item.setPercentage(res.getFloat(res.getColumnIndex(COUPON_PER)));
                item.setStatus(res.getString(res.getColumnIndex(STATUS)));
                item.setStartDate(res.getString(res.getColumnIndex(START_DATE)));
                item.setEndDate(res.getString(res.getColumnIndex(END_DATE)));
                itemList.add(item);
            }while (res.moveToNext());

        }

        return itemList;
    }


    public boolean updateProductFreeOffer(ProductDiscountOffer item, String updatedAt, String  shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, item.getId());
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(PROD_BUY_ID, item.getProdBuyId());
        contentValues.put(PROD_FREE_ID, item.getProdFreeId());
        contentValues.put(PROD_BUY_QTY, item.getProdBuyQty());
        contentValues.put(PROD_FREE_QTY, item.getProdFreeQty());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update(PROD_FREE_OFFER_TABLE,contentValues,ID+" = ?" + " AND " + SHOP_CODE + "=?" ,
                new String[]{String.valueOf(item.getId()), shopCode});
        Log.i("DbHelper","Row is updated");
        return true;
    }


    public boolean updateProductPriceOffer(ProductComboOffer item, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(ID, item.getId());
        contentValues.put(PROD_ID, item.getProdId());
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update(PROD_PRICE_TABLE,contentValues,ID+" = ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(item.getId()), shopCode});
        Log.i("DbHelper","Row is updated");
        return true;
    }

    public boolean updateProductPriceDetailOffer(ProductComboDetails item, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFER_ID, item.getPcodPcoId());
        contentValues.put(TOTAL_QTY, item.getPcodProdQty());
        contentValues.put(PROD_SP, item.getPcodPrice());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update(PROD_PRICE_DETAIL_TABLE,contentValues,ID+" = ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(item.getId()), shopCode});
        Log.i("DbHelper","Row is updated");
        return true;
    }

    public boolean updateProductComboOffer(ProductComboOffer item, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, item.getOfferName());
        contentValues.put(START_DATE, item.getStartDate());
        contentValues.put(END_DATE, item.getEndDate());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update(PROD_COMBO_TABLE,contentValues,ID+" = ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(item.getId()), shopCode});
        Log.i("DbHelper","Row is updated");
        return true;
    }

    public boolean updateProductComboDetailOffer(ProductComboDetails item, String updatedAt, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROD_ID, item.getPcodProdId());
        contentValues.put(TOTAL_QTY, item.getPcodProdQty());
        contentValues.put(PROD_SP, item.getPcodPrice());
        contentValues.put(STATUS, item.getStatus());
        contentValues.put(UPDATED_AT, updatedAt);
        db.update(PROD_COMBO_DETAIL_TABLE,contentValues,ID+" = ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(item.getId()), shopCode});
        Log.i("DbHelper","Row is updated");
        return true;
    }

    public void updateFreeCartData(int id,int qty,float totalAmount, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        // query="UPDATE "+PRODUCT_TABLE+" SET "+PROD_QOH+" = ? where "+PROD_CODE+" = ?";
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROD_CGST, 0);
        contentValues.put(PROD_IGST, 0);
        contentValues.put(PROD_SGST, 0);
        contentValues.put(TOTAL_QTY, qty);
        contentValues.put(TOTAL_AMOUNT, totalAmount);
        db.update(CART_TABLE,contentValues,ID+" = ? AND "+PROD_SP+" == ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(id),String.valueOf(0f), shopCode});
        //  db.update(CART_TABLE,contentValues,PROD_BARCODE+" = ? AND "+PROD_SP+" == ?",
        //          new String[]{String.valueOf(id),String.valueOf(0f)});

    }


    public void updateOfferCounterCartData(int offerCounter,int prodId, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        // query="UPDATE "+PRODUCT_TABLE+" SET "+PROD_QOH+" = ? where "+PROD_CODE+" = ?";
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFER_ITEM_COUNTER, offerCounter);

        double result = db.update(CART_TABLE, contentValues, ID + " = ?" + " AND " + SHOP_CODE + "=?" + " AND " + PROD_SP+" != ?",
                new String[] { String.valueOf(prodId),  shopCode, String.valueOf(0)});
        Log.d(TAG, "offerCounter updated with "+offerCounter +" update status "+result);
    }

    public void updateFreePositionCartData(int position,int prodId, String shopCode){
        SQLiteDatabase db = this.getReadableDatabase();
        // query="UPDATE "+PRODUCT_TABLE+" SET "+PROD_QOH+" = ? where "+PROD_CODE+" = ?";
        ContentValues contentValues = new ContentValues();
        contentValues.put(FREE_PRODUCT_POSITION, position);
        db.update(CART_TABLE,contentValues,ID+" = ? AND "+PROD_SP+" != ?" + " AND " + SHOP_CODE + "=?",
                new String[]{String.valueOf(prodId),String.valueOf(0f), shopCode});

    }

    /*public boolean removeProductFromCart(String prodBarCode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CART_TABLE, PROD_BARCODE+" = ? AND "+PROD_SP+" != ?",new String[]{prodBarCode,String.valueOf(0f)});
        return true;
    }*/

    public boolean removeProductFromCart(String id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(CART_TABLE, ID+" = ? AND "+PROD_SP+" != ?"  + " AND " + SHOP_CODE + "=?",new String[]{String.valueOf(id),String.valueOf(0f),shopCode});
        Log.d(TAG, "removed product from cart status "+val + "id  "+id);
        if(getCartCount(shopCode)<1)
            removeShopDeliveryDetails(shopCode);
        return true;
    }

    public boolean removeFreeProductFromCart(int id, String shopCode){
        SQLiteDatabase db = this.getWritableDatabase();
        long val = db.delete(CART_TABLE, ID+" = ? AND "+PROD_SP+" = ?"  + " AND " + SHOP_CODE + "=?",new String[]{String.valueOf(id),String.valueOf(0f),shopCode });
        Log.d(TAG, "removed free product from cart status "+val);
        return true;
    }

    public boolean removeUnit(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCT_UNIT_TABLE, ID+" = ?",new String[]{String.valueOf(id)});
        return true;
    }

    public boolean removeSize(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCT_SIZE_TABLE, ID+" = ?",new String[]{id});
        db.delete(PRODUCT_COLOR_TABLE, SIZE_ID+" = ?",new String[]{id});
        return true;
    }

    public boolean removeColor(String  id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCT_COLOR_TABLE, ID+" = ?",new String[]{id});
        return true;
    }

    public boolean deleteTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        return true;
    }

    public void deleteAllTable(){
        deleteTable(DbHelper.PRODUCT_UNIT_TABLE);
        deleteTable(DbHelper.PRODUCT_SIZE_TABLE);
        deleteTable(DbHelper.PRODUCT_COLOR_TABLE);
        deleteTable(DbHelper.CART_TABLE);
        deleteTable(DbHelper.PROD_COMBO_TABLE);
        deleteTable(DbHelper.PROD_COMBO_DETAIL_TABLE);
        deleteTable(DbHelper.PROD_PRICE_TABLE);
        deleteTable(DbHelper.PROD_PRICE_DETAIL_TABLE);
        deleteTable(DbHelper.PROD_FREE_OFFER_TABLE);
        deleteTable(DbHelper.COUPON_TABLE);
        deleteTable(DbHelper.SHOP_DELIVERY_DETAILS_TABLE);

    }

}