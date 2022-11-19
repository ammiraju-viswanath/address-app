package com.interview;

import java.net.URI;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.interview.model.Address;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = AddressAppApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AddressApiTest {

	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemp = new TestRestTemplate();


	private final HttpHeaders headers = new HttpHeaders();

	private final Address address1 = Address.builder().line1("address1-1").line2("address2-1").line3("address3-1")
			.line4("address4-1").postcode("postcode-1").build();
	private final Address address2 = Address.builder().line2("address2-2").line3("address2-3")
			.line4("address2-4").postcode("postcode2").build();

	public URI loaduri(String uri) {
		return URI.create(String.format("http://localhost:%s%s", port, uri));
	}

	@BeforeEach
	public void loadUser() {
		headers.setContentType(MediaType.APPLICATION_JSON);

	}

	@Test
	@Order(14)
	public void when_invalid_data_format_shouldFail() {

		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses"), HttpMethod.POST,
				new HttpEntity<>(address2, headers), Object.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), reply.getStatusCode().value());

	}
	@Test
	@Order(13)
	public void when_invalid_format_shouldFail() {

		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses/as"), HttpMethod.GET,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), reply.getStatusCode().value());

	}

	@Test
	@Order(12)
	public void when_invalid_Id_shouldFail() {

		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses/10000"), HttpMethod.GET,
				new HttpEntity<>(address1, headers), Object.class);
		final var id = "10000";
		final var expected =String.format("Address with ID as %s  not found", id);

		Assert.assertTrue(reply.getBody().toString().contains(expected));


	}



	@Test
	@Order(15)
	public void when_invalid_method_call_shouldFail() {
		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses"), HttpMethod.DELETE,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), reply.getStatusCode().value());


	}




	@Order(15)
	public void when_invalid_uri_format_shouldFail() {

		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/incorrecturi"), HttpMethod.POST,
				new HttpEntity<>(address2, headers), Object.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), reply.getStatusCode().value());

	}




	@Test
	@Order(1)
	public void whenValidUser_create() {
		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses"), HttpMethod.POST,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.CREATED.value(), reply.getStatusCode().value());
		Assert.assertNotNull(reply.getHeaders().getLocation());



	}

	@Test
	@Order(5)
	public void whenValidUser_deleteUsers() {

		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses/1"), HttpMethod.DELETE,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), reply.getStatusCode().value());



	}

	@Test
	@Order(3)
	public void whenValidUser_retriveAllUsers() {
		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses"), HttpMethod.GET,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());


	}

	@Test
	@Order(6)
	public void whenValidUser_retriveAllUsersWithFilterAndVersion() {


		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/v1/addresses"), HttpMethod.GET,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());



	}

	@Test
	@Order(2)
	public void whenValidUser_retriveOneUsers() {
		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses/1"), HttpMethod.GET,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.OK.value(), reply.getStatusCode().value());



	}

	@Test
	@Order(4)
	public void whenValidUser_updateUsers() {

		address1.setLine1("updated");


		final ResponseEntity<Object> reply = restTemp.exchange(loaduri("/addresses/1"), HttpMethod.PUT,
				new HttpEntity<>(address1, headers), Object.class);
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), reply.getStatusCode().value());



	}
}
