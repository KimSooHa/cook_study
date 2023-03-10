package com.study.cook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import com.study.cook.dto.RecipeDto;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.service.CategoryService;
import com.study.cook.service.HeartService;
import com.study.cook.service.RecipeFieldService;
import com.study.cook.service.RecipeService;
import com.study.cook.util.DateParser;
import com.study.cook.util.JsonMaker;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeFieldService recipeFieldService;
    private final CategoryService categoryService;
    private final HeartService heartService;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;
    private final FileStore fileStore;
    private final JsonMaker jsonMaker;



    @GetMapping("/list")
    public String list(HttpServletRequest request, Model model) {
        List<Recipe> list = recipeService.findList();

        model.addAttribute("recipes", list);

        return "recipe/list";
    }

    @GetMapping
    public String createForm(Model model) {
        List<Category> categories = categoryService.findList();

        model.addAttribute("recipeForm", new RecipeForm());
        model.addAttribute("fieldForm", new RecipeFieldForm());
        model.addAttribute("categories", categories);

        return "recipe/create-form";
    }

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String create(Model model, @ModelAttribute @Valid @RequestPart RecipeForm form, BindingResult result,
//            HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            log.info("errors={}", result);
//            return "recipe/create-form";
//        }
//        Member member = memberFinder.getMember(session);
//
//        List<RecipeFieldForm> fieldForms = form.getFieldForms();
//
//        Long id = null;
//        try {
//            id = recipeService.create(form, member);
//            for (RecipeFieldForm fieldForm : fieldForms) {
//                Long fieldId = recipeFieldService.create(id, fieldForm, member);
//            }
//
//        } catch (StoreFailException e) {
//            model.addAttribute("msg", "????????? ??????????????????.");
//            model.addAttribute("url", "/recipes");
//            log.info("?????? ??????!");
//            return "recipe/create-form";
//        }
//
//
//        redirectAttributes.addAttribute("recipeId", id);
//        log.info("?????? ??????! id = {}", id);
//        model.addAttribute("msg", "?????????????????????!");
//        model.addAttribute("url", "/recipes/{id}");
//
//        return "redirect:/recipes/{id}";
//    }
    @ResponseBody
    @PostMapping //(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String create(Model model,
                              @Valid @RequestPart RecipeForm recipeForm, @RequestPart MultipartFile imageFile,
                                @Valid @RequestPart List<String> fieldForms, @RequestPart List<MultipartFile> multipartFiles,  // @RequestPart??? ???????????? Json ????????? ????????? ???????????? ?????????

                         BindingResult result,
                              HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) throws JsonProcessingException {

        Map<String, Object> results = new HashMap<>();
        if (result.hasErrors() || fieldForms.size() != multipartFiles.size()) {
            log.info("errors={}", result);
            results.put("SUCCESS", false);
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
            results.put("SUCCESS", false);
            results.put("msg", "????????? ??????????????????.");
            results.put("url", "/recipes");

            // json?????? ??????
            log.info("?????? ??????!");
            String json = jsonMaker.getJson(results);
            return json;
        }


        redirectAttributes.addAttribute("recipeId", id);
        log.info("?????? ??????! id = {}", id);
        results.put("SUCCESS", true);
        model.addAttribute("msg", "?????????????????????!");
        model.addAttribute("url", "/recipes/{id}");

        // json?????? ??????
        return jsonMaker.getJson(results);
    }

//    @PostMapping(value = "/recipeFields", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String create(Model model, @ModelAttribute @Valid @RequestPart RecipeFieldForm fieldform, @RequestPart MultipartFile imageFile,
//                         BindingResult result,
//                         HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            log.info("errors={}", result);
//            return "recipe/create-form";
//        }
//        Member member = memberFinder.getMember(session);
//
////        List<RecipeFieldForm> fieldForms = form.getFieldForms();
//
//        Long id = null;
//        try {
////            id = recipeService.create(form, member);
////            for (RecipeFieldForm fieldForm : fieldForms) {
//            Long fieldId = recipeFieldService.create(id, fieldform, imageFile, member);
////            }
//
//        } catch (StoreFailException e) {
//            model.addAttribute("msg", "????????? ??????????????????.");
//            model.addAttribute("url", "/recipes");
//            log.info("?????? ??????!");
//            return "recipe/create-form";
//        }
//
//
//        redirectAttributes.addAttribute("recipeId", id);
//        log.info("?????? ??????! id = {}", id);
//        model.addAttribute("msg", "?????????????????????!");
//        model.addAttribute("url", "/recipes/{id}");
//
//        return "redirect:/recipes/{id}";
//    }

    // <img> ????????? ?????? -> ????????? ???????????? ??????
    @ResponseBody
    @GetMapping("images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));  // ????????? ?????? ???????????? ??????????????? ??????
    }


    // ?????? ????????????
    @GetMapping("/attach/{recipeId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long recipeId) throws MalformedURLException {
        Recipe recipe = recipeService.findOneById(recipeId);
        String storeFileName = recipe.getPhoto().getStoreFileName();
        String uploadFileName = recipe.getPhoto().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8); // UriUtils: ????????? ?????????????????? ??????
        String contentDisposition = "Attachment; filename=\"" + encodedUploadFileName + "\"";    // ?????? ??????????????? ??? ????????? ????????? ????????? ??????(????????? ?????????)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }


    @GetMapping("/{recipeId}")
    public String detail(@PathVariable Long recipeId, Model model, HttpSession session) {
        Recipe recipe = recipeService.findOneById(recipeId);
//        Long heartCount = heartService.countByRecipe(recipe);
        RecipeDto recipeDto = new RecipeDto(recipe.getTitle(), recipe.getIntroduction(), recipe.getPhoto(), recipe.getIngredients(), recipe.getCookingTime(), recipe.getServings(), recipe.getCategory().getName());
        Member member = recipe.getMember();

        model.addAttribute("recipeDto", recipeDto);

        Member loginMember = memberFinder.getMember(session);

        if (member.getId().equals(loginMember.getId())) {    // ???????????? ????????? ???????????? ????????? ??????????????? ?????? ?????? ????????????!
            return "recipe/detail-manager";
        }
        return "recipe/detail";
    }

    @GetMapping("/{recipeId}/edit")
    public String update(@PathVariable Long recipeId, Model model) {

        Recipe recipe = recipeService.findOneById(recipeId);

        RecipeForm recipeForm = new RecipeForm();
        recipeForm.getTitle();
        recipeForm.getIngredients();
        recipeForm.getIntroduction();
        recipeForm.getImageFile();
        recipeForm.getCookingTime();
        recipeForm.getCategory();
        recipeForm.getServings();

        model.addAttribute("form", recipeForm);
        return "recipe/update-form";
    }


    @PutMapping("/{recipeId}/edit")
    public String update(@PathVariable Long recipeId, @Valid RecipeForm form, BindingResult result) throws StoreFailException {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "recipe/update-form";
        }

        recipeService.update(recipeId, form);

        return "redirect:/mypage";
    }

    // ??????
    @DeleteMapping("/{recipeId}")
    public String delete(@PathVariable Long recipeId) {
        recipeService.delete(recipeId);

        return "redirect:/";
    }
}
