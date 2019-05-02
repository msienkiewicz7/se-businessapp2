package com.businessapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

	public static void main( String[] args ) {
		// System.err.println( "\nHello, latest version of SpringApplication!\n" );
		System.out.println("\nHello, Michal Sienkiewicz!\n");
		SpringApplication.run( Application.class, args );
		System.out.println("\nBye, Michal Sienkiewicz!\n");
		// System.err.println( "\nBye, SpringApplication!\n" );


	}

}
