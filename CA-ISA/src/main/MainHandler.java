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
	private Word fetchedFExDE;
	private Word fetchedDExEX;
	private Word fetchedEXxMA;
	private Word fetchedMAxWB;
	private boolean[] signals;
	private boolean[] signalsDExEX;
	private boolean[] signalsEXxMA;
	private boolean[] signalsMAxWB;
	private int resultEXxMA;
	private int resultMAxWB;
	private boolean branchedEXxMA;
	private boolean branchedMAxWB;
	private Word dataFromMemToRegMAxWB;
	private String fetchedFromDecode;
	static int count = 0;

	public MainHandler() {
		this.getUserInstructions();
		instMem.loadProgramFileToMemory(instructions);
		this.fetchThread.start();
		// fetching.release();
		this.decodeThread.start();
		this.executeThread.start();
		this.memoryAccessThread.start();
		this.writeBackThread.start();
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
					if (fetched != null)
						System.out.println("INSTRUCTION " + fetched.getWord() + " IS FETCHED " + ++count);
					fetchedFExDE = fetched;
					if (fetched == null) {
						break;
					}
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
					if (fetchedFExDE == null && fetched == null) {
						fetchedDExEX=null;
						break;
					}
					decoding.acquire();
					String opCode;
					if (fetchedFExDE != null) {
						opCode = fetchedFExDE.getWord().substring(0, 6);
						signalsDExEX = cu.evaluateOpCode(opCode);
					}
					fetchedDExEX = fetchedFExDE;
					if (fetchedFExDE == null) {
						break;
					}
					fetching.release();
					System.out.println("Instruction : " + fetchedDExEX + " Is Decoding" + ++count);
					executing.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	};

	public Thread executeThread = new Thread() {
		public void run() {
			while (true) {
				try {
					if (fetchedDExEX == null && fetchedFExDE == null && fetched == null) {
						break;
					}
					executing.acquire();

					boolean[] signalsEx = signalsDExEX;
					Word currentInst = fetchedDExEX;
					String instruction = fetchedDExEX.getWord();
					decoding.release();
					int result = 0;
					boolean branched = false;
					int opcode = Integer.parseInt(getSegment(31, 26, instruction));
					if (fetchedDExEX != null) {

						if (signalsEx[12]) { // All Instructions Involving ALU
							if (!signalsEx[9]) { // Getting result of R Arithmetic operations
								int funct = Integer.parseInt(getSegment(5, 0, instruction));
								int source1 = Integer.parseInt(getSegment(25, 21, instruction));
								int source2 = Integer.parseInt(getSegment(20, 16, instruction));
								int shamt = Integer.parseInt(getSegment(10, 6, instruction));
								result = alu.getResultOfRType(regs.getValueReg(source1), regs.getValueReg(source2), shamt, funct, opcode);
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
								String newLocation = (getSegment(25, 0, instruction));
								instMem.adjustPCForBranching(newLocation); // After that we need to pass "branched"
																			// variable
																			// to all next stages to indicate that
																			// nothing
																			// will be done in them for this instruction
																			// anymore
							}
						}
						if (signalsEx[3]) { // UnConditional Branching
							String newLocation = (getSegment(25, 0, instruction));
							instMem.adjustPCForBranching(newLocation); // After that we need to pass "branched" variable
							// Add branched boolean for next stages // to all next stages to indicate that
							// nothing
							// will be done in them for this instruction
							// anymore
						}
						if ((signalsEx[0] && signalsEx[1] && signalsEx[8]) || signalsEx[10]) { // LoadWord or StoreWord
							int source1 = Integer.parseInt(getSegment(25, 21, instruction));
							int constant = Integer.parseInt(getSegment(15, 0, instruction));
							int addressByte = alu.performAddition(source1, constant);
							result = addressByte / 4;
						}
					}
					resultEXxMA = result;
					fetchedEXxMA = currentInst;
					signalsEXxMA = signalsEx;
					if (fetchedDExEX == null) {
						break;
					}
					System.out.println(result + " Result of execution");
					System.out.println("Instruction : " + instruction + " Is Executing" + ++count);
					memoryAccessing.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public Thread memoryAccessThread = new Thread() {
		public void run() {
			while (true) {
				if (fetchedEXxMA == null && fetchedDExEX == null && fetchedFExDE == null && fetched == null) {
					break;
				}
				try {
					memoryAccessing.acquire();

					boolean[] signalsMA = signalsEXxMA;
					Word currentInst = fetchedEXxMA;
					int temp = resultEXxMA;
					executing.release();
					if ((signalsMA[0] && signalsMA[1] && signalsMA[8])) { // LoadWord
						int address = temp;
						dataFromMemToRegMAxWB = dataMem.getWordFromMemory(address);
					} else if (signalsMA[10] && !signalsMA[8]) { // Store Word
						int address = temp;
						String instruction = currentInst.getWord();
						int source2 = Integer.parseInt(getSegment(20, 16, instruction));
						int registerContent = regs.getValueReg(source2);
						dataMem.setWordAtMemory(address, new Word(Integer.toBinaryString(registerContent)));
					}

					fetchedMAxWB = currentInst;
					signalsMAxWB = signalsMA;
					resultMAxWB = temp;
					if (fetchedEXxMA == null) {
						break;
					}
					writingBack.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	};

	public Thread writeBackThread = new Thread() {
		public void run() {
			while (true) {
				try {
					if (fetchedMAxWB == null && fetchedEXxMA == null && fetchedDExEX == null && fetchedFExDE == null
							&& fetched == null) {
						break;
					}
					writingBack.acquire();
					if (fetchedMAxWB == null) {
						break;
					}
					boolean[] signals = signalsMAxWB;
					Word currentInst = fetchedMAxWB;
					String instruction = currentInst.getWord();
					int result = resultMAxWB;
					Word dataToReg = dataFromMemToRegMAxWB;
					memoryAccessing.release();
					Word toBeWritten = null;
					int destinationRegister = 1;
					if (signals[1]) { // Result From ALU
						toBeWritten = new Word(Integer.toBinaryString(result));
					} else if (!signals[1]) { // Result from Memory
						toBeWritten = dataToReg;
					}
					if (signals[7]) { // Write to Third Register
						destinationRegister = Integer.parseInt(getSegment(15, 11, instruction));
					} else if (!signals[7]) { // Write To Second Register
						destinationRegister = Integer.parseInt(getSegment(20, 16, instruction));
					}
					regs.setValueReg(destinationRegister, Integer.parseInt(toBeWritten.getWord()));
					// fetching.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
		new MainHandler();
	}
}
