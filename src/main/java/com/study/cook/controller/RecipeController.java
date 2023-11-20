package com.study.cook.controller;

import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import com.study.cook.domain.RecipeField;
import com.study.cook.dto.RecipeDto;
import com.study.cook.dto.RecipeFieldDto;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.service.CategoryService;
import com.study.cook.service.RecipeFieldService;
import com.study.cook.service.RecipeService;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final CategoryService categoryService;
    private final MemberFinder memberFinder;


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
    public String createdList(String categoryName, @RequestParam(defaultValue = "") String title, Model model, @PageableDefault(size = 8, sort = "regDate", direction = DESC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Member member = memberFinder.getMember();
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


    @GetMapping("/{recipeId}")
    public String detail(@PathVariable Long recipeId, Model model) {
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

        Member loginMember = memberFinder.getMember();
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

    // 삭제
    @DeleteMapping("/{recipeId}")
    public String delete(@PathVariable Long recipeId, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "/recipes/list") String redirectURL) {
        recipeService.delete(recipeId);
        redirectAttributes.addFlashAttribute("msg", "삭제하였습니다.");
        return "redirect:" + redirectURL + "?categoryName=";
    }
}
