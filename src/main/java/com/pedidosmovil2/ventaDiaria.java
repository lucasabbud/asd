package com.pedidosmovil2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ventaDiaria extends Activity 
{
	private DBAdapter db;
	private ArrayList<InvoiceModel> invoice;
	private ArrayList<ProductModel> product;
	private ArrayList<InvoiceDetailModel> invDetail;
	private ArrayList<InvoiceDetailModel> invDetail2;
	public static void main(String[] args) {		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onCreate(null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.venta_diaria);
		db = new DBAdapter(this);
		db.open();
		//Get data
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date());	
		product = db.fetchAllProducts();
		invoice = db.fetchAllInvoices2(today);
		ArrayList<InvoiceDetailModel> invDetail = new ArrayList<InvoiceDetailModel>();
		for (int i = 0; i < invoice.size(); i++) {
			invDetail2 = db.fetchAllInvoiceDetails(invoice.get(i).getID());
			invDetail.addAll(invDetail2) ;
		}
		TableLayout tableDetails = (TableLayout)findViewById(R.id.TableDetails);
		float importe = 0f;
		for(int i=0; i<product.size(); i++)
		{
			int cant = 0;
			ProductModel pro = db.fetchProduct(product.get(i).getID());
			int stock = (int) pro.getStock();
			for (int j = 0; j < invDetail.size(); j++) {
				if (pro.getID()==invDetail.get(j).getProductID()) {
					cant+=invDetail.get(j).getQuantity();
				}
			}
			float cosTot = pro.getCosto() * cant;
			TableRow row = new TableRow(this);		
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(5, 0, 5, 0);
			//odd rows get light blue background
//			if(i%2!=0) row.setBackgroundColor(Color.rgb(173, 216, 230)); 
			TextView prodName = new TextView(this);
			TextView prodCant = new TextView(this);
			TextView prodStock = new TextView(this);
			TextView prodCos = new TextView(this);
			TextView prodTot = new TextView(this);
			prodName.setText(pro.getNombrePV());
			prodName.setLayoutParams(lp);
			prodName.setWidth(250);
			prodName.setGravity(Gravity.CENTER);
			prodCant.setText(String.valueOf(cant));
			prodCant.setLayoutParams(lp);
			prodCant.setGravity(Gravity.CENTER);
			prodStock.setText(String.valueOf(stock));
			prodStock.setLayoutParams(lp);
			prodStock.setGravity(Gravity.CENTER);
			prodCos.setText(String.format("%.02f", pro.getCosto()));
			prodCos.setLayoutParams(lp);
			prodCos.setGravity(Gravity.CENTER);
			prodTot.setText(String.format("%.02f", cosTot));
			prodTot.setLayoutParams(lp);
			prodTot.setGravity(Gravity.CENTER);			
			row.addView(prodName);
			row.addView(prodCant);
			row.addView(prodStock);
			row.addView(prodCos);
			row.addView(prodTot);
			if (cant>0) {
				tableDetails.addView(row);
				importe += cosTot;
			}
		}
		TextView importe2 = (TextView)findViewById(R.id.Total);
		importe2.setText("Importe: "+String.format("%.02f", importe));
		db.close();
		}	
}
