package foundation.privacybydesign.bigregister;

import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class BIGWebSearch {
    private static Client client = ClientBuilder.newClient();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BIGWebSearchEntry {
        public String hcpNumber;

        public BIGWebSearchEntry() {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BIGWebSearchResult {
        public List<BIGWebSearchEntry> hcps;

        public BIGWebSearchResult() {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BIGWebDetailRegistration {
        public String registrationNumber;

        public BIGWebDetailRegistration() {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BIGWebDetailEntry {
        public List<BIGWebDetailRegistration> registrations;

        public BIGWebDetailEntry() {
        }
    }

    private URI constructSearchURI(String name, Date dateOfBirth, String gender) throws BIGRequestException {
        // Build the search query
        StringBuilder queryBuilder = new StringBuilder();
        // Name is always present
        queryBuilder.append("name=");
        try {
            queryBuilder.append(URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new BIGRequestException("Unexpected encoding error: " + e.getMessage());
        }
        // Date if available
        if (dateOfBirth != null) {
            queryBuilder.append("&dateOfBirth=");
            queryBuilder.append(new SimpleDateFormat("dd-MM-yyyy").format(dateOfBirth));
        }
        // Gender if available
        if (gender.equals("male")) {
            queryBuilder.append("&gender=1");
        } else if (gender.equals("female")) {
            queryBuilder.append("&gender=2");
        }

        try {
            return new URI("https", "zoeken.bigregister.nl", "/api/search/criteria", queryBuilder.toString(), null);
        } catch (URISyntaxException e) {
            throw new BIGRequestException("Unexpected URI error: " + e.getMessage());
        }
    }

    private URI constructDetailURI(String hcpNumber) throws BIGRequestException {
        // Build the path
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append("/api/search/criteria/details/");
        pathBuilder.append(hcpNumber);
        try {
            return new URI("https", "zoeken.bigregister.nl", pathBuilder.toString(), null, null);
        } catch (URISyntaxException e) {
            throw new BIGRequestException("Unexpected URI error: " + e.getMessage());
        }
    }

    public String getBIGNumber(String name, Date dateOfBirth, String gender) throws BIGRequestException, BIGWebNoResultsException, BIGWebTooManyException {
        // Construct search URI
        URI searchURI = constructSearchURI(name, dateOfBirth, gender);

        // Contact server
        Response searchResponse = client.target(searchURI).request(MediaType.APPLICATION_JSON).get();

        // Handle abnormal status codes
        if (searchResponse.getStatus() == 400) {
            throw new BIGWebTooManyException();
        } else if (searchResponse.getStatus() == 404) {
            throw new BIGWebNoResultsException();
        } else if (searchResponse.getStatus() != 200) {
            throw new BIGRequestException("Invalid status code " + Integer.toString(searchResponse.getStatus()) + " for BIG search");
        }

        // Extract data
        BIGWebSearchResult searchResult = searchResponse.readEntity(BIGWebSearchResult.class);

        // Handle too many/few results
        if (searchResult.hcps.size() > 1) {
            throw new BIGWebTooManyException();
        } else if (searchResult.hcps.size() == 0) {
            throw new BIGWebNoResultsException();
        }

        // Build the detail query
        URI detailURI = constructDetailURI(searchResult.hcps.get(0).hcpNumber);

        // Contact server
        Response detailResponse = client.target(detailURI).request(MediaType.APPLICATION_JSON).get();

        // Handle abnormal status codes
        if (detailResponse.getStatus() != 200) {
            throw new BIGRequestException("Invalid status code " + Integer.toString(detailResponse.getStatus()) + " for BIG details");
        }

        // Extract data
        BIGWebDetailEntry detailResult = detailResponse.readEntity(BIGWebDetailEntry.class);

        // Handle too few results
        if (detailResult.registrations.size() == 0) {
            throw new BIGWebNoResultsException();
        }

        return detailResult.registrations.get(0).registrationNumber;
    }
}