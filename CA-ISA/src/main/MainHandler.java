package main;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MainHandler {
	private ArrayList<Word> instructions = new ArrayList<Word>();
	private InstructionMemory instMem = new InstructionMemory();
	private DataMemory dataMem = new DataMemory();
	private ControlUnit cu = new ControlUnit();
	private ALU alu = new ALU();
	private RegisterFile regs = new RegisterFile();
	private Semaphore fetching = new Semaphore(1);
	private Semaphore decoding = new Semaphore(0);
	private Semaphore executing = new Semaphore(0);
	private Semaphore memoryAccessing = new Semaphore(0);
	private Semaphore writingBack = new Semaphore(0);
	private Word fetched;
	private boolean[] signals;
	private String fetchedFromDecode;
	static int count = 0;

	public MainHandler() {
		this.getUserInstructions();
		instMem.loadProgramFileToMemory(instructions);
		this.fetchThread.start();
		// fetching.release();
		this.decodeThread.start();
		this.executeThread.start();
		// this.memoryAccessThread.start();
		// this.writeBackThread.start();
	}

	public int getInt(String str) {
		return Integer.parseInt(str);
	}

	public Thread fetchThread = new Thread() {
		public void run() {
			while (true) {
				try {
					fetching.acquire();
					fetched = instMem.fetchNextInstruction();
					if (fetched == null) {
						break;
					}
					System.out.println("INSTRUCTION " + fetched.getWord() + " IS FETCHED " + ++count);
					decoding.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	public Thread decodeThread = new Thread() {
		public void run() {
			while (true) {
				try {
					decoding.acquire();
					if (fetched == null) {
						break;
					}
					String opCode = fetched.getWord().substring(0, 6);
					signals = cu.evaluateOpCode(opCode);
					fetchedFromDecode = fetched.getWord();
					fetching.release();
					System.out.println("Instruction : " + fetchedFromDecode + " Is Decoding" + ++count);
					executing.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};
	public Thread executeThread = new Thread() {
		public void run() {
			while (true) {
				try {
					executing.acquire();
					if (fetchedFromDecode == null) {
						break;
					}
					boolean[] signalsEx = signals;
					String instruction = fetchedFromDecode;
					decoding.release();
					long result = 0;
					boolean branched = false;
					int opcode = Integer.parseInt(getSegment(31, 26, instruction));
					if (signalsEx[12]) { // All Instructions Involving ALU
						if (!signalsEx[9]) { // Getting result of R Arithmetic operations
							int funct = Integer.parseInt(getSegment(5, 0, instruction));
							int source1 = Integer.parseInt(getSegment(25, 21, instruction));
							int source2 = Integer.parseInt(getSegment(20, 16, instruction));
							int shamt = Integer.parseInt(getSegment(10, 6, instruction));
							result = alu.getResultOfRType(regs.getValueReg(source1), regs.getValueReg(source2), shamt,
									funct);
						} else { // Getting result of Immediate Operations
							int source1 = Integer.parseInt(getSegment(25, 21, instruction));
							int constant = Integer.parseInt(getSegment(15, 0, instruction));
							result = alu.getResultOfIType(regs.getValueReg(source1), constant, opcode);
						}
						if (signalsEx[13]) { // Setting ASPR for CMP Instruction
							int source1 = Integer.parseInt(getSegment(25, 21, instruction));
							int source2 = Integer.parseInt(getSegment(20, 16, instruction));
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
							int newLocation = Integer.parseInt(getSegment(25, 0, instruction));
							instMem.adjustPCForBranching(newLocation); // After that we need to pass "branched" variable
																		// to all next stages to indicate that nothing
																		// will be done in them for this instruction anymore
						}
					}
					System.out.println(result + " Result of execution");
					System.out.println("Instruction : " + instruction + " Is Executing" + ++count);
					// memoryAccessing.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public Thread memoryAccessThread = new Thread() {
		public void run() {
			// while (true) {
			// try {
			// memoryAccessing.acquire();
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// executing.release();
			// }

		}
	};
	public Thread writeBackThread = new Thread() {
		public void run() {

		}
	};

	public String getSegment(int start, int end, String val) {
		return val.substring(31 - start, 32 - end);
	}

	public void getUserInstructions() {
		instructions.add(new Word("000000000110000100100000000000000000"));
		// instructions.add(new Word("000000000000000000000000000000000000"));
		// instructions.add(new Word("000000000000000000000000000000000000"));

	}

	public static void main(String[] args) {
		MainHandler main = new MainHandler();
	}
}
