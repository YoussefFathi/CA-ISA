package main;

public class DataMemory {
	
	Word[] dataMemory = new Word[(int) (64 * (Math.pow(10, 6)))];
	
	public Word getWordFromMemory(int i) {
		return dataMemory[i];
	}
	public void setWordAtMemory(int i,Word input) {
		dataMemory[i]=input;
	}
}
