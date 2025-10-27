package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.InvalidParametersQueryString;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll(int size, String sort, int from) {
        if (size < 0) {
            throw new InvalidParametersQueryString("size must be greater than 0");
        }
        if (from < 0) {
            throw new InvalidParametersQueryString("from must be greater than 0");
        }

        List<Post> result = posts.values()           // объявление возвращаемого результата
                .stream()
                .sorted(switch (SortOrder.from(sort)) {
                    case ASCENDING ->     // сортировка по возрастанию
                            Comparator.comparing(Post::getPostDate).reversed();
                    case DESCENDING ->    // сортировка по убыванию
                            Comparator.comparing(Post::getPostDate);
                })
                .toList();

        if (posts.size() > from) {     // если количество пропущенных элементов < размера
            result = result.stream().skip(from).limit(size).collect(Collectors.toList());
        }

        return result;
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}