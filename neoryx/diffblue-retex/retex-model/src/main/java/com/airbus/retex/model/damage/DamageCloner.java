package com.airbus.retex.model.damage;

import java.util.Collection;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Scope;

import com.airbus.retex.model.basic.AbstractCloner;
import com.airbus.retex.model.basic.CloningContext;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageTranslation;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.translation.Translate;

@Scope(value="prototype")
@Mapper(componentModel = "spring")
public abstract class DamageCloner extends AbstractCloner {

	// TODO Collect a list of translate to be saved for each translated entity with
	// a AfterMapping in CloningContext
    protected abstract Translate cloneTranslate(Translate translate);

    public abstract Damage cloneDamage(Damage damage, @Context CloningContext context);

    abstract Media cloneImage(Media media, @Context CloningContext context);

    abstract Set<Media> cloneImagesSet(Set<Media> medias, @Context CloningContext context);

    abstract Set<DamageTranslation> cloneDamageTranslation(Set<DamageTranslation> damageTranslations, @Context CloningContext context);

    abstract DamageTranslation cloneDamageTranslation(DamageTranslation damageTranslation, @Context CloningContext context);

    abstract FunctionalityDamage cloneFunctionalityDamage(FunctionalityDamage functionalityDamages, @Context CloningContext context);

    abstract Set<FunctionalityDamage> cloneFunctionalityDamagesSet(Set<FunctionalityDamage> functionalityDamages, @Context CloningContext context);

    abstract Set<FunctionalityDamageTranslation> cloneDamageTranslation(Collection<FunctionalityDamageTranslation> damageTranslation, @Context CloningContext context);

    abstract FunctionalityDamageTranslation cloneDamageTranslation(FunctionalityDamageTranslation damageTranslation, @Context CloningContext context);



}

