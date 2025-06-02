package com.pedidosmovil2;

import com.pedidosmovil2.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements OnTaskCompleted, OnProgressUpdate
{
	private ProgressDialog barProgressDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences settings = getSharedPreferences("PREFERENCES", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("filtros", "" );
        editor.commit();
        
    	Button b1 = (Button)findViewById(R.id.bAgregar);
    	Button b3 = (Button)findViewById(R.id.Button03);
    	Button b4 = (Button)findViewById(R.id.Button04);
    	Button b5 = (Button)findViewById(R.id.Button05);
    	Button i1 = (Button)findViewById(R.id.bImportC);
    	Button i2 = (Button)findViewById(R.id.bImportP);
    	Button i3 = (Button)findViewById(R.id.bImportPed);
    	b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Customer.class));
			}
		});
    	
    	b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Product.class));
			}
		});
    	
    	b4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Invoice.class));
			}
		});
    	
    	b5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ventaDiaria.class));
			}
		});
    	
    	i1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),FileExplorer.class);
				intent.putExtra("item", ImportExcel.TYPE_CUSTOMER);
				startActivityForResult(intent, R.integer.REQUEST_FILE);
			}
		});

    	i2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),FileExplorer.class);
				intent.putExtra("item", ImportExcel.TYPE_PRODUCT);
				startActivityForResult(intent, R.integer.REQUEST_FILE);
			}
		});
    	
    	i3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				barProgressDialog = new ProgressDialog(MainActivity.this);
				barProgressDialog.setTitle("Por favor espere...");
				barProgressDialog.setMessage("importando datos...");
				barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
				barProgressDialog.setProgress(0);
				barProgressDialog.show();
				ImportExcel2 imp;
				imp = new ImportExcel2(getApplicationContext());
				imp.setType(ImportExcel2.TYPE_INVOICE);
//				imp.setOnTaskCompletedListener(this);
//				imp.setOnProgressUpdateListener(this);
				imp.setProgressDialog(barProgressDialog);
				imp.execute(0);
			}
		});
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
        getMenuInflater().inflate(R.menu.imports, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		//If products selected, first we need to choose a company
		if(item.getItemId() == R.id.action_importProducts)
		{
			{
				Intent intent = new Intent(getApplicationContext(),FileExplorer.class);
				switch(item.getItemId()) {
				case R.id.action_importProducts:
					intent.putExtra("item", ImportExcel.TYPE_PRODUCT);
					break;
				default:
					break;
				}
				startActivityForResult(intent, R.integer.REQUEST_FILE);
			}
		}	
		if(item.getItemId() == R.id.action_importInvoices)
		{
			{
				barProgressDialog = new ProgressDialog(MainActivity.this);
				barProgressDialog.setTitle("Por favor espere...");
				barProgressDialog.setMessage("importando datos...");
				barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
				barProgressDialog.setProgress(0);
				barProgressDialog.show();
				ImportExcel2 imp;
				imp = new ImportExcel2(getApplicationContext());
				imp.setType(ImportExcel2.TYPE_INVOICE);
//				imp.setOnTaskCompletedListener(this);
//				imp.setOnProgressUpdateListener(this);
				imp.setProgressDialog(barProgressDialog);
				imp.execute(0);
			}
		}	
		if(item.getItemId() == R.id.action_importCustomers)
		{
			Intent intent = new Intent(getApplicationContext(),FileExplorer.class);
			switch(item.getItemId()) {
			case R.id.action_importCustomers:
				intent.putExtra("item", ImportExcel.TYPE_CUSTOMER);
				break;
			default:
				break;
			}
			startActivityForResult(intent, R.integer.REQUEST_FILE);
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK)
		{
			if(requestCode == R.integer.REQUEST_FILE) 
			{
				final String chosenFile = data.getStringExtra("chosenFile");
				final int item = data.getIntExtra("item", 0);
				if(chosenFile != null && chosenFile != "") 
				{
					barProgressDialog = new ProgressDialog(MainActivity.this);
					barProgressDialog.setTitle("Por favor espere...");
					barProgressDialog.setMessage("importando datos...");
					barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
					barProgressDialog.setProgress(0);
					barProgressDialog.show();
					
					ImportExcel importer = new ImportExcel(getApplicationContext());
					importer.setFilePath(chosenFile);
					importer.setType(item);	
					importer.setOnTaskCompletedListener(this);
					importer.setOnProgressUpdateListener(this);
					importer.setProgressDialog(barProgressDialog);
					importer.execute(0);
				}
			}			
		}
	}

	@Override
	public void onProgressUpdate(int value) 
	{
		barProgressDialog.setProgress(value);
	}

	@Override
	public void onTaskCompleted(int result) 
	{
		barProgressDialog.dismiss();
		
		if(result == 0)
			Toast.makeText(getApplicationContext(), "�Datos importados con �xito!", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(), "Ha ocurrido un error al importar datos", Toast.LENGTH_SHORT).show();
	}
}
