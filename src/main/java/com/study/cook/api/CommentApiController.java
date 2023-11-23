package com.study.cook.api;

import com.study.cook.controller.CommentForm;
import com.study.cook.domain.Comment;
import com.study.cook.dto.CommentDto;
import com.study.cook.service.CommentService;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class CommentApiController {

    private final CommentService commentService;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;

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

    @PostMapping
    public CommentDto create(@Valid @RequestBody CommentForm form) {
        
        Long commentId = commentService.create(form, memberFinder.getMember());
        Comment comment = commentService.findOneById(commentId);
        CommentDto commentDto = new CommentDto(commentId, comment.getContent(), comment.getRegDate(), comment.getMember().getId(), comment.getMember().getLoginId());

        return commentDto;
    }

    @PutMapping("/{commentId}")
    public Map<String, Object> update(@PathVariable Long commentId, @RequestBody @Valid CommentForm form) {

        commentService.update(commentId, form);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    @DeleteMapping("/{commentId}")
    public Map<String, Object> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "삭제되었습니다.");

        return map;
    }
}
