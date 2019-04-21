package main;

public class DataMemory {
	
	Word[] dataMemory = new Word[64*10^6];
	
	public Word getWordFromMemory(int i) {
		return dataMemory[i];
	}
	public void setWordAtMemory(int i,Word input) {
		dataMemory[i]=input;
	}
}
