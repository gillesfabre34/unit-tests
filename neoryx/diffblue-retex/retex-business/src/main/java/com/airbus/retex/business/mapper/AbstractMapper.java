package com.airbus.retex.business.mapper;

import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.service.impl.util.HibernateUtil;
import com.airbus.retex.service.media.IMediaService;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.airbus.retex.business.mapper.MapperUtils.updateList;

/**
 * Abstract class for entity to/from dto mapping
 */
public class AbstractMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IMediaService mediaService;

    public <T extends IIdentifiedModel> T resolveManagedEntity(T source, @TargetType Class<T> targetType) {
        if (source != null) {
            return entityManager.getReference(HibernateUtil.getPersistentClass(source), source.getId());
        } else {
            return null;
        }
    }

    public Set<Media> updateMediaList(List<UUID> sourceList, @MappingTarget Set<Media> targetList) {
        BiFunction<UUID, Media, Boolean> isSame = (source, target) -> target.getUuid().equals(source);
        Function<UUID, Media> resolveDestinationFromSource = source -> {
            Optional<Media> mediaOpt = mediaService.getAsPermanentMedia(source);
            return mediaOpt.orElse(null);
        };
        updateList(sourceList, targetList, isSame, resolveDestinationFromSource);

        return targetList;
    }

    public List<UUID> updateMediaUuidList(Set<Media> sourceList, @MappingTarget List<UUID> targetList) {
        sourceList.forEach(media -> targetList.add(media.getUuid()));
        return targetList;
    }
}
