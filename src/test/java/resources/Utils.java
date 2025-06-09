package resources;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class Utils {
    RequestSpecification reqSpec;
    public RequestSpecification requestSpecification() throws IOException {
        if(reqSpec==null) {

            PrintStream log = new PrintStream(new FileOutputStream("logging.txt"));
            reqSpec = new RequestSpecBuilder().setBaseUri(getGlobalValue("BaseUrl"))
                    .addFilter(RequestLoggingFilter.logRequestTo(log))
                    .addFilter(ResponseLoggingFilter.logResponseTo(log))
                    .setContentType(ContentType.JSON).build();
            return reqSpec;
        }
        else return reqSpec;
    }

    public String getGlobalValue(String key) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/test/java/resources/global.properties");
        prop.load(fis);
        return prop.getProperty(key);
    }
}
