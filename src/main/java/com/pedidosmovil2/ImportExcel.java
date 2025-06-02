package com.pedidosmovil2;

import jxl.*;
import jxl.read.biff.BiffException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ImportExcel extends AsyncTask<Integer, Integer, Integer>
{
	public static final int TYPE_CUSTOMER = 0;
	public static final int TYPE_PRODUCT = 1;
	public static final int TYPE_INVOICE = 2;
	
	private InvoiceModel invoice;
	
	private int COL_PROD_NOMBREPV = 0;
	private int COL_PROD_NOMBRE = 1;
	private int COL_PROD_GRAMAJE = 2;
	private int COL_PROD_COSTO = 3;
	private int COL_PROD_IMP = 4;
	private int COL_PROD_STOCK = 5;
	private int COL_PROD_STOCKMIN = 6;
	
	private int COL_NOMBRE = 0;
	private int COL_APENOM = 1;
//	private int COL_RAZSOC = 2;
	private int COL_DIR = 2;
	private int COL_TEL = 3;
	private int COL_EMAIL = 4;
	private int COL_OBS = 5;

	private int COL_CLIENTE = 0;
	private int COL_FECHA = 1;
	private int COL_COMENTARIO = 2;
	private int COL_TOTAL = 3;
	private int COL_SUBTOTAL = 4;
	private int COL_DESCUENTO = 5;
	private int COL_EMPLEADO = 6;

	private int COL_NOMPV = 0;
	private int COL_CANT = 1;
	private int COL_BONIF = 2;
	private int COL_TOTALD = 3;
	
	private DBAdapter db;
	private Context context;
	private String filePath;
	private int type;
	private ProgressDialog progressDialog;
	private OnTaskCompleted taskCompletedListener;
	private OnProgressUpdate progressUpdateListener;
	
	public static void main(String[] args) {
	}
	
	public ImportExcel(Context context) {
		this.context = context;
	}
	
	public void setOnTaskCompletedListener(OnTaskCompleted listener) {
		this.taskCompletedListener = listener;
	}
	
	public void setOnProgressUpdateListener(OnProgressUpdate listener) {
		this.progressUpdateListener = listener;
	}
	
	public void setProgressDialog(ProgressDialog dialog) {
		this.progressDialog = dialog;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	@Override
	protected Integer doInBackground(Integer... params) 
	{	
		int retValue = 0;
		db = new DBAdapter(context);
		db.open();
		
		try 
		{
			switch(type) {
			case TYPE_CUSTOMER:
				db.deleteTable(DBOpenHelper.TABLE_CUSTOMER);
				break;
			case TYPE_PRODUCT:
				db.deleteTable(DBOpenHelper.TABLE_PRODUCT);
				break;
			}
			WorkbookSettings ws = new WorkbookSettings();

	        ws.setEncoding("ISO-8859-1");
	        
			Workbook workbook = Workbook.getWorkbook(new File(filePath),ws);
			Sheet sheet = workbook.getSheet(0);
			progressDialog.setMax(sheet.getRows());

			for(int i=0; i<sheet.getRows(); i++)
			{						
				switch(type) 
				{
				case TYPE_CUSTOMER: {
					if (i==0) {
						i++;
					}
					String nombre = sheet.getCell(COL_CLIENTE,i).getContents();
					String apenom = sheet.getCell(COL_APENOM,i).getContents();
//					String razsoc = sheet.getCell(COL_RAZSOC,i).getContents();
					String dir = sheet.getCell(COL_DIR,i).getContents();
					String tel = sheet.getCell(COL_TEL,i).getContents();
					String email = sheet.getCell(COL_EMAIL,i).getContents();
					String obs = sheet.getCell(COL_OBS,i).getContents();
					CustomerModel customer = new CustomerModel();
					customer.setNombre(nombre);
					customer.setApenom(apenom);
//					customer.setRazsoc(razsoc);
					customer.setDir(dir);
					customer.setTel(tel);
					customer.setEmail(email);
					customer.setObs(obs);
					db.insertRecord(customer);
					break;
					}
				case TYPE_INVOICE: {
					if (i==0) {
						String cli = sheet.getCell(COL_CLIENTE,i).getContents();
						CustomerModel customer = db.fetchCustomerName(cli);
						String com = sheet.getCell(COL_COMENTARIO,i).getContents();
						Float tot = Float.valueOf(sheet.getCell(COL_TOTAL,i).getContents().replace(',','.'));
						Float subtot = Float.valueOf(sheet.getCell(COL_SUBTOTAL,i).getContents().replace(',','.'));
						Float desc = Float.valueOf(sheet.getCell(COL_DESCUENTO,i).getContents().replace(',','.'));
						String emp = sheet.getCell(COL_EMPLEADO,i).getContents();
						invoice = new InvoiceModel();
						invoice.setCustomerID(customer.getID());
						invoice.setComments(com);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String today = sdf.format(new Date());
						invoice.setDate(today);
						invoice.setDiscount(desc);
						invoice.setTotal(tot);
						invoice.setSubtotal(subtot);
						invoice.setEmpleado(emp);
						int invoice_id = db.insertRecord(invoice);
						invoice.setID(invoice_id);
					}
					else {
						String nompv = sheet.getCell(COL_NOMPV,i).getContents();
						ProductModel prod = db.productPV(nompv);
						int cant = Integer.valueOf(sheet.getCell(COL_CANT,i).getContents().replace(',','.'));
						Float bonif = Float.valueOf(sheet.getCell(COL_BONIF,i).getContents().replace(',','.'));
						Float totd = Float.valueOf(sheet.getCell(COL_TOTALD,i).getContents().replace(',','.'));
						InvoiceDetailModel invoiceDetail = new InvoiceDetailModel();
						invoiceDetail.setInvoiceID(invoice.getID());
						invoiceDetail.setProductID(prod.getID());
						invoiceDetail.setQuantity(cant);
						invoiceDetail.setBonif(bonif);
						invoiceDetail.setTotal(totd);
						db.insertRecord(invoiceDetail);	
					}
					
					break;
					}
				case TYPE_PRODUCT: {
					if (i==0) {
						i++;
					}
					String nombrepv = sheet.getCell(COL_PROD_NOMBREPV,i).getContents();
					String nombrep = sheet.getCell(COL_PROD_NOMBRE,i).getContents();
					String gramaje = sheet.getCell(COL_PROD_GRAMAJE,i).getContents();
					Float costo = Float.valueOf(sheet.getCell(COL_PROD_COSTO,i).getContents().replace(',','.'));
					Float imp = Float.valueOf(sheet.getCell(COL_PROD_IMP,i).getContents().replace(',','.'));
					Float stock = Float.valueOf(sheet.getCell(COL_PROD_STOCK,i).getContents().replace(',','.'));
					Float stockmin = Float.valueOf(sheet.getCell(COL_PROD_STOCKMIN,i).getContents().replace(',','.'));
					ProductModel product = new ProductModel();
					product.setNombrePV(nombrepv);
					product.setNombre(nombrep);
					product.setGramaje(gramaje);
					product.setCosto(costo);
					product.setImp(imp);
					product.setStock(stock);
					product.setStockmin(stockmin);
					db.insertRecord(product);
					break;
					}
				}
				Thread.sleep(10);
				publishProgress(i);
			}

		} catch (BiffException e) {				
			e.printStackTrace();
			retValue = -1;
		} catch (Exception e) {			
			e.printStackTrace();
			retValue = -1;
		}

		db.close();
		return retValue;
	}
	
	@Override
	protected void onPostExecute(Integer result) 
	{
		taskCompletedListener.onTaskCompleted(result);
		
		progressDialog.dismiss();
		
		if(result == 0)
			Toast.makeText(context, "�Datos importados con �xito!", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(context, "Ha ocurrido un error al importar datos", Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) 
	{
		progressUpdateListener.onProgressUpdate(values[0]);
		progressDialog.setProgress(values[0]);
	}
}
