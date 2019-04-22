package main;

import java.util.ArrayList;

public class InstructionMemory {

	Word[] instructionMemory = new Word[(int) (64 * (Math.pow(10, 6)))];
	private static int PC;
	private ArrayList<Integer> reserve = new ArrayList<Integer>();

	public InstructionMemory() {
		PC = 0;
	}
	public int getCurrentPC() {
		return PC;
	}
	public Word getWordFromMemory(int i) {
		return instructionMemory[i];
	}

	public void setWordAtMemory(int i, Word input) {
		instructionMemory[i] = input;
	}

	public Word fetchNextInstruction() {
		return instructionMemory[PC++];
	}

	public Word fetchBranchedInstruction(int i) {
		PC = PC + 1;
		reserve.add(PC);
		PC = i;
		return instructionMemory[PC++];
	}

	public void adjustPCForBranching(String i) {
		String stringPC = Integer.toBinaryString(PC);
		for(int j=0;j<(32-stringPC.length());j++) {
			stringPC = "0"+stringPC;
		}
		stringPC = stringPC.substring(0, 4);
		String newLocationString = stringPC + i + "00";
		PC = PC + 1;
		reserve.add(PC);
		PC = Integer.parseInt(newLocationString,2) / 4; // Divide by 4 to make it word addressable just like the Words in
														// the array
	}

	public void loadProgramFileToMemory(ArrayList<Word> program) {
		int count = 0;
		for (int i = 0; i < instructionMemory.length && count < program.size(); i++) {
			if (instructionMemory[i] == null) {
				instructionMemory[i] = program.get(count);
				count++;
			}
		}
	}
}
