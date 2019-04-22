package main;

import java.util.ArrayList;

public class PipelineRegister {
	ArrayList<PipeRegister> reg = new ArrayList<PipeRegister>();

	public PipelineRegister(String indicator) {
		switch (indicator) {
		case "FExDE": {
			reg.add(new PipeRegister("Instruction", null));
			break;
		}
		case "DExEX": {
			reg.add(new PipeRegister("Signals", null));
			reg.add(new PipeRegister("Instruction", null));
			break;
		}
		case "EXxMA": {
			reg.add(new PipeRegister("Signals", null));
			reg.add(new PipeRegister("Instruction", null));
			reg.add(new PipeRegister("ALUResult", null));
			reg.add(new PipeRegister("DidBranch", null));
		}
		case "MAxWB": {
			reg.add(new PipeRegister("Signals", null));
			reg.add(new PipeRegister("Instruction", null));
			reg.add(new PipeRegister("ALUResult", null));
			reg.add(new PipeRegister("DidBranch", null));
			reg.add(new PipeRegister("MemoryData", null));
		}
		}
	}

	public boolean isEmpty() {
		for (int i = 0; i < reg.size(); i++) {
			if (!(reg.get(i).getValue()==null)) {
				return false;
			}
		}
		return true;
	}

	public void setValue(String name, Object val) {
		for (int i = 0; i < reg.size(); i++) {
			if (reg.get(i).getName().equals(name)) {
				reg.get(i).setValue(val);
			}
		}
	}

	public Object getValue(String name) {
		for (int i = 0; i < reg.size(); i++) {
			if (reg.get(i).getName().equals(name)) {
				return reg.get(i).getValue();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void revalidate(PipelineRegister FExDETemp) {
		for(int i=0;i<reg.size();i++) {
			reg.set(i, FExDETemp.reg.get(i));
		}
		
	}

}
