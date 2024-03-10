package com.nicokuchling.wegfest.scene_service.services.factories;

import com.nicokuchling.wegfest.api.core.scene.Scene;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneAggregate;
import com.nicokuchling.wegfest.api.core.scene.services.ServiceAddresses;
import com.nicokuchling.wegfest.api.core.survey.MultipleChoiceQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SceneAggregateFactory {

    public static SceneAggregate from(
            Scene scene,
            Set<MultipleChoiceQuestion> questions) {

        // 1. Setup scene info
        int sceneId = scene.getSceneId();
        String name = scene.getName();
        String description = scene.getDescription();
        Scene.DIFFICULTY difficulty = scene.getDifficulty();
        String serviceAddress = scene.getServiceAddress();

        // 2. Get relevant multiple choice questions
        // TODO: miserable time complexity (n^2)
        List<MultipleChoiceQuestion> relevantQuestions = new ArrayList<>();
        scene.getMultipleChoiceQuestionIds().forEach(questionId -> {
            for(MultipleChoiceQuestion question : questions) {
                if(question.getQuestionId() == questionId) {
                    relevantQuestions.add(question);
                    break;
                }
            }
        });

        // 3. Create info regarding involved microservice addresses
        String surveyAddress = questions.stream().findAny().get().getServiceAddress();
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, surveyAddress);

        return new SceneAggregate(
                sceneId,
                name,
                description,
                difficulty,
                relevantQuestions,
                serviceAddresses);
    }
}
