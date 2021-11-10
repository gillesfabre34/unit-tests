package com.airbus.retex.business.mapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.airbus.retex.business.exception.TechnicalError;

/**
 * Utilities for mappers
 */
public class MapperUtils {

	/**
	 * private contructor
	 */
	private MapperUtils() {

	}

    /**
     * Create a BiFunction used for <i>isSame</i> parameter in {@link #updateList(Collection, Collection, BiFunction, Function)}
     * of in {@link #updateList(Collection, Collection, BiFunction, Supplier, BiConsumer)}.
     *
     * Useful if the type of source collection is the same in destination collection.
     *
     * @param sourceAndDestinationGetter the getter of E type field on which the match is done
     * @param <E> the type of source and destination collection
     * @param <T> the type of the E type field
     * @return
     */
    public static <E, T> BiFunction<E, E, Boolean> makeIsSameLambda(Function<E, T> sourceAndDestinationGetter) {
        return makeIsSameLambda(sourceAndDestinationGetter, sourceAndDestinationGetter);
    }

    /**
     * Create a BiFunction used for <i>isSame</i> parameter in {@link #updateList(Collection, Collection, BiFunction, Function)}
     * of in {@link #updateList(Collection, Collection, BiFunction, Supplier, BiConsumer)}.
     *
     * Useful if the matching condition between a source element and a destination element is on one attribute.
     *
     * @param sourceGetter the getter of S type field on which the match is done
     * @param destinationGetter the getter of D type field on which the match is done
     * @param <S> the type of source elements
     * @param <D> the type of destination elements
     * @param <T> the type of the S type field and of the D type field
     * @return
     */
    public static <S, D, T> BiFunction<S, D, Boolean> makeIsSameLambda(Function<S, T> sourceGetter, Function<D, T> destinationGetter) {
        return (s, d) -> {
            T sourceKey = sourceGetter.apply(s);
            if(sourceKey == null) {
                return false;
            } else {
                return sourceKey.equals(destinationGetter.apply(d));
            }
        };
    }

    /**
     * Update a destination list from a source list using a Function to resolve destination from source
     *
     * Useful if you need to map a collection of primary keys to a collection of entities.
     *
     * @param sourceCollection the collection from which map data are taken
     * @param destinationCollection the collection into which data goes
     * @param isSame BiFunction that return true if the two parameters are considered to be the same element
     * @param resolveDestinationFromSource Function that return the corresponding destination element (to be added to the destination collection) from a source element
     * @param <S> the type of source elements
     * @param <D> the type of destination elements
     */
    public static <S, D> void updateList(Collection<S> sourceCollection, Collection<D> destinationCollection,
                                     BiFunction<S, D, Boolean> isSame,
                                     Function<S, D> resolveDestinationFromSource) {
        internalUpdateList(sourceCollection, destinationCollection, isSame, resolveDestinationFromSource, null);
    }

    /**
     * Update a destination list from a source list using a Supplier to build new instance and a BiConsumer to update new/existing entities
     *
     * Useful if you need to map a collection of DTOs to a collection of entities.
     *
     * @param sourceCollection the collection from which map data are taken
     * @param destinationCollection the collection into which data goes
     * @param isSame BiFunction that return true if the two parameters are considered to be the same element
     * @param newDestinationInstance Supplier that create a new element for the destination collection
     * @param mapSourceToDestination BiConsumer that update a destination item from a source element
     * @param <S> the type of source elements
     * @param <D> the type of destination elements
     */
    public static <S, D> void updateList(Collection<S> sourceCollection, Collection<D> destinationCollection,
                                     BiFunction<S, D, Boolean> isSame,
                                     Supplier<D> newDestinationInstance,
                                     BiConsumer<S, D> mapSourceToDestination) {
        internalUpdateList(sourceCollection, destinationCollection, isSame,
				s -> {
                    D d = newDestinationInstance.get();
                    if(mapSourceToDestination != null) {
                        mapSourceToDestination.accept(s, d);
                    }
                    return d;
                },
                (s, d) -> {
                    if(mapSourceToDestination != null) {
                        mapSourceToDestination.accept(s, d);
                    }
                });
    }

    /**
     * Internal implementation of the algorithm that update a list from another list, parametrized with lambda expressions
     *
     * @param sourceCollection the collection from which map data are taken
     * @param destinationCollection the collection into which data goes
     * @param isSame BiFunction that return true if the two parameters are considered to be the same element
     * @param resolveDestinationFromSource Function that return the corresponding destination element (to be added to the destination collection) from a source element
     * @param mapSourceToDestination BiConsumer that update a destination item from a source element
     * @param <S> the type of source elements
     * @param <D> the type of destination elements
     */
    private static <S, D> void internalUpdateList(Collection<S> sourceCollection, Collection<D> destinationCollection,
                                           BiFunction<S, D, Boolean> isSame,
                                           Function<S, D> resolveDestinationFromSource,
                                           BiConsumer<S, D> mapSourceToDestination) {
        //Remove item that are absent in source
        Iterator<D> destinationIt = destinationCollection.iterator();
        while(destinationIt.hasNext()) {
            D destinationItem = destinationIt.next();
            //Look for a matching item in source
            boolean existsInSource = sourceCollection.stream()
                    .anyMatch(sourceItem -> isSame.apply(sourceItem, destinationItem));
            if(!existsInSource) {//Remove item if not present in source
                destinationIt.remove();
            }
        }

        //Add new item and update existing ones
        for(var sourceItem : sourceCollection) {
            boolean foundInDestinationList = false;
            destinationIt = destinationCollection.iterator();

            while(destinationIt.hasNext()) {//Look for the existing item
                D destinationItem = destinationIt.next();
                if(isSame.apply(sourceItem, destinationItem)) {//Update if item exists
                    if(mapSourceToDestination != null) {
                        mapSourceToDestination.accept(sourceItem, destinationItem);
                    }
                    foundInDestinationList = true;
                    break;
                }
            }

            if(!foundInDestinationList) {//Item doesn't exists, add a new one
                D item = resolveDestinationFromSource.apply(sourceItem);

                if (null == item) {
                    throw new TechnicalError("retex.cant.resolve.item.from.source");
                }

                destinationCollection.add(item);
            }
        }
    }
}
