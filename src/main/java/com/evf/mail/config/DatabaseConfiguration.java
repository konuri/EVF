package com.evf.mail.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("com.evf.mail.repository")
//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {
    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final Environment env;
    
  

    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

  /*  @Bean("areaShortNames")
    public Map<String,ShortNameAndTipDetails> getAreaAndTipDetails(){
    	 Map<String,ShortNameAndTipDetails> map=new HashMap<>();
    	List<ShortNameAndTipDetails> shortNameAndTipDetails= shortNameAndTipDetailsRepository.findAll();
    	Map<String, ShortNameAndTipDetails> result =
    			shortNameAndTipDetails.stream().collect(Collectors.toMap(ShortNameAndTipDetails::getZipcode,
    		                                              Function.identity()));
    	System.out.println(result);
    	return map;
    }*/
    /**
     * Open the TCP port for the H2 database, so it is available remotely.
     *
     * @return the H2 database TCP server.
     * @throws SQLException if the server failed to start.
     */
   /* @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public Object h2TCPServer() throws SQLException {
        String port = getValidPortForH2();
        log.debug("H2 database is available on port {}", port);
        return H2ConfigurationHelper.createServer(port);
    }

    private String getValidPortForH2() {
        int port = Integer.parseInt(env.getProperty("server.port"));
        if (port < 10000) {
            port = 10000 + port;
        } else {
            if (port < 63536) {
                port = port + 2000;
            } else {
                port = port - 2000;
            }
        }
        return String.valueOf(port);
    }*/
}
