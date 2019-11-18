package com.chancho.catan.map;

public class Resource {
	public enum ResType{
		GRAIN,LUMBER,ORE,BRICK,WOOL,NULL
	}
	public ResType type;
	public Resource(ResType type) {
		this.type=type;
	}
}
