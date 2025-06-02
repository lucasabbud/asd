package com.pedidosmovil2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper 
{
	//Table Customers
	public final static String TABLE_CUSTOMER = "customer";
	public final static String CUSTOMER_KEY_ID = "_id";
	public final static String CUSTOMER_COL_NOMBRE = "nombre";
	public final static String CUSTOMER_COL_APENOM = "apenom";
	public final static String CUSTOMER_COL_RAZSOC = "razsoc";
	public final static String CUSTOMER_COL_DIR = "dir";
	public final static String CUSTOMER_COL_TEL = "tel";
	public final static String CUSTOMER_COL_EMAIL = "email";
	public final static String CUSTOMER_COL_OBS = "obs";
	public final static String CUSTOMER_COL_DSC = "dsc";
	
	//Table product
	public final static String TABLE_PRODUCT = "product";
	public final static String PRODUCT_KEY_ID = "_id";
	public final static String PRODUCT_COL_NOMBREPV = "nombrepv";
	public final static String PRODUCT_COL_NOMBRE = "nombre";
	public final static String PRODUCT_COL_GRAMAJE = "gramaje";
	public final static String PRODUCT_COL_COSTO = "costo";
	public final static String PRODUCT_COL_IMP = "imp";
	public final static String PRODUCT_COL_STOCK = "stock";
	public final static String PRODUCT_COL_STOCKMIN = "stockmin";

	//Table Invoice
	public final static String TABLE_INVOICE = "invoice";
	public final static String INVOICE_KEY_ID = "_id";
	public final static String INVOICE_COL_FOLIO = "folio";
	public final static String INVOICE_COL_CUSTOMER_ID = "customer_id";
	public final static String INVOICE_COL_DATE = "date";
	public final static String INVOICE_COL_EMP = "empleado";
	public final static String INVOICE_COL_COMMENTS = "comments";
	public final static String INVOICE_COL_SUBTOTAL = "subtotal";
	public final static String INVOICE_COL_TOTAL = "total";
	public final static String INVOICE_COL_DISCOUNT = "discount";
	public final static String INVOICE_COL_SIGNATURE_ID = "signature_id";
	
	//Table Invoice details
	public final static String TABLE_INVOICE_DETAIL = "invoice_detail";
	public final static String INVOICE_DETAIL_KEY_ID = "_id";
	public final static String INVOICE_DETAIL_COL_INVOICE_ID = "invoice_id";
	public final static String INVOICE_DETAIL_COL_PRODUCT_ID = "product_id";
	public final static String INVOICE_DETAIL_COL_QUANTITY = "quantity";
	public final static String INVOICE_DETAIL_COL_BONIF = "bonificacion";
	public final static String INVOICE_DETAIL_COL_TOTAL = "total";
	
	//Table Customer Products Discounts
	public final static String TABLE_CUSTOMER_PRODUCT = "customer_product";
	public final static String CUSTOMER_PRODUCT_KEY_ID = "_id";
	public final static String CUSTOMER_PRODUCT_COL_CUSTOMER_ID = "customer_id";
	public final static String CUSTOMER_PRODUCT_COL_PRODUCT_ID = "product_id";
	public final static String CUSTOMER_PRODUCT_COL_BON = "bonificacion";
	
	private final static String DATABASE_NAME = "PedidosMovil";
	private final static int DATABASE_VERSION = 7;
	
	private final static String CREATE_TABLE_CUSTOMER = 
			"CREATE TABLE customer(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"nombre nvarchar(50)," +
            "apenom nvarchar(50)," +
            "razsoc nvarchar(50)," +
            "dir nvarchar(50)," +
            "tel nvarchar(20)," +
            "email nvarchar(20)," + 
            "obs nvarchar(100)," +
            "dsc real)";
	private final static String CREATE_TABLE_PRODUCT =
			"CREATE TABLE product(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"nombrepv nvarchar(50)," +
			"nombre nvarchar(50)," +
			"gramaje nvarchar(10)," +
            "costo real," +
            "imp real," +
			"stock real," +
			"stockmin real)";
	private final static String CREATE_TABLE_INVOICE =
            "CREATE TABLE invoice(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "folio int," +
            "customer_id int REFERENCES customer(_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "date datetime," +
            "comments ntext," +
            "empleado ntext," +
            "subtotal real," +
            "discount real," +
            "total real," +
            "boxes int," +
            "signature_id)";		
	private final static String CREATE_TABLE_INVOICE_DETAIL =
            "CREATE TABLE invoice_detail(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "invoice_id int REFERENCES invoice(_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "product_id int REFERENCES product(_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "quantity int," +
            "bonificacion int," +
            "total real)";	
	
	private final static String CREATE_TABLE_CUSTOMER_PRODUCT =
            "CREATE TABLE customer_product(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "customer_id int REFERENCES customer(_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "product_id int REFERENCES product(_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "bonificacion real)";	
	
	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDB) 
	{
		sqLiteDB.execSQL(CREATE_TABLE_CUSTOMER);
		sqLiteDB.execSQL(CREATE_TABLE_PRODUCT);
		sqLiteDB.execSQL(CREATE_TABLE_INVOICE);
		sqLiteDB.execSQL(CREATE_TABLE_INVOICE_DETAIL);
		sqLiteDB.execSQL(CREATE_TABLE_CUSTOMER_PRODUCT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion, int newVersion) 
	{
		sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
		sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
		sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICE);
		sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICE_DETAIL);
		sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_PRODUCT);
		
		onCreate(sqLiteDB);
	}
}
