package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.common.AbstractSpringTest;
import com.github.alebabai.tg2vk.util.TestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.alebabai.tg2vk.util.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Transactional
abstract public class AbstractRepositoryTest<T extends Persistable<ID>, ID extends Serializable, REPO extends PagingAndSortingRepository<T, ID>> extends AbstractSpringTest {

    protected T entity;
    protected List<? extends T> entities;
    protected Sort sort;
    protected Pageable pageable;
    protected List<String> columnNames;

    @Autowired
    protected REPO repository;

    protected abstract T generateEntity();

    protected List<? extends T> generateEntities(int maxEntitiesCount) {
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> generateEntity())
                .collect(Collectors.toList());
    }

    protected Sort generateSort() {
        return new Sort(getRandomFieldName());
    }

    protected Pageable generatePageable() {
        return new PageRequest(1, TestUtils.getRandomInteger(TestUtils.MAX_ENTITIES_COUNT));
    }

    protected List<String> getFieldsNames() {
        return Stream.of(entity.getClass().getDeclaredFields())
                .filter(field -> !"serialVersionUID".equals(field.getName()))
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    protected String getRandomFieldName() {
        return getFieldsNames().get(RandomUtils.nextInt(0, getFieldsNames().size()));
    }

    @Before
    public void initialization() {
        this.entity = generateEntity();
        this.entities = generateEntities(getRandomInteger(TestUtils.MAX_ENTITIES_COUNT));
        this.sort = generateSort();
        this.pageable = generatePageable();
        this.columnNames = getFieldsNames();
    }

    @Test
    public void countEntitiesTest() {
        final long before = repository.count();
        repository.save(entity);
        final long actual = repository.count();

        assertThat(actual, is(before + 1));
    }

    @Test
    public void deleteEntityByIdTest() {
        final ID savedId = repository.save(entity).getId();
        repository.delete(savedId);

        assertThat(repository.exists(savedId), is(false));
    }

    @Test
    public void deleteSequenceOfEntitiesTest() {
        final Iterable<? extends T> saved = repository.save(entities);
        final List<ID> ids = StreamSupport.stream(saved.spliterator(), true)
                .map(Persistable::getId)
                .collect(Collectors.toList());
        repository.delete(saved);

        assertThat(repository.findAll(ids), emptyIterable());
    }

    @Test
    public void deleteEntityTest() {
        final T saved = repository.save(entity);
        repository.delete(saved);

        assertThat(repository.exists(saved.getId()), is(false));
    }

    @Test
    public void deleteAllEntitiesTest() {
        final T entities = repository.save(entity);
        repository.deleteAll();

        assertThat(repository.findAll(), not(hasItem(entities)));
    }

    @Test
    public void entityExistenceByIdTest() {
        final T saved = repository.save(entity);

        assertThat(repository.exists(saved.getId()), is(true));
    }

    @Test
    public void findAllEntitiesTest() {
        final Iterable<? extends T> saved = repository.save(entities);

        assertThat(saved, is(repository.findAll()));
    }

    @Test
    public void findAllEntitiesByIds() {
        final Iterable<? extends T> saved = repository.save(entities);
        final List<ID> ids = StreamSupport.stream(saved.spliterator(), true)
                .map(Persistable::getId)
                .collect(Collectors.toList());
        final Iterable<T> found = repository.findAll(ids);

        assertThat(found, is(saved));
    }

    @Test
    public void findAllEntitiesByPageableTest() {
        repository.save(entities);
        final Page<T> found = repository.findAll(pageable);

        assertThat(found.getTotalPages() <= entities.size(), is(true));
    }

    @Test
    public void findAllEntitiesBySortTest() {
        repository.save(entities);
        final Iterable<T> found = repository.findAll(sort);

        assertThat(found, not(emptyIterable()));
    }

    @Test
    public void findOneEntityByIdTest() {
        final T saved = repository.save(entity);
        final T found = repository.findOne(entity.getId());

        assertThat(saved, is(found));
    }

    @Test
    public void saveSequenceOfEntitiesTest() {
        final Iterable<? extends T> saved = repository.save(entities);

        assertThat(repository.findAll(), contains(saved));
    }

    @Test
    public void saveOneEntityTest() {
        final T saved = repository.save(entity);

        assertThat(repository.findOne(saved.getId()), is(saved));
    }

    @Test
    public void saveOneEntityAndFlushTest() {
        final T saved = repository.save(entity);

        assertThat(repository.findOne(saved.getId()), is(saved));
    }
}
