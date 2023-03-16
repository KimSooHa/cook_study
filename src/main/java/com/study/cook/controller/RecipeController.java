package com.study.cook.controller;

import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import com.study.cook.domain.RecipeField;
import com.study.cook.dto.*;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.service.*;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeFieldService recipeFieldService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final HeartService heartService;
    private final MemberFinder memberFinder;
    private final FileStore fileStore;


    @GetMapping("/list")
    public String list(String categoryName, @RequestParam(defaultValue = "") String title, Model model, @PageableDefault(size = 8, sort = "regDate", direction = DESC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Page<RecipeListDto> list = recipeService.findList(condition, pageable);
        List<Category> categories = categoryService.findList();

        model.addAttribute("list", list);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("categories", categories);
        model.addAttribute("title", title);
        model.addAttribute("categoryName", categoryName);

        return "recipe/list";
    }

    /**
     * 등록한 리스트
     */
    @GetMapping("/list/created")
    public String createdList(String categoryName, @RequestParam(defaultValue = "") String title, HttpSession session, Model model, @PageableDefault(size = 8, sort = "regDate", direction = DESC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Member member = memberFinder.getMember(session);
        Page<RecipeListDto> list = recipeService.findByMember(member.getId(), condition, pageable);
        List<Category> categories = categoryService.findList();

        model.addAttribute("list", list);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("categories", categories);
        model.addAttribute("title", title);
        model.addAttribute("categoryName", categoryName);

        return "recipe/created-list";
    }

    @GetMapping
    public String createForm(Model model) {
        List<Category> categories = categoryService.findList();

        model.addAttribute("recipeForm", new RecipeForm());
        model.addAttribute("fieldForm", new RecipeFieldForm());
        model.addAttribute("categories", categories);

        return "recipe/create-form";
    }


    @ResponseBody
    @PostMapping
    public ResultVO create(@Valid @RequestPart RecipeForm recipeForm, @RequestPart MultipartFile imageFile,
                           @Valid @RequestPart List<String> fieldForms, @RequestPart List<MultipartFile> multipartFiles,  // @RequestPart를 사용하면 Json 파일로 넘어온 데이터를 바인딩
                           BindingResult result, HttpSession session) {

        if (result.hasErrors() || fieldForms.size() != multipartFiles.size()) {
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
            return new ResultVO("등록에 실패하였습니다.", "/recipes", false);
        }

        log.info("등록 성공! id = {}", id);
        return new ResultVO("등록되었습니다!", "/recipes/" + id, true);

    }


    // <img> 이미지 조회 -> 이미지 바이너리 반환
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));  // 파일에 직접 접근해서 스트링으로 반환
    }


    // 파일 다운로드
