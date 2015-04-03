import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import static java.awt.event.KeyEvent.*;


/**
 * 
 * @author Jacob Aspinall
 * @version 1.0 March 26, 2015
 *
 */
public class Flowchart {

	Robot robot = new Robot();
	String[] procedures;
	String currentProcedure = "main";
	int procedureCount = 0;
	boolean inFunction = false;
	boolean inWhile = false;
	boolean inIf = false;
	boolean[] inElse;
	int inElsePosition = 0;
	int[] lineCounts;
	int lineCount = 0;
	int lineCountsPosition = 0;
	
	final int UP = VK_UP;
	final int DOWN = VK_DOWN;
	final int LEFT = VK_LEFT;
	final int RIGHT = VK_RIGHT;
	final int ENTER = VK_ENTER;
	final int TAB = VK_TAB;
	final int ALT = VK_ALT;
	final int M = VK_M;
	final int F = VK_F;
	
	/** A VisualLogic Flowchart
	 * 
	 * @param pseudocode	Array of strings originally entered in the GUI
	 * @throws AWTException
	 */
	public Flowchart(String[] pseudocode) throws AWTException {
	    
	    robot.setAutoDelay(1);
	    robot.setAutoWaitForIdle(true); 
	    robot.delay(1000);
	    robot.mouseMove(40, 130);
	    leftClick();

	    lineCounts = new int[pseudocode.length];
	    procedures = new String[pseudocode.length]; 
	    inElse = new boolean[pseudocode.length];
	    
	    for(boolean b : inElse){
	    	b = false;
	    }
	    
	    createProcedures(pseudocode);
	    createChart(pseudocode);
	   
			
	}
    
