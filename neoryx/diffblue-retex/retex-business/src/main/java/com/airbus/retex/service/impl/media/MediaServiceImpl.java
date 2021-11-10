package com.airbus.retex.service.impl.media;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.config.ThumbnailSize;
import com.airbus.retex.model.media.IBaseMedia;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.media.MediaTmp;
import com.airbus.retex.persistence.admin.MediaRepository;
import com.airbus.retex.persistence.admin.MediaTemporaryRepository;
import com.airbus.retex.service.impl.util.MediaUtil;
import com.airbus.retex.service.media.IMediaService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class MediaServiceImpl implements IMediaService {
    private Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);
    private static final String FILE_NAME_REGEX_CONDITION = "[A-Za-z0-9\\-\\_\\.]+";

    @Value("${retex.job.clean.tempo.media.before.x.days}")
    private int cleanTemporaryMediaBeforeXDays;
    @Autowired
    private RetexConfig retexConfig;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private MediaTemporaryRepository mediaTemporaryRepository;
    @Autowired
    private CacheManager cacheManager;

    private IBaseMedia internalWriteStream(String filename, InputStreamSource inputStreamSource, boolean isFromThingworx, boolean isTemporary) throws FunctionalException {
        if (!formatAccepted(filename)) {
            throw new FunctionalException("retex.error.media.format.denied");
        }
        // deleteVersion accents
        filename = Normalizer.normalize(filename, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        // check if filename match regex rule
        if (!filename.matches(FILE_NAME_REGEX_CONDITION)) {
            throw new FunctionalException("retex.error.media.file.name.denied");
        }
        IBaseMedia media = null;
        if (isTemporary) {
            media = new MediaTmp(filename, isFromThingworx);
            media = mediaTemporaryRepository.save((MediaTmp) media);
        } else {
            media = new Media(filename, isFromThingworx);
            media = mediaRepository.save((Media) media);
        }

        Path targetPath = getMediaPath(media);
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStreamSource.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new TechnicalError("Unable to write media on disk", e);
        }

        return media;
    }

    @Override
    public Media writeStream(String filename, InputStreamSource inputStreamSource) throws FunctionalException {
        return (Media) internalWriteStream(filename, inputStreamSource, Boolean.FALSE, false);
    }

    //  TODO : QUE FAIRE SI LE FRONT NOUS ENVOIE UNE IMAGE TEMPO ET une autre VALIDEEE

    /**
     * @param uuid
     * @return
     * @throws FunctionalException
     */
    @Override
    public Optional<Media> temporaryToPermanentMedia(UUID uuid) {
        Optional<MediaTmp> optionalMediaTemporary = mediaTemporaryRepository.findByUuid(uuid);
        if (optionalMediaTemporary.isPresent()) {
            return Optional.of(moveToPermanentMedia(optionalMediaTemporary.get()));
        }

        return Optional.empty();
    }

    /**
     * Get a set of medias with set of Uuids, permanent or a temporary moved automatically to permanent
     *
     * @param uuids
     * @return
     */
    @Override
    public Set<Media> getAsPermanentMedias(List<UUID> uuids) {
        Set<Media> mediaSet = new HashSet<>();
        for (UUID uuid : uuids) {
            Optional<Media> mediaOpt = getAsPermanentMedia(uuid);
            if (mediaOpt.isPresent()) {
                mediaSet.add(mediaOpt.get());
            }
        }
        return mediaSet;

    }

    /**
     * Return Media with Uuid, if media is temporary, it moves in permanent
     *
     * @param uuid
     * @return
     */
    @Override
    public Optional<Media> getAsPermanentMedia(UUID uuid) {
        Optional<MediaTmp> optionalMediaTemporary = findTemporary(uuid);
        if (optionalMediaTemporary.isPresent()) {
            return Optional.of(moveToPermanentMedia(optionalMediaTemporary.get()));
        } else {
            return find(uuid);
        }
    }

    public Media moveToPermanentMedia(MediaTmp mediaTemporary) {
        Media media = new Media(mediaTemporary);
        mediaTemporaryRepository.delete(mediaTemporary);

        return mediaRepository.save(media);
    }

    @Override
    public InputStreamSource readStream(IBaseMedia media) throws NotFoundException {
        return new FileSystemResource(getMediaPath(media));
    }

    @Override
    public Optional<IBaseMedia> retrieveById(UUID uuid) {
        Optional<Media> mediaOpt = find(uuid);

        if (mediaOpt.isPresent()) {
            return Optional.of(mediaOpt.get());
        } else {
            Optional<MediaTmp> tmpOpt = findTemporary(uuid);
            if (tmpOpt.isPresent()) {
                return Optional.of(tmpOpt.get());
            }
            return Optional.empty();
        }
    }

    @Override
    public IBaseMedia retrieveMedia(UUID uuid) {
        return retrieveById(uuid).get();
    }

    public Path getMediaPath(IBaseMedia media) throws IllegalArgumentException {
        if (null == media.getUuid()) {
            throw new IllegalArgumentException("No UUID has been set to media");
        }

        StringBuilder builder = new StringBuilder(retexConfig.getMediaDirectory());
        builder.append("/");

        String[] folders = media.getUuid().toString()
                .replace("-", "")
                .split("(?<=\\G.{2})");
        builder.append(String.join("/", folders));
        builder.append("/");
        builder.append(media.getFilename());

        return Paths.get(builder.toString());
    }

    @Override
    public Optional<Media> find(UUID uid) {
        return mediaRepository.findById(uid);
    }

    private Optional<MediaTmp> findTemporary(UUID uid) {
        return mediaTemporaryRepository.findById(uid);
    }

    @Override
    public IBaseMedia saveTemporaryFile(MultipartFile file) throws FunctionalException {
        return this.internalWriteStream(file.getOriginalFilename(), file, false, true);
    }

    @Override
    public IBaseMedia saveThingworxTemporaryFile(MultipartFile file) throws FunctionalException {
        return this.internalWriteStream(file.getOriginalFilename(), file, true, true);
    }

    private byte[] generateThumbnail(IBaseMedia media, ThumbnailSize thumbnailSize) throws FunctionalException {
        try {
            String outputFormat = "JPEG";
            String imageFilename = MediaUtil.getFilenameForFormat(null, media.getFilename());
            String format = Files.probeContentType(new File(imageFilename).toPath());
            if (format != null) {
                if (!format.startsWith("image")) {
                    throw new FunctionalException(imageFilename + "is not of type image.");
                }
                outputFormat = format.split("/")[1];
            }

            BufferedImage mediaFile = ImageIO.read(readStream(media).getInputStream());

            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            Thumbnails.of(mediaFile)
                    .size(thumbnailSize.getWidth(), thumbnailSize.getHeight())
                    .outputFormat(outputFormat)
                    .toOutputStream(byteArrayOut);

            return byteArrayOut.toByteArray();
        } catch (IOException e) {
            throw new TechnicalError("Unable to generate thumbnail", e);
        }
    }

    // @Cacheable not working here because this method is called directly and not through a proxy
    private byte[] getOrGenerateThumbnail(IBaseMedia media, ThumbnailSize thumbnailSize) throws FunctionalException {

        Cache cache = cacheManager.getCache(Media.MEDIA_CACHE);
        String key = media.getUuid().toString() + "-" + thumbnailSize.getWidth() + "-" + thumbnailSize.getHeight();

        byte[] thumbnail = cache.get(key, byte[].class);
        if (thumbnail == null) {
            thumbnail = generateThumbnail(media, thumbnailSize);
            cache.put(key, thumbnail);
        }
        return thumbnail;
    }

    @Override
    public InputStreamSource readThumbnailStream(IBaseMedia media, ThumbnailSize thumbnailSize) throws FunctionalException {
        byte[] thumbnail = getOrGenerateThumbnail(media, thumbnailSize);
        return () -> new ByteArrayInputStream(thumbnail);
    }

    private boolean formatAccepted(String fileName) {
        return !StringUtils.isEmpty(fileName) && Arrays.asList(retexConfig.getMediaFormats()).contains(FilenameUtils.getExtension(fileName));
    }

    @Override
    public void removeOldTemporaryMedias() {
        Optional<List<MediaTmp>> mediasTemporary = mediaTemporaryRepository.findTemporaryMedia(LocalDateTime.now().minusDays(cleanTemporaryMediaBeforeXDays));
        if (mediasTemporary.isPresent() && !mediasTemporary.get().isEmpty()) {
            mediasTemporary.get().forEach(media -> {
                File file = new File(media.getFilename());
                try {
                    deleteRecursively(file);
                    if (!file.exists()) {
                        mediaTemporaryRepository.delete(media);
                    }
                } catch (IOException e) {
                    logger.error("Error while deleting media and media folder", e);
                }
            });
        }
    }

    private void deleteRecursively(File file) throws IOException {
        if (file.isFile()) {
            // if we deleted media file, then deleteVersion media'a folder
            if (file.delete()) {
                file = file.getParentFile();
            } else {
                logger.error("Unable to deleteVersion file \"{}\"", file.getAbsolutePath());
            }
        }
        if (file.isDirectory()) {
            File parentFile = file.getParentFile();
            FileUtils.deleteDirectory(file);
            // don't deleteVersion "retex-media" folder
            if (!retexConfig.getMediaDirectory().contains(parentFile.getName())) {
                deleteRecursively(parentFile);
            }
        }
    }

}
