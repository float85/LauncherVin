package com.launcher.vin_group.model;

import com.launcher.vin_group.model.db.db_entity.DbVideo;
import com.launcher.vin_group.model.entity.Video;
import com.launcher.vin_group.model.entity.enums.PLAY_TYPE;



public class EntityMapper {
    public static DbVideo VideoToDbVideo(Video video) {
        DbVideo dbVideo = new DbVideo();
        dbVideo.id = video.id;
        dbVideo.type = video.type.getValue();
        dbVideo.url = video.url;
        dbVideo.livestreaming = video.livestreaming;
        dbVideo.description = video.description;
        dbVideo.schedule = video.schedule;
        dbVideo.start = video.start;
        dbVideo.duration = video.duration;
        dbVideo.filesize = video.filesize;
        dbVideo.created = video.created;
        dbVideo.modified = video.modified;
        dbVideo.fileName = video.fileName;
        dbVideo.format = video.format;
        dbVideo.sortorder = video.sortorder;
        return dbVideo;
    }

    public static Video DbVideoToVideo(DbVideo dbVideo) {
        Video video = new Video();
        video.id = dbVideo.id;
        video.type = PLAY_TYPE.findByValue(dbVideo.type);
        video.url = dbVideo.url;
        video.livestreaming = dbVideo.livestreaming;
        video.description = dbVideo.description;
        video.schedule = dbVideo.schedule;
        video.start = dbVideo.start;
        video.duration = dbVideo.duration;
        video.filesize = dbVideo.filesize;
        video.created = dbVideo.created;
        video.modified = dbVideo.modified;
        video.fileName = dbVideo.fileName;
        video.format = dbVideo.format;
        video.sortorder = dbVideo.sortorder;
        return video;
    }
}
