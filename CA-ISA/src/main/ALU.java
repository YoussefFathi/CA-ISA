package main;

public class ALU {
	public ALU() {
		
	}
	public int getResultOfRType(int source1,int source2,int shamt,int funct) {
		int result=0;
		switch(funct) {
		case 0: result = source1+source2;break;
		//Continue cases of R type
		}
		return result;
	}
	public int getResultOfIType(int valueReg, int constant, int opcode) {
		int result =0;
		switch(opcode) {
		case 1: result = valueReg +constant;break;
		//Continue cases of I Type
		}

		return result;
	}
	public void updateASPR(int source1,int source2,RegisterFile regs) {
		int result = source1-source2;
		int max = Integer.MAX_VALUE;
		int min = Integer.MIN_VALUE;
		regs.resetASPR();
		if(result==0) {
			regs.setZ(true);
		}
		if(result<0) {
			regs.setN(true);
		}
		if(result>(max)||result<min) {
			regs.setV(true);
		}
		
	}
}
