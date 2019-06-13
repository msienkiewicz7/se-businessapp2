package com.businessapp.repositories;

import java.util.*;

import com.businessapp.logic.ManagedComponentIntf;
import com.businessapp.model.Article;
import com.businessapp.model.Customer;
import com.businessapp.model.EntityIntf;
import com.businessapp.model.Note;
import com.businessapp.model.Customer.CustomerStatus;
import com.businessapp.model.Note;
import com.businessapp.model.Reservation;
import com.businessapp.persistence.PersistenceProviderIntf;
import com.businessapp.persistence.PersistenceProviderFactory;
import com.businessapp.persistence.PersistenceProviderFactory.PersistenceSelector;


/**
 * Public class that builds an in-memory mock model with a selection
 * of entity instances.
 *
 * @author Sven Graupner
 *
 */
public class RepositoryBuilder implements ManagedComponentIntf {
	private static RepositoryBuilder _singleton = getInstance();

//	private final CustomerRepositoryIntf customerRepository;
//	private final ArticleRepositoryIntf articleRepository;
//	private final ReservationRepositoryIntf reservationRepository;

	public static final String DataPath	= "data/";

	/*
	 * List of repository names.
	 */
	public static final String Customer	= "Customer";
	public static final String Article	= "Article";
	public static final String Reservation = "Reservation";

	/*
	 * List of repository configurations.
	 */
	private final List<RepositoryConfiguration> repoConfigList;

	/*
	 * Map of repository instances.
	 */
	private final HashMap<String,RepositoryIntf<?>> repoMap;

	/**
	 * Private constructor as part of singleton pattern that initializes
	 * repository configurations and an empty repository map.
	 */
	private RepositoryBuilder() {

		// List<Article> articleList = new ArrayList<Article>();
		// this.articleRepository = new ArticleRepositoryImpl( articleList );
		//
		// List<Reservation> reservationList = new ArrayList<Reservation>();
		// this.reservationRepository = new ReservationRepositoryImpl( reservationList );

		repoConfigList = Arrays.asList(

			new RepositoryConfiguration(
				Customer,
				PersistenceSelector.Default,
				this::buildCustomerFixture
			),

			new RepositoryConfiguration(
				Article,
				PersistenceSelector.Default,
				this::buildArticleFixture
			),

			new RepositoryConfiguration(
				Reservation,
				PersistenceSelector.Default,
				this::buildReservationFixture
			)
		);

		repoMap = new HashMap<String,RepositoryIntf<?>>();

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


	/**
	 * Start RepositoryBuilder.
	 * Creates all configured repository instances.
	 */
	@Override
	public void start() {

		// customerRepository.start();
		// buildCustomerFixture( customerRepository.findAll() );
		//
		// articleRepository.start();
		// buildArticleFixture( articleRepository.findAll() );
		//
		// reservationRepository.start();
		// buildReservationListFixture( reservationRepository.findAll() );
		// }
		//
		//
		// @Override
		// public void stop() {
		// articleRepository.stop();
		// customerRepository.stop();
		// reservationRepository.stop();
		for( RepositoryConfiguration repoConfig : repoConfigList ) {

			switch( repoConfig.getName() ) {

			case Customer:
				configure( repoConfig, new CustomerRepositoryImpl( new ArrayList<Customer>() ), Customer.class );
				break;

			case Article:
				configure( repoConfig, new ArticleRepositoryImpl( new ArrayList<Article>() ), Article.class );
				break;

			case Reservation:
				configure( repoConfig, new ReservationRepositoryImpl( new ArrayList<Reservation>() ), Reservation.class );
				break;
			}
		}
	}


	/*
	 * Private method that configures a new repository instance.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	private void configure( RepositoryConfiguration repoConfig, RepositoryIntf<?> repository, Class<? extends EntityIntf> clazz ) {

		repoMap.put( repoConfig.getName(), repository );

		String path = DataPath + repoConfig.getName();
		PersistenceProviderIntf provider = PersistenceProviderFactory.getInstance(
			repoConfig.getSelector(),
			path,
			clazz
		);
		repository.findAll().clear();

		provider.readAll( e -> {
			((RepositoryIntf)repository).update( e, true );
		});

		repository.inject( provider );

		if( repository.findAll().size() <= 0 ) {

			repoConfig.buildFixture( repository );

			provider.updateAll( (List<? extends EntityIntf>)repository.findAll() );
		}

		repository.start();
	}


	/**
	 * Stop RepositoryBuilder.
	 * Stops all repository instances.
	 */
	@Override
	public void stop() {
		for( RepositoryConfiguration repoConfig : repoConfigList ) {
			RepositoryIntf<?> repo = repoMap.get( repoConfig.getName() );
			if( repo != null ) {
				repo.stop();
			}
		}
	}


	/**
	 * Getter for RepositoryBuilder name.
	 * @return RepositoryBuilder name
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


	/**
	 * Getter for CustomerRepository.
	 * @return CustomerRepository instance
	 */
	public CustomerRepositoryIntf getCustomerRepository() {
		return (CustomerRepositoryIntf)repoMap.get( Customer );
	}

	/**
	 * Getter for ArticleRepository.
	 * @return ArticleRepository instance
	 */
	public ArticleRepositoryIntf getArticleRepository() {
		return (ArticleRepositoryIntf)repoMap.get( Article );
	}

	/**
	 * Getter for ReservationRepository
	 * @return ReservationRepository instance
	 */
	public ReservationRepositoryIntf getReservationRepository() { return (ReservationRepositoryIntf)repoMap.get( Reservation ); }

	/*
	 * Private methods.
	 */

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
		list.add( new Article("Makita Akku-Bohrschrauber 18V / 5,0 Ah", "Akku-Bohrschrauber", 149.99 ) );

		list.add( new Article("Makita DUC353Z Akku-Kettensäge 2x18V / 35 cm","Akku-Kettensäge",  189.99));

		list.add( new Article("Makita HP2051FJ Schlagbohrmaschine 720 W mit LED","Schlagbohrmaschine",  179.99));

		list.add( new Article("Makita 9558HNRG Winkelschleifer 125 mm 840 W","Winkelschleifer",  99.99));

		list.add( new Article("Makita BO3711 Schwingschleifer 93 x 228 mm","Schwingschleifer",  159.99));

		list.add( new Article("Makita DLM380Z Akku-Rasenmäher 2 x 18 V","Elektro-Rasenmäher",  349.99));

		list.add( new Article("Makita DVC860LZ Akku-Staubsauger 2x18V","Gebläse/Sauger",  499.99));

		list.add( new Article("Makita P-90532 Werkzeug-Set 227-teilig 8 x 160 mm ","Werkzeugset 227tl",  199.99));

		return list;
	}

	private List<Reservation> buildReservationFixture(List<Reservation> list) {

//		repoConfigList;
		List<Customer> cList = getCustomerRepository().findAll();
		List<Article> aList = getArticleRepository().findAll();
//
		Reservation r = new Reservation();
		r.setDate(new GregorianCalendar(Locale.GERMANY).getTime());
//		r.setDate(new Date(2019, 1, 1, 12, 0));
		r.setCustomerId(cList.get(0).getId());
		r.setStatus(com.businessapp.model.Reservation.ReservationStatus.ACTIVE);
		r.setArticle(aList.get(0).getId());
		list.add(r);


		return list;
	}
}
