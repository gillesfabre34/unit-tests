package com.airbus.retex.service.media;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.airbus.retex.model.media.AbstractMedia;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.config.ThumbnailSize;
import com.airbus.retex.model.media.IBaseMedia;
import com.airbus.retex.model.media.Media;

public interface IMediaService {

    /**
     * @param filename
     * @param inputStreamSource
     * @return
     * @throws IOException
     * @throws IllegalArgumentException If filename is not a valid filename (=basename + extension)
     */
    Media writeStream(String filename, InputStreamSource inputStreamSource) throws FunctionalException;

    /**
     * Remove temporary media and make it permanent(save on table media)
     *
     * @param uuid
     * @return
     */
    Optional<Media> temporaryToPermanentMedia(UUID uuid);

    Set<Media> getAsPermanentMedias(List<UUID> uuids) throws FunctionalException;

    Optional<Media> getAsPermanentMedia(UUID uuid);
    /**
     * @param media
     * @return
     * @throws IOException
     */
    InputStreamSource readStream(IBaseMedia media) throws NotFoundException;


    Optional<IBaseMedia> retrieveById(UUID uuid);

    IBaseMedia retrieveMedia(UUID uuid);

    /**
     * @param uid
     * @return
     */
    Optional<Media> find(UUID uid);


    /**
     * Pre-upload file to get temporary UUID to use later on save request
     *
     * @param file
     * @return
     */
    IBaseMedia saveTemporaryFile(MultipartFile file) throws FunctionalException;

    /**
     * Pre-upload file from thingworx to get temporary UUID to use later on save request
     * @param file
     * @return
     * @throws FunctionalException
     */
    IBaseMedia saveThingworxTemporaryFile(MultipartFile file) throws FunctionalException;

    /**
     * Get thumbnail sized for the given media
     *
     * @param media
     * @param thumbnailSize
     * @return
     * @throws FunctionalException
     */
    InputStreamSource readThumbnailStream(IBaseMedia media, ThumbnailSize thumbnailSize) throws FunctionalException;

    /**
     * Remove media in bdd and corresponding file on disk (located folder)
     */
    void removeOldTemporaryMedias();
}
