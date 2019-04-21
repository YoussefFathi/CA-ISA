package main;

public class ALU {
	
	public ALU() {
		
	}
	
	public int getResultOfRType(int source1,int source2,int shamt,int funct, int opcode) { // S--> source1  T--> source2  D--->result  Shift-->shamt 
		int result=-1;

		if(opcode==7){ //R TYPE - MOV
			result = source1;
		}else{
			if(opcode==0){ //R TYPE - ARITHMETIC & LOGICAL
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
					resb=s1b;
					for(int i=0; i<shamt; i++) {
						resb = resb.substring(1); //all but first bit
						resb = resb+"0";
					}
					result =  Integer.parseInt(resb, 2);
					break; 
				}
				case 8: { //SRA
					resb =s1b;
					for(int i=0; i<shamt; i++) {
						temp2 = resb.substring(0,1); //first bit
						resb = resb.substring(0, s1b.length()-1); //all but first bit
						resb = temp2 + resb;
					}
					result =  Integer.parseInt(resb, 2);
					break; 
				}
				case 9: { //SRL
					resb =s1b;
					for(int i=0; i<shamt; i++) {
						resb = resb.substring(0, s1b.length()-1); //all but first bit
						resb = "0" + resb;
					}
					result =  Integer.parseInt(resb, 2);
					break; 
				}
				case 10: result = source1+(source2*4); break; //ADDB
				
				}
			}
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

		ALU alu = new ALU();

		//Testing ALU Ops 12=1100 9=1001
		int add = alu.getResultOfRType(9,12,0,0,0); //21 PASSED
		int sub = alu.getResultOfRType(12,9,0,1,0); //3 PASSED
		int and = alu.getResultOfRType(9,12,0,2,0); //1000=8 PASSED
		int or = alu.getResultOfRType(9,12,0,3,0); //1101=13 PASSED
		int xor = alu.getResultOfRType(9,12,0,4,0); //1010=10 PASSED  
		int nor = alu.getResultOfRType(9,12,0,5,0); //0010=2 PASSED 
		int rotr = alu.getResultOfRType(9,0,1,6,0); //1100=12 PASSED
		int sll = alu.getResultOfRType(9,0,4,7,0); //0000=0 PASSED
		int sra = alu.getResultOfRType(9,0,3,8,0); //1111=15 PASSED
		int srl = alu.getResultOfRType(9,0,3,9,0); //0001=1 PASSED
		int addb = alu.getResultOfRType(12,9,0,10,0); //48 PASSED
		int mov = alu.getResultOfRType(12, 0, 0, 0, 7); //12 PASSED

		int addi = alu.getResultOfIType(9, 12, 1); //21 PASSED
		int subi = alu.getResultOfIType(12, 9, 2); //3 PASSED
		int andi = alu.getResultOfIType(9, 12, 3); //1000=8 PASSED
		int ori = alu.getResultOfIType(9, 12, 4); //1101=13 PASSED
		int xori = alu.getResultOfIType(9, 12, 5); //1010=10 PASSED
		int nori = alu.getResultOfIType(9, 12, 6); //0010=2 PASSED
		int movi = alu.getResultOfIType(9, 12, 8); //12 PASSED

		System.out.println(mov);

	}
}
