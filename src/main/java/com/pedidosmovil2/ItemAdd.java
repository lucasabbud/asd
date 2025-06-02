package com.pedidosmovil2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ItemAdd extends Activity 
{
	private EditText nombre;
	private EditText apenom;
	private EditText dir;
	private EditText tel;
	private EditText email;
	private EditText obs;
	private EditText nombrePV;
	private EditText nombreP;
	private EditText gramaje;
	private EditText costo; 
	private EditText imp;	
	private EditText stock;
	private EditText stockmin;
	private int DataType;
	private int itemID;
	private DBAdapter db;
	
	public static void main(String[] args) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		
		Intent myIntent = getIntent();
		DataType = myIntent.getIntExtra("DataType", 0);
		itemID = myIntent.getIntExtra("id", 0);
		
		switch(DataType)
		{
			case R.integer.customer_type:
				setContentView(R.layout.customer_add);
				nombre = (EditText)findViewById(R.id.editNombre);
				apenom = (EditText)findViewById(R.id.editApeNom);
				dir = (EditText)findViewById(R.id.editDir);
				tel = (EditText)findViewById(R.id.editTel);
				email = (EditText)findViewById(R.id.editEmail);
				obs = (EditText)findViewById(R.id.editObs);
				if(itemID > 0)
				{
					db = new DBAdapter(this);
					db.open();
					CustomerModel customer = db.fetchCustomer(itemID);
					nombre.setText(customer.getNombre());
					apenom.setText(customer.getApenom());
					dir.setText(customer.getDir());
					tel.setText(customer.getTel());
					email.setText(customer.getEmail());
					obs.setText(customer.getObs());
					db.close();
				}				
				break;
			case R.integer.product_type:
				setContentView(R.layout.product_add);
				nombrePV = (EditText)findViewById(R.id.editNomPV);
				nombreP = (EditText)findViewById(R.id.editNombre);
				gramaje = (EditText)findViewById(R.id.editGram);
				costo = (EditText)findViewById(R.id.editPrice);
				imp = (EditText)findViewById(R.id.editImp);	
				stock = (EditText)findViewById(R.id.editStock);	
				stockmin = (EditText)findViewById(R.id.editStockMin);	
	
				if(itemID > 0)
				{
					db = new DBAdapter(this);
					db.open();
					ProductModel product = db.fetchProduct(itemID);
					nombrePV.setText(product.getNombrePV());
					nombreP.setText(product.getNombre());
					gramaje.setText(product.getGramaje());
					costo.setText(Float.valueOf(product.getCosto()).toString());
					imp.setText(Float.valueOf(product.getImp()).toString());
					stock.setText(Float.valueOf(product.getStock()).toString());
					stockmin.setText(Float.valueOf(product.getStockmin()).toString());
					db.close();
				}
				break;
			case R.integer.invoice_type:
				setContentView(R.layout.invoice_add);
				break;
			default:
				break;
		}
		
		Button bSave = (Button)findViewById(R.id.ButtonSave);
		//Invoice does not have save button...
		if(bSave!=null)
		{
			bSave.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					saveData();
					Toast.makeText(getApplicationContext(), R.string.item_saved, Toast.LENGTH_SHORT).show();
					finish();
				}
			});
		}
	}
	
	private void saveData()
	{
		db = new DBAdapter(this);
		db.open();
		
		switch(DataType) {
		case R.integer.customer_type:
			//create new company model object
			CustomerModel cust = new CustomerModel();
			cust.setNombre(nombre.getText().toString());
			cust.setApenom(apenom.getText().toString());
			cust.setDir(dir.getText().toString());
			cust.setTel(tel.getText().toString());
			cust.setEmail(email.getText().toString());
			cust.setObs(obs.getText().toString());
			if(itemID > 0) {
				cust.setID(itemID);
				db.updateRecord(cust);
			}
			else
				db.insertRecord(cust);
			break;
		case R.integer.product_type:
			//create new product model object
			ProductModel prod = new ProductModel();
			prod.setNombrePV(nombrePV.getText().toString());
			prod.setNombre(nombreP.getText().toString());
			prod.setGramaje(gramaje.getText().toString());
			try {
				prod.setStock(Float.valueOf(stock.getText().toString()));
			}catch(Exception e){
				prod.setStock(0.0f);
			}
			try {
				prod.setStockmin(Float.valueOf(stockmin.getText().toString()));
			}catch(Exception e){
				prod.setStockmin(0.0f);
			}
			try {
				prod.setCosto(Float.valueOf(costo.getText().toString()));
			}catch(Exception e){
				prod.setCosto(0.0f);
			}
			try {
				prod.setImp(Float.valueOf(imp.getText().toString()));
			}catch(Exception e){
				prod.setImp(0.0f);
			}
			if(itemID > 0) {
				prod.setID(itemID);
				db.updateRecord(prod);
			}
			else
				db.insertRecord(prod);			
			break;
		}
		
		db.close();
	}
}
