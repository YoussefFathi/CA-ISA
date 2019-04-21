package main;

public class RegisterFile {
	Register[] registers = new Register[32];
	public RegisterFile() {
		registers[0] = new StatusRegister("ASPR");
		registers[1] = new ValueRegister("%v0");
		registers[2] = new ValueRegister("%v1");
		registers[3] = new ValueRegister("%v2");
		registers[4] = new ValueRegister("%a0");
		registers[5] = new ValueRegister("%a1");
		registers[6] = new ValueRegister("%a2");
		registers[7] = new ValueRegister("%a3");
		registers[8] = new ValueRegister("%a4");
		registers[9] = new ValueRegister("%ar");
		registers[10] = new ValueRegister("%t0");
		registers[11] = new ValueRegister("%t1");
		registers[12] = new ValueRegister("%t2");
		registers[13] = new ValueRegister("%t3");
		registers[14] = new ValueRegister("%t4");
		registers[15] = new ValueRegister("%t5");
		registers[16] = new ValueRegister("%t6");
		registers[17] = new ValueRegister("%t7");
		registers[18] = new ValueRegister("%p0");
		registers[19] = new ValueRegister("%p1");
		registers[20] = new ValueRegister("%p2");
		registers[21] = new ValueRegister("%p3");
		registers[22] = new ValueRegister("%p4");
		registers[23] = new ValueRegister("%p5");
		registers[24] = new ValueRegister("%p6");
		registers[25] = new ValueRegister("%p7");
		registers[26] = new ValueRegister("%k0");
		registers[27] = new ValueRegister("%k1");
		registers[28] = new ValueRegister("%sp");
		registers[29] = new ValueRegister("%fp");
		registers[30] = new ValueRegister("%gp");
		registers[31] = new ValueRegister("%ra");
	}

public void setValueReg(int i,int value) {
	((ValueRegister)registers[i]).setValue(value);
}
public int getValueReg(int i) {
	return ((ValueRegister)registers[i]).getValue();
}

public boolean getC() {
	return ((StatusRegister)registers[0]).getC();
}
public boolean getN() {
	return ((StatusRegister)registers[0]).getN();
}
public void setN(boolean n) {
	 ((StatusRegister)registers[0]).setN(n);
}
public boolean getV() {
	return ((StatusRegister)registers[0]).getV();
}
public void setV(boolean v) {
	 ((StatusRegister)registers[0]).setV(v);
}
public boolean getZ() {
	return ((StatusRegister)registers[0]).getZ();
}
public void setZ(boolean z) {
	 ((StatusRegister)registers[0]).setZ(z);
}
public void setC(boolean c) {
	 ((StatusRegister)registers[0]).setC(c);
}
public void resetASPR() {
	setZ(false);
	setC(false);
	setN(false);
	setV(false);
}


}
