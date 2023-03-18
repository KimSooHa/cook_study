package com.study.cook.controller;

import com.study.cook.domain.Comment;
import com.study.cook.dto.CommentDto;
import com.study.cook.exception.FindCommentException;
import com.study.cook.exception.FindRecipeException;
import com.study.cook.service.CommentService;
import com.study.cook.util.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final DateParser dateParser;

    @ResponseBody
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Long recipeId, @PageableDefault(size = 4, sort = "regDate", direction = DESC) Pageable pageable) {

        Page<CommentDto> comments = commentService.findList(recipeId, pageable);
        comments.forEach(cd -> cd.setRegDateStr(dateParser.getFormatDateDash(cd.getRegDate())));
        int size = comments.getSize();
        long totalElements = comments.getTotalElements();
        int totalPages = comments.getTotalPages();

        Map<String, Object> map = new HashMap<>();
        map.put("comments", comments);
        map.put("size", size);
        map.put("totalElements", totalElements);
        map.put("totalPages", totalPages);
        return map;
    }


    @ResponseBody
    @PostMapping
    public CommentDto create(@Valid @RequestBody CommentForm form, HttpSession session) {

        CommentDto commentDto;
        try {
            Long commentId = commentService.create(form, session);
            Comment comment = commentService.findOneById(commentId);
            commentDto = new CommentDto(commentId, comment.getContent(), comment.getRegDate(), comment.getMember().getId(), comment.getMember().getLoginId());
        } catch (IllegalArgumentException e) {
            throw new FindRecipeException(e.getMessage());
        }

        return commentDto;
    }


    @ResponseBody
    @PutMapping("/{commentId}")
    public Map<String, Object> update(@PathVariable Long commentId, @RequestBody @Valid CommentForm form) {

        Map<String, Object> map = new HashMap<>();

        try {
            commentService.update(commentId, form);
        } catch (IllegalArgumentException e) {
            throw new FindCommentException(e.getMessage());
        }
        map.put("success", true);
        return map;
    }

    @ResponseBody
    @DeleteMapping("/{commentId}")
    public Map<String, Object> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "삭제되었습니다.");

        return map;
    }
}
