package com.nicokuchling.wegfest.scene_service.services.factories;

import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneAggregate;
import com.nicokuchling.wegfest.api.core.scene.SceneInteractionRecord;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneInteractionRecordAggregate;
import com.nicokuchling.wegfest.api.core.scene.services.ServiceAddresses;
import com.nicokuchling.wegfest.api.core.survey.MultipleChoiceQuestion;
import com.nicokuchling.wegfest.api.core.survey.SurveyResponse;

import java.util.Set;

public class SceneInteractionRecordAggregateFactory {
    public static SceneInteractionRecordAggregate from(
            SceneInteractionRecord record,
            Set<SceneAggregate> scenes,
            Set<MultipleChoiceQuestion> questions,
            Set<SurveyResponse> surveyResponses,
            String serviceAddress) {

        // 1. Setup Scene interaction record info
        int sceneInteractionRecordId = record.getSceneInteractionRecordId();
        String unitySceneSetupId = record.getUnitySceneSetupId();
        String playerPosition = record.getPlayerPosition();
        SceneInteractionRecord.DAYTIME daytime = record.getDaytime();
        SceneInteractionRecord.SPEEDLIMIT speedLimit = record.getSpeedLimit();
        SceneInteractionRecord.FREQUENCY eMobilityFrequency = record.geteMobilityFrequency();
        SceneInteractionRecord.FREQUENCY trafficVolume = record.getTrafficVolume();
        boolean hasCyclists = record.hasCyclists();
        boolean hasTrafficLights = record.hasTrafficLights();
        int timeNeededForOrientation = record.getTimeNeededForOrientation();
        int timeNeededForRoadCrossing = record.getTimeNeededForRoadCrossing();
        int numberOfDangerousSituations = record.getNumberOfDangerousSituations();
        int numberOfAccidents = record.getNumberOfAccidents();
        int numberOfCrossingAttempts = record.getNumberOfCrossingAttempts();

        // 2. Get correct scene object
        SceneAggregate scene = scenes
                .stream()
                .filter(s -> s.getSceneId() == record.getSceneId())
                .findFirst()
                .get();

        // 3. Get correct survey response object
        SurveyResponse surveyResponse = surveyResponses
                .stream()
                .filter(response -> response.getSurveyResponseId() == record.getSurveyResponseId())
                .findFirst()
                .get();

        // 4. Create info regarding the involved microservice addresses
        String sceneAddress = serviceAddress;
        String surveyAddress = surveyResponse.getServiceAddress();
        ServiceAddresses serviceAddresses = new ServiceAddresses(sceneAddress, surveyAddress);

        return new SceneInteractionRecordAggregate(
                sceneInteractionRecordId,
                scene,
                unitySceneSetupId,
                playerPosition,
                daytime,
                speedLimit,
                eMobilityFrequency,
                trafficVolume,
                hasCyclists,
                hasTrafficLights,
                timeNeededForOrientation,
                timeNeededForRoadCrossing,
                numberOfDangerousSituations,
                numberOfAccidents,
                numberOfCrossingAttempts,
                surveyResponse,
                serviceAddresses);
    };
}
