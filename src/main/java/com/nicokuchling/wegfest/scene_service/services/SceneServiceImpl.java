package com.nicokuchling.wegfest.scene_service.services;

import com.nicokuchling.wegfest.api.core.scene.Scene;
import com.nicokuchling.wegfest.api.core.scene.SceneInteractionRecord;
import com.nicokuchling.wegfest.api.core.scene.SceneService;
import com.nicokuchling.wegfest.api.exceptions.InvalidInputException;
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

    @Autowired
    public SceneServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Set<Scene> getAllScenes() {
        LOG.debug("/scene return all available scenes");

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
    public List<SceneInteractionRecord> getSceneInteractionRecordsByIds(List<Integer> sceneInteractionRecordId) {
        LOG.debug("/scene/interaction/record get records for defined IDs: " + sceneInteractionRecordId);

        if(sceneInteractionRecordId.isEmpty()) {
            throw new InvalidInputException(
                    "Please define one or more values for the query parameter: sceneInteractionRecordId");
        }

        List<SceneInteractionRecord> records = new ArrayList<>();

        for (Integer id : sceneInteractionRecordId) {
            SceneInteractionRecord record = new SceneInteractionRecord();
            record.setSceneInteractionRecordId(id);
            records.add(record);
        }

        if (records.isEmpty()) {
            throw new InvalidInputException("No records found for the given IDs");
        }

        return records;
    }
}
