package com.airbus.retex.service.impl.admin;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.config.ThumbnailSize;
import com.airbus.retex.helper.MediaHelper;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.media.MediaTmp;
import com.airbus.retex.persistence.admin.MediaRepository;
import com.airbus.retex.service.media.IMediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MediaServiceIT extends AbstractServiceIT {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IMediaService service;

    @Autowired
    private MediaRepository mediaRepository;

    @Test
    public void saveTmpFile_thenSetAsPermanent_keepSameUuid() throws FunctionalException {
        String filename = "file.png";
        MockMultipartFile file = new MockMultipartFile(filename, filename, "png", "data".getBytes());
        MediaTmp media = (MediaTmp) service.saveTemporaryFile(file);
        assertFalse(media.getUuid().toString().isEmpty());
        Optional<Media> m = service.temporaryToPermanentMedia(media.getUuid());
        assertTrue(m.isPresent());
        assertThat(m.get().getUuid(), equalTo(media.getUuid()));
        assertThat(m.get().getFilename(), equalTo(filename));
        assertThat(m.get().getIsFromThingworx(), equalTo(false));
    }

    @Test
    public void writeNullInputStreamMustRollbackDbTransaction() throws FunctionalException {
        String filename = "Sample_Ok.png";
        long initialCount = mediaRepository.count();
        MockMultipartFile file = new MockMultipartFile(filename, filename, "png", "data".getBytes());
        service.writeStream(filename, file);
        long transactionOkCount = mediaRepository.count();
        // no exception, item is actually persisted
        assertThat(transactionOkCount, equalTo(initialCount + 1));
        try {
            service.writeStream(filename, null); // should raise an exception and rollback
        }catch (NullPointerException e){
            long finalCount = mediaRepository.count();
            assertThat(finalCount, equalTo(transactionOkCount));
            assertThat(finalCount, equalTo(initialCount + 1));
            return;
        }
        throw new RuntimeException("Should not be thrown");

    }

    private Media prepareMedia() throws IOException, FunctionalException {
        InputStreamSource mediaStream = MediaHelper.loadStream();
        return service.writeStream("h160.jpg", mediaStream);
    }

    @Test
    void testCache() throws Exception {
        Media media = prepareMedia();
        ThumbnailSize thumbnailSize1 = new ThumbnailSize();
        thumbnailSize1.setHeight(10);
        thumbnailSize1.setWidth(10);
        ThumbnailSize thumbnailSize2 = new ThumbnailSize();
        thumbnailSize2.setHeight(15);
        thumbnailSize2.setWidth(15);

        Cache cache = cacheManager.getCache(Media.MEDIA_CACHE);
        String key1 = media.getUuid().toString() + "-" + thumbnailSize1.getWidth() + "-" + thumbnailSize1.getHeight();
        String key2 = media.getUuid().toString() + "-" + thumbnailSize2.getWidth() + "-" + thumbnailSize2.getHeight();
        assertNull(cache.get(key1));
        assertNull(cache.get(key2));

        byte[] thumbnail1 = service.readThumbnailStream(media, thumbnailSize1).getInputStream().readAllBytes();
        assertTrue(thumbnail1.length > 0);

        assertNull(cache.get(key2));
        byte[] thumbnail1FromCache = cache.get(key1, byte[].class);
        assertArrayEquals(thumbnail1, thumbnail1FromCache);

        byte[] thumbnail2 = service.readThumbnailStream(media, thumbnailSize2).getInputStream().readAllBytes();
        assertTrue(thumbnail2.length > 0);
        assertNotEquals(thumbnail1.length, thumbnail2.length);

        byte[] thumbnail2FromCache = cache.get(key2, byte[].class);
        assertArrayEquals(thumbnail2, thumbnail2FromCache);
    }
}
