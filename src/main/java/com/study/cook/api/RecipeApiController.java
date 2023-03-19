package com.study.cook.api;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Member;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.service.RecipeFieldService;
import com.study.cook.service.RecipeService;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
@Slf4j
public class RecipeApiController {

    private final RecipeService recipeService;
    private final RecipeFieldService recipeFieldService;
    private final MemberFinder memberFinder;
    private final FileStore fileStore;

    @ResponseBody
    @PostMapping
    public ResultVO create(@Valid @RequestPart RecipeForm recipeForm, @RequestPart MultipartFile imageFile,
                           @Valid @RequestPart List<String> fieldForms, @RequestPart List<MultipartFile> multipartFiles,  // @RequestPart를 사용하면 Json 파일로 넘어온 데이터를 바인딩
                           HttpSession session) {

        if (fieldForms.size() != multipartFiles.size()) {
            return new ResultVO("등록에 실패하였습니다! 선택을 안하거나 빈칸이 있는지 확인해주세요.", "/recipes", false);
        }
        Member member = memberFinder.getMember(session);

        Long id = null;
        int cnt = 0;
        try {
            id = recipeService.create(recipeForm, imageFile, member);
            for (String fieldForm : fieldForms) {
                Long fieldId = recipeFieldService.create(id, fieldForm, multipartFiles.get(cnt), member);
                cnt++;
            }

        } catch (StoreFailException e) {
            return new ResultVO(e.getMessage(), "/recipes", false);
        }

        return new ResultVO("등록되었습니다!", "/recipes/" + id, true);

    }


    // <img> 이미지 조회 -> 이미지 바이너리 반환
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));  // 파일에 직접 접근해서 스트링으로 반환
    }

    @ResponseBody
    @PutMapping("/{recipeId}")
    public ResultVO update(@PathVariable Long recipeId,
                           @Valid @RequestPart RecipeForm recipeForm, @RequestPart(required = false) Optional<MultipartFile> imageFile,
                           @Valid @RequestPart List<String> fieldForms, @RequestPart(required = false) Optional<List<MultipartFile>> multipartFiles,
                           @RequestPart(required = false) Optional<List<Integer>> imgIndexes, HttpSession session) throws StoreFailException {

        if ((multipartFiles.isPresent() && imgIndexes.isPresent())) {
            if (multipartFiles.get().size() != imgIndexes.get().size())
                return new ResultVO("저장에 실패하였습니다! 선택을 안하거나 입력칸에 빈칸이 있는지 확인해주세요.", "/recipes/" + recipeId + "/edit", false);
        }

        Member member = memberFinder.getMember(session);

        try {
            recipeService.update(recipeId, recipeForm, imageFile);
            recipeFieldService.update(recipeId, fieldForms, multipartFiles, imgIndexes, member);

        } catch (StoreFailException e) {
            return new ResultVO(e.getMessage(), "/recipes/" + recipeId + "/edit", false);
        }

        return new ResultVO("저장되었습니다!", "/recipes/" + recipeId, true);
    }

}
