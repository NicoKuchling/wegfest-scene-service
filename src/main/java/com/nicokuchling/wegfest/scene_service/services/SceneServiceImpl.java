package com.nicokuchling.wegfest.scene_service.services;

import com.nicokuchling.wegfest.api.core.scene.*;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneAggregate;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneInteractionRecordAggregate;
import com.nicokuchling.wegfest.api.core.scene.services.SceneService;
import com.nicokuchling.wegfest.api.core.survey.MultipleChoiceQuestion;
import com.nicokuchling.wegfest.api.core.survey.SurveyResponse;
import com.nicokuchling.wegfest.api.exceptions.InvalidInputException;
import com.nicokuchling.wegfest.scene_service.services.factories.SceneAggregateFactory;
import com.nicokuchling.wegfest.scene_service.services.factories.SceneInteractionRecordAggregateFactory;
import com.nicokuchling.wegfest.shared.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SceneServiceImpl implements SceneService {

    private static final Logger LOG = LoggerFactory.getLogger(SceneServiceImpl.class);

    private ServiceUtil serviceUtil;

    private SceneIntegration integration;

    @Autowired
    public SceneServiceImpl(ServiceUtil serviceUtil, SceneIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Set<SceneAggregate> getAllSceneAggregates() {
        LOG.debug("/scene return all available scenes");

        Set<MultipleChoiceQuestion> questions = integration.getAllMultipleChoiceQuestions();
        Set<Scene> scenes = getAllScenes();

        Set<SceneAggregate> sceneAggregates = new HashSet<>();
        scenes.forEach(scene -> {
            SceneAggregate sceneAggregate = SceneAggregateFactory.from(scene, questions);
            sceneAggregates.add(sceneAggregate);
        });

        return sceneAggregates;
    }

    private Set<Scene> getAllScenes() {
        Scene scene1 = new Scene(
                1,
                "scene-1",
                "scene-1-description",
                Scene.DIFFICULTY.EASY,
                Arrays.asList(1, 2),
                serviceUtil.getServiceAddress());

        Scene scene2 = new Scene(
                2,
                "scene-2",
                "scene-2-description",
                Scene.DIFFICULTY.INTERMEDIATE,
                Arrays.asList(1,2),
                serviceUtil.getServiceAddress());

        Set<Scene> scenes = new HashSet<>();
        scenes.add(scene1);
        scenes.add(scene2);

        return scenes;
    }

    @Override
    public List<SceneInteractionRecordAggregate> getSceneInteractionRecordAggregatesByIds(
            List<Integer> sceneInteractionRecordIds) {
        LOG.debug("/scene/interaction/record get records for defined IDs: " + sceneInteractionRecordIds);

        List<SceneInteractionRecord> sceneInteractionRecords =
                getSceneInteractionRecordsByIds(sceneInteractionRecordIds);

        Set<SceneAggregate> scenes = getAllSceneAggregates();
        Set<MultipleChoiceQuestion> questions = integration.getAllMultipleChoiceQuestions();

        List<Integer> surveyResponseIds = new ArrayList<>();
        sceneInteractionRecords.forEach(record -> surveyResponseIds.add(record.getSurveyResponseId()));

        Set<SurveyResponse> surveyResponses = integration.getSurveyResponsesByIds(surveyResponseIds);

        List<SceneInteractionRecordAggregate> sceneInteractionRecordAggregates = new ArrayList<>();

        sceneInteractionRecords.forEach(record -> {
            SceneInteractionRecordAggregate aggregate = SceneInteractionRecordAggregateFactory.from(
                    record,
                    scenes,
                    questions,
                    surveyResponses,
                    serviceUtil.getServiceAddress());

            sceneInteractionRecordAggregates.add(aggregate);
        });

        return sceneInteractionRecordAggregates;
    }

    private List<SceneInteractionRecord> getSceneInteractionRecordsByIds(List<Integer> sceneInteractionRecordIds) {
        if(sceneInteractionRecordIds.isEmpty()) {
            throw new InvalidInputException(
                    "Please define one or more values for the query parameter: sceneInteractionRecordId");
        }

        List<SceneInteractionRecord> records = new ArrayList<>();

        SceneInteractionRecord record =
                new SceneInteractionRecord(
                        1,
                        1,
                        "unitySceneId",
                        "playerPosition",
                        SceneInteractionRecord.DAYTIME.DAY,
                        SceneInteractionRecord.SPEEDLIMIT.MEDIUM,
                        SceneInteractionRecord.FREQUENCY.LOW,
                        SceneInteractionRecord.FREQUENCY.MEDIUM,
                        false,
                        false,
                        100,
                        100,
                        1,
                        1,
                        1,
                        1,
                        serviceUtil.getServiceAddress());

        records.add(record);

        if (records.isEmpty()) {
            throw new InvalidInputException("No records found for the given IDs");
        }

        return records;
    }
}
