import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

/* a stub for your team's fuzzer */
public class Fuzzer {

    private static final String OUTPUT_FILE = "fuzz.txt";

	private static final String STATE_FILE = "state.txt";
	
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final String ALPHABET_AND_WHITESPACE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 \n";

	//the number of times that generation-based fuzzer will be executed
	private static final int GENERATION_BASED_FUZZER_EXECUTION_TIMES = 5;

	/*
	 * The number of times that modified random fuzzer will be executed.
	 * The number of times that mutation fuzzer will be executed equals
	 * 100 - GENERATION_BASED_FUZZER_EXECUTION_TIMES - MODIFIED_RANDOM_-
	 * FUZZER_EXECUTION_TIMES
	 */
	private static final int MODIFIED_RANDOM_FUZZER_EXECUTION_TIMES = 70;
   
	/*
	 * If USE_STATE is true, the number of different fuzzing techniques will be
	 * exactly how it is defined by the above two variables. If it is false, the
	 * number will be closed to how it is defined but not exactly.
	 */
	private static final Boolean USE_STATE = true;
	
	private static final int MAX_LENGTH_OF_INSTRUCTION = 1022;

	private static final int MAX_NUMBER_OF_INSTRUCTION = 7000;

	private static final int MAX_INTEGER_VALUE = 2147483647;
	
	private static final Random random = new Random();

	private static int state = 0;

	private static int outputFileCount = 0;

	private static int dcStackCount = 0;

	private static ArrayList<String> varName = new ArrayList<String>();

	public static <T extends Enum<?>> T randomEnum(Class<T> classes) {
		int x = random.nextInt(classes.getEnumConstants().length);
		return classes.getEnumConstants()[x];
	}

	//generate a random string for load/store/remove instructions
	public static String randomString(String baseAlphabet, int maxLength) {
		int randomStringLen = random.nextInt(maxLength);
		StringBuilder stringBuilder = new StringBuilder(randomStringLen);

		//generate a random-length long random string
		for(int i = 0; i < randomStringLen; i++) {
			stringBuilder.append(baseAlphabet.charAt(random.nextInt(baseAlphabet.length())));
		}
		return stringBuilder.toString();
	}