//    @GetMapping("/attach/{recipeId}")
//    public ResponseEntity<Resource> downloadAttach(@PathVariable Long recipeId) throws MalformedURLException {
//        Recipe recipe = recipeService.findOneById(recipeId);
//        String storeFileName = recipe.getPhoto().getStoreFileName();
//        String uploadFileName = recipe.getPhoto().getUploadFileName();
//
//        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
//
//        log.info("uploadFileName={}", uploadFileName);
//        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8); // UriUtils: 파일을 인코딩하도록 지원
//        String contentDisposition = "Attachment; filename=\"" + encodedUploadFileName + "\"";    // 파일 다운로드할 때 필요한 헤더에 들어갈 정보(업로드 파일명)
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .body(resource);
//    }


    @GetMapping("/{recipeId}")
    public String detail(@PathVariable Long recipeId, Model model, HttpSession session) {
        Recipe recipe = recipeService.findOneById(recipeId);

        RecipeDto recipeDto = new RecipeDto(recipe.getTitle(), recipe.getIntroduction(), recipe.getPhoto(), recipe.getIngredients(), recipe.getCookingTime(), recipe.getServings(), recipe.getCategory().getName());
        Member member = recipe.getMember();

        Optional<List<RecipeField>> recipeFieldList = recipeFieldService.findByRecipeId(recipeId);
        if (recipeFieldList.isPresent()) {
            List<RecipeFieldDto> recipeFieldDtoList = recipeFieldList.get().stream().map(rf -> {
                RecipeFieldDto recipeFieldDto = new RecipeFieldDto(rf.getPhoto(), rf.getContent());
                return recipeFieldDto;
            }).collect(toList());

            model.addAttribute("recipeFieldDtoList", recipeFieldDtoList);
        }

        model.addAttribute("recipeDto", recipeDto);
        model.addAttribute("recipeId", recipeId);

        Member loginMember = memberFinder.getMember(session);
        model.addAttribute("member", member);
        model.addAttribute("loginMember", loginMember);

        return "recipe/detail";
    }

    @GetMapping("/{recipeId}/edit")
    public String update(@PathVariable Long recipeId, Model model) {

        Recipe recipe = recipeService.findOneById(recipeId);

        RecipeForm recipeForm = new RecipeForm();
        recipeForm.setTitle(recipe.getTitle());
        recipeForm.setIngredients(recipe.getIngredients());
        recipeForm.setIntroduction(recipe.getIntroduction());
        recipeForm.setImg(recipe.getPhoto());
        recipeForm.setCookingTime(recipe.getCookingTime());
        recipeForm.setCategoryId(recipe.getCategory().getId());
        recipeForm.setServings(recipe.getServings());
        List<Category> categories = categoryService.findList();

        Optional<List<RecipeField>> recipeFieldList = recipeFieldService.findByRecipeId(recipeId);
        List<RecipeFieldForm> fieldForms = recipeFieldList.get().stream().map(rf -> {
            RecipeFieldForm fieldForm = new RecipeFieldForm();
            fieldForm.setImg(rf.getPhoto());
            fieldForm.setContent(rf.getContent());
            return fieldForm;
        }).collect(toList());

        model.addAttribute("fieldForms", fieldForms);
        model.addAttribute("categories", categories);
        model.addAttribute("recipeForm", recipeForm);
        model.addAttribute("recipeId", recipeId);
        return "recipe/update-form";
    }

    @ResponseBody
    @PutMapping("/{recipeId}")
    public ResultVO update(@PathVariable Long recipeId,
                           @Valid @RequestPart RecipeForm recipeForm, @RequestPart(required = false) Optional<MultipartFile> imageFile,
                           @Valid @RequestPart List<String> fieldForms, @RequestPart(required = false) Optional<List<MultipartFile>> multipartFiles,
                           @RequestPart(required = false) Optional<List<Integer>> imgIndexes, BindingResult result, HttpSession session) throws StoreFailException {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return new ResultVO("저장에 실패하였습니다! 선택을 안하거나 입력칸에 빈칸이 있는지 확인해주세요.", "/recipes/" + recipeId + "/edit", false);
        }

        if ((multipartFiles.isPresent() && imgIndexes.isPresent())) {
            if (multipartFiles.get().size() != imgIndexes.get().size())
                return new ResultVO("저장에 실패하였습니다! 선택을 안하거나 입력칸에 빈칸이 있는지 확인해주세요.", "/recipes/" + recipeId + "/edit", false);
        }

        Member member = memberFinder.getMember(session);

        try {
            recipeService.update(recipeId, recipeForm, imageFile);
            recipeFieldService.update(recipeId, fieldForms, multipartFiles, imgIndexes, member);

        } catch (StoreFailException e) {
            return new ResultVO("저장에 실패하였습니다.", "/recipes/" + recipeId + "/edit", false);
        }

        return new ResultVO("저장되었습니다!", "/recipes/" + recipeId, true);

    }

    // 삭제
    @DeleteMapping("/{recipeId}")
    public String delete(@PathVariable Long recipeId, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "/recipes/list") String redirectURL) {
        recipeService.delete(recipeId);
        redirectAttributes.addFlashAttribute("msg", "삭제하였습니다.");
        return "redirect:" + redirectURL;
    }
}
