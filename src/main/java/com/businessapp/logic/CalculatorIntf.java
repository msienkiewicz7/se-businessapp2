package com.businessapp.logic;


/**
 * Public interface between Calculator logic and GUI.
 * Input received from the Calculator GUI is passed as stream of tokens
 * to the Calculator logic using
 * 		nextToken( Token token );
 * 
 * And output from the Calculator logic is passed back to the Calculator
 * GUI for display using
 * 		write( int selector, String text );
 * 
 * @author Sven Graupner.
 *
 */
public interface CalculatorIntf extends ManagedComponentIntf {
	public static final String Calculator = "Calculator";
	public static final int DISPLAY_MAXDIGITS = 16;

	/*
	 * Tokens passed from Calculator GUI to Calculator logic.
	 */
	public enum Token {
		K_VAT,	K_CE,	K_C,	K_BACK,
		K_MPLUS,K_MR,	K_MC,	K_DIV,
		K_7,	K_8,	K_9,	K_MUL,
		K_4,	K_5,	K_6,	K_MIN,
		K_1,	K_2,	K_3,	K_PLUS,
		K_0,	K_1000,	K_DOT,	K_EQ,
	};

	/*
	 * Token mapping on Calculator key pad.
	 */
	public final static String[] KeyLabels = new String[] {
		"MwSt",	"CE",	"C",	"<-",
		"M+",	"MR",	"MC",	"/",
		"7",	"8",	"9",	"*",
		"4",	"5",	"6",	"-",
		"1",	"2",	"3",	"+",
		"0",	"1000",	",",	"=",
	};


	/**
	 * Public access method to new Calculator instance;
	 * @return
	 */
	public static CalculatorIntf createInstance() {
		CalculatorIntf calc = new CalculatorLogic();
		return calc;
	}

	/**
	 * Method to inject reference to establish interaction with paired GUI-controller.
	 * @param component paired GUI-controller.
	 */
	public void inject( CalculatorIntf component );

	/**
	 * Method to pass input token from GUI to Calculator logic.
	 * @param token token received as GUI-input
	 */
	public void nextToken( Token token );

	/**
	 * Method to pass output from Calculator logic to the GUI.
	 * @param selector select GUI-field to which output is directed (0: displayTextField,
	 * 					1: sideTextArea).
	 * @param text output text to display.
	 */
	public void write( int selector, String text );

}
