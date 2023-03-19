package com.study.cook.api;

import com.study.cook.controller.ClubForm;
import com.study.cook.exception.FindClubException;
import com.study.cook.service.ClubService;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")
@Slf4j
public class ClubApiController {

    private final ClubService clubService;


    @PostMapping
    public ResultVO create(@Valid @RequestBody ClubForm form, HttpSession session) {
        Long clubId = clubService.create(form, session);
        return new ResultVO("등록하였습니다.", "/clubs/" + clubId + "/detail", true);
    }

    @PutMapping("/{clubId}")
    public ResultVO update(@PathVariable Long clubId, @RequestBody @Valid ClubForm form) {

        try {
            clubService.update(clubId, form);
        } catch (IllegalArgumentException e) {
            throw new FindClubException("수정 실패: " + e.getMessage());
        }

        return new ResultVO("수정하였습니다!", "/clubs/" + clubId + "/detail", true);
    }
}
