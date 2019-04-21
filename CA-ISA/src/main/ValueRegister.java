package main;

public class ValueRegister extends Register {
	
	private int value=0;
	
	public ValueRegister(String name) {
		super(name);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
