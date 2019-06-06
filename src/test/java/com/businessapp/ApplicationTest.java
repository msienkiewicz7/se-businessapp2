package com.businessapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

	/*
	 * When 'mvn package' runs tests, test runs will execute
	 * @EventListener( ApplicationReadyEvent.class ) public void startup() {...}
	 * and launch the FXGui. In order to prevent that, exclude ApplicationStartup
	 * event listener when testing by creating a mock bean that gets created for
	 * running tests.
	 * https://stackoverflow.com/questions/46597149/exclude-applicationstartup-event-listener-when-testing/46600386
	 */
	@MockBean
	private Application application;

	@Test
	public void contextLoads() {
	}

}
