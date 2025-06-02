package com.pedidosmovil2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InvoiceAdd extends Activity 
{
	private Calculator calculator;
	private int customerID;
	private int productID;
	private int invoiceID;
	private int customerProductID;
	private Intent intent;
	private DBAdapter db;
	private ProductModel products;
	private CustomerProductModel customerProduct;
	private Integer quantities;
	private Float bonif;
	private Float totals;
	private InvoiceModel invoice;
	private InvoiceDetailModel invoiceDetail;
	private String observations;
	private Float discount;
	
	public static void main(String[] args) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invoice_product);
		
		//Get products from selected company
		intent = getIntent();
		customerID = intent.getIntExtra("customerID", 0);
		productID = intent.getIntExtra("productID", 0);
		invoiceID = intent.getIntExtra("invoiceID", 0);
		db = new DBAdapter(this);
		db.open();
		products = db.fetchProduct(productID);
		customerProduct = db.fetchCustomerProduct(customerID, productID);
		quantities = 0;
		if (customerProduct!=null)
		{
			bonif = customerProduct.getBonificacion();
			customerProductID = customerProduct.getID();
		} else
		{
			customerProductID = -1;
			bonif = 0.0f;
		}
		totals = 0.0f;
		TextView productName = (TextView)this.findViewById(R.id.TextProductName);	
		final TextView productPrice = (TextView)this.findViewById(R.id.TextPrice);
		final EditText productQty = (EditText)findViewById(R.id.editQty);
		final EditText productBon = (EditText)findViewById(R.id.editBon);
		final TextView productTotal = (TextView)this.findViewById(R.id.TextTotal);
//		final Button bCalc = (Button)this.findViewById(R.id.ButtonCalc);
		final float price = products.getCosto()*(1+(products.getImp()/100));
		String formattedString = String.format("%.02f", price);
		//set their values		
		productName.setText(products.getNombrePV());
		productPrice.setText(String.valueOf(formattedString));
		productTotal.setText(String.format("%.2f", totals));
		int bon = Math.round(bonif);
		productBon.setText(String.valueOf(bon));
		productQty.addTextChangedListener(new TextWatcher() {
		
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	            // TODO Auto-generated method stub
	        	String ss = productQty.getText().toString();
	    		if (ss.equals("")) {
	    			productTotal.setText("0");
	    			quantities = 0;
	    			totals = 0.0f;
				}else
				{
		        	int pp = Integer.valueOf(ss);
		        	float bon = Integer.valueOf(productBon.getText().toString());
		        	float rest = bon/100;
		        	rest = price*rest;
		        	productPrice.setText(String.format("%.02f", price-rest));
					productTotal.setText(String.format("%.2f", pp*(price-rest)));
					totals =  pp*(price-rest);
					quantities = pp;
				}
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }
	    });

		productBon.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	            // TODO Auto-generated method stub
	        	String ss = productBon.getText().toString();
	    		if (ss.equals("")) {
	    			productPrice.setText(String.format("%.02f", price));
				}else
				{
		        	float pp = Integer.valueOf(ss);
		        	float rest = pp/100;
		        	rest = price*rest;
		        	productPrice.setText(String.format("%.02f", price-rest));
		        	String qty = productQty.getText().toString();
		        	int pq = Integer.valueOf(qty);
		        	productTotal.setText(String.format("%.2f", pq*(price-rest)));
		        	totals =  pq*(price-rest);
		        	bonif = pp;
				}
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	            // TODO Auto-generated method stub
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }
	    });
		
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//		bCalc.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(callback != null) {
//					callback.OpenDialog(1);
//				}
//			}
//		});
		
		discount = 0.0f;
		observations = "";
		

        Button bSave = (Button)findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveInvoice();
			}
		});
	}
	
