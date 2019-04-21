package main;

public class ControlUnit {
	
	private String opCode;
	boolean[] signals = new boolean[13];

	public ControlUnit() {

	}

	public void setOpcode(String op) {
		opCode = op;
	}

	public void resetSignals() {
		for (int i = 0; i < signals.length; i++) {
			signals[i] = false;
		}
	}

	public void one(int i) {
		signals[i] = true;
	}

	public void zero(int i) {
		signals[i] = false;
	}

	public boolean[] evaluateOpCode(String op) {
		int opNum = Integer.parseInt(op);
		switch (opNum) {
		case 0: {
			one(12);
			zero(9);
			break;
		}
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6: {
			one(9);
			one(12);
			break;
		}
		case 7: {
			one(5);
			break;
		}
		case 8: {
			one(6);
			break;
		}
		case 9: {
			one(12);
			break;
		}
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15: {
			one(2);
			break;
		}
		case 16: {
			one(0);
			one(1);
			one(8);
			break;
		}
		case 17: {
			one(10);
			zero(8);
			break;
		}
		case 20: {
			one(3);
			break;
		}
		case 21: {
			one(11);
			one(8);
			break;
		}
		case 22: {
			one(4);
			zero(8);
			break;
		}
		}
		return signals;
	}
}
// All Signals Will be mapped to an Array of 13 elements
/*
 * 0= MemRead 1 = MemToReg 2 = branchCond 3 = branchUncond 4 = jmpReg 5= Mov 6=
 * MoveImmediate 7= RegDestination 8= RegWrite 9= ALUSrc 10= MemWrite 11= BAS 12
 * = AluOperation
 * 
 */
