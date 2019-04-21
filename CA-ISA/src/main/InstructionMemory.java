package main;

import java.util.ArrayList;

public class InstructionMemory {
	
	Word[] instructionMemory = new Word[64 * 10 ^ 6];
	private static int PC;
	private ArrayList<Integer> reserve = new ArrayList<Integer>();

	public InstructionMemory() {
		PC = 0;
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
