package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;


@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public Collection<Post> findAll(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String sort, @RequestParam(defaultValue = "0") int from) {
        return postService.findAll(size, sort, from);
    }

    @GetMapping("/{id}")
    public Optional<Post> findById(@PathVariable Optional<Long> id) {
        return id.map(postService::findById).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}