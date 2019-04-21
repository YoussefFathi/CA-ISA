package main;

public class ALU {
	
	public ALU() {
		
	}
	
	public int getResultOfRType(int source1,int source2,int shamt,int funct) { // S--> source1  T--> source2  D--->result  Shift-->shamt 
		
		int result=0;
		int temp;
		String temp2;
		String resb = "";
		String s1b = Integer.toBinaryString(source1);
		String s2b = Integer.toBinaryString(source2);
		
		switch(funct) {
		case 0: result = source1+source2; break; //ADD
		case 1: result = source1-source2; break; //SUB
		case 2: { //AND
			for(int i=0; i<s1b.length(); i++) {
				resb = resb +""+ ((Integer.parseInt(""+s1b.charAt(i)))&((Integer.parseInt(""+s2b.charAt(i)))));
			}
			result = Integer.parseInt(resb,2);
			break; 
		}
		case 3: { //OR
			for(int i=0; i<s1b.length(); i++) {
				resb = resb +""+ ((Integer.parseInt(""+s1b.charAt(i)))|((Integer.parseInt(""+s2b.charAt(i)))));
			}
			result = Integer.parseInt(resb,2);
			break; 
		}
		case 4: { //XOR
			for(int i=0; i<s1b.length(); i++) {
				temp = ((Integer.parseInt(""+s1b.charAt(i)))^((Integer.parseInt(""+s2b.charAt(i)))));
				resb = resb +""+ (temp==0? 1:0);
			}
			result = Integer.parseInt(resb,2);
			break; 
		}
		case 5: { //NOR
			for(int i=0; i<s1b.length(); i++) {
				temp = ((Integer.parseInt(""+s1b.charAt(i)))|((Integer.parseInt(""+s2b.charAt(i)))));
				resb = resb +""+ (temp==0? 1:0);
			}
			result = Integer.parseInt(resb,2);
			break; 
		}
		case 6: { //ROTR
			for(int i=0; i<shamt; i++) {
				temp2 = s1b.substring(s1b.length()-1); //last bit
				resb = s1b.substring(0, s1b.length()-1); //all but last bit
				resb = temp2 + resb;
			}
			result =  Integer.parseInt(resb, 2);
			break; 
		}
		case 7:  { //SLL
			for(int i=0; i<shamt; i++) {
				resb = s1b.substring(1); //all but first bit
				resb = resb+"0";
			}
			result =  Integer.parseInt(resb, 2);
			break; 
		}
		case 8: { //SRA
			for(int i=0; i<shamt; i++) {
				temp2 = s1b.substring(0,1); //first bit
				resb = s1b.substring(0, s1b.length()-1); //all but first bit
				resb = temp2 + resb;
			}
			result =  Integer.parseInt(resb, 2);
			break; 
		}
		case 9: { //SRL
			for(int i=0; i<shamt; i++) {
				resb = s1b.substring(0, s1b.length()-1); //all but first bit
				resb = "0" + resb;
			}
			result =  Integer.parseInt(resb, 2);
			break; 
		}
		case 10: result = source1+(source2*4); break; //ADDB
		
		}
		return result;
	}
	
	public int getResultOfIType(int valueReg, int constant, int opcode) {
		
		int result=0;
		int temp;
		String resb = "";
		String vb = Integer.toBinaryString(valueReg);
		String cb = Integer.toBinaryString(constant);
		
		switch(opcode) {
		case 1: result = valueReg+constant; break; //ADDI
		case 2: result = valueReg-constant; break; //SUBI
		case 3: { //ANDI
			for(int i=0; i<vb.length(); i++) {
				resb = resb +""+ ((Integer.parseInt(""+vb.charAt(i)))&((Integer.parseInt(""+cb.charAt(i)))));
			}
			result = Integer.parseInt(resb,2);
			break;
		}
		case 4: { //ORI
			for(int i=0; i<vb.length(); i++) {
				resb = resb +""+ ((Integer.parseInt(""+vb.charAt(i)))|((Integer.parseInt(""+cb.charAt(i)))));
			}
			result = Integer.parseInt(resb,2);
			break;
		}
		case 5: { //XORI
			for(int i=0; i<vb.length(); i++) {
				temp = ((Integer.parseInt(""+vb.charAt(i)))^((Integer.parseInt(""+cb.charAt(i)))));
				resb = resb +""+ (temp==0? 1:0);
			}
			result = Integer.parseInt(resb,2);
			break;
		}
		case 6: { //NORI
			for(int i=0; i<vb.length(); i++) {
				temp = ((Integer.parseInt(""+vb.charAt(i)))|((Integer.parseInt(""+cb.charAt(i)))));
				resb = resb +""+ (temp==0? 1:0);
			}
			result = Integer.parseInt(resb,2);
			break;
		}
		case 8: result=constant; break; //MOVI
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
	public int performAddition(int source1,int constant) {
		return source1+constant;
	}
	
	public static void main(String[] args) {
		
	}
}
