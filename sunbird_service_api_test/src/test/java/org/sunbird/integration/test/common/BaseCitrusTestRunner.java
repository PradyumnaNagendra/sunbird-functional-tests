package org.sunbird.integration.test.common;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.sunbird.common.action.TestActionUtil;
import org.sunbird.common.util.Constant;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;

public class BaseCitrusTestRunner extends TestNGCitrusTestRunner {

  @Autowired protected TestGlobalProperty config;
  @Autowired protected TestContext testContext;

  public static final String REQUEST_FORM_DATA = "request.params";
  public static final String REQUEST_JSON = "request.json";
  public static final String RESPONSE_JSON = "response.json";

  public static final String LMS_ENDPOINT = "restTestClient";
  public static final String KEYCLOAK_ENDPOINT = "keycloakTestClient";

  public static Map<String, List<String>> deletedRecordsMap = new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteCassandraRecordsMap =
      new HashMap<String, List<String>>();
  public static Map<String, List<String>> toDeleteEsRecordsMap =
      new HashMap<String, List<String>>();

  public BaseCitrusTestRunner() {
    System.out.println("context = " + testContext);
  }

  public String getLmsApiUriPath(String apiGatewayUriPath, String localUriPath) {
    return config.getLmsUrl().contains("localhost") ? localUriPath : apiGatewayUriPath;
  }

  public void performMultipartTest(
      TestNGCitrusTestRunner runner,
      String templateDir,
      String testName,
      String requestUrl,
      String requestFile,
      Map<String, Object> requestHeaders,
      Boolean isAuthRequired,
      HttpStatus responseCode,
      String responseJson) {
    runner.http(
        builder ->
            TestActionUtil.getMultipartRequestTestAction(
                testContext,
                builder,
                LMS_ENDPOINT,
                templateDir,
                testName,
                requestUrl,
                requestFile,
                requestHeaders,
                runner.getClass().getClassLoader(),
                config));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, LMS_ENDPOINT, templateDir, testName, responseCode, responseJson));
  }

  public void performPostTest(
      TestNGCitrusTestRunner runner,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      HttpStatus responseCode,
      String responseJson) {
    runner.http(
        builder ->
            TestActionUtil.getPostRequestTestAction(
                builder,
                LMS_ENDPOINT,
                templateDir,
                testName,
                requestUrl,
                requestJson,
                contentType,
                TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, LMS_ENDPOINT, templateDir, testName, responseCode, responseJson));
    runner.sleep(Constant.ES_SYNC_WAIT_TIME);
  }

  public void performPatchTest(
      TestNGCitrusTestRunner runner,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      HttpStatus responseCode,
      String responseJson) {
    runner.http(builder -> TestActionUtil.getPatchRequestTestAction(
        builder,
        LMS_ENDPOINT,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        contentType,
        TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, LMS_ENDPOINT, templateDir, testName, responseCode, responseJson));
  }

  public void performDeleteTest(
      TestNGCitrusTestRunner runner,
      String templateDir,
      String testName,
      String requestUrl,
      String requestJson,
      String contentType,
      boolean isAuthRequired,
      HttpStatus responseCode,
      String responseJson) {
    runner.http(builder -> TestActionUtil.getDeleteRequestTestAction(
        builder,
        LMS_ENDPOINT,
        templateDir,
        testName,
        requestUrl,
        requestJson,
        contentType,
        TestActionUtil.getHeaders(isAuthRequired)));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(
                builder, LMS_ENDPOINT, templateDir, testName, responseCode, responseJson));
  }

  public void getAuthToken(TestNGCitrusTestRunner runner, Boolean isAuthRequired) {
    if (isAuthRequired) {
      runner.http(builder -> TestActionUtil.getTokenRequestTestAction(builder, KEYCLOAK_ENDPOINT));
      runner.http(builder -> TestActionUtil.getTokenResponseTestAction(builder, KEYCLOAK_ENDPOINT));
    }
  }

  public void performGetTest(
      TestNGCitrusTestRunner runner,
      String templateDir,
      String testName,
      String requestUrl,
      Boolean isAuthRequired,
      HttpStatus responseCode,
      String responseJson) {
    getAuthToken(runner, isAuthRequired);
    runner.http(
        builder ->
            TestActionUtil.performGetTest(
                builder,
                LMS_ENDPOINT,
                testName,
                requestUrl,
                TestActionUtil.getHeaders(isAuthRequired),
                config));
    runner.http(
        builder ->
            TestActionUtil.getResponseTestAction(builder, LMS_ENDPOINT, testName, responseCode));
  }

  public String getLmsApiUriPath(String apiGatewayUriPath, String localUriPath, String pathParam) {
    return config.getLmsUrl().contains("localhost")
        ? localUriPath + pathParam
        : apiGatewayUriPath + pathParam;
  }
}
