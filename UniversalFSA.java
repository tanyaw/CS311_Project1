/**
 * CS 311 Formal Languages and Automata
 * Project #1 - Build Universal FSA
 * Winter Quarter 2017
 * Dr. Daisy Sang
 * Author: Tanya Wanwatanakool
 *
 * In this project, we defined five FSAs in the input text file.
 * This java program implements a universal FSA which reads in the 
 * file and determines whether or not the test strings are in the 
 * FSA's language.
 *  
 *
 * How to compile, link, and run this program:
 *   1) Please make sure InputFile.txt is in the directory before running the program
 * 	 2) javac UniversalFSA.java
 *   3) java UniversalFSA
 * 
 * Please note the following special characters and metacharacters in the InputFile.txt
 * 	 1) / 			represents an empty string
 *   2) ########## 	represents the end_marker for a FSA definition
 *   3) 0%9			represents digits 0...9
 *   4) 1%9			represents digits 1...9
 *   5) a%z			represents lowercase characters a...z
 *   6) A%Z			represents uppercase characters A...Z
 *   
 *   The empty string character '/' is included in the alphabet of all the state machine definitions.
 * */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class UniversalFSA {
	private static Map<String,String> transitionTable = new HashMap<String,String>();
	private static ArrayList<String> metaCharLibrary = new ArrayList<String>();
	private static ArrayList<String> testStrings = new ArrayList<String>();
	private static ArrayList<String> finalStates = new ArrayList<String>();
	private static ArrayList<String> language = new ArrayList<String>();
	private static int numStates;
	private static String trapState;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		/** READ INPUT FILE */
		BufferedReader br = new BufferedReader(new FileReader("InputFile.txt"));
		
		//Create metaCharLibrary
		createMetaChar();
		
		/** FOR EACH FSA MACHINE */
		for(int fsa=0; fsa < 5; fsa++) {
			 System.out.println("----- Finite State Automation #" + (fsa+1) + " -----");
			 
			/** READ FSA DEFINITION */
			try {
				String[] tempStates;
				String[] alphabet;
				String line;
				
				/** READ NUMBER OF STATES */
				numStates = Integer.parseInt(br.readLine());
				trapState = String.valueOf((numStates -1));
				
				/** SET FINAL STATES */
				line = br.readLine();
				tempStates = line.split(",");
				for(int i=0; i < tempStates.length; i++) {
					finalStates.add(tempStates[i]);
				}
				
				/** READ ALPHABET */
				line = br.readLine();
				alphabet = line.split(",");
				for(int i=0; i < alphabet.length; i++) {				
					language.add(alphabet[i]);
				}
				
				/** READ TRANSITION SEQUENCES */
				while(  !((line = br.readLine()).equals("##########")) ) {	//Checks for FSA end_marker
					if(line.charAt(0) == '(') {
						//Format line, Crop '(' and ')'
						line = line.replaceAll("[()]", "");
						String[] transitionSequence = line.split(" ");
						String k = transitionSequence[0] + " " + transitionSequence[1];
						String v = transitionSequence[2];
						
						/** 
						 * STORE <KEY, VALUE> IN TRANSITIONTABLE HASHMAP 
						 * where key = current state + input symbol
						 * 		 value = next state	
						 **/
						transitionTable.put(k, v);
					} else { 
						/** READ TEST STRINGS */
						testStrings.add(line);
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			/** PRINT FSA Definition */
			printFSADef();
			
			/** FOR EACH SET OF TEST STRINGS */
			for(int i=0; i < testStrings.size(); i++) {
				String s = testStrings.get(i);
				String state = "0";
				String k = "";
				char preInput = ' ';
				
				//Send string to checkString
				//Check if symbols are in alphabet
				if (checkString(s)) {
					/** FOR EACH TEST STRING */
					for(int j=0; j < s.length(); j++) {	
						preInput = s.charAt(j);
						String input = "" + preInput;
						
						//Replaces symbol with meta-character
						input = changeMetaChar(input);
						
						//Create key
						k = state + " " + input;
						
						//Get next state
						state = transitionTable.get(k);
						//System.out.println("Next State: " + state);
	
					}
					
					/** CHECK IF LAST STATE IS IN FINAL OR TRAP */
					if(finalStates.contains(state)) {
		 				//System.out.println("State: " + state);
		 				System.out.printf("    %-40.40s %-30.30s %n", s, "Accept");
		 			} else {
		 				System.out.printf("    %-40.40s %-30.30s %n", s, "Reject");
		 			}
					
				} else {
					if(s.equals("/")) {
						//Empty string begins at initial state, Set state = "0"
						if(finalStates.contains("0")) {
							System.out.printf("    %-40.40s %-30.30s %n", "Empty String", "Accept");
						} else {
							System.out.printf("    %-40.40s %-30.30s %n", "Empty String" , "Reject");
						}
					} else {
						System.out.printf("    %-40.40s %-30.30s %n", s, "Reject");
					}
				}
			}
			
			System.out.println();
			/**CLEAR DATA STRUCTURES FOR NEXT FSA*/
			testStrings.clear();
			language.clear();
			finalStates.clear();
			transitionTable.clear();
	 	}
	}

	/** 
	 * Helper Method - Checks if input symbol is in alphabet
	 *   True, if in alphabet
	 *   False, if not
	 */
	private static Boolean checkString(String s) {
		char preInput = ' ';
		String state = "0";
		String k = "";
		
		for(int i=0; i < s.length(); i++) {			
			preInput = s.charAt(i);
			String input = "" + preInput;
			//System.out.println("input: " + input);

			/**Edge Case - Empty String*/
			if(input.equals("/")) {	
 				return false;
 			}
			
			/**Case 1 - Handles meta-characters*/
			if(language.contains("1%9") || language.contains("0%9") || language.contains("a%z") || language.contains("A%Z")) {
				if(metaCharLibrary.contains(input)) {
					return true;
				}
			}
			
			/**Case 2 - Checks if input isn't in alphabet*/
			if(!language.contains(input)) {
				return false;
			} 
			
			/**Case 3 - Checks for intermediate trap states within string*/			
			k = state + " " + input;
			state = transitionTable.get(k); 
			if(state.equals(trapState)) {
 				return false;
 			}
		}
		return true;
	}

	/**
	 * Helper Method - If meta-character is contained in language, 
	 * 				   replaces input symbol with associated meta-character
	 * */
	private static String changeMetaChar(String s) {
		if(language.contains("1%9")) {
			if(s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6") || s.equals("7") || s.equals("8") || s.equals("9")){
				s = "1%9";
			}
		} 
		
		if(language.contains("0%9")) {
			if(s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6") || s.equals("7") || s.equals("8") || s.equals("9")){
				s = "0%9";
			}
		}
		
		if(language.contains("a%z")) {
			for(int h=97; h<123; h++) {
				char pre = (char) h;
				String in = "" + pre;
									
				if(s.equals(in)){
					s = "a%z";
				}
			}
		}
		
		if(language.contains("A%Z")) {
			for(int h=65; h<91; h++) {
				char pre = (char) h;
				String in = "" + pre;
		
				if(s.equals(in)){
					s = "a%z";
				}
			}
		}
		
		return s;
	}
	
	/**
	 * Print Method - Prints FSA Definition
	 * 		(# of states, Final states, Alphabet, Transition sequence)
	 * */
	private static void printFSADef() {
		System.out.println("(1)Number of States: " + numStates);
		
		String s = Arrays.toString(finalStates.toArray());
		s = s.replaceAll("\\[", "").replaceAll("\\]","");
		System.out.println("(2)Final States: " + s);
		
		String s2 = Arrays.toString(language.toArray());
		s2 = s2.replaceAll("\\[", "").replaceAll("\\]","");
		System.out.println("(3)Alphabet: " + s2);
		
		System.out.println("(4)Transitions: ");
		for (String k: transitionTable.keySet()){
            String value = transitionTable.get(k);  
            System.out.println("    " + k + " " + value);  
		} 
		System.out.println("5)Strings: ");	
	}
	
	/**
	 * Helper Method - Adds all characters associated with meta-characters to metaCharLibrary
	 */
	 private static void createMetaChar() {
		//Add digits
		for(int i=0; i<10; i++) {
			String input = Integer.toString(i);
			metaCharLibrary.add(input);
		}
		
		//Add lowercase characters
		for(int i=97; i<123; i++) {
			char preInput = (char) i;
			String input = "" + preInput;
			metaCharLibrary.add(input);
		}
		
		//Add uppercase characters
		for(int i=65; i<91; i++) {
			char preInput = (char) i;
			String input = "" + preInput;
			metaCharLibrary.add(input);
		}
	}
}