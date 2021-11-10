package com.airbus.retex.audit;

import com.airbus.retex.BaseRepositoryTest;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.persistence.damage.DamageRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuditTestIT extends BaseRepositoryTest {

    @Autowired
    DamageRepository damageRepository;

    @Test
    public void shouldAuditEntityWhenInsert() {
        Damage damage = createDamage();

        runInTransaction(() -> {
            Damage damageAudited = findLastDamageRevision(damage, 1, RevisionType.ADD);

            assertAuditEqual(damageAudited, damage);
        });
    }

    @Test
    public void shouldAuditEntityWhenUpdate() {
        Damage damage = createDamage();

        runInTransaction(() -> {
            damage.setState(EnumActiveState.INACTIVE);
            damageRepository.saveVersion(damage);
        });


        runInTransaction(() -> {
            Damage damageAudited = findLastDamageRevision(damage, 2, RevisionType.MOD);

            assertAuditEqual(damageAudited, damage);
        });
    }

    @Test
    public void shouldAuditEntityWhenDelete() {
        Damage damage = createDamage();

        runInTransaction(() -> {
            damageRepository.deleteVersion(damage);
        });

        runInTransaction(() -> {
            Damage damageAudited = findLastDamageRevision(damage, 2, RevisionType.DEL);
            assertThat(damageAudited.getTechnicalId(), equalTo(damage.getTechnicalId()));
            assertThat(damageAudited.getNaturalId(), equalTo(null
            ));

        });
    }

    private Damage createDamage() {
        AtomicReference<Damage> damage = new AtomicReference<>();
        runInTransaction(() -> {
            damage.set(dataSetInitializer.createDamage(EnumActiveState.ACTIVE, "damageName"));
            Media mediaOne = dataSetInitializer.createMedia();
            Media mediaTwo = dataSetInitializer.createMedia();
            damage.get().addImage(mediaOne);
            damage.get().addImage(mediaTwo);
            damage.get().setFunctionalityDamages(Set.of(dataSetInitializer.createFunctionalityDamage(dataset.functionality_teeth, damage.get())));

        });
        return damage.get();
    }

    private void assertAuditEqual(Damage damageAudited, Damage damage){
        assertThat(damageAudited.getNaturalId(), equalTo(damage.getNaturalId()));
        assertThat(damageAudited.getTechnicalId(), equalTo(damage.getTechnicalId()));
        assertThat(damageAudited.getIsLatestVersion(), equalTo(damage.getIsLatestVersion()));
        assertThat(damageAudited.getStatus(), equalTo(damage.getStatus()));
        assertThat(damageAudited.getClassification(), equalTo(damage.getClassification()));
        assertThat(damageAudited.getVersionNumber(), equalTo(damage.getVersionNumber()));
        assertThat(damageAudited.getState(), equalTo(damage.getState()));
        assertThat(damageAudited.getImages(), notNullValue());
        assertThat(damageAudited.getImages().size(), greaterThan(0));
        assertThat(damageAudited.getImages().size(), equalTo(damage.getImages().size())); // TEST AUDIT @MANY_TO_MANY
        assertThat(damageAudited.getFunctionalityDamages().size(), equalTo(damage.getFunctionalityDamages().size())); // TEST AUDIT @MANY_TO_MANY
    }

    private Damage findLastDamageRevision(Damage damage, int countRevision, RevisionType revisionType) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        assertThat(auditReader.getRevisions(Damage.class, damage.getTechnicalId()).size(), equalTo(countRevision));
        Number revisionNumber = auditReader.getRevisions(Damage.class, damage.getTechnicalId()).get(auditReader.getRevisions(Damage.class, damage.getTechnicalId()).size() -1);

        AuditQuery auditResult = auditReader.createQuery().forRevisionsOfEntity(Damage.class,false, true)
                .add(AuditEntity.revisionNumber().eq(revisionNumber));
        List result = auditResult.getResultList();
        assertThat(((Object[]) result.get( 0 ))[2], equalTo(revisionType));

        return  auditReader.find(Damage.class,
                damage.getTechnicalId(),
                revisionNumber);
    }

}
