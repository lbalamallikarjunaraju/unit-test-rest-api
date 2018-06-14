package com.arjun.thirpart.restapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.glassfish.jersey.test.TestProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class ExpressVpnTest extends JerseyTestNg.ContainerPerMethodTest {

	private static final String UNIT_TEST = "unit";

	private static final String THIRD_PARY_REST_URL = "/1.0/third/part/rest-url";

	private static final String DATA_PROVIDER_NAME_CANNOT_CREATE_GATEWAY_THROW_EXCEPTION = "cannotCreateGateway_throwException_dataProvider";

	private static final String TEST_ID = "012356789";
	private static final String TEST_IDENTIFIER = "test.identifier";

	private MyService mockService;

	@BeforeMethod(groups = UNIT_TEST)
	@Override
	public void setUp() throws Exception {
		super.setUp();
		reset(mockService);
	}

	@AfterMethod(groups = UNIT_TEST)
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected Application configure() {
		set(TestProperties.CONTAINER_PORT, 0);

		mockService = mock(MyService.class);
		//Service method mocking can be done here.
		
		ResourceConfig config = new ResourceConfig();
		//Need to register ThirdPartyAPI class if you know.
		//config.register(ThirdPartyAPIClass.class);

		JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider()
				.configure(DeserializationFeature.WRAP_EXCEPTIONS, false);

		config.register(jacksonJsonProvider);

		return config;
	}

	@Test(groups = UNIT_TEST)
	public void test() {
		PropertyNamingStrategy.SnakeCaseStrategy prop = new PropertyNamingStrategy.SnakeCaseStrategy();
		String s = prop.translate("myJsonTranslatorThatAlreadyDefined");
		String sa = prop.translate("myJsonTranslatorThatAlreadyDefined");
		System.out.println(s + "\n" + sa);
	}

	@Test(groups = UNIT_TEST, description = "Verify if create Payload endpoint works.")
	public void canCreateGateway() {
		Payload Payload = generateGateway();
		Response response = target().path(UriBuilder.fromUri(THIRD_PARY_REST_URL).build().toString())
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.json(Payload));
		response.bufferEntity();
		MyExpectedResponseIfAny MyExpectedResponseIfAny = response.readEntity(MyExpectedResponseIfAny.class);
		assertEquals(MyExpectedResponseIfAny.getTestResult(), TEST_IDENTIFIER);
	}

	@DataProvider(name = DATA_PROVIDER_NAME_CANNOT_CREATE_GATEWAY_THROW_EXCEPTION)
	public Object[][] dataProviderCreateCarrierThrowException() {
		return new Object[][] { { "Test for resource not found.", new Exception(""), ErrorCode.RESOURCE_NOT_FOUND } };
	}

	@Test(groups = UNIT_TEST, description = "Verify the error response when exception is thrown during create Payload.", dataProvider = DATA_PROVIDER_NAME_CANNOT_CREATE_GATEWAY_THROW_EXCEPTION)
	public void cannotCreateGateway_throwException(String testcase, Exception exception, ErrorCode expectedErrorCode)
			throws Exception {
		Payload gatewayRequest = generateGateway();
		Response actualResponse = target().path(UriBuilder.fromUri(THIRD_PARY_REST_URL).build().toString())
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(gatewayRequest));
		actualResponse.bufferEntity();
		verifyErrorResponse(actualResponse, expectedErrorCode);
	}

	private Payload generateGateway() {
		Payload Payload = generateGateway();
		Payload.setParam1(TEST_ID);
		Payload.setParam2(String.valueOf(System.currentTimeMillis()));
		return Payload;
	}

	private void verifyErrorResponse(Response response, ErrorCode expectedErrorCode) {
		assertNotNull(response);

		response.bufferEntity();
		ErrorResponse result = response.readEntity(ErrorResponse.class);
		assertEquals(result.getCode(), expectedErrorCode);
	}
}
//Test - End.



/************************************************
 * 
 * Note: For testing purpose I have added below classes.
 *
 *************************************************/

	class MyService {
		public void someMethod() {
		}
	}

	class Payload {
		String param1;
		String param2;

		public String getParam1() {
			return param1;
		}

		public void setParam1(String param1) {
			this.param1 = param1;
		}

		public String getParam2() {
			return param2;
		}

		public void setParam2(String param2) {
			this.param2 = param2;
		}

	}

	enum ErrorCode {
		RESOURCE_NOT_FOUND;
	}

	class MyExpectedResponseIfAny {
		String testResult;

		public String getTestResult() {
			return testResult;
		}

		public void setTestResult(String testResult) {
			this.testResult = testResult;
		}

	}

	class ErrorResponse {
		private int status;
		private ErrorCode code;
		private String message;

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public ErrorCode getCode() {
			return code;
		}

		public void setCode(ErrorCode code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}


