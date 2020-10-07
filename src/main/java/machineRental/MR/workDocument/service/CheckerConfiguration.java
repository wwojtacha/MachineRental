package machineRental.MR.workDocument.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import machineRental.MR.security.UserDetailsServiceImpl;
import machineRental.MR.security.filter.JwtRequestFilter;
import machineRental.MR.workDocument.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CheckerConfiguration {

  @Bean
  public Map<DocumentType, WorkDocumentUpdateChecker> getWorkDocumentCheckers(WorkReportUpdateChecker workReportUpdateChecker,
      RoadCardUpdateChecker roadCardUpdateChecker) {
    Map<DocumentType, WorkDocumentUpdateChecker> workDocumentCheckers = new HashMap<>();

    workDocumentCheckers.put(DocumentType.WORK_REPORT, workReportUpdateChecker);
    workDocumentCheckers.put(DocumentType.ROAD_CARD, roadCardUpdateChecker);

    return workDocumentCheckers;
  }

}
