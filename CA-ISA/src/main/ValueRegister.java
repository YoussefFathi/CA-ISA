package main;

public class ValueRegister extends Register {
	
	private long value=0;
	
	public ValueRegister(String name) {
		super(name);
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
