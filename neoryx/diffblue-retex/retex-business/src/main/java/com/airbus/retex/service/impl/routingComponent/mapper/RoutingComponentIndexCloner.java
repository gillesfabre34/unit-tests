package com.airbus.retex.service.impl.routingComponent.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.airbus.retex.business.mapper.AbstractCloner;
import com.airbus.retex.business.mapper.CloningContext;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.PostTranslation;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.routingComponent.RoutingComponentIndexTranslation;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepTranslation;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.translation.Translate;

@Scope(value="prototype")
@Service
@Mapper(componentModel = "spring")
public abstract class RoutingComponentIndexCloner extends AbstractCloner {

    public abstract RoutingComponentIndex cloneRoutingComponentIndex(RoutingComponentIndex routingComponentIndex,
            @Context CloningContext context);

    public abstract TodoList cloneTodoList(TodoList todoList, @Context CloningContext context);

    public abstract RoutingComponent cloneRoutingComponent(RoutingComponent routingComponent,
            @Context CloningContext context);

    abstract Set<Step> cloneStepsSet(Set<Step> steps, @Context CloningContext context);

    abstract List<Step> cloneStepsList(List<Step> steps, @Context CloningContext context);

    public abstract List<Media> cloneMediasList(List<Media> files, @Context CloningContext context);

    public abstract Set<Media> cloneMediasSet(Set<Media> files, @Context CloningContext context);

    protected abstract Step cloneStep(Step step, @Context CloningContext context);

    protected abstract Set<Post> clonePostsSet(Set<Post> posts, @Context CloningContext context);

    protected abstract Set<StepTranslation> cloneStepTranslationsSet(Collection<StepTranslation> translations,
            @Context CloningContext context);

    protected abstract StepTranslation cloneStepTranslation(StepTranslation translation,
            @Context CloningContext context);

    protected abstract Set<PostTranslation> clonePostsTranslationsSet(Set<PostTranslation> posts,
            @Context CloningContext context);

    protected abstract PostTranslation clonePostTranslationSet(PostTranslation translation,
            @Context CloningContext context);

    protected abstract Post clonePost(Post post, @Context CloningContext context);

    //TODO Collect a list of translate to be saved for each translated entity with a AfterMapping in CloningContext
    protected abstract Translate cloneTranslate(Translate translate);

    protected abstract Set<RoutingComponentIndexTranslation> cloneTranslationsSet(
            Set<RoutingComponentIndexTranslation> translations, @Context CloningContext context);

    protected abstract RoutingComponentIndexTranslation cloneTranslation(RoutingComponentIndexTranslation translation,
            @Context CloningContext context);
}
