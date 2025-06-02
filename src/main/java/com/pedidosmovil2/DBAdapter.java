package com.pedidosmovil2;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter 
{
	private Context context;
	private SQLiteDatabase database;
	private DBOpenHelper dbHelper;
	
	public DBAdapter(Context context) {
		this.context = context;
	}
	
	public DBAdapter open() throws SQLException 
	{
		dbHelper = new DBOpenHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getCursor(String tableName, String[] fields, String whereFieldName, CharSequence constraint,String order)
	{
		String query;
		String selection = "*";
		if(fields != null) 
		{
			int i=0;
			selection = "";
			for(i=0; i<fields.length-1; i++) {
				selection += fields[i];
				selection += ",";
			}
			selection += fields[i];
		}
		
		if(constraint != null) {
			query = "SELECT " + selection + " FROM " + tableName + " WHERE " + whereFieldName + " LIKE '%" + constraint.toString() + "%'"+" ORDER BY "+order;
		}
		else {
			query = "SELECT " + selection + " FROM " + tableName+" ORDER BY "+order;
		}	
		
		Cursor cursor = database.rawQuery(query,  null);
		return cursor;
	}
	
	public Cursor getCursorInvoice(String tableName, String[] fields, String whereFieldName, CharSequence constraint,String order)
	{
		String query;
		
		query = "SELECT a._id,b.nombre || '_' || a.date AS nombre FROM invoice a inner join customer b on a.customer_id = b._id ORDER BY a.date";
		
		Cursor cursor = database.rawQuery(query,  null);
		return cursor;
	}
		
	
	public void deleteTable(String tableName)
	{
		database.execSQL("DROP TABLE "+tableName);
		if (tableName=="customer") {
			database.execSQL("CREATE TABLE customer(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"nombre nvarchar(50)," +
		            "apenom nvarchar(50)," +
		            "razsoc nvarchar(50)," +
		            "dir nvarchar(50)," +
		            "tel nvarchar(20)," +
		            "email nvarchar(20)," + 
		            "obs nvarchar(100)," +
		            "dsc real)");
		}
		else {
			database.execSQL("CREATE TABLE product(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"nombrepv nvarchar(50)," +
					"nombre nvarchar(50)," +
					"gramaje nvarchar(10)," +
		            "costo real," +
		            "imp real," +
					"stock real," +
					"stockmin real)");
		}
	}
	
	//-------------------------------------------------------------------------
	// CUSTOMER TABLE
	//-------------------------------------------------------------------------
	public int insertRecord(CustomerModel customer) 
	{
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.CUSTOMER_COL_NOMBRE, customer.getNombre());
		values.put(DBOpenHelper.CUSTOMER_COL_APENOM, customer.getApenom());
		values.put(DBOpenHelper.CUSTOMER_COL_RAZSOC, customer.getRazsoc());
		values.put(DBOpenHelper.CUSTOMER_COL_DIR, customer.getDir());
		values.put(DBOpenHelper.CUSTOMER_COL_EMAIL, customer.getEmail());
		values.put(DBOpenHelper.CUSTOMER_COL_TEL, customer.getTel());
		values.put(DBOpenHelper.CUSTOMER_COL_OBS, customer.getObs());
		values.put(DBOpenHelper.CUSTOMER_COL_DSC, customer.getDsc());
		
		return (int)database.insert(DBOpenHelper.TABLE_CUSTOMER, null, values);
	}
	
	public boolean updateRecord(CustomerModel customer)
	{
		String[] id = { String.valueOf(customer.getID()) };		
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.CUSTOMER_COL_NOMBRE, customer.getNombre());
		values.put(DBOpenHelper.CUSTOMER_COL_APENOM, customer.getApenom());
		values.put(DBOpenHelper.CUSTOMER_COL_RAZSOC, customer.getRazsoc());
		values.put(DBOpenHelper.CUSTOMER_COL_DIR, customer.getDir());
		values.put(DBOpenHelper.CUSTOMER_COL_EMAIL, customer.getEmail());
		values.put(DBOpenHelper.CUSTOMER_COL_TEL, customer.getTel());
		values.put(DBOpenHelper.CUSTOMER_COL_OBS, customer.getObs());
		values.put(DBOpenHelper.CUSTOMER_COL_DSC, customer.getDsc());
		
		return database.update(DBOpenHelper.TABLE_CUSTOMER, values, DBOpenHelper.CUSTOMER_KEY_ID + "=?", id) > 0;
	}

	public boolean deleteRecord(CustomerModel customer) 
	{
		String[] id = { String.valueOf(customer.getID()) };
		
		return database.delete(DBOpenHelper.TABLE_CUSTOMER, DBOpenHelper.CUSTOMER_KEY_ID + "=?", id) > 0;
	}
	
	public ArrayList<CustomerModel> fetchAllCustomers() 
	{
		ArrayList<CustomerModel> records = new ArrayList<CustomerModel>();
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_CUSTOMER;
		
		Cursor cursor = database.rawQuery(query,  null);
		if(cursor.moveToFirst())
		{
			do {			
				CustomerModel cust = new CustomerModel();
				cust.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_KEY_ID))));
				cust.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_NOMBRE))));
				cust.setApenom(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_APENOM))));
				cust.setRazsoc(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_RAZSOC))));
				cust.setDir(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DIR))));
				cust.setTel(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_TEL))));
				cust.setEmail(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_EMAIL))));
				cust.setObs(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_OBS))));
				cust.setDsc(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DSC))));
				records.add(cust);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		return records;
	}	

	public CustomerModel fetchCustomer(int id) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_CUSTOMER + " WHERE " + DBOpenHelper.CUSTOMER_KEY_ID + "=" + id;		
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			
			CustomerModel cust = new CustomerModel();
			cust.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_KEY_ID))));
			cust.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_NOMBRE))));
			cust.setApenom(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_APENOM))));
			cust.setRazsoc(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_RAZSOC))));
			cust.setDir(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DIR))));
			cust.setTel(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_TEL))));
			cust.setEmail(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_EMAIL))));
			cust.setObs(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_OBS))));	
			cust.setDsc(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DSC))));
			
			return cust;
		}
		cursor.close();
		return null;
	}
	
	public CustomerModel fetchCustomerName(String id) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_CUSTOMER + " WHERE " + DBOpenHelper.CUSTOMER_COL_NOMBRE + "='" + id+"'";		
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			
			CustomerModel cust = new CustomerModel();
			cust.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_KEY_ID))));
			cust.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_NOMBRE))));
			cust.setApenom(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_APENOM))));
			cust.setRazsoc(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_RAZSOC))));
			cust.setDir(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DIR))));
			cust.setTel(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_TEL))));
			cust.setEmail(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_EMAIL))));
			cust.setObs(cursor.getString((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_OBS))));	
			cust.setDsc(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_COL_DSC))));
			
			return cust;
		}
		cursor.close();
		return null;
	}


	//-------------------------------------------------------------------------
	// PRODUCT TABLE
	//-------------------------------------------------------------------------
	public int insertRecord(ProductModel product) 
	{
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.PRODUCT_COL_NOMBREPV, product.getNombrePV());
		values.put(DBOpenHelper.PRODUCT_COL_NOMBRE, product.getNombre());
		values.put(DBOpenHelper.PRODUCT_COL_GRAMAJE, product.getGramaje());
		values.put(DBOpenHelper.PRODUCT_COL_COSTO, product.getCosto());
		values.put(DBOpenHelper.PRODUCT_COL_IMP, product.getImp());
		values.put(DBOpenHelper.PRODUCT_COL_STOCK, product.getStock());
		values.put(DBOpenHelper.PRODUCT_COL_STOCKMIN, product.getStockmin());
		
		return (int)database.insert(DBOpenHelper.TABLE_PRODUCT, null, values);
	}
	
	public boolean updateRecord(ProductModel product)
	{
		String[] id = { String.valueOf(product.getID()) };		
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.PRODUCT_COL_NOMBREPV, product.getNombrePV());
		values.put(DBOpenHelper.PRODUCT_COL_NOMBRE, product.getNombre());
		values.put(DBOpenHelper.PRODUCT_COL_GRAMAJE, product.getGramaje());
		values.put(DBOpenHelper.PRODUCT_COL_COSTO, product.getCosto());
		values.put(DBOpenHelper.PRODUCT_COL_IMP, product.getImp());
		values.put(DBOpenHelper.PRODUCT_COL_STOCK, product.getStock());
		values.put(DBOpenHelper.PRODUCT_COL_STOCKMIN, product.getStockmin());
		
		return database.update(DBOpenHelper.TABLE_PRODUCT, values, DBOpenHelper.PRODUCT_KEY_ID + "=?", id) > 0;
	}

	public boolean deleteRecord(ProductModel product) 
	{
		String[] id = { String.valueOf(product.getID()) };
		
		return database.delete(DBOpenHelper.TABLE_PRODUCT, DBOpenHelper.PRODUCT_KEY_ID + "=?", id) > 0;
	}
	
	public ArrayList<ProductModel> fetchAllProducts() 
	{
		ArrayList<ProductModel> records = new ArrayList<ProductModel>();
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_PRODUCT + " ORDER BY nombrepv ASC";
		
		Cursor cursor = database.rawQuery(query,  null);
		if(cursor.moveToFirst())
		{
			do {			
				ProductModel model = new ProductModel();
				model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_KEY_ID))));
				model.setNombrePV(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBREPV))));
				model.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBRE))));
				model.setGramaje(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_GRAMAJE))));
				model.setCosto(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_COSTO))));
				model.setImp(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_IMP))));
				model.setStock(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCK))));
				model.setStockmin(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCKMIN))));
				records.add(model);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		return records;
	}	

	public ProductModel fetchProduct(int id) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_PRODUCT + " WHERE " + DBOpenHelper.PRODUCT_KEY_ID + "=" + id;
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			ProductModel model = new ProductModel();
			model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_KEY_ID))));
			model.setNombrePV(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBREPV))));
			model.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBRE))));
			model.setGramaje(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_GRAMAJE))));
			model.setCosto(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_COSTO))));
			model.setImp(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_IMP))));		
			model.setStock(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCK))));
			model.setStockmin(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCKMIN))));
			cursor.close();
			return model;
		}
		return null;
	}
	
	public ProductModel productPV(String PV) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_PRODUCT + " WHERE " + DBOpenHelper.PRODUCT_COL_NOMBREPV + "='" + PV+"'";
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			ProductModel model = new ProductModel();
			model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_KEY_ID))));
			model.setNombrePV(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBREPV))));
			model.setNombre(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_NOMBRE))));
			model.setGramaje(cursor.getString((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_GRAMAJE))));
			model.setCosto(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_COSTO))));
			model.setImp(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_IMP))));		
			model.setStock(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCK))));
			model.setStockmin(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.PRODUCT_COL_STOCKMIN))));
			cursor.close();
			return model;
		}
		return null;
	}

	//-------------------------------------------------------------------------
	// INVOICE TABLE
	//-------------------------------------------------------------------------
	public int insertRecord(InvoiceModel invoice) 
	{
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.INVOICE_COL_COMMENTS, invoice.getComments());
		values.put(DBOpenHelper.INVOICE_COL_CUSTOMER_ID, invoice.getCustomerID());
		values.put(DBOpenHelper.INVOICE_COL_DATE, invoice.getDate());
		values.put(DBOpenHelper.INVOICE_COL_DISCOUNT,  invoice.getDiscount());
		values.put(DBOpenHelper.INVOICE_COL_FOLIO, invoice.getFolio());
		values.put(DBOpenHelper.INVOICE_COL_SUBTOTAL,  invoice.getSubtotal());
		values.put(DBOpenHelper.INVOICE_COL_EMP,  invoice.getEmpleado());
		values.put(DBOpenHelper.INVOICE_COL_TOTAL,  invoice.getTotal());
		values.put(DBOpenHelper.INVOICE_COL_SIGNATURE_ID, invoice.getSignatureID());
		
		return (int)database.insert(DBOpenHelper.TABLE_INVOICE, null, values);
	}
	
	public boolean updateRecord(InvoiceModel invoice)
	{
		String[] id = { String.valueOf(invoice.getID()) };		
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.INVOICE_COL_COMMENTS, invoice.getComments());
		values.put(DBOpenHelper.INVOICE_COL_CUSTOMER_ID, invoice.getCustomerID());
		values.put(DBOpenHelper.INVOICE_COL_DATE, invoice.getDate());
		values.put(DBOpenHelper.INVOICE_COL_DISCOUNT,  invoice.getDiscount());
		values.put(DBOpenHelper.INVOICE_COL_FOLIO, invoice.getFolio());
		values.put(DBOpenHelper.INVOICE_COL_SUBTOTAL,  invoice.getSubtotal());
		values.put(DBOpenHelper.INVOICE_COL_EMP,  invoice.getEmpleado());
		values.put(DBOpenHelper.INVOICE_COL_TOTAL,  invoice.getTotal());
		values.put(DBOpenHelper.INVOICE_COL_SIGNATURE_ID, invoice.getSignatureID());
		
		return database.update(DBOpenHelper.TABLE_INVOICE, values, DBOpenHelper.INVOICE_KEY_ID + "=?", id) > 0;
	}

	public boolean deleteRecord(InvoiceModel invoice) 
	{
		String[] id = { String.valueOf(invoice.getID()) };
		
		return database.delete(DBOpenHelper.TABLE_INVOICE, DBOpenHelper.INVOICE_KEY_ID + "=?", id) > 0;
	}
	
	public ArrayList<InvoiceModel> fetchAllInvoices() 
	{
		ArrayList<InvoiceModel> records = new ArrayList<InvoiceModel>();
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_INVOICE;
		
		Cursor cursor = database.rawQuery(query,  null);
		if(cursor.moveToFirst())
		{
			do {			
				InvoiceModel model = new InvoiceModel();
				model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_KEY_ID))));
				model.setComments(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_COMMENTS))));
				model.setCustomerID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_CUSTOMER_ID))));
				model.setDate(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DATE))));
				model.setDiscount(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DISCOUNT))));
				model.setFolio(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_FOLIO))));
				model.setSubtotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SUBTOTAL))));
				model.setTotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_TOTAL))));
				model.setSignatureID(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SIGNATURE_ID))));
				model.setEmpleado(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_EMP))));
				records.add(model);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		return records;
	}	
	
	public ArrayList<InvoiceModel> fetchAllInvoices2(String date) 
	{
		ArrayList<InvoiceModel> records = new ArrayList<InvoiceModel>();
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_INVOICE + " WHERE " + "DATE("+DBOpenHelper.INVOICE_COL_DATE + ")='" + date+"'";
		
		Cursor cursor = database.rawQuery(query,  null);
		if(cursor.moveToFirst())
		{
			do {			
				InvoiceModel model = new InvoiceModel();
				model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_KEY_ID))));
				model.setComments(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_COMMENTS))));
				model.setCustomerID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_CUSTOMER_ID))));
				model.setDate(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DATE))));
				model.setDiscount(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DISCOUNT))));
				model.setFolio(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_FOLIO))));
				model.setSubtotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SUBTOTAL))));
				model.setTotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_TOTAL))));
				model.setSignatureID(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SIGNATURE_ID))));
				model.setEmpleado(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_EMP))));
				records.add(model);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		return records;
	}	

	public InvoiceModel fetchInvoice(int id) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_INVOICE + " WHERE " + DBOpenHelper.INVOICE_KEY_ID + "=" + id;
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			
			InvoiceModel model = new InvoiceModel();
			model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_KEY_ID))));
			model.setComments(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_COMMENTS))));
			model.setCustomerID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_CUSTOMER_ID))));
			model.setDate(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DATE))));
			model.setDiscount(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_DISCOUNT))));
			model.setFolio(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_FOLIO))));
			model.setSubtotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SUBTOTAL))));
			model.setTotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_TOTAL))));			
			model.setSignatureID(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_SIGNATURE_ID))));
			model.setEmpleado(cursor.getString((cursor.getColumnIndex(DBOpenHelper.INVOICE_COL_EMP))));
			cursor.close();
			return model;
		}
		
		return null;
	}

	//-------------------------------------------------------------------------
	// INVOICE_DETAIL TABLE
	//-------------------------------------------------------------------------
	public int insertRecord(InvoiceDetailModel invoice) 
	{
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_INVOICE_ID, invoice.getInvoiceID());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_PRODUCT_ID, invoice.getProductID());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_QUANTITY, invoice.getQuantity());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_BONIF, invoice.getBonif());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_TOTAL, invoice.getTotal());
		
		return (int)database.insert(DBOpenHelper.TABLE_INVOICE_DETAIL, null, values);
	}
	
	public boolean updateRecord(InvoiceDetailModel invoice)
	{
		String[] id = { String.valueOf(invoice.getID()) };		
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_INVOICE_ID, invoice.getInvoiceID());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_PRODUCT_ID, invoice.getProductID());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_QUANTITY, invoice.getQuantity());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_BONIF, invoice.getBonif());
		values.put(DBOpenHelper.INVOICE_DETAIL_COL_TOTAL, invoice.getTotal());
		
		return database.update(DBOpenHelper.TABLE_INVOICE_DETAIL, values, DBOpenHelper.INVOICE_DETAIL_KEY_ID + "=?", id) > 0;
	}

	public boolean deleteRecord(InvoiceDetailModel invoice) 
	{
		String[] id = { String.valueOf(invoice.getID()) };
		
		return database.delete(DBOpenHelper.TABLE_INVOICE_DETAIL, DBOpenHelper.INVOICE_DETAIL_KEY_ID + "=?", id) > 0;
	}
	
	public ArrayList<InvoiceDetailModel> fetchAllInvoiceDetails(int invoiceID) 
	{
		ArrayList<InvoiceDetailModel> records = new ArrayList<InvoiceDetailModel>();
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_INVOICE_DETAIL + " WHERE " + DBOpenHelper.INVOICE_DETAIL_COL_INVOICE_ID + "=" + invoiceID;
		
		Cursor cursor = database.rawQuery(query,  null);
		if(cursor.moveToFirst())
		{
			do {			
				InvoiceDetailModel model = new InvoiceDetailModel();
				model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_KEY_ID))));
				model.setInvoiceID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_INVOICE_ID))));
				model.setProductID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_PRODUCT_ID))));
				model.setQuantity(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_QUANTITY))));
				model.setBonif(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_BONIF))));
				model.setTotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_TOTAL))));
				records.add(model);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		return records;
	}	

	public InvoiceDetailModel fetchInvoiceDetail(int id) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_INVOICE_DETAIL + " WHERE " + DBOpenHelper.INVOICE_DETAIL_KEY_ID + "=" + id;
		Cursor cursor = database.rawQuery(query,  null);
		
		if (cursor != null) 
		{
			cursor.moveToFirst();
			
			InvoiceDetailModel model = new InvoiceDetailModel();
			model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_KEY_ID))));
			model.setInvoiceID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_INVOICE_ID))));
			model.setProductID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_PRODUCT_ID))));
			model.setQuantity(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_QUANTITY))));
			model.setBonif(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_BONIF))));
			model.setTotal(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.INVOICE_DETAIL_COL_TOTAL))));
			cursor.close();
			return model;
		}
		
		return null;
	}
	
	//-------------------------------------------------------------------------
	// CUSTOMER_PRODUCT TABLE
	//-------------------------------------------------------------------------
	public int insertRecord(CustomerProductModel customerProduct) 
	{
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_CUSTOMER_ID, customerProduct.getCustomer_id());
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_PRODUCT_ID, customerProduct.getProduct_id());
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_BON, customerProduct.getBonificacion());
		
		return (int)database.insert(DBOpenHelper.TABLE_CUSTOMER_PRODUCT, null, values);
	}
	
	public boolean updateRecord(CustomerProductModel customerProduct)
	{
		String[] id = { String.valueOf(customerProduct.getID()) };		
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_CUSTOMER_ID, customerProduct.getCustomer_id());
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_PRODUCT_ID, customerProduct.getProduct_id());
		values.put(DBOpenHelper.CUSTOMER_PRODUCT_COL_BON, customerProduct.getBonificacion());
		
		return database.update(DBOpenHelper.TABLE_CUSTOMER_PRODUCT, values, DBOpenHelper.CUSTOMER_PRODUCT_KEY_ID + "=?", id) > 0;
	}

	public boolean deleteRecord(CustomerProductModel customerProduct) 
	{
		String[] id = { String.valueOf(customerProduct.getID()) };
		
		return database.delete(DBOpenHelper.TABLE_CUSTOMER_PRODUCT, DBOpenHelper.CUSTOMER_PRODUCT_KEY_ID + "=?", id) > 0;
	}
	
	public CustomerProductModel fetchCustomerProduct(int idc,int idp) 
	{
		String query = "SELECT * FROM " + DBOpenHelper.TABLE_CUSTOMER_PRODUCT + " WHERE " + DBOpenHelper.CUSTOMER_PRODUCT_COL_CUSTOMER_ID + "=" + idc + " AND " + DBOpenHelper.CUSTOMER_PRODUCT_COL_PRODUCT_ID + "=" + idp;
		Cursor cursor = database.rawQuery(query,  null);
//		Log.d("log1", "busca");
		if (cursor != null && cursor.moveToFirst()) 
		{
			cursor.moveToFirst();
			CustomerProductModel model = new CustomerProductModel();
			model.setID(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_PRODUCT_KEY_ID))));
			model.setCustomer_id(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_PRODUCT_COL_CUSTOMER_ID))));
			model.setProduct_id(cursor.getInt((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_PRODUCT_COL_PRODUCT_ID))));
			model.setBonificacion(cursor.getFloat((cursor.getColumnIndex(DBOpenHelper.CUSTOMER_PRODUCT_COL_BON))));
			cursor.close();
			return model;
		}
		return null;
	}
	
}