//	@Override
//	public void OpenDialog(int position) 
//	{
//		calculator = new Calculator(InvoiceAdd.this);
//		calculator.setPosition(position);
//		calculator.setParentView((ListView)findViewById(R.id.listView1));
////		calculator.setQuantities(quantities);
////		calculator.setTotals(totals);
//		calculator.show();
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent myIntent;
		
		switch(item.getItemId()) {
		case R.id.action_options:
			myIntent = new Intent(getApplicationContext(), Options.class);
			myIntent.putExtra("discount", discount);
			myIntent.putExtra("observation", observations);
			startActivityForResult(myIntent, R.integer.REQUEST_OPTIONS);
			break;
		default:
			break;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case R.integer.REQUEST_OPTIONS:
			if(resultCode == RESULT_OK) {
				discount = data.getFloatExtra("discount", 0.0f);
				observations = data.getStringExtra("observations");
			}
			break;
		case R.integer.REQUEST_SIGNATURE:
			if(resultCode == RESULT_OK) {
				//TODO
			}
			break;
		}
	}
	
	public void saveInvoice()
	{
		if (customerProductID!=-1)
		{
			customerProduct.setBonificacion(bonif);
			db.updateRecord(customerProduct);
		} else
		{
			customerProduct = new CustomerProductModel();
			customerProduct.setBonificacion(bonif);
			customerProduct.setCustomer_id(customerID);
			customerProduct.setProduct_id(productID);
			db.insertRecord(customerProduct);
		}
		if (invoiceID==0) {
			int folio = 1;
			float subtotal = 0.0f;
			float total = 0.0f;
			invoice = new InvoiceModel();
			invoiceDetail = new InvoiceDetailModel();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());
			
			ArrayList<InvoiceModel> invoices = db.fetchAllInvoices();
			if(invoices.size()>0)
				folio = invoices.get(invoices.size()-1).getFolio()+1;
			
			//Create new invoice here
			invoice.setComments("");
			invoice.setCustomerID(customerID);
			invoice.setDate(today);
			invoice.setDiscount(discount);
			invoice.setComments(observations);
			invoice.setFolio(folio);
			invoice.setEmpleado("Facu");
			invoice.setSubtotal(0.0f);
			invoice.setTotal(0.0f);
			int invoice_id = db.insertRecord(invoice);
			invoice.setID(invoice_id);

			subtotal+=totals;
			//Insert detail here
			invoiceDetail.setInvoiceID(invoice.getID());
			invoiceDetail.setProductID(products.getID());
			invoiceDetail.setQuantity(quantities);
			invoiceDetail.setBonif(bonif);
			invoiceDetail.setTotal(totals);
			db.insertRecord(invoiceDetail);	
			
			//descuenta stock
//			products.setStock(products.getStock()-quantities);
//			db.updateRecord(products);
			
			//set invoice totals
			total = subtotal - (subtotal * (discount/100.0f));
			invoice.setSubtotal(subtotal);
			invoice.setTotal(total);
			db.updateRecord(invoice);
		} else {
			invoice = db.fetchInvoice(invoiceID);
			float subtotal = invoice.getSubtotal();
			float total = invoice.getTotal();
			invoiceDetail = new InvoiceDetailModel();
			subtotal+=totals;
			//Insert detail here
			invoiceDetail.setInvoiceID(invoice.getID());
			invoiceDetail.setProductID(products.getID());
			invoiceDetail.setQuantity(quantities);
			invoiceDetail.setBonif(bonif);
			invoiceDetail.setTotal(totals);
			db.insertRecord(invoiceDetail);	
			
//			//descuenta stock
//			products.setStock(products.getStock()-quantities);
//			db.updateRecord(products);
			
			//set invoice totals
			total = subtotal - (subtotal * (discount/100.0f));
			invoice.setSubtotal(subtotal);
			invoice.setTotal(total);
			db.updateRecord(invoice);
		}
			
		Toast.makeText(getApplicationContext(), R.string.item_saved, Toast.LENGTH_SHORT).show();
//		finish();
		Intent myIntent = new Intent(getApplicationContext(), ItemDetail.class);
		myIntent.putExtra("DataType", R.integer.invoice_type);
		myIntent.putExtra("id", invoice.getID());
		startActivity(myIntent);
	}	
}
