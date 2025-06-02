package com.pedidosmovil2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.os.Environment;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.itextpdf.text.DocumentException;

public class ExportXLS 
{
	private WritableWorkbook workbook;
	private Context context;
	private DBAdapter db;
	private InvoiceModel invoice;
	private ProductModel product;
	private CustomerModel customer;
	private ArrayList<InvoiceDetailModel> details;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public ExportXLS(Context context)
	{
		this.context = context;
	}

	public void Export(int invoiceID)
	{
		try {
			String nombre = "Pedido_"+String.valueOf(invoiceID)+".xls";
		    File path = new File(Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_DOWNLOADS), "PEDIDOS");
		    path.mkdirs();
		    File file = new File(path, nombre);
            workbook = Workbook.createWorkbook(file);	
        	
	        //get data from DB	        
			db = new DBAdapter(context);
			db.open();
			invoice = db.fetchInvoice(invoiceID);
			details = db.fetchAllInvoiceDetails(invoiceID);
			db.close();
			addInvoiceData(workbook);	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void addInvoiceData(WritableWorkbook workbook) throws DocumentException, RowsExceededException, WriteException, IOException
	{
		Integer i=0,j=0;
        WritableSheet sheet = workbook.createSheet("pedido", 0);
		db.open();
		customer = db.fetchCustomer(invoice.getCustomerID());
		db.close();
        jxl.write.Label customername = new jxl.write.Label(i, j, customer.getNombre());
        sheet.addCell(customername);
        i++;
        jxl.write.Label date = new jxl.write.Label(i, j, invoice.getDate());
        sheet.addCell(date);
        i++;
        jxl.write.Label comments = new jxl.write.Label(i, j, invoice.getComments());
        sheet.addCell(comments);
        i++;
        jxl.write.Number subtotal = new jxl.write.Number(i, j, invoice.getSubtotal());
        sheet.addCell(subtotal);
        i++;
        jxl.write.Number total = new jxl.write.Number(i, j, invoice.getTotal());
        sheet.addCell(total);
        i++;
        jxl.write.Number discount = new jxl.write.Number(i,j, invoice.getDiscount());
        sheet.addCell(discount);
        i++;
        jxl.write.Label empleado = new jxl.write.Label(i,j, invoice.getEmpleado());
        sheet.addCell(empleado);
        
	    for(int a=0; a<details.size(); a++)
	    {
	    	j++;
	    	//get the data
	    	i=0;
			db.open();
			product = db.fetchProduct(details.get(a).getProductID());
			db.close();
	    	jxl.write.Label productID = new jxl.write.Label(i,j, product.getNombrePV());
	        sheet.addCell(productID);
	        i++;
	    	jxl.write.Number qty = new jxl.write.Number(i,j, details.get(a).getQuantity());
	        sheet.addCell(qty);
	        i++;
	    	jxl.write.Number bon = new jxl.write.Number(i,j, details.get(a).getBonif());
	        sheet.addCell(bon);
	        i++;
	    	jxl.write.Number total2 = new jxl.write.Number(i,j, details.get(a).getTotal());
	        sheet.addCell(total2);
	    }
	    
        workbook.write();
        workbook.close();
	}
	
}
