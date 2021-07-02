package com.github.dreamhead.todo.core;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoItemServiceTest {
    private TodoItemService service;
    private TodoRepository repository;

    @BeforeEach
    public void setUp() {
        this.repository = mock(TodoRepository.class);
        this.service = new TodoItemService(this.repository);
    }

    @Test
    public void should_add_todo_item() {
        when(repository.save(any())).then(returnsFirstArg());
        TodoItem item = service.addTodoItem(new TodoParameter("foo"));
        assertThat(item.getContent()).isEqualTo("foo");
    }

    @Test
    public void should_throw_exception_for_null_todo_item() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.addTodoItem(null));
    }

    @Test
    public void should_mark_todo_item_as_done() {
        when(repository.findAll()).thenReturn(ImmutableList.of(new TodoItem("foo")));
        when(repository.save(any())).then(returnsFirstArg());

        final Optional<TodoItem> todoItem = service.markTodoItemDone(1);

        assertThat(todoItem).isPresent();
        final TodoItem actual = todoItem.get();
        assertThat(actual.isDone()).isTrue();
    }

    @Test
    public void should_not_mark_todo_item_for_negative_index() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.markTodoItemDone(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.markTodoItemDone(0));
    }

    @Test
    public void should_not_mark_todo_item_for_out_of_scope_index() {
        when(repository.findAll()).thenReturn(ImmutableList.of(new TodoItem("foo")));
        final Optional<TodoItem> todoItem = service.markTodoItemDone(2);
        assertThat(todoItem).isEmpty();
    }

    @Test
    public void should_list_all() {
        when(repository.findAll()).thenReturn(ImmutableList.of(new TodoItem("foo")));

        List<IndexedTodoItem> items = service.list(true);
        assertThat(items).hasSize(1);
        final IndexedTodoItem item = items.get(0);
        assertThat(item.getIndex()).isEqualTo(0);
        assertThat(item.getContent()).isEqualTo("foo");
    }

    @Test
    public void should_not_list_without_item() {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        List<IndexedTodoItem> items = service.list(true);
        assertThat(items).hasSize(0);
    }

    @Test
    public void should_list_all_without_done() {
        final TodoItem doneItem = new TodoItem("foo");
        doneItem.markDone();
        final TodoItem regularItem = new TodoItem("bar");

        when(repository.findAll()).thenReturn(ImmutableList.of(doneItem, regularItem));

        List<IndexedTodoItem> items = service.list(false);
        assertThat(items).hasSize(1);
        final IndexedTodoItem item = items.get(0);
        assertThat(item.getIndex()).isEqualTo(1);
        assertThat(item.getContent()).isEqualTo("bar");
    }

    @Test
    public void should_not_list_without_done_item() {
        final TodoItem doneItem = new TodoItem("foo");
        doneItem.markDone();

        when(repository.findAll()).thenReturn(ImmutableList.of(doneItem));

        List<IndexedTodoItem> items = service.list(false);
        assertThat(items).hasSize(0);
    }
}