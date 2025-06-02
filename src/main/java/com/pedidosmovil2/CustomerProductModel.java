package com.pedidosmovil2;

public class CustomerProductModel extends DatabaseModel
{
	private int customer_id;
	private int product_id;
	private float bonificacion;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public int getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public float getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(float f) {
		this.bonificacion = f;
	}
	

}