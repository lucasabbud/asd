package com.pedidosmovil2;

public class ProductModel extends DatabaseModel
{
	private String nombrePV;
	private String nombre;
	private String gramaje;
	private float costo; 
	private float imp;
	private float stock;
	private float stockmin;

	public ProductModel() {
		//Empty constructor
		super();
	}	
	
	@Override
	public String toString() {
		return this.nombre;
	}

	public String getNombrePV() {
		return nombrePV;
	}

	public void setNombrePV(String nombrePV) {
		this.nombrePV = nombrePV;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getGramaje() {
		return gramaje;
	}

	public void setGramaje(String gramaje) {
		this.gramaje = gramaje;
	}

	public float getCosto() {
		return costo;
	}

	public void setCosto(float costo) {
		this.costo = costo;
	}

	public float getImp() {
		return imp;
	}

	public void setImp(float imp) {
		this.imp = imp;
	}
	
	public float getStock() {
		return stock;
	}

	public void setStock(float stock) {
		this.stock = stock;
	}

	public float getStockmin() {
		return stockmin;
	}

	public void setStockmin(float stockmin) {
		this.stockmin = stockmin;
	}	
	
}

	