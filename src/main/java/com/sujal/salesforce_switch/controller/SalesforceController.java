package com.sujal.salesforce_switch.controller;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SalesforceController {

	private final String CLIENT_ID =
			System.getenv("CLIENT_ID");

			private final String CLIENT_SECRET =
			System.getenv("CLIENT_SECRET");

			private final String REDIRECT_URI =
					"https://salesforce-validation-rule-manager-3vyu.onrender.com/callback";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {

        String url = "https://login.salesforce.com/services/oauth2/authorize"
                + "?response_type=token"
                + "&client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI;

        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public String callback() {

        return "callback";
    }
    
    @GetMapping("/rules")
    public ResponseEntity<String> getRules(
            @RequestParam("token") String accessToken,
            @RequestParam("instanceUrl") String instanceUrl) {

        String query =
                "SELECT Id, ValidationName, Active FROM ValidationRule LIMIT 10";

        String url =
                instanceUrl +
                "/services/data/v60.0/tooling/query?q=" +
                query.replace(" ", "+");

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity =
                new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        return ResponseEntity.ok(response.getBody());
    }
    
    @GetMapping("/toggleRule")
    public ResponseEntity<String> toggleRule(
            @RequestParam("id") String ruleId) {

        System.out.println("Toggle requested for Rule ID: " + ruleId);

        return ResponseEntity.ok(
                "Toggle API called successfully for Rule ID: " + ruleId
        );
    }
}