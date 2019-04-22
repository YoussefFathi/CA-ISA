package main;

import java.util.ArrayList;

public class MainHandler2 {
	private ArrayList<Word> instructions = new ArrayList<Word>();
	private InstructionMemory instMem = new InstructionMemory();
	private DataMemory dataMem = new DataMemory();
	private ControlUnit cu = new ControlUnit();
	private ALU alu = new ALU();
	private RegisterFile regs = new RegisterFile();
	private int count;
	private boolean endFetch = false;
	PipelineRegister FExDE;
	PipelineRegister DExEX;
	PipelineRegister EXxMA;
	PipelineRegister MAxWB;
	private int cycles;

	public MainHandler2() {
		this.getUserInstructions();
		count = instructions.size();
		cycles = 0;
		instMem.loadProgramFileToMemory(instructions);
		FExDE = new PipelineRegister("FExDE");
		DExEX = new PipelineRegister("DExEX");
		EXxMA = new PipelineRegister("EXxMA");
		MAxWB = new PipelineRegister("MAxWB");
		while (count > 0) {
			PipelineRegister FExDETemp = new PipelineRegister("FExDE");
			PipelineRegister DExEXTemp = new PipelineRegister("DExEX");
			PipelineRegister EXxMATemp = new PipelineRegister("EXxMA");
			PipelineRegister MAxWBTemp = new PipelineRegister("MAxWB");
			FExDETemp = fetch(FExDETemp);
			DExEXTemp = decode(DExEXTemp);
			EXxMATemp = execute(EXxMATemp);
			MAxWBTemp = memoryAccess(MAxWBTemp);
			MAxWBTemp=writeBack(MAxWBTemp);
			FExDE = FExDETemp;
			DExEX = DExEXTemp;
			EXxMA = EXxMATemp;
			MAxWB = MAxWBTemp;
			cycles++;
			System.out.println(cycles + "Cycles");
		}
		for (int i = 0; i < regs.registers.length; i++) {
			if (i > 0) {
				System.out.println(
						((Register) regs.registers[i]).name + " : " + ((ValueRegister) regs.registers[i]).getValue());
			} else {
				System.out.println("ASPR :" + ((StatusRegister) regs.registers[i]).getZ());
			}
		}
	}

	public PipelineRegister fetch(PipelineRegister FExDE) {
		if (!endFetch) {
			Word instruction = instMem.fetchNextInstruction();

			if (!(instruction == null))
				FExDE.setValue("Instruction", instruction);
			else {
				endFetch = true;
			}
		}
		return FExDE;
	}

	public PipelineRegister decode(PipelineRegister DExEXTemp) {
		if (!FExDE.isEmpty()) {
			Word instruction = (Word) FExDE.getValue("Instruction");
			String opCode = getSegment(31, 26, instruction.getWord());
			boolean[] signals = cu.evaluateOpCode(opCode);
			System.out.println("Instruction :" +instruction.getWord());
			System.out.println("Signals :" +signals[12] + signals[13]);
			DExEXTemp.setValue("Signals", signals);
			DExEXTemp.setValue("Instruction", instruction);
		}
		return DExEXTemp;

	}

