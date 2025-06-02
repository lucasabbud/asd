package com.pedidosmovil2;

import jxl.*;
import jxl.read.biff.BiffException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class ImportExcel2 extends AsyncTask<Integer, Integer, Integer>
{
	public static final int TYPE_INVOICE = 2;
	private InvoiceModel invoice;

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
//	private String filePath;
	private String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/PEDIDOS";
	File directory = new File(filePath);
	File[] files = directory.listFiles();
	private int type;
	private ProgressDialog progressDialog;
	private OnTaskCompleted taskCompletedListener;
	private OnProgressUpdate progressUpdateListener;
	
	public static void main(String[] args) {
	}
	
	public ImportExcel2(Context context) {
		this.context = context;
	}
	
//	public void setOnTaskCompletedListener(OnTaskCompleted listener) {
//		this.taskCompletedListener = listener;
//	}
//	
//	public void setOnProgressUpdateListener(OnProgressUpdate listener) {
//		this.progressUpdateListener = listener;
//	}
	
	public void setProgressDialog(ProgressDialog dialog) {
		this.progressDialog = dialog;
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
			for (int j = 0; j < files.length; j++)
			{
			Workbook workbook = Workbook.getWorkbook(new File(files[j].getAbsolutePath()));
			Sheet sheet = workbook.getSheet(0);
			progressDialog.setMax(sheet.getRows());
			for(int i=0; i<sheet.getRows(); i++)
			{						
				
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
				Thread.sleep(10);
				publishProgress(i);
			}
			
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
//		taskCompletedListener.onTaskCompleted(result);
		
		progressDialog.dismiss();
		if(result == 0)
			Toast.makeText(context, "�Datos importados con �xito!", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(context, "Ha ocurrido un error al importar datos", Toast.LENGTH_SHORT).show();
	}
	
//	@Override
//	protected void onProgressUpdate(Integer... values) 
//	{
//		Log.d("log1", "o");
//		progressUpdateListener.onProgressUpdate(values[0]);
//		progressDialog.setProgress(values[0]);
//		Log.d("log1", "p");
//	}
	
	
}
