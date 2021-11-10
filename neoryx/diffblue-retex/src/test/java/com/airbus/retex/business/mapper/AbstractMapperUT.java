package com.airbus.retex.business.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.airbus.retex.business.mapper.MapperUtils.makeIsSameLambda;
import static com.airbus.retex.business.mapper.MapperUtils.updateList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

public class AbstractMapperUT {

    private static final String FIRST = "First";
    private static final String SECOND = "Second";
    private static final String THIRD = "Third";
    private static final String FORTH = "Forth";
    private static final String FIFTH = "Fifth";
    private static final String SIXTH = "Sixth";

    private TestMapper mapper = new TestMapper();

    private List<TestSource> sourceList = buildList(TestSource::new);
    private List<TestDestination> destinationList = buildList(TestDestination::new);
    private List<TestDestination> originalDestinationItems = new ArrayList<>(destinationList);

    @Test
    public void updateList_nothing() {
        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(3));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, originalDestinationItems.get(1),2, SECOND);
        assertHasItem(destinationList, originalDestinationItems.get(2),3, THIRD);
    }

    @Test
    public void updateList_add() {
        sourceList.add(new TestSource(4, "Forth"));

        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(4));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, originalDestinationItems.get(1),2, SECOND);
        assertHasItem(destinationList, originalDestinationItems.get(2),3, THIRD);
        assertHasItem(destinationList, null, FORTH);
    }

    @Test
    public void updateList_remove() {
        sourceList.remove(1);

        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(2));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, originalDestinationItems.get(2),3, THIRD);
    }

    @Test
    public void updateList_update() {
        sourceList.get(1).setLabel("New");

        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(3));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, originalDestinationItems.get(1),2, "New");
        assertHasItem(destinationList, originalDestinationItems.get(2),3, THIRD);

    }

    @Test
    public void updateList_replace() {
        sourceList.set(1, new TestSource(null, FORTH));

        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(3));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, null, FORTH);
        assertHasItem(destinationList, originalDestinationItems.get(2),3, THIRD);
    }

    @Test
    public void updateList_addUpdateRemoveReplace() {
        sourceList.get(0).setLabel("New");
        sourceList.set(1, new TestSource(null, FORTH));
        sourceList.remove(2);
        sourceList.add(new TestSource(null, FIFTH));

        mapper.updateListFromObjects(sourceList, destinationList);

        assertThat(destinationList, hasSize(3));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, "New");
        assertHasItem(destinationList, null, FORTH);
        assertHasItem(destinationList,null, FIFTH);

    }

    @Test
    public void updateListIds_addUpdateRemoveReplace() {
        List<Integer> sourceIdsList = List.of(1, 4, 5);

        mapper.updateListFromIds(sourceIdsList, destinationList);

        assertThat(destinationList, hasSize(3));
        assertHasItem(destinationList, originalDestinationItems.get(0),1, FIRST);
        assertHasItem(destinationList, 4, "4");
        assertHasItem(destinationList,5, "5");

    }

    public void assertHasItem(List<TestDestination> list, Integer id, String label) {
        assertThat(list, hasItem(allOf(
                hasProperty("id", equalTo(id)),
                hasProperty("label", equalTo(label))
        )));
        assertHasItem(list, null, id, label);
    }

    public void assertHasItem(List<TestDestination> list, TestDestination object, Integer id, String label) {
        if(object !=  null) {
            assertThat(list, hasItem(object));
        }
        assertThat(list, hasItem(allOf(
                hasProperty("id", equalTo(id)),
                hasProperty("label", equalTo(label))
        )));
    }

    private <T> List<T> buildList(BiFunction<Integer, String, T> constructor) {
        List<T> result = new ArrayList<>();
        result.add(constructor.apply(1, FIRST));
        result.add(constructor.apply(2, SECOND));
        result.add(constructor.apply(3, THIRD));
        return result;
    }

    public static class TestMapper extends AbstractMapper {

        public void updateListFromObjects(List<TestSource> sourceList, List<TestDestination> destinationList) {
            BiFunction<TestSource, TestDestination, Boolean> isSame = makeIsSameLambda(TestSource::getId, TestDestination::getId);
            BiConsumer<TestSource, TestDestination> mapSourceToDestination = (s, d) -> {
                d.setLabel(s.getLabel());
            };
            updateList(sourceList, destinationList,
                    isSame, TestDestination::new, mapSourceToDestination);
        }

        public void updateListFromIds(List<Integer> sourceList, List<TestDestination> destinationList) {
            BiFunction<Integer, TestDestination, Boolean> isSame = (s,d) -> s.equals(d.getId());
            Function<Integer, TestDestination> resolveDestinationFromSource = (s) -> new TestDestination(s, s.toString());
            updateList(sourceList, destinationList,
                    isSame, resolveDestinationFromSource);
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestSource {
        private Integer id;
        private String label;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestDestination {
        private Integer id;
        private String label;
    }
}
