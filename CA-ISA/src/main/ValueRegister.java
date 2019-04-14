package main;

public class ValueRegister extends Register {
	private long value ;
	
	public ValueRegister(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