	//parse the Instruction enum and generate a random input according to the instruction
	public static String parseInstruction(Instruction instruction) {
		if(instruction.equals(Instruction.PLUS)) {
			if(dcStackCount >= 2) {
				dcStackCount--;
				return "+";
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.SUB)) {
			if(dcStackCount >= 2) {
				dcStackCount--;
				return "-";
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.MULT)) {
			if(dcStackCount >= 2) {
				dcStackCount--;
				return "*";
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.DIV)) {
			if(dcStackCount >= 2) {
				dcStackCount--;
				return "/";
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.PUSH)) {
			int randNum = random.nextInt(MAX_INTEGER_VALUE);
			dcStackCount++;
			return "push " + String.valueOf(randNum);
		}
		else if(instruction.equals(Instruction.POP)) {
			if(dcStackCount >= 1) {
				dcStackCount--;
				return "pop";
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.LOAD)) {
			if(random.nextBoolean() || varName.size() == 0) {
				String randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 5);
				return "load " + randomString;
			}
			else {
				String randomString = varName.get(random.nextInt(varName.size()));
				dcStackCount++;
				return "load " + randomString;
			}
		}
		else if(instruction.equals(Instruction.REM)) {
			if(random.nextBoolean() || varName.size() == 0) {
				String randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 7);
				return "remove " + randomString;
			}
			else {
				int index = random.nextInt(varName.size());
				String randomString = varName.get(index);
				varName.remove(index);
				return "remove " + randomString;
			}
		}
		else if(instruction.equals(Instruction.STORE)) {
			if(dcStackCount >= 1) {
				//store the variable in a freshly generated variable name
				//and then add the variable name into the ArrayList varName
				if(random.nextBoolean() || varName.size() == 0) {
					String randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 6);
					varName.add(randomString);
					dcStackCount--;
					return "store " + randomString;
				}
				//choose a variable name randomly from the ArrayList varName
				else {
					String randomString = varName.get(random.nextInt(varName.size()));
					dcStackCount--;
					return "store " + randomString;
				}
			}
			else {
				return parseInstruction(Instruction.PUSH);
			}
		}
		else if(instruction.equals(Instruction.SAVE)) {
			return "save " + "variables" + String.valueOf(outputFileCount++);
		}
		else if(instruction.equals(Instruction.LIST)) {
			return "list";
		}
		else if(instruction.equals(Instruction.PRINT)) {
			return "print";
		}
		else {
			//temporary
			return "print";
		}
	}

	/* a generation-based fuzzer that generates random input files */
	public static void generationBasedFuzzing() throws IOException {
		//the number of instructions in a input file
		int loopTimes = random.nextInt(MAX_NUMBER_OF_INSTRUCTION);

		FileOutputStream out = null;
		PrintWriter pw = null;
		
		try {
			//create an OUTPUT_FILE, which is fuzz.txt in this case
			out = new FileOutputStream(OUTPUT_FILE);
			pw = new PrintWriter(out);
			
			//generate random commands based on some rules
			for(int i = 0; i < loopTimes; i++) {
				Instruction instruction = randomEnum(Instruction.class);
				if(instruction.equals(Instruction.DIV)) instruction = randomEnum(Instruction.class); //reduce the chance of division
				String parsedInstruction = parseInstruction(instruction);
				pw.println(parsedInstruction);
			}
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if(pw != null) pw.flush();
			if(out != null) out.close();
		}
	}

	public static String parseInstructionWithoutRules(Instruction instruction) {
		if(instruction.equals(Instruction.PLUS)) {
			return "+";
		}
		else if(instruction.equals(Instruction.SUB)) {
			return "-";
		}
		else if(instruction.equals(Instruction.MULT)) {
			return "*";
		}
		else if(instruction.equals(Instruction.DIV)) {
			return "/";
		}
		else if(instruction.equals(Instruction.PUSH)) {
			int randNum = random.nextInt(MAX_INTEGER_VALUE);
			return "push " + String.valueOf(randNum);
		}
		else if(instruction.equals(Instruction.POP)) {
			return "pop";
		}
		else if(instruction.equals(Instruction.LOAD)) {
			String randomString;
			if(varName.size() != 0) randomString = varName.get(random.nextInt(varName.size()));
			else randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 5);
			return "load " + randomString;
		}
		else if(instruction.equals(Instruction.REM)) {
			String randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 7);
			return "remove " + randomString;
		}
		else if(instruction.equals(Instruction.STORE)) {
			String randomString = randomString(ALPHABET, MAX_LENGTH_OF_INSTRUCTION - 6);
			varName.add(randomString);
			return "store " + randomString;
		}
		else if(instruction.equals(Instruction.SAVE)) {
			return "save " + "variables" + String.valueOf(outputFileCount++);
		}
		else if(instruction.equals(Instruction.LIST)) {
			return "list";
		}
		else if(instruction.equals(Instruction.PRINT)) {
			return "print";
		}
		else {
			//temporary
			return "print";
		}
	}

	public static void mutationBasedFuzzing() throws IOException {
		FileOutputStream out = null;
		PrintWriter pw = null;
		
		String seedInput = "";

		for(int i = 0; i < 1024; i++) {
			Instruction instruction = randomEnum(Instruction.class);
			String parsedInstruction = parseInstruction(instruction);
			seedInput = seedInput + parsedInstruction + "\n";
		}
		
		String randomString;
		String mutatedInput = seedInput.substring(0, seedInput.length() - 1);
		int breakPoint = random.nextInt(mutatedInput.length());
		if(random.nextBoolean()) randomString = randomString(ALPHABET_AND_WHITESPACE, 2 * MAX_LENGTH_OF_INSTRUCTION);
		else randomString = randomString(ALPHABET, 2 * MAX_LENGTH_OF_INSTRUCTION);
		mutatedInput = mutatedInput.substring(0, breakPoint) + randomString + mutatedInput.substring(breakPoint, mutatedInput.length() - 1);

		try {
			out = new FileOutputStream(OUTPUT_FILE);
			pw = new PrintWriter(out);
			pw.println(mutatedInput);
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if(pw != null) pw.flush();
			if(out != null) out.close();
		}
	}

	public static String enumToString(Instruction instruction) {
		if(instruction.equals(Instruction.PLUS)) {
			return "+";
		}
		else if(instruction.equals(Instruction.SUB)) {
			return "-";
		}
		else if(instruction.equals(Instruction.MULT)) {
			return "*";
		}
		else if(instruction.equals(Instruction.DIV)) {
			return "/";
		}
		else if(instruction.equals(Instruction.PUSH)) {
			return "push";
		}
		else if(instruction.equals(Instruction.POP)) {
			return "pop";
		}
		else if(instruction.equals(Instruction.LOAD)) {
			return "load";
		}
		else if(instruction.equals(Instruction.REM)) {
			return "remove";
		}
		else if(instruction.equals(Instruction.STORE)) {
			return "store";
		}
		else if(instruction.equals(Instruction.SAVE)) {
			return "save";
		}
		else if(instruction.equals(Instruction.LIST)) {
			return "list";
		}
		else if(instruction.equals(Instruction.PRINT)) {
			return "print";
		}
		else {
			return "error";
		}
	}


	public static void modifiedRandomFuzzing() throws IOException {
		FileOutputStream out = null;
		PrintWriter pw = null;
		try {
			out = new FileOutputStream(OUTPUT_FILE);
			pw = new PrintWriter(out);
			int rand = random.nextInt(3);
			if(random.nextBoolean()) {
				//the first type of modified random fuzzing
				if(rand != 0) {
					for(int i = 0; i < 512; i++) {
						pw.println("push " + String.valueOf(MAX_INTEGER_VALUE));
					}
				}
				for(int i = 0; i < 600; i++) {
					String instruction = parseInstructionWithoutRules(randomEnum(Instruction.class));
					pw.println(instruction);
				}
			}
			else {
				//the seconde type of modified random fuzzing
				int randInt = random.nextInt(3);
				String instruction = enumToString(randomEnum(Instruction.class));
				for(int i = 0; i < 100; i++) {
					if(randInt != 0) instruction += (" " + enumToString(randomEnum(Instruction.class)));
					else instruction += "\n";
				}
				pw.println(instruction);
			}
		} catch(Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if(pw != null) pw.flush();
			if(out != null) out.close();
		}
	}

	//get the state of the executed times of this program
	public static void getState() throws IOException {
		FileOutputStream out = null;
		FileInputStream in = null;
		PrintWriter pw = null;

		try {
			in = new FileInputStream(STATE_FILE);
			Scanner scanner = new Scanner(in);
			state = scanner.nextInt();
			if(in != null) in.close();
			out = new FileOutputStream(STATE_FILE);
			pw = new PrintWriter(out);
			if(state > 99) {
				state = 0;
				pw.println(1);
			}
			else {
				pw.println(state + 1);
			}
		}
		catch (Exception e) {
			try {
				out = new FileOutputStream(STATE_FILE);
				pw = new PrintWriter(out);
				pw.println(1);
			} catch (Exception x) {
				e.printStackTrace(System.err);
				System.exit(1);
			} finally {
				if(pw != null) pw.flush();
				if(out != null) out.close();
			}
		} finally {
			if(pw != null) pw.flush();
			if(out != null) out.close();
		}
	}

    public static void main(String[] args) throws IOException {
		if(USE_STATE) getState();
		else state = random.nextInt(100);

		if(state < GENERATION_BASED_FUZZER_EXECUTION_TIMES) {
			generationBasedFuzzing();
		}
		else if(state < GENERATION_BASED_FUZZER_EXECUTION_TIMES + MODIFIED_RANDOM_FUZZER_EXECUTION_TIMES) {
			modifiedRandomFuzzing();
		}
		else {
			mutationBasedFuzzing();
		}
	}
}
