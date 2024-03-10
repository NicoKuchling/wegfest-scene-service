package com.nicokuchling.wegfest.scene_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicokuchling.wegfest.api.core.scene.Scene;
import com.nicokuchling.wegfest.api.core.scene.SceneInteractionRecord;
import com.nicokuchling.wegfest.api.core.survey.MultipleChoiceQuestion;
import com.nicokuchling.wegfest.api.core.survey.SurveyResponse;
import com.nicokuchling.wegfest.api.core.survey.SurveyService;
import com.nicokuchling.wegfest.api.exceptions.InvalidInputException;
import com.nicokuchling.wegfest.api.exceptions.NotFoundException;
import com.nicokuchling.wegfest.shared.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class SceneIntegration implements SurveyService {
    private static final Logger LOG = LoggerFactory.getLogger(SceneIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String surveyServiceUrl;

    @Autowired
    public SceneIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${spring.webflux.base-path}") String basePath,
            @Value("${app.survey-service.host}") String surveyServiceHost,
            @Value("${app.survey-service.port}") int surveyServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.surveyServiceUrl =
                "http://" + surveyServiceHost + ":" + surveyServicePort + basePath + "/survey";
    }

    @Override
    public Set<MultipleChoiceQuestion> getAllMultipleChoiceQuestions() {
        try {
            String url = surveyServiceUrl + "/multiple-choice-question";
            LOG.debug("Will call survey API on URL: {}", url);
            ResponseEntity<Set<MultipleChoiceQuestion>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

            Set<MultipleChoiceQuestion> questions = response.getBody();
            LOG.debug("Found {} question objects", questions.size());

            return questions;

        } catch (HttpClientErrorException ex) {

            LOG.warn("Unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            throw ex;
        }
    }

    @Override
    public Set<SurveyResponse> getSurveyResponsesByIds(List<Integer> surveyResponseIds) {
        try {
            StringBuilder sb = new StringBuilder("/response?");

            for(int i = 0; i < surveyResponseIds.size(); i++) {
                sb.append("surveyResponseId=");
                sb.append(surveyResponseIds.get(i));

                if(i < surveyResponseIds.size() - 1)
                    sb.append("&");
            }

            String url = surveyServiceUrl + sb.toString();
            LOG.debug("Will call survey API on URL: {}", url);

            ResponseEntity<Set<SurveyResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

            Set<SurveyResponse> surveyResponses = response.getBody();
            LOG.debug("Found {} survey response objects", surveyResponses.size());

            return surveyResponses;

        } catch (HttpClientErrorException ex) {

            switch (HttpStatus.resolve(ex.getStatusCode().value())) {
                case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
                case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
                default -> {
                    LOG.warn("Unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
                }
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioException) {
            return ioException.getMessage();
        }
    }
}
