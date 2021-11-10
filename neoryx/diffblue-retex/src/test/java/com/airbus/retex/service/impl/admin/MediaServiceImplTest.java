package com.airbus.retex.service.impl.admin;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.config.ThumbnailSize;
import com.airbus.retex.helper.MediaHelper;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.persistence.admin.MediaRepository;
import com.airbus.retex.persistence.admin.MediaTemporaryRepository;
import com.airbus.retex.service.impl.media.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(MediaServiceImplTest.Configuration.class)
@TestPropertySource(properties = {
    "retex.job.clean.tempo.media.before.x.days=1",
})
public class MediaServiceImplTest {

    final private static String MEDIA_TEST_FOLDER = "build/test-media";

    static class Configuration {
        @Bean
        public MediaServiceImpl mediaService() {
            return new MediaServiceImpl();
        }

        @Bean
        public DtoConverter dtoConverter() {
            return new DtoConverter();
        }
    }

    @MockBean
    private CacheManager cacheManager;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private RetexConfig retexConfig;

    @MockBean
    private MediaRepository mediaRepository;

    @MockBean
    private MediaTemporaryRepository mediaTemporaryRepository;

    @Autowired
    private MediaServiceImpl mediaService;

    @BeforeEach
    public void before() throws IOException {
        File mediaFolder = new File(MediaServiceImplTest.MEDIA_TEST_FOLDER);
        if (mediaFolder.delete()) {
            System.out.println(String.format("'%s' deleted", MediaServiceImplTest.MEDIA_TEST_FOLDER));
        }
        mediaFolder.mkdirs();

        Media media = new Media();
        media.setFilename("media/h160.jpg");

        UUID mediaUuid = new UUID(9014761168911944623L, -7416333625812013188L); // 7d1add85-57fb-4baf-9913-e3dc59b0af7c
        media.setUuid(mediaUuid);

        ArrayList<ThumbnailSize> thumbnails = new ArrayList<ThumbnailSize>();
        thumbnails.add(new ThumbnailSize(200, 100));
        thumbnails.add(new ThumbnailSize(300, 150));

        File mediaDir = new File("build/test-media/");
        Files.walk(mediaDir.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> media);
        when(retexConfig.getMediaDirectory()).thenReturn(MediaServiceImplTest.MEDIA_TEST_FOLDER);
    }

    @Test
    public void testFilenameMustHaveExtension() {
        assertThrows(FunctionalException.class, () -> {
            String [] mediaFormats={"png","jpg","jpeg","csv","pdf"};
            when(retexConfig.getMediaFormats()).thenReturn(mediaFormats);
            InputStreamSource inputStreamStub = new ByteArrayResource("some file data".getBytes("UTF-8"));
            mediaService.writeStream("filename_without_extension", inputStreamStub);
        });
    }

    @Test
    public void testFileIsWrittenOnDisk() throws Exception {
        assertDoesNotThrow(() -> {
            InputStreamSource mediaStream = MediaHelper.loadStream();
            String [] mediaFormats={"png","jpg","jpeg","csv","pdf"};
            when(retexConfig.getMediaFormats()).thenReturn(mediaFormats);
            Media media = mediaService.writeStream("h160.jpg", mediaStream);

            String name = mediaService.getMediaPath(media).toString();
            File writtenMedia = new File(name);
            assertTrue(writtenMedia.exists());
        });
    }

    @Test
    public void testMediaPathCanOnlyBeDeterminedIfMediaHasUUID() {
        assertThrows(IllegalArgumentException.class, () -> {
            Media media = new Media(null, "h160.jpg");
            mediaService.getMediaPath(media);
        });
    }
}

