package com.airbus.retex.model.basic;

import java.util.Iterator;
import java.util.LinkedList;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

/**
 * Context for Mapper that give access to mapped ancestor
 */
public class AncestorContext {//FIXME move this class into business module

    private LinkedList<Object> ancestors = new LinkedList<>();

    /**
     * Find the first clone ancestor of the given type and that is known by this context.
     * @param ancestorClazz
     * @param <T>
     * @return
     */
    @BeforeMapping
    public <T extends IModel> T findCloneAncestor(@TargetType Class<T> ancestorClazz) {
        Iterator<Object> iterator = ancestors.iterator();
        while(iterator.hasNext()) {
            Object item = iterator.next();
            if(ancestorClazz.isAssignableFrom(item.getClass())) {
                return ancestorClazz.cast(item);
            }
        }
        return null;
    }

    /**
     * Add an ancestor that will become first direct ancestor
     * @param ancestor
     */
    @BeforeMapping
    public <T extends IModel> void pushCloneAncestor(@MappingTarget T ancestor) {
        ancestors.offerLast(ancestor);
    }

    /**
     * Remove the first direct ancestor
     */
    @AfterMapping
    public <T extends IModel> void popCloneAncestor(@MappingTarget T ancestor) {
        ancestors.pollLast();
    }
}