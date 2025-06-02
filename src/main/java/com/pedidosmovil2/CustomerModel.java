package com.pedidosmovil2;

public class CustomerModel extends DatabaseModel
{
	private String nombre;
	private String apenom;
	private String razsoc;
	private String dir;
	private String tel;
	private String email;
	private String obs;
	private float dsc;
	
	public CustomerModel() {
		//Empty constructor
		super();
	}

	@Override
	public String toString() {
		return this.nombre;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApenom() {
		return apenom;
	}

	public void setApenom(String apenom) {
		this.apenom = apenom;
	}

	public String getRazsoc() {
		return razsoc;
	}

	public void setRazsoc(String razsoc) {
		this.razsoc = razsoc;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public float getDsc() {
		return dsc;
	}

	public void setDsc(float dsc) {
		this.dsc = dsc;
	}	


}
