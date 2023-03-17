package com.study.cook.controller;

import com.study.cook.domain.Comment;
import com.study.cook.dto.CommentDto;
import com.study.cook.service.*;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    private final MemberService memberService;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;

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
    public Map<String, Object> create(@Valid @RequestBody CommentForm form, BindingResult result, HttpSession session) {

        Map<String, Object> map = new HashMap<>();

        if (result.hasErrors()) {
            ResultVO resultVO = new ResultVO("댓글 등록에 실패했습니다.", "", false);
            map.put("result", resultVO);
            return map;
        }

        try {
            Long commentId = commentService.create(form, session);
            Comment comment = commentService.findOneById(commentId);
            CommentDto commentDto = new CommentDto(commentId, comment.getContent(), comment.getRegDate(), comment.getMember().getId(), comment.getMember().getLoginId());
            map.put("commentDto", commentDto);
        } catch (IllegalArgumentException e) {
            map.put("msg", e.getMessage());
        }

        return map;
    }


    @ResponseBody
    @PutMapping("/{commentId}")
    public ResultVO update(@PathVariable Long commentId, @RequestBody @Valid CommentForm form, BindingResult result) {

        ResultVO resultVO = new ResultVO();
        if (result.hasErrors()) {
            resultVO.setMsg("수정에 실패했습니다.");
            resultVO.setSuccess(false);
            return resultVO;
        }

        try {
            commentService.update(commentId, form);
        } catch (IllegalArgumentException e) {
            resultVO.setMsg(e.getMessage());
            resultVO.setSuccess(false);
            return resultVO;
        }

//        resultVO.setMsg("수정하였습니다!");
        return resultVO;
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
