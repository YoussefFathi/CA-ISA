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
	private Semaphore fetching= new Semaphore(1);
	private Semaphore decoding= new Semaphore(0);
	private Semaphore executing= new Semaphore(0);
	private Semaphore memoryAccessing= new Semaphore(0);
	private Semaphore writingBack= new Semaphore(0);
	private Word fetched;
	private boolean[] signals;
	private String fetchedFromDecode;
	static int count=0;
	public MainHandler() {
		this.getUserInstructions();
		instMem.loadProgramFileToMemory(instructions);
		this.fetchThread.start();
//		fetching.release();
		this.decodeThread.start();
		this.executeThread.start();
//		this.memoryAccessThread.start();
//		this.writeBackThread.start();
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
					if(fetched==null) {
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
					if(fetchedFromDecode==null) {
						break;
					}
					boolean[] signalsEx = signals;
					String instruction = fetchedFromDecode;
					decoding.release();
					System.out.println("Instruction : " + instruction + " Is Executing" + ++count);
//					memoryAccessing.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	public Thread memoryAccessThread = new Thread() {
		public void run() {
//			while (true) {
//				try {
//					memoryAccessing.acquire();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				executing.release();
//			}

		}
	};
	public Thread writeBackThread = new Thread() {
		public void run() {

		}
	};

	public void getUserInstructions() {
		instructions.add(new Word("000000000000000000000000000000000000"));
		instructions.add(new Word("000000000000000000000000000000000001"));
		instructions.add(new Word("000000000000000000000000000000000010"));

	}

	public static void main(String[] args) {
		MainHandler main = new MainHandler();
	}
}