	public PipelineRegister execute(PipelineRegister EXxMA) {
		if (!DExEX.isEmpty()) {
			boolean[] signalsEx = (boolean[]) DExEX.getValue("Signals");
			Word currentInst = (Word) DExEX.getValue("Instruction");
			String instruction = currentInst.getWord();
			int result = 0;
			System.out.println(instruction + "In exec");
			System.out.println(signalsEx[12] +""+ signalsEx[13] +"In exec");
			boolean branched = false;
			int opcode = Integer.parseInt(getSegment(31, 26, instruction), 2);
			if (signalsEx[12]) { // All Instructions Involving ALU
				if (!signalsEx[9]) { // Getting result of R Arithmetic operations
					int funct = Integer.parseInt(getSegment(5, 0, instruction), 2);
					int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
					int source2 = Integer.parseInt(getSegment(20, 16, instruction), 2);
					int shamt = Integer.parseInt(getSegment(10, 6, instruction), 2);
					result = alu.getResultOfRType(regs.getValueReg(source1), regs.getValueReg(source2), shamt, funct,
							opcode);
					System.out.println("Result " + result);
				} else { // Getting result of Immediate Operations
					int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
					int constant = Integer.parseInt(getSegment(15, 0, instruction), 2);
					result = alu.getResultOfIType(regs.getValueReg(source1), constant, opcode);
					System.out.println(result);
				}
				if(signalsEx[5]) { //Mov
					int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
					result = alu.getResultOfRType(regs.getValueReg(source1), 0, 0, 0,
							opcode);
				}
				if(signalsEx[6]) { //MovI
					int constant = Integer.parseInt(getSegment(15, 0, instruction), 2);
					result = alu.getResultOfIType(0, constant, opcode);
				}
				if (signalsEx[13]) { // Setting ASPR for CMP Instruction
					int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
					int source2 = Integer.parseInt(getSegment(20, 16, instruction), 2);
					alu.updateASPR(regs.getValueReg(source1), regs.getValueReg(source2), regs);
				}
			}
			if (signalsEx[2]) { // All Signals involving Conditional Branching
				branched = true;
				boolean N = regs.getN();
				boolean V = regs.getV();
				boolean Z = regs.getZ();
				boolean willBranch = false;
				switch (opcode) {
				case 10: { // BEQ
					if (Z) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				case 11: { // BNE
					if (!Z) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				case 12: { // BLT
					if (N != V) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				case 13: { // BLE
					if ((Z) || (N != V)) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				case 14: { // BGT
					if ((!Z) && (N == V)) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				case 15: {
					if (N == V) {
						willBranch = true;
					} else {
						willBranch = false;
					}
					break;
				}
				}
				if (willBranch) {
					String newLocation = (getSegment(25, 0, instruction));
					instMem.adjustPCForBranching(newLocation); // After that we need to pass "branched"
																// variable
																// to all next stages to indicate that
																// nothing
																// will be done in them for this instruction
																// anymore
					branched = true;
				}
			} else if (signalsEx[3]) { // UnConditional Branching
				String newLocation = (getSegment(25, 0, instruction));
				instMem.adjustPCForBranching(newLocation); // After that we need to pass "branched" variable
															// to all next stages to indicate that
				branched = true;

			} else if (signalsEx[4]) { // Jump Register
				int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
				int location = regs.getValueReg(source1);
				instMem.adjustPCForBranching(Integer.toBinaryString(location));
				branched = true;
			}
			if ((signalsEx[0] && signalsEx[1] && signalsEx[8]) || signalsEx[10]) { // LoadWord or StoreWord
				int source1 = Integer.parseInt(getSegment(25, 21, instruction), 2);
				int regContent = regs.getValueReg(source1);
				int constant = Integer.parseInt(getSegment(15, 0, instruction), 2);
				int addressByte = alu.performAddition(regContent, constant);
				result = addressByte / 4;
			}
			EXxMA.setValue("Instruction", currentInst);
			EXxMA.setValue("Signals", signalsEx);
			EXxMA.setValue("ALUResult", result);
			EXxMA.setValue("DidBranch", branched);
		}
		return EXxMA;
	}

	public PipelineRegister memoryAccess(PipelineRegister MAxWB) {
		if (!EXxMA.isEmpty()) {
			boolean branched = (boolean) EXxMA.getValue("DidBranch");
			if (!branched) {
				boolean[] signalsMA = (boolean[]) EXxMA.getValue("Signals");
				Word currentInst = (Word) EXxMA.getValue("Instruction");
				String instruction = currentInst.getWord();
				if ((signalsMA[0] && signalsMA[1] && signalsMA[8])) { // LoadWord
					int address = (int) EXxMA.getValue("ALUResult");
					Word dataFromMem = dataMem.getWordFromMemory(address);
					MAxWB.setValue("MemoryData", dataFromMem);
				} else if (signalsMA[10] && !signalsMA[8]) { // Store Word
					int address = (int) EXxMA.getValue("ALUResult");
					int source2 = Integer.parseInt(getSegment(20, 16, instruction), 2);
					int registerContent = regs.getValueReg(source2);
					dataMem.setWordAtMemory(address, new Word(Integer.toBinaryString(registerContent)));
				}
				MAxWB.setValue("Instruction", currentInst);
				MAxWB.setValue("Signals", signalsMA);
				MAxWB.setValue("ALUResult", EXxMA.getValue("ALUResult"));
				MAxWB.setValue("DidBranch", EXxMA.getValue("DidBranch"));
			}
		}
		return MAxWB;

	}

	public PipelineRegister writeBack(PipelineRegister MAxWBTemp) {
		if (!MAxWB.isEmpty()) {
			boolean branched = (boolean) MAxWB.getValue("DidBranch");

			boolean[] signals = (boolean[]) MAxWB.getValue("Signals");
			Word currentInst = (Word) MAxWB.getValue("Instruction");
			String instruction = currentInst.getWord();

			int result = (int) MAxWB.getValue("ALUResult");
			Word dataToReg = (Word) MAxWB.getValue("MemoryData");
			Word toBeWritten = null;
			if (branched && signals[11]) {
				regs.setValueReg(31, instMem.getCurrentPC());
			}
			if (!branched && signals[8]) {
				int destinationRegister = 1;
				if (!signals[1]) { // Result From ALU
					toBeWritten = new Word(Integer.toBinaryString(result));
				} else if (signals[1]) { // Result from Memory
					toBeWritten = dataToReg;
				}
				if (signals[7]) { // Write to Third Register
					destinationRegister = Integer.parseInt(getSegment(15, 11, instruction), 2);
				} else if (!signals[7]) { // Write To Second Register
					destinationRegister = Integer.parseInt(getSegment(20, 16, instruction), 2);
				}
				if (!(toBeWritten == null)&&result!=-1)
					regs.setValueReg(destinationRegister, Integer.parseInt(toBeWritten.getWord(), 2));
				
			}
			count--;
		}
		return MAxWBTemp;
	}

	public String getSegment(int start, int end, String val) {
		return val.substring(31 - start, 32 - end);
	}

	public void getUserInstructions() {
		instructions.add(new Word("000001010101001000100000000000000000"));
		instructions.add(new Word("000001010101001100100000000000000000"));
//		instructions.add(new Word("000001010101010100100000000000000000"));
//		instructions.add(new Word("010001010101010100000000000000000000"));
		instructions.add(new Word("000000010101001000100000000000000001"));
		instructions.add(new Word("000000010101001000100000000000000010"));
		instructions.add(new Word("000000010101001000100000000000000011"));
		instructions.add(new Word("000000010101001000100000000000000100"));
		instructions.add(new Word("000000010101001000100000000000000101"));
		instructions.add(new Word("000000010101001000100000000000000110"));
		instructions.add(new Word("000000010101001000100000000000000111"));
		instructions.add(new Word("000000010101001000100000000000001000"));
		instructions.add(new Word("000000010101001000100000000000001001"));
		instructions.add(new Word("000000010101001000100000000000001010"));





//		instructions.add(new Word("001001100101001100100000000000000000"));

		// instructions.add(new Word("000000000000000000000000000000000000"));

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainHandler2 main = new MainHandler2();
	}

}
