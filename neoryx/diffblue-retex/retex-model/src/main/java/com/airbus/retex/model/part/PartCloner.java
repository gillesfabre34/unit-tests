package com.airbus.retex.model.part;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Scope;

import com.airbus.retex.model.basic.AbstractCloner;
import com.airbus.retex.model.basic.CloningContext;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.translation.Translate;

@Scope(value="prototype")
@Mapper(componentModel = "spring")
public abstract class PartCloner extends AbstractCloner {

	// TODO Collect a list of translate to be saved for each translated entity with
	// a AfterMapping in CloningContext
	protected abstract Translate cloneTranslate(Translate translate);

	public abstract Part clonePart(Part part, @Context CloningContext context);

	abstract Mpn cloneMpn(Mpn mpn, @Context CloningContext context);

    abstract Set<Mpn> cloneMpn(Set<Mpn> mpn, @Context CloningContext context);

	public abstract FunctionalArea cloneFunctionalArea(FunctionalArea functionalArea, @Context CloningContext context);

    abstract SortedSet<FunctionalArea> cloneFunctionalAreas(Collection<FunctionalArea> functionalAreas, @Context CloningContext context);

    abstract List<FunctionalArea> cloneFunctionalAreaList(List<FunctionalArea> functionalAreas, @Context CloningContext context);



}

