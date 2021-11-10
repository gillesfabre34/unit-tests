package com.airbus.retex.model;

import com.airbus.retex.BaseRepositoryTest;
import com.airbus.retex.business.audit.Revision;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import org.hibernate.envers.Audited;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.junit.jupiter.api.Test;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class ModelIT extends BaseRepositoryTest {
    private List<Class> excludedClasses = new ArrayList<>();

    @Test
    public void checkAllEntitiesAudited() {
        excludedClasses.add(WebsocketIdentifier.class);

        Metamodel metaModel = entityManager.getMetamodel();
        Set<EntityType<?>> entities = metaModel.getEntities();
        for (var entity : entities) {
            if (!excludedClasses.contains(((EntityTypeImpl) entity).getJavaType())) {
                Class<?> javaClass = entity.getJavaType();
                if (!javaClass.equals(Revision.class)) {
                    assertThat("The class " + javaClass.getSimpleName() + " must be annotated with @Audited",
                            javaClass.getAnnotation(Audited.class), not(nullValue()));
                }
            }
        }
    }

    @Test
    public void checkAllSuperClassAudited() {
        excludedClasses.add(WebsocketIdentifier.class);

        Metamodel metaModel = entityManager.getMetamodel();
        Set<EntityType<?>> entities = metaModel.getEntities();
        for (var entity : entities) {
            if (!excludedClasses.contains(((EntityTypeImpl) entity).getJavaType())) {
                Class<?> javaClass = entity.getJavaType();
                isAudited(javaClass.getSuperclass());
            }
        }
    }

    private void isAudited(Class<?> javaClass) {
        if(javaClass.equals(Object.class)) {
            return;
        }
        if (!javaClass.equals(Revision.class)) {
            assertThat("The class " + javaClass.getSimpleName() + " must be annotated with @Audited",
                    javaClass.getAnnotation(Audited.class), not(nullValue()));
        }
        isAudited(javaClass.getSuperclass());
    }
}
