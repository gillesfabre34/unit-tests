package com.airbus.retex.business.mapper;

import com.airbus.retex.model.basic.IModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Cloning context used to keep track of ancestors during mapping and collect some datas during mapping.
 *
 * Mainly used in mapping to manage bidirectional relationship between entities.
 */
public class CloningContext {

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

    //TODO add something to collect translate to be saved after clone is saved

}
