package com.pedidosmovil2;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetail extends Activity
{
	private DBAdapter db;
	//clientes
	private TextView textNombre;
	private TextView textApenom;
	private TextView textDir;
	private TextView textTel;
	private TextView textEmail;
	private TextView textObs;
	
	//productos
	private TextView textNombrePV;
	private TextView textNombreP;
	private TextView textGramaje;
	private TextView textCosto; 
	private TextView textPFinal; 
	private TextView textImp;
	private TextView textStock;
	private TextView textStockmin;
	
	//pedido
	private TextView textDate;
	private TextView textObservations;
	private EditText textDiscount;
	private TextView textTotal;
	private TextView textSubTotal;
	private CustomerModel customer;
	private ProductModel product;
	private InvoiceModel invoice;
	private ArrayList<InvoiceDetailModel> invDetail;
	private InvoiceDetailModel invDetailDelete;
	private int dataType;
	private Spinner spinner;
	private ProductModel products;
	
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
		Intent myIntent = getIntent();
		//final int DataType = myIntent.getIntExtra("DataType", 0);
		dataType = myIntent.getIntExtra("DataType", 0);
		final int id = myIntent.getIntExtra("id", 0);
		db = new DBAdapter(this);
		db.open();
		switch(dataType)
		{		
			case R.integer.customer_type:
				//Get views
				setContentView(R.layout.customer_detail);
				textNombre = (TextView)findViewById(R.id.textNombre);
				textApenom = (TextView)findViewById(R.id.textApeNom);
				textDir = (TextView)findViewById(R.id.textDir);
				textTel = (TextView)findViewById(R.id.textTel);
				textEmail = (TextView)findViewById(R.id.textEmail);
				textObs = (TextView)findViewById(R.id.textObs);
				//Get data
				customer = db.fetchCustomer(id);
				//Set data
				textNombre.setText(customer.getNombre());
				textApenom.setText(customer.getApenom());
				textDir.setText(customer.getDir());
				textTel.setText(customer.getTel());
				textObs.setText(customer.getObs());	
				textEmail.setText(customer.getEmail());
				break;			
			case R.integer.product_type:
				//Get views
				setContentView(R.layout.product_detail);
				textNombrePV = (TextView)findViewById(R.id.textNomPV);
				textNombreP = (TextView)findViewById(R.id.textNombre);
				textGramaje = (TextView)findViewById(R.id.textGram);
				textCosto = (TextView)findViewById(R.id.textCost);
				textImp = (TextView)findViewById(R.id.textImp);	
				textPFinal = (TextView)findViewById(R.id.textPFinal);
				textStock = (TextView)findViewById(R.id.textStock);	
				textStockmin = (TextView)findViewById(R.id.textStockMin);	
				//Get data
				product = db.fetchProduct(id);
				//Set data
				textNombrePV.setText(product.getNombrePV());
				textNombreP.setText(product.getNombre());
				textGramaje.setText(product.getGramaje());
				textImp.setText("Aumento: " + Float.valueOf(product.getImp()).toString() + "%");
				textCosto.setText("Costo: $" + Float.valueOf(product.getCosto()).toString());
				textPFinal.setText("Precio Final: $" + String.format("%.2f", (Float.valueOf(product.getCosto())*((1+(Float.valueOf(product.getImp()))/100)))));
				textStock.setText("Stock: " + Float.valueOf(product.getStock()).toString());
				textStockmin.setText("Stock Min: " + Float.valueOf(product.getStockmin()).toString());
				break;
			case R.integer.invoice_type:
				//Get views
				setContentView(R.layout.invoice_detail);
				textNombre = (TextView)findViewById(R.id.textNombre);
				textApenom = (TextView)findViewById(R.id.textapenom);
				textDir = (TextView)findViewById(R.id.textDir);
				textTel = (TextView)findViewById(R.id.textTel);
				textEmail = (TextView)findViewById(R.id.textEmail);
				textDate = (TextView)findViewById(R.id.textDate);
				textObservations = (EditText)findViewById(R.id.Observaciones);
				textDiscount = (EditText)findViewById(R.id.editDisc);
				final Button dsc = (Button)findViewById(R.id.btnDsc);
				textTotal = (TextView)findViewById(R.id.textTotal);
				textSubTotal = (TextView)findViewById(R.id.textSubTotal);
				invoice = db.fetchInvoice(id);
				invDetail = db.fetchAllInvoiceDetails(id);
				customer = db.fetchCustomer(invoice.getCustomerID());
				spinner = (Spinner) findViewById(R.id.vendedor);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				        R.array.vendedores, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				spinner.setAdapter(adapter);
				spinner.setSelection(obtenerPosicionItem(spinner, invoice.getEmpleado()));
				//Set data
				textNombre.setText(customer.getNombre());
				textApenom.setText(customer.getApenom());
				textDir.setText(customer.getDir());
				textTel.setText(customer.getTel());
				textEmail.setText(customer.getEmail());
				textDate.setText("Fecha: " + invoice.getDate());
				textObservations.setText(invoice.getComments());
				if (invoice.getDiscount()>0) {
					textDiscount.setText(String.valueOf(Math.round(invoice.getDiscount())));
				}
				else
				{
					if (customer.getDsc()>0)
						textDiscount.setText(String.valueOf(Math.round(customer.getDsc())));
					else
						textDiscount.setText("0");
				}
				textTotal.setText("Total: " + String.format("%.2f", invoice.getTotal()));
				textSubTotal.setText("Sub Total: " + String.format("%.2f", invoice.getSubtotal()));
				
				dsc.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//Get data for edit here
						db.open();
						float tot = invoice.getSubtotal();
						float desc = ((Float.parseFloat(textDiscount.getText().toString())/100)-1)*-1;
						tot = tot*desc;
						invoice.setDiscount(Float.parseFloat(textDiscount.getText().toString()));
						invoice.setTotal(tot);
						db.updateRecord(invoice);
						customer.setDsc(Float.parseFloat(textDiscount.getText().toString()));
						db.updateRecord(customer);
						db.close();
						Intent myIntent = new Intent(getApplicationContext(), ItemDetail.class);
						myIntent.putExtra("DataType", R.integer.invoice_type);
						myIntent.putExtra("id", invoice.getID());
						startActivity(myIntent);			
					}
				});
				
				//Get table layout and add rows dynamically 
				TableLayout tableDetails = (TableLayout)findViewById(R.id.TableDetails);
				for(int i=0; i<invDetail.size(); i++)
				{
					ProductModel product = db.fetchProduct(invDetail.get(i).getProductID());
					TableRow row = new TableRow(this);		
					TableRow.LayoutParams lp = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(5, 0, 5, 0);
					//odd rows get light blue background
					if(i%2!=0) row.setBackgroundColor(Color.rgb(173, 216, 230)); 
					TextView prodName = new TextView(this);
					TextView prodPrice = new TextView(this);
					TextView prodQty = new TextView(this);
					TextView prodBon = new TextView(this);
					TextView prodTotal = new TextView(this);
					
					final Button elim = new Button(this);
					elim.setId(invDetail.get(i).getID());
					elim.setBackgroundResource(R.drawable.cancel);
					
					prodName.setText(product.getNombrePV());
					prodPrice.setText(String.format("%.2f",(product.getCosto()*(1+(product.getImp()/100)))));
					prodQty.setText(String.valueOf(invDetail.get(i).getQuantity()));
					prodBon.setText(String.valueOf(invDetail.get(i).getBonif()));
					prodTotal.setText(String.format("%.2f",invDetail.get(i).getTotal()));	
					prodName.setLayoutParams(lp);
					prodName.setGravity(Gravity.CENTER);
					prodPrice.setLayoutParams(lp);
					prodPrice.setGravity(Gravity.CENTER);
					prodQty.setLayoutParams(lp);
					prodQty.setGravity(Gravity.CENTER);
					prodBon.setLayoutParams(lp);
					prodBon.setGravity(Gravity.CENTER);
					prodTotal.setLayoutParams(lp);
					prodTotal.setGravity(Gravity.RIGHT);
					
					
					elim.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							//Get data for edit here
							db.open();
							invDetailDelete = db.fetchInvoiceDetail(elim.getId());
							products = db.fetchProduct(invDetailDelete.getProductID());
							
//							//descuenta stock
//							products.setStock(products.getStock()+invDetailDelete.getQuantity());
//							db.updateRecord(products);
							
							db.deleteRecord(invDetailDelete);
							float sub = invoice.getSubtotal();
							float tot = invoice.getTotal();
							sub = sub - invDetailDelete.getTotal();
							tot = tot - invDetailDelete.getTotal();
							invoice.setSubtotal(sub);
							invoice.setTotal(tot);
							db.updateRecord(invoice);
							db.close();
							Intent myIntent = new Intent(getApplicationContext(), ItemDetail.class);
							myIntent.putExtra("DataType", R.integer.invoice_type);
							myIntent.putExtra("id", invoice.getID());
							startActivity(myIntent);			
						}
					});
					
					row.addView(prodName,250,100);
					row.addView(prodPrice);
					row.addView(prodQty);
					row.addView(prodBon);
					row.addView(prodTotal);
					row.addView(elim,50,50);
					tableDetails.addView(row);
				}
				break;
			default:
				break;
		}		
		db.close();
		Button agregar = (Button)findViewById(R.id.agregar);
		Button guardar = (Button)findViewById(R.id.guardar);
		
		Button bEdit = (Button)findViewById(R.id.ButtonEdit);
		//Invoice does not have edit button...
		if(bEdit!=null)
		{
			bEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Get data for edit here
					Intent myIntent = new Intent(getApplicationContext(), ItemAdd.class);
					myIntent.putExtra("DataType", dataType);
					myIntent.putExtra("id", id);
					startActivity(myIntent);
				}
			});
		}	
		
		if(agregar!=null)
		{
			agregar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Get data for edit here
					Intent myIntent = new Intent(getApplicationContext(), Product.class);				
					myIntent.putExtra("requestCode", R.integer.REQUEST_PRODUCT);
					myIntent.putExtra("customerID", invoice.getCustomerID());
					myIntent.putExtra("invoiceID", id);
					startActivityForResult(myIntent,R.integer.REQUEST_PRODUCT);
					Toast.makeText(getApplicationContext(), R.string.select_product, Toast.LENGTH_SHORT).show();
				}
			});
		}	
		
		if(guardar!=null)
		{
			guardar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//Get data for edit here
					db.open();
					invoice.setComments(textObservations.getText().toString());
					invoice.setEmpleado(spinner.getSelectedItem().toString());
					db.updateRecord(invoice);
					db.close();
					ExportXLS xls = new ExportXLS(getApplicationContext());
					xls.Export(invoice.getID());
					Toast.makeText(getApplicationContext(), "Elemento Guardado", Toast.LENGTH_SHORT).show();
				}
			});
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		if(dataType == R.integer.invoice_type) {
			getMenuInflater().inflate(R.menu.print, menu);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent shareIntent;
		ExportPDF pdf;
		ExportXLS xls;
		
		switch(item.getItemId()) {
		case R.id.action_signature:
			shareIntent = new Intent(getApplicationContext(), Signature.class);
			startActivityForResult(shareIntent, R.integer.REQUEST_SIGNATURE);
			break;
			
		case R.id.action_print: 
			pdf = new ExportPDF(getApplicationContext());
			pdf.Export(invoice.getID());

			File file = new File(getExternalFilesDir(null)+"/pedido.pdf");
			shareIntent = new Intent(Intent.ACTION_VIEW);
			shareIntent.setDataAndType(Uri.fromFile(file), "application/pdf");						
			shareIntent.setPackage("com.dynamixsoftware.printershare");
			
			try {
			    startActivity(shareIntent);
			} catch (ActivityNotFoundException e) {				
				Toast.makeText(this, "No es posible imprimir, por favor instale PrinterShare(R)", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.action_export: 
			xls = new ExportXLS(getApplicationContext());
			xls.Export(invoice.getID());

			Uri uri2 = Uri.parse("file://" + getExternalFilesDir(null)+"/pedido.xls");			
			shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("message/rfc822");
			shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "otrofacu@hotmail.com" });
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, customer.getNombre()+ " - " +invoice.getDate() );
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Pedido adjunto");
			shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri2);
			try {
				startActivity(Intent.createChooser(shareIntent,"Enviar email"));
			} catch(ActivityNotFoundException e) {
				Toast.makeText(this, "No es posible enviar e-mail. No existe una aplicaci�n instalada", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.action_email: 
//			pdf = new ExportPDF(getApplicationContext());
//			pdf.Export(invoice.getID());
//			
//			Uri uri = Uri.parse("file://" + getExternalFilesDir(null)+"/pedido.pdf");			
//
//			shareIntent = new Intent(Intent.ACTION_SEND);
//			shareIntent.setType("message/rfc822");
//			shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { customer.getEmail() });
//			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Pedido adjunto");
//			shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
//			try {
//				startActivity(Intent.createChooser(shareIntent,"Enviar email"));
//			} catch(ActivityNotFoundException e) {
//				Toast.makeText(this, "No es posible enviar e-mail. No existe una aplicaci�n instalada", Toast.LENGTH_SHORT).show();
//			}
//			break;
			db.open();
			invoice.setComments(textObservations.getText().toString());
			invoice.setEmpleado(spinner.getSelectedItem().toString());
			db.updateRecord(invoice);
			db.close();
			xls = new ExportXLS(getApplicationContext());
			xls.Export(invoice.getID());
			Toast.makeText(this, "Elemento Guardado", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK) 
		{
			if(requestCode == R.integer.REQUEST_SIGNATURE)
			{
				Bundle bundle = data.getExtras();
				String ID = bundle.getString("ID");
				db.open();
				invoice.setSignatureID(ID);
				db.updateRecord(invoice);
				db.close();
				Toast.makeText(this, "Firma guardada con �xito!",Toast.LENGTH_SHORT).show();
			}
		}
		if(requestCode == R.integer.REQUEST_PRODUCT) 	
		{
			Bundle bundle = data.getExtras();
			Intent myIntent = new Intent(getApplicationContext(), InvoiceAdd.class);
			myIntent.putExtra("customerID", data.getIntExtra("customerID",0));
			myIntent.putExtra("productID", data.getIntExtra("itemID",0));
			myIntent.putExtra("invoiceID", invoice.getID());
			startActivity(myIntent);
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	// Esto es lo que hace mi bot�n al pulsar ir a atr�s
        	if (dataType == R.integer.invoice_type) {
        		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        		startActivity(myIntent);
			}
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
    //M�todo para obtener la posici�n de un �tem del spinner
    public static int obtenerPosicionItem(Spinner spinner, String fruta) {
        //Creamos la variable posicion y lo inicializamos en 0
        int posicion = 0;
        //Recorre el spinner en busca del �tem que coincida con el parametro `String fruta`
        //que lo pasaremos posteriormente
        for (int i = 0; i < spinner.getCount(); i++) {
            //Almacena la posici�n del �tem que coincida con la b�squeda
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(fruta)) {
                posicion = i;
            }
        }
        //Devuelve un valor entero (si encontro una coincidencia devuelve la
        // posici�n 0 o N, de lo contrario devuelve 0 = posici�n inicial)
        return posicion;
    }
	
}
