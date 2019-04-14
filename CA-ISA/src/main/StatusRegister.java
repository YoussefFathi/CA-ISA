package main;

public class StatusRegister extends Register {
	public StatusRegister(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	private boolean N=false;
	private boolean V=false;
	private boolean C=false;
	private boolean Z=false;
	public boolean getC() {
		return C;
	}
	public boolean getN() {
		return N;
	}
	public void setN(boolean n) {
		N = n;
	}
	public boolean getV() {
		return V;
	}
	public void setV(boolean v) {
		V = v;
	}
	public boolean getZ() {
		return Z;
	}
	public void setZ(boolean z) {
		Z = z;
	}
	public void setC(boolean c) {
		C = c;
	}

	
}
