package com.businessapp.logic;


/**
 * Private (incomplete) implementation of CalculatorIntf.
 * 
 * @author Sven Graupner
 *
 */
class CalculatorLogic implements CalculatorIntf {
	private StringBuffer dsb = new StringBuffer();
	private final double VAT_RATE = 19.0;
	private CalculatorIntf calcGui = null;

	private enum Op { NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE }
	Op triggerOp = Op.NOOP;


	@Override
	public void start() { }

	@Override
	public void stop() { }

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void inject( CalculatorIntf component ) {
		calcGui = component;
	}

	@Override
	public void write( int selector, String text ) {
		if( calcGui != null ) {
			calcGui.write( selector, text );
		} else {
			//TODO: log
			System.out.println( text );
		}
	}

	@Override
	public void nextToken( Token tok ) {
		String entry = dsb.toString();
		try {
			switch( tok ) {
			case K_0:	appendBuffer( "0" ); break;
			case K_1:	appendBuffer( "1" ); break;
			case K_2:	appendBuffer( "2" ); break;
			case K_3:	appendBuffer( "3" ); break;
			case K_4:	appendBuffer( "4" ); break;
			case K_5:	appendBuffer( "5" ); break;
			case K_6:	appendBuffer( "6" ); break;
			case K_7:	appendBuffer( "7" ); break;
			case K_8:	appendBuffer( "8" ); break;
			case K_9:	appendBuffer( "9" );
				break;

			case K_1000:appendBuffer( "000" );
				break;

			case K_DIV:
				if(entry.matches(".*[*/+-]")) {break;};
				calculateExpression(entry);
				appendBuffer( "/" );
				triggerOp = Op.DIVIDE; break;

			case K_MUL:
				if(entry.matches(".*[*/+-]")) {break;};
				calculateExpression(entry);
				appendBuffer( "*" );
				triggerOp = Op.MULTIPLY; break;

			case K_PLUS:
				if(entry.matches(".*[*/+-]")) {break;};
				calculateExpression(entry);
				appendBuffer( "+" );
				triggerOp = Op.ADD; break;

			case K_MIN:
				if(entry.matches(".*[*/+-]")) {break;};
				calculateExpression(entry);
				appendBuffer( "-" );
				triggerOp = Op.SUBTRACT; break;

			case K_EQ:
				if(entry.matches(".*[*/+-]")) {break;};
				calculateExpression(entry);
				break;

			case K_VAT:
				double brutto;
				double mwst;
				double netto;
				try {
					brutto = Double.valueOf(dsb.toString());
					netto = brutto / (1 + VAT_RATE/100);
					mwst = brutto - netto;

				} catch (NumberFormatException e) {
					break;
				}

				calcGui.write(1,
						String.format("Brutto: %,.2f%n"
								+ "%.1f%% MwSt:%n"
								+ "%.2f%n"
								+ "Netto: %.2f", brutto, VAT_RATE, mwst, netto)
				);
				break;

			case K_DOT:	appendBuffer( "." );
				if(entry.matches(".*[\\p{Punct}]")) {break;};
				break;

			case K_BACK:
				dsb.setLength( Math.max( 0, dsb.length() - 1 ) );
				break;

			case K_C:
				calcGui.write( 1, "" );
			case K_CE:
				dsb.delete( 0,  dsb.length() );
				break;



//			case K_DIV:
//				throw new ArithmeticException( "ERR: div by zero" );
//			case K_MUL:	appendBuffer( "*" ); break;
//			case K_PLUS:appendBuffer( "+" ); break;
//			case K_MIN:	appendBuffer( "-" ); break;
//			case K_EQ:	appendBuffer( "=" ); break;
//
//			case K_VAT:
//				write(
//					"Brutto:  1,000.00\n" +
//					VAT_RATE + "% MwSt:  159.66\n" +
//					"Netto:  840.34"
//				);
//				break;
//
//			case K_DOT:	appendBuffer( "." );
//				break;
//
//			case K_BACK:
//				dsb.setLength( Math.max( 0, dsb.length() - 1 ) );
//				break;
//
//			case K_C:
//				write( "" );
//			case K_CE:
//				dsb.delete( 0,  dsb.length() );
//				break;

			default:
			}
			String display = dsb.length()==0? "0" : dsb.toString();
			writeDisplay( display );

		} catch( ArithmeticException e ) {
			writeDisplay( e.getMessage() );
		}
	}

	/*MS
	 * Own private methods
	 */

	/*	Die Methode funktioriert nur wenn entry folgens aufgebaut ist: <zahl><operator><zahl>
	 * 	Die berechnung passiert in der methode getResult.
	 * 	Anschliesend nimmt die methode den result, entcheidet ob es sich um eine Dezimalzahl oder Fixpunktzahl handelt
	 * 	und fugt diesen Ergebnis dem Buffer hinzu.
	 *
	 */
	private void calculateExpression(String entry) {
		if(getStringNumbers(entry).length != 1) {
			Double result = getResult(entry);
			dsb.delete( 0,  dsb.length() );
			if(result == Math.floor(result)) {
				appendBuffer( Integer.toString(result.intValue()));
			} else {
				appendBuffer( result.toString() );
			}
		}
	}


	/*	Methode nimmt einen entry der aus zwei zahlen und einen operator besteht.
	 *
	 *
	 */
	private double getResult(String entry) {
		double result = 0;
		String[] numbers = getStringNumbers(entry);
		double n1 = Double.valueOf(numbers[0]);
		double n2 = Double.valueOf(numbers[1]);
		switch(triggerOp) {
			case ADD:		result = n1 + n2;
				break;
			case DIVIDE:
				if(n2 == 0) {
					throw new ArithmeticException( "ERR: div by zero" );
				}
				result = n1 / n2;
				break;
			case MULTIPLY:	result = n1 * n2;
				break;
			case NOOP:
				break;
			case SUBTRACT:	result = n1 - n2;
				break;
			default:
				break;

		}
		return result;
	}

	/* Aus einem entry wird ein array gebildet der deren Zahlen besteht.
	 *
	 */
	private String[] getStringNumbers(String entry) {
		return entry.split("[*/+-]");
	}


	/*
	 * Private method(s).
	 */

	private void appendBuffer( String d ) {
		if( dsb.length() <= DISPLAY_MAXDIGITS ) {
			dsb.append( d );
		}
	}

	private void writeDisplay( String text ) {
		write( 0, text );
	}

	private void write( String text ) {
		write( 1, text );
	}

}
