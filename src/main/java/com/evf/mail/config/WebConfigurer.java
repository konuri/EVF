package com.evf.mail.config;

import static java.net.URLDecoder.decode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.evf.mail.domain.ShortNameAndTipDetails;
import com.evf.mail.repository.ShortNameAndTipDetailsRepository;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.JHipsterProperties;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {
    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

	@Value("${key.name}")
	String userName;
	
	@Value("${key.value}")
	String password;
	
    @Autowired
    ShortNameAndTipDetailsRepository shortNameAndTipDetailsRepository;
    
    private final JHipsterProperties jHipsterProperties;

    public WebConfigurer(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }

        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
          //  initH2Console(servletContext);
        }
        log.info("Web application fully configured");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(WebServerFactory server) {
        setMimeMappings(server);
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(server);
    }

    private void setMimeMappings(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory) {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
            mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
            mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            servletWebServer.setMimeMappings(mappings);
        }
    }

    private void setLocationForStaticAssets(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory) {
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            File root;
            String prefixPath = resolvePathPrefix();
            root = new File(prefixPath + "target/classes/static/");
            if (root.exists() && root.isDirectory()) {
                servletWebServer.setDocumentRoot(root);
            }
        }
    }

    /**
     * Resolve path prefix to static resources.
     */
    private String resolvePathPrefix() {
        String fullExecutablePath;
        try {
            fullExecutablePath = decode(this.getClass().getResource("").getPath(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            /* try without decoding if this ever happens */
            fullExecutablePath = this.getClass().getResource("").getPath();
        }
        String rootPath = Paths.get(".").toUri().normalize().getPath();
        String extractedPath = fullExecutablePath.replace(rootPath, "");
        int extractionEndIndex = extractedPath.indexOf("target/");
        if (extractionEndIndex <= 0) {
            return "";
        }
        return extractedPath.substring(0, extractionEndIndex);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = jHipsterProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
        }
        return new CorsFilter(source);
    }
    
    @Bean("areaShortNames")
    public Map<String,ShortNameAndTipDetails> getAreaAndTipDetails(){
    	List<ShortNameAndTipDetails> shortNameAndTipDetails= shortNameAndTipDetailsRepository.findAll();
    	Map<String, ShortNameAndTipDetails> result =
    			shortNameAndTipDetails.stream().collect(Collectors.toMap(ShortNameAndTipDetails::getZipcode,
    		                                              Function.identity()));
    	return result;
    }

    @Bean("webDriverConnection")
    public ChromeDriver webdrive(){
    	try{
    	Resource   resource = new ClassPathResource("chromedriver.exe");
		System.setProperty("webdriver.chrome.driver", resource.getFile().getPath());
		ChromeDriver driver=new ChromeDriver();
		driver.navigate().to("https://identity.doordash.com/auth?scope=*&response_type=code&redirect_uri=https%3A%2F%2Fwww.doordash.com%2Fdrive%2Fportal%2Fauth&state=%2Fdrive%2Fportal&prompt=none&client_id=1649316525849964797");  
		waitForSeconds(10);
	    // Click on the search text box and send value  
	    driver.findElement(By.id("FieldWrapper-0")).sendKeys(userName);
	    driver.findElement(By.id("FieldWrapper-1")).sendKeys(password); 
	    driver.findElement(By.id("login-submit-button")).click();
	    return  driver;
    	}catch(Exception e) {
    		return null;
    	}
    }
    
	public static void waitForSeconds(long sec) throws InterruptedException{
		Thread.sleep(sec *1000);
	}
    /**
     * Initializes H2 console.
     */
/*    private void initH2Console(ServletContext servletContext) {
        log.debug("Initialize H2 console");
        H2ConfigurationHelper.initH2Console(servletContext);
    }*/
    
    
}
