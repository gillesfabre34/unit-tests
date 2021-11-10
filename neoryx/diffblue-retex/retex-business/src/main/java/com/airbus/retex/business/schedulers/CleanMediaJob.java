package com.airbus.retex.business.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.airbus.retex.service.media.IMediaService;

@Component
public class CleanMediaJob {
    private static final String CRON_OLD_TEMPORARY_MEDIA_CLEAN_DAILY_MIDNIGHT = "0 * * * * ?";

    @Autowired
    private IMediaService iMediaService;

    @Scheduled(cron = CRON_OLD_TEMPORARY_MEDIA_CLEAN_DAILY_MIDNIGHT)
    public void removeOldTemporaryMedia() throws Exception {
        iMediaService.removeOldTemporaryMedias();
    }
}
