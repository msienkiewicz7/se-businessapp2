package com.businessapp.repositories;

import java.util.ArrayList;
import java.util.List;

import com.businessapp.logic.ManagedComponentIntf;
import com.businessapp.model.Article;
import com.businessapp.model.Customer;
import com.businessapp.model.Customer.CustomerStatus;
import com.businessapp.model.Note;


/**
 * Public class that builds an in-memory mock model with a selection
 * of entity instances.
 *
 * @author Sven Graupner
 *
 */
public class RepositoryBuilder implements ManagedComponentIntf {
	private static RepositoryBuilder _singleton = null;

	public static final String Customer = "Customer";
	public static final String Article = "Article";

	private final CustomerRepositoryIntf customerRepository;
	private final ArticleRepositoryIntf articleRepository;

	/**
	 * Private constructor.
	 */
	private RepositoryBuilder() {
		List<Customer> customerList = new ArrayList<Customer>();
		this.customerRepository = new CustomerRepositoryImpl( customerList );

		List<Article> articleList = new ArrayList<Article>();
		this.articleRepository = new ArticleRepositoryImpl( articleList );
	}

	/**
	 * Public access method according to the Singleton pattern.
	 *
	 * @return reference to ModelBuilder singleton instance.
	 */
	public static RepositoryBuilder getInstance() {
		if( _singleton == null ) {
			_singleton = new RepositoryBuilder();
		}
		return _singleton;
	}


	@Override
	public void start() {
		customerRepository.start();
		buildCustomerFixture( customerRepository.findAll() );
		articleRepository.start();
		buildArticleFixture( articleRepository.findAll() );
	}

	@Override
	public void stop() {
		articleRepository.stop();
		customerRepository.stop();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	public CustomerRepositoryIntf getCustomerRepository() {
		return customerRepository;
	}

	public ArticleRepositoryIntf getArticleRepository() {
		return articleRepository;
	}

	/**
	 * Method to create a set of Customer entities.
	 * @param list container into which entities are inserted.
	 */
	public List<Customer> buildCustomerFixture( List<Customer> list ) {
		Customer c = new Customer( "Jens", "Baumann" );
		c.getContacts().add( "eme@yahoo.com" );
		c.getContacts().add( "meyer244@gmail.com" );
		c.getContacts().add( "+49170482395" );
		list.add( c );

		c = new Customer( "Anne", "Meyer" );
		list.add( c );

		c = new Customer( "Jacob", "Schneider" );
		c.getContacts().add( "jacob.schneider@example.com" );
		list.add( c );

		c = new Customer( "Isabella", "Johnson" );
		c.getContacts().add( "isabella.johnson@example.com" );
		c.setStatus( CustomerStatus.SUSPENDED );
		list.add( c );

		c = new Customer( "Ethan", "Williams" );
		c.getContacts().add( "ethan.williams@example.com" );
		list.add( c );

		c = new Customer( "Emma", "Jones" );
		c.getContacts().add( "emma.jones@example.com" );
		list.add( c );

		c = new Customer( "Michael", "Brown" );
		c.getContacts().add( "michael.brown@example.com" );
		list.add( c );

		//UEBUNG E

		//new customer
		c = new Customer( "Pierre", "Faparius" );
		c.getContacts().add( "p.fap@tuxedo.org" );
		c.setStatus( CustomerStatus.SUSPENDED );
		list.add( c );

		//delete customer "Jacob Schneider"
		// Customer cus = null;
		// for(Customer cus2 : list){
		// 	if (cus2.getFullName().equals("Jacob Schneider")) {
		// 		cus = cus2;
		// 		break;
		// 	}
		// }
		// if (cus != null) {
		// 	list.remove(cus);
		//
		// }

		// update customer "Emma Jones"
		for(Customer cus : list){
			if (cus.getFullName().equals("Emma Jones")) {
				cus.setStatus( CustomerStatus.SUSPENDED );
			}
		}


		// new customer Frau Dr Mararethe Böse
		c = new Customer( "Dr. Mararethe", "Böse" );
		c.getContacts().add( "drmb@yahoo.de" );
		c.getContacts().add( "030 826 5204" );
		c.setStatus( CustomerStatus.SUSPENDED );
		c.getNotes().add(new Note( "Zahlt Rechnung verspätet" ));
		c.getNotes().add(new Note( "Beschwert sich über Mitarbeiter" ));
		c.getNotes().add(new Note( "Greift Angestellte verbal an" ));
		c.getNotes().add(new Note( "Wurde aus dem Geschäft verwiesen" ));
		list.add( c );




		return list;
	}


	/**
	 * Method to create a set of Article entities.
	 * @param list container into which entities are inserted.
	 */
	public List<Article> buildArticleFixture( List<Article> list ) {
		list.add( new Article( "Canon Objektiv EF 50mm f/1.2L USM", 154900 ) );
		list.add( new Article( "Canon Objektiv EF 50mm f/1.4 USM", 44900 ) );

		list.add( new Article( "Canon Objektiv EF 40mm f/2.8 STM", 23900 ) );
		list.add( new Article( "Canon Objektiv EF 50mm f/1.8 STM", 13900 ) );
		list.add( new Article( "Canon Objektiv EF 24-70mm f/4L IS USM", 92900 ) );
		// list.add( new Article( "Canon Objektiv EF 24-105mm f/4L IS II USM", "1.199,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 24-70mm f/2.8L II USM", "2.019,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-55mm f/4-5.6 IS STM", "249,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-55mm f/3.5-5.6 IS II", "199,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-55mm f/3.5-5.6 IS STM", "249,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 24-105mm f/3.5-5.6 IS STM", "479,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS STM + EW 73B + LC Kit", "499,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-200mm f/3.5-5.6 IS ", "585,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS STM", "499,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 15-85mm f/3.5-5.6 IS USM", "799,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS USM", "549,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 17-55mm f/2.8 IS USM", "919,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 400mm f/4 DO IS II USM", "7.029,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 800mm f/5.6L IS USM ", "14.149,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 300mm f/4L IS USM", "1.469,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 200mm f/2.8L II USM ", "829,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 400mm f/5.6L USM", "1.449,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 135mm f/2L USM ", "1.109,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 85mm f/1.8 USM", "479,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 100mm f/2 USM ", "529,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 200mm f/2L IS USM", "6.309,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 85mm f/1.2L II USM", "2.239,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 300mm f/2.8L IS II USM", "6.499,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 400mm f/2.8L IS II USM", "11.019,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 85mm f/1.4L IS USM", "1.599,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 500mm f/4L IS II USM", "9.979,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 600mm f/4L IS II USM", "12.639,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-300mm f/4-5.6 IS II USM", "539,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-200mm f/4L IS USM", "1.409,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 28-300mm f/3.5-5.6L IS USM", "2.659,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 100-400mm f/4.5-5.6L IS II USM ", "2.379,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 75-300mm f/4-5.6 III USM", "369,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 75-300mm f/4-5.6 III", "299,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-300mm f/4-5.6L IS USM", "1.429,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 55-250mm f/4-5.6 IS STM + ET 63 + LC Kit", "349,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF-S 55-250mm f/4-5.6 IS STM", "349,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-200mm f/2.8L IS II USM", "2.299,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-200mm f/4L USM", "689,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 70-200mm f/2.8L USM", "1.579,00 EUR" ) );
		// list.add( new Article( "Canon Objektiv EF 200-400mm f/4L IS USM + Extender 1.4x", "11.699,00 EUR" ) );

		return list;
	}

}
