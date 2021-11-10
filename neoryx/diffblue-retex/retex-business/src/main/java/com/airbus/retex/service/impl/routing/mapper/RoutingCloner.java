package com.airbus.retex.service.impl.routing.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.airbus.retex.business.mapper.AbstractCloner;
import com.airbus.retex.business.mapper.CloningContext;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingTranslation;
import com.airbus.retex.model.step.StepActivation;

@Scope(value="prototype")
@Service
@Mapper(componentModel = "spring")
public abstract class RoutingCloner extends AbstractCloner {

	public abstract Routing cloneRouting(Routing routing, @Context CloningContext context);

	protected abstract Set<RoutingTranslation> cloneTranslationsSet(Collection<RoutingTranslation> translations,
			@Context CloningContext context);

	protected abstract RoutingTranslation cloneTranslation(RoutingTranslation translation,
			@Context CloningContext context);

	protected abstract Set<Operation> cloneOperationSet(Collection<Operation> operations,
			@Context CloningContext context);

	protected abstract Operation cloneOperation(Operation operation, @Context CloningContext context);

	protected abstract Set<OperationFunctionalArea> cloneOperationFunctionalAreaSet(
			Collection<OperationFunctionalArea> ofas, @Context CloningContext context);

	protected abstract OperationFunctionalArea cloneOperationFunctionalArea(OperationFunctionalArea ofa,
			@Context CloningContext context);

	protected abstract Set<StepActivation> cloneStepActivationSet(Collection<StepActivation> stepActivations,
			@Context CloningContext context);

	protected abstract StepActivation cloneStepActivation(StepActivation stepActivation,
			@Context CloningContext context);

	protected abstract List<RoutingFunctionalAreaPost> cloneRoutingFunctionalAreaPostSet(
			Collection<RoutingFunctionalAreaPost> rfaps, @Context CloningContext context);

	protected abstract RoutingFunctionalAreaPost cloneRoutingFunctionalAreaPost(RoutingFunctionalAreaPost rfap,
			@Context CloningContext context);
}