	/** Reads pseudocode line by line to create flowchart
	 * 
	 * @param pseudocode	Array of strings originally entered in the GUI
	 */
	private void createChart(String[] pseudocode){
		
		pseudocode = clearSpaces(pseudocode);
		
		for(int i = 0; i<pseudocode.length ; i++){
			
			String line = pseudocode[i];
			
			if(line == null)
				continue;
			
			if(lineCountsPosition == 0){
				inIf = false;
				inElse[inElsePosition] = false;
			}
			
			String[] criticalValues;
			String criticalValue; 
			String nextline = " ";
			int lineLength = line.length();
			
			if(i < pseudocode.length - 1)
				nextline = pseudocode[i + 1];
			
			if(!(Character.isLetter(line.charAt(0))))
				continue;
			
			if(lineLength == 5 && (line.substring(0, 5).equals("start"))){
				inFunction = true;
				continue;
			}
			
			if(lineLength == 4 && (line.substring(0, 4).equals("stop"))){
				inFunction = false;
				continue;
			}
			
			if(lineLength == 12 && ((line.substring(0, 12).toLowerCase()).equals("declarations") || line.substring(0, 5).equals("start"))){
				continue;
			}
			
			if(lineLength > 6 && (line.substring(0, 7).equals("string "))){
				criticalValues = findStringAssignment(line.substring(6));
				createAssignment(criticalValues[0], criticalValues[1]);
				continue;
			}
			
			if(lineLength > 3 && (line.substring(0, 4).equals("num "))){
				criticalValues = findNumAssignment(line.substring(3));
				createAssignment(criticalValues[0], criticalValues[1]);
				continue;
			}
			
			if(lineLength > 6 && (line.substring(0, 7).equals("output "))){	
				
				if(nextline == null){
					criticalValue = findOutputValue(line.substring(6));
					createOutput(criticalValue);
					continue;
				
				
				}
				
				if(nextline.length() > 5 && nextline.substring(0, 6).equals("input ")){
					criticalValues = findInputValues(line.substring(6),nextline.substring(5));
					createInput(criticalValues[0], criticalValues[1]);
					i++;
					continue;
				}
				
					
				criticalValue = findOutputValue(line.substring(6));
				createOutput(criticalValue);
				continue;
				
				
				
			}
				
			if(lineLength > 5 && line.substring(0, 6).equals("while ")){
				
	
				criticalValue = findWhileCondition(line.substring(5));
				createWhile(criticalValue);
				
				lineCountsPosition++;
				inElsePosition++;
				inElse[inElsePosition] = false;
				lineCount = 0;
				inWhile = true;
				continue;
			}
			
			if(line.length() > 7 && (line.substring(0, 8).toLowerCase()).equals("endwhile")){
				DOWN();
				DOWN();
				lineCounts[lineCountsPosition] = 0;
				lineCountsPosition--;
				inElsePosition--;
				lineCount = lineCounts[lineCountsPosition];
				
				if(lineCountsPosition == 0)
					inWhile = false;
				
			}
						
			if(lineLength > 2 && (line.substring(0, 3).equals("if "))){
				criticalValue = findIfCondition(line.substring(2));
				createIf(criticalValue);
				inIf = true;
				lineCountsPosition++;
				lineCount = 0;
				inElsePosition++;
				inElse[inElsePosition] = false;
				continue;

				
			}
			
			if(line.length() > 3 && line.substring(0, 4).equals("else")){		
				inElse[inElsePosition] = true;
				LEFT();
				lineCount = 0;
				lineCounts[lineCountsPosition] = 0;
			}
			
			if(line.length() > 4 && (line.substring(0, 5).toLowerCase()).equals("endif")){
				DOWN();
				
				if ( inElse[inElsePosition]){
					lineCounts[lineCountsPosition] = 0;
					lineCountsPosition--;
					lineCount = lineCount + lineCounts[lineCountsPosition] + 1;
				}
				else{
					lineCounts[lineCountsPosition] = 0;
					lineCountsPosition--;
					lineCount = lineCounts[lineCountsPosition] + 1;
				}
				
				inElse[inElsePosition] = false;
				inElsePosition--;
				
				if(lineCountsPosition == 0){
					inIf = false;
					inElse[inElsePosition] = false;
				}
				
				
				
			}
			
			if(line.substring(line.length()-2,line.length()).equals("()")) {
				
				boolean alreadyCreated = false;
				
				if(inFunction == false){
					inFunction = true;
					currentProcedure = line;
					goToProcedure(line);
				}
				else{
					for(int g = 0; g < procedures.length; g++){
						if(procedures[g] == null){
							continue;
						}
						
						if(procedures[g].equals(line)){
							createProcedureCall(line);
							alreadyCreated = true;
							break;
						}	
					}
					
					if(alreadyCreated == false){
						createProcedure(line);	
						createProcedureCall(line);
					}
					
				}
				continue;
			}
			
			if(line.length() == 6 && line.substring(0,6).equals("return")) {			
				inFunction = false;
				continue;
			}
			
			if(Character.isLetter(line.charAt(0))){	
				int equalsLocation = line.indexOf("=");
				
				if(equalsLocation < 0)
					continue;
				else
					criticalValues = findProcessValues(line, equalsLocation);
				
				createAssignment(criticalValues[0], criticalValues[1]);
				continue;
			}
		}
			
		goToMain();
	}	

	
	

/**	finds values for a process box
 * 
 * @param line					Current line of pseudocode
 * @param equalsLocation		Location of equals sign in "line"
 * @return						An array containing values to be entered into the process box
 */
	private String[] findProcessValues(String line, int equalsLocation) {
		String[] criticalValues = new String[2];
		criticalValues[0] = line.substring(0,equalsLocation);
		criticalValues[1] = line.substring(equalsLocation + 1);
		return criticalValues;
	}

/**	Finds values for an input box
 * 
 * @param output				The prompt
 * @param input					The input variable
 * @return						An array containing values to be entered into the input box
 */
	private String[] findInputValues(String output, String input) {
		
		String[] criticalValues = new String[2];
		String line = output;
		
		for(int l = 0; l < 2; l++){
			
			int quotes = 0;
			String result = "";
		
			for(int i = 0; i < line.length() ; i++){
				String c = line.substring(i,i+1);
				if(c.equals("\""))
					quotes++;
			
				if( c.equals(",") && (quotes % 2 == 0)){
					result = result + " & ";
					continue;
				}
			
				result = result + c;
				
			}
			criticalValues[l] = result;
			line = input;
		}
		return criticalValues;
	}

/** Finds values for an if statement box
 * 
 * @param line					Current line of pseudocode
 * @return						If condition
 */
	private String findIfCondition(String line) {
		
		String result = "";
		
		for(int i = 0; i < line.length() ; i++){
			
			String c = line.substring(i,i+1);
			
			result = result + c;
				
		}
		return result;
	}

/** Finds value for a while loop box
 * 
 * @param line					Current line of pseudocode
 * @return						While condition
 */
	private String findWhileCondition(String line) {
		
		String result = "";
		
		for(int i = 0; i < line.length() ; i++){
			String c = line.substring(i,i+1);
			
			result = result + c;
				
		}
		return result;
	}

/** Finds value for an output box
 * 
 * @param line					Current line of pseudocode
 * @return						Output 
 */
	private String findOutputValue(String line) {
		
		int quotes = 0;
		String result = "";
		
		for(int i = 0; i < line.length() ; i++){
			String c = line.substring(i,i+1);
			if(c.equals("\""))
				quotes++;
			
			if( c.equals(",") && (quotes % 2 == 0)){
				result = result + " & ";
				continue;
			}
			
			result = result + c;
				
		}
		return result;
	}

/**	Finds values for a num assignment box
 * 
 * @param line					Current line of pseudocode
 * @return						Array containing values to be put into an assignment box
 */
	private String[] findNumAssignment(String line) {
		
		String[] criticalValues = new String[10];
		boolean equalSign = false;
		criticalValues[0] = "";
		
		for(int i = 0; i < line.length() ; i++){
			
			char c = line.charAt(i); 
			
			if(c=='='){
				criticalValues[1] = line.substring(i + 1);
				i = line.length();
				equalSign = true;
				continue;
			}		
			else if(c!=(' ') && c!=('=') && c!= '\0'){
				criticalValues[0] = criticalValues[0] + c;
				continue;
			}
			
			
			
		}
		if(equalSign == false){
			criticalValues[1] = "0";
		}
		return criticalValues;
	}

/**	Finds values for a string assignment box
*
* @param line					Current line of pseudocode
* @return						Array containing values to be put into an assignment box
*/
	private String[] findStringAssignment(String line) {
		
		String[] criticalValues = new String[10];
		boolean equalSign = false;
		criticalValues[0] = "";
		
		for(int i = 0; i < line.length() ; i++){
			
			char c = line.charAt(i); 
			
			if(c=='='){
				criticalValues[1] = line.substring(i + 1);
				i = line.length();
				equalSign = true;
				continue;
			}	
			else if(!(Character.isLetter(c) || c == '_' || Character.isDigit(c))){
				continue;
			}	
			else if(c!=(' ') && c!=('=') && c!= '\0'){
				criticalValues[0] = criticalValues[0] + c;
				continue;
			}
			
			
			
		}
		
		if(equalSign == false){
			criticalValues[1] = "\" \"";
		}
		return criticalValues;
	}

/** Clears spaces at the beginning of a line
 * 
 * @param original				Array containing the original lines of pseudocode
 * @return						The original with spaces deleted
 */
	private String[] clearSpaces(String[] original){
		
		String[] result = new String[original.length];
		
		for(int i = 0; i < original.length; i++){
			
			String s = original[i];
			int c = 0;
			
			if(s == null)
				continue;
			
			if(s.length() > 0){
				
				//Clean spaces in front of line
				while((s.substring(c, c + 1).equals( " " ) || s.substring(c, c + 1).equals( "\n" ) || 
						s.substring(c, c + 1).equals( "\t" ) || s.substring(c, c + 1).equals( "" )) && c < s.length() - 1 ){
					
						c++;
						
				}
			
				result[i] = s.substring(c);
				
				//int c2 = result[i].length();
				
				//Clear spaces behind line
				
				//while((s.substring(c2-1, c2).equals( " " ) || s.substring(c2-1, c2).equals( "\n" ) || 
				//		s.substring(c2-1, c2).equals( "\t" ) || s.substring(c2-1, c2).equals( "" )) && c2 > 0 ){
					
					//	c2--;
						
				//}
			
				//result[i] = s.substring(c, c2);
			
			}
		}	
		return result;
	}
/** Types a string
 * 	
 * @param toChar				String to be typed
 */
	private void type(String toChar){
		
		for(int i = 0; i < toChar.length(); i++){
			
			typeChar(toChar.charAt(i));
		}
	}
	

/** Types a character
 *typeChar() and doType() courtesy of
 *http://stackoverflow.com/questions/1248510/convert-string-to-keyevents/1248709#1248709
 * 
 * @param character				Character to be typed
 */
	 private void typeChar(char character) {
	            switch (character) {
	            case 'a': doType(VK_A); break;
	            case 'b': doType(VK_B); break;
	            case 'c': doType(VK_C); break;
	            case 'd': doType(VK_D); break;
	            case 'e': doType(VK_E); break;
	            case 'f': doType(VK_F); break;
	            case 'g': doType(VK_G); break;
	            case 'h': doType(VK_H); break;
	            case 'i': doType(VK_I); break;
	            case 'j': doType(VK_J); break;
	            case 'k': doType(VK_K); break;
	            case 'l': doType(VK_L); break;
	            case 'm': doType(VK_M); break;
	            case 'n': doType(VK_N); break;
	            case 'o': doType(VK_O); break;
	            case 'p': doType(VK_P); break;
	            case 'q': doType(VK_Q); break;
	            case 'r': doType(VK_R); break;
	            case 's': doType(VK_S); break;
	            case 't': doType(VK_T); break;
	            case 'u': doType(VK_U); break;
	            case 'v': doType(VK_V); break;
	            case 'w': doType(VK_W); break;
	            case 'x': doType(VK_X); break;
	            case 'y': doType(VK_Y); break;
	            case 'z': doType(VK_Z); break;
	            case 'A': doType(VK_SHIFT, VK_A); break;
	            case 'B': doType(VK_SHIFT, VK_B); break;
	            case 'C': doType(VK_SHIFT, VK_C); break;
	            case 'D': doType(VK_SHIFT, VK_D); break;
	            case 'E': doType(VK_SHIFT, VK_E); break;
	            case 'F': doType(VK_SHIFT, VK_F); break;
	            case 'G': doType(VK_SHIFT, VK_G); break;
	            case 'H': doType(VK_SHIFT, VK_H); break;
	            case 'I': doType(VK_SHIFT, VK_I); break;
	            case 'J': doType(VK_SHIFT, VK_J); break;
	            case 'K': doType(VK_SHIFT, VK_K); break;
	            case 'L': doType(VK_SHIFT, VK_L); break;
	            case 'M': doType(VK_SHIFT, VK_M); break;
	            case 'N': doType(VK_SHIFT, VK_N); break;
	            case 'O': doType(VK_SHIFT, VK_O); break;
	            case 'P': doType(VK_SHIFT, VK_P); break;
	            case 'Q': doType(VK_SHIFT, VK_Q); break;
	            case 'R': doType(VK_SHIFT, VK_R); break;
	            case 'S': doType(VK_SHIFT, VK_S); break;
	            case 'T': doType(VK_SHIFT, VK_T); break;
	            case 'U': doType(VK_SHIFT, VK_U); break;
	            case 'V': doType(VK_SHIFT, VK_V); break;
	            case 'W': doType(VK_SHIFT, VK_W); break;
	            case 'X': doType(VK_SHIFT, VK_X); break;
	            case 'Y': doType(VK_SHIFT, VK_Y); break;
	            case 'Z': doType(VK_SHIFT, VK_Z); break;
	            case '`': doType(VK_BACK_QUOTE); break;
	            case '0': doType(VK_0); break;
	            case '1': doType(VK_1); break;
	            case '2': doType(VK_2); break;
	            case '3': doType(VK_3); break;
	            case '4': doType(VK_4); break;
	            case '5': doType(VK_5); break;
	            case '6': doType(VK_6); break;
	            case '7': doType(VK_7); break;
	            case '8': doType(VK_8); break;
	            case '9': doType(VK_9); break;
	            case '-': doType(VK_MINUS); break;
	            case '–': doType(VK_MINUS); break;
	            case '=': doType(VK_EQUALS); break;
	            case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
	            case '!': doType(VK_SHIFT, VK_1); break;
	            case '@': doType(VK_SHIFT, VK_2); break;
	            case '#': doType(VK_SHIFT, VK_3); break;
	            case '$': doType(VK_SHIFT, VK_4); break;
	            case '%': doType(VK_SHIFT, VK_5); break;
	            case '^': doType(VK_SHIFT, VK_6); break;
	            case '&': doType(VK_SHIFT, VK_7); break;
	            case '*': doType(VK_SHIFT, VK_8); break;
	            case '(': doType(VK_SHIFT, VK_9); break;
	            case ')': doType(VK_SHIFT, VK_0); break;
	            case '_': doType(VK_SHIFT, VK_MINUS); break;
	            case '+': doType(VK_SHIFT, VK_EQUALS); break;
	            case '\t': ; break;
	            case '\n': doType(VK_ENTER); break;
	            case '[': doType(VK_OPEN_BRACKET); break;
	            case ']': doType(VK_CLOSE_BRACKET); break;
	            case '\\': doType(VK_BACK_SLASH); break;
	            case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
	            case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
	            case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
	            case ';': doType(VK_SEMICOLON); break;
	            case ':': doType(VK_SHIFT, VK_SEMICOLON); break;
	            case '\'': doType(VK_QUOTE); break;
	            case '"': doType(VK_SHIFT, VK_QUOTE); break;
	            case '“': doType(VK_SHIFT, VK_QUOTE); break;
	            case '”': doType(VK_SHIFT, VK_QUOTE); break;
	            case ',': doType(VK_COMMA); break;
	            case '<': doType(VK_SHIFT, VK_COMMA); break;
	            case '.': doType(VK_PERIOD); break;
	            case '>': doType(VK_SHIFT, VK_PERIOD); break;
	            case '/': doType(VK_SLASH); break;
	            case '?': doType(VK_SHIFT, VK_SLASH); break;
	            case ' ': doType(VK_SPACE); break;
	            default:
	                throw new IllegalArgumentException("Cannot type character " + character);
	            }
	    	
	    }
/** Types a char using the keyboard
 * 
 *typeChar() and doType() courtesy of
 *http://stackoverflow.com/questions/1248510/convert-string-to-keyevents/1248709#1248709	
 *      
 * @param keyCodes				keyCodes of chars to be typed
 */
	    private void doType(int... keyCodes) {
	        doType(keyCodes, 0, keyCodes.length);
	    }
/** Types a char using the keyboard
* 
*typeChar() and doType() courtesy of
*http://stackoverflow.com/questions/1248510/convert-string-to-keyevents/1248709#1248709	
*      
* @param keyCodes				keyCodes of chars to be typed
*/
	    private void doType(int[] keyCodes, int offset, int length) {
	        if (length == 0) {
	            return;
	        }

	        robot.keyPress(keyCodes[offset]);
	        doType(keyCodes, offset + 1, length - 1);
	        robot.keyRelease(keyCodes[offset]);
	    }
/** Goes to a procedure
 * 	    
 * @param procedureName			Name of procedure
 */
	private void goToProcedure(String procedureName){
		
		if(procedureName.equals("main"))
				goToMain();
		
		int c = 0;
		for(int i = 0; i < procedures.length; i++){
			if(procedures[i] == null)
				continue;
			
			if(procedures[i].equals(procedureName)){
				ALT_F();
				RIGHT();
				RIGHT();
				RIGHT();
				RIGHT();
				RIGHT();
				
				while(c != i + 1){
					DOWN();
					c++;
				}
					
				ENTER();	
			}
		}	
	}
/** Goes to the main procedure
 * 	    
 */
	private void goToMain(){
		
		ALT_F();
		RIGHT();
		RIGHT();
		RIGHT();
		RIGHT();
		RIGHT();
		ENTER();
		
	}
/** Returns to current box when inside a while/for/if body
 * 	
 */
	private void returnToBox(){
		
		if(lineCountsPosition == 0)
			return;
		lineCounts[lineCountsPosition] = lineCount;
		DOWN();
		
		if(inElse[inElsePosition])
			LEFT();
		else
			RIGHT();
		int c = 0;
		
		while( c < (lineCounts[lineCountsPosition])-1){
			DOWN();
			c++;
		}
		
	}
/** Presses right arrow key
 * 	
 */
	private void RIGHT(){
		
		robot.keyPress(RIGHT);
		robot.keyRelease(RIGHT);
		
	}
/** Presses left arrow key
* 	
*/	
	private void LEFT(){
		
		robot.keyPress(LEFT);
		robot.keyRelease(LEFT);
		
	}
/** Presses enter key
* 	
*/
	private void ENTER(){
		
		robot.keyPress(ENTER);
		robot.keyRelease(ENTER);
		
	}
/** Presses up arrow key
* 	
*/	
	private void UP(){
		
		robot.keyPress(UP);
		robot.keyRelease(UP);
	}
/** Presses down arrow key
* 	
*/	
	private void DOWN(){
		
		robot.keyPress(DOWN);
		robot.keyRelease(DOWN);
	}
	
/** Presses tab key
* 	
*/	
	private void TAB(){
		
		robot.keyPress(TAB);
		robot.keyRelease(TAB);
	}
/** Presses alt-m key combination
* 	
*/	
	private void ALT_M(){
		
		robot.keyPress(ALT);
		robot.keyPress(M);
		robot.keyRelease(M);
		robot.keyRelease(ALT);
	}
/** Presses alt-f key combination
* 	
*/	
	private void ALT_F(){
		
		robot.keyPress(ALT);
		robot.keyPress(F);
		robot.keyRelease(F);
		robot.keyRelease(ALT);
	}
/** Left clicks the mouse
 * 	
 */
	private void leftClick()
	  {
	    robot.mousePress(InputEvent.BUTTON1_MASK);
	    robot.delay(200);
	    robot.mouseRelease(InputEvent.BUTTON1_MASK);
	    robot.delay(200);
	  }
/**	Creates all needed procedures for the flowchart
 * 	
 * @param pseudocode			Array of strings originally entered in the GUI
 */
	private void createProcedures(String[] pseudocode){
		
		
		pseudocode = clearSpaces(pseudocode);
		
		for(int i = 0; i<pseudocode.length ; i++){
			
			String line = pseudocode[i];

			
			if(line == null)
				continue;
			
			int lineLength = line.length();
			
			
			if(lineLength == 5 && (line.substring(0, 5).equals("start"))){
				inFunction = true;
				continue;
			}
			
			if(lineLength == 4 && (line.substring(0, 4).equals("stop"))){
				inFunction = false;
				continue;
			}

			if(inFunction == false && line.substring(line.length()-2,line.length()).equals("()")) {
				boolean alreadyCreated = false;
				
				if(inFunction == false){
					inFunction = true;
					goToProcedure(line);
				}
				else{
					for(int g = 0; g < procedures.length; g++){
						if(procedures[g] == null){
							continue;
						}
						
						if(procedures[g].equals(line)){
							alreadyCreated = true;
							break;
						}
						
					}
					
					}
				if(alreadyCreated == false){
					createProcedure(line);
				}
				continue;
			}

			if(line.length() == 6 && line.substring(0,6).equals("return")) {
				inFunction = false;
				continue;
			}
			
			
			
		}
		
		
	}
/**	Creates a box in the flowchart
 * 
 * @param downAmount				Position of the box in the box creating menu
 */
	private void createBox(int downAmount){
		int[] keyCombo = new int[downAmount + 2];
		
		keyCombo[0] = ENTER;
		
		for(int i = 1; i <= downAmount; i++)
			keyCombo[i] = DOWN;
		
		keyCombo[downAmount + 1] = ENTER;
		
		for(int key : keyCombo)	{
			robot.keyPress(key);
			robot.keyRelease(key);
		}
	
		if(inWhile){
			returnToBox();
			
			ENTER();
		}
		else if(inIf || inElse[inElsePosition]){
			
			returnToBox();
			ENTER();
		}
		else if (downAmount == 4 ){
			UP();
			UP();
			ENTER();
		}
		else{
			UP();
			ENTER();
		}
		
		
	}
/**	Creates an input box
 * 
 * @param prompt				Input prompt 
 * @param input					Input variable
 */
	private void createInput(String prompt, String input) {
		lineCount+=2;
		createBox(1);
		
		type(input);
		ALT_M();
		TAB();
		type(prompt);
		TAB();
		ENTER();
		DOWN();
		
	}
	
/**	Creates an input box
 * 
 * @param input					Input variable
 */
	private void createInput(String input) {
		lineCount+=2;
		createBox(1);
		
		type(input);
		
		ENTER();
		DOWN();
		
	}
	
/**	Creates an assignment box
 * 	
 * @param variable				Assignment variable
 * @param expression			Assignment expression
 */
	private void createAssignment(String variable, String expression ) {
		lineCount+=2;
		createBox(2);
		
		type(variable);
		TAB();
		type(expression);
		ENTER();
		DOWN();
	
		
	}
/**	Creates an output box
 * 	
 * @param output				Output statement
 */
	private void createOutput(String output) {
		lineCount+=2;
		
		createBox(3);
		
		type(output);
		
		ALT_M();
		TAB();
		RIGHT();
		
		int c = 0;
		while( c < output.length()){
			RIGHT();
			c++;
		}
		
		ENTER();
		TAB();
		ENTER();
		DOWN();

		
	}
	
/**	Creates an if statement box
 * 	
 * @param condition				If condition
 */
	private void createIf(String condition) {
		lineCount+=2;
		createBox(4);

		type(condition);
		ENTER();
		RIGHT();
	}
/** Creates a for loop box
 * 	
 * @param condition				For loop condition
 * @param initialValue			Initial value
 * @param finalValue			Final value
 * @param step					step amount
 */
	private void createFor(String condition, String initialValue, String finalValue, String step) {
	
		createBox(5);
		type(condition);
		TAB();
		type(initialValue);
		TAB();
		type(finalValue);
		TAB();
		type(step);
		ENTER();
		DOWN();
		
	}
/**	Creates a while loop box
 * 
 * @param condition				While condition
 */
	private void createWhile(String condition) {
		lineCount+=2;
		createBox(6);		
		type(condition);
		ENTER();
		RIGHT();	
	}
/**	Creates a procedure
 * 	
 * @param name					Name of procedure
 */
	private void createProcedure(String name) {
		int c = 0;
		ENTER();
		
		while(c != 10){
			DOWN();
			c++;
		}
		
		ENTER();
		
		for(int i = 0; i < procedureCount; i++)
			DOWN();
		
		ENTER();
		type(name);
		ENTER();
		goToProcedure(currentProcedure);
		
		procedures[procedureCount] = name;
		procedureCount++;
		
		UP();
		UP();	
	}
/** Creates a call to a procedure
 * 	
 * @param name					Name of procedure
 */
	private void createProcedureCall(String name){
		
		if(inWhile){
			
			returnToBox();
		
		
		if(lineCount > 0)
			DOWN();
		}
		
		for(int i = 0; i < procedures.length; i++){
			if(procedures[i] == null)
				continue;
			
			if(procedures[i].equals(name)){
				int c = 0;
				ENTER();
				
				while(c != 10){
					DOWN();
					c++;
				}
				
				ENTER();
				int g = 0;
				while(g < i){
					DOWN();
					g++;
				}
				
				ENTER();
				lineCount+=2;
				if(inWhile){
					returnToBox();	
					DOWN();
					
				}
				if(inIf){
					returnToBox();
					DOWN();
				}
				
			}
		}
	}
}