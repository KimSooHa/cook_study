package com.study.cook.controller;

import com.study.cook.exception.*;
import com.study.cook.util.ResultVO;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(SelectMissException.class)
    public ResultVO selectExHandle(SelectMissException e) {
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg(e.getMessage());
        return resultVO;
    }

    @ExceptionHandler
    public ResultVO reserveExHandle(NoSuchElementException e) {
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg(e.getMessage());
        return resultVO;
    }

    // 데이터 찾기 예외
    @ExceptionHandler({FindClubException.class, FindCommentException.class, FindRecipeException.class,
            FindScheduleException.class, FindReservationException.class, FindCookingRoomException.class})
    public ResultVO findExHandle(IllegalArgumentException e) {
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg(e.getMessage());
        return resultVO;
    }


    // @Valid 예외
    @ExceptionHandler
    public ResultVO processValidationError(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bindingResult.getAllErrors().size(); i++) {
            stringBuilder.append(bindingResult.getAllErrors().get(i).getDefaultMessage());
            if(i == (bindingResult.getAllErrors().size()-1))
                continue;

            stringBuilder.append(", ");
        }

        ResultVO resultVO = new ResultVO();
        resultVO.setMsg(stringBuilder.toString());
        return resultVO;
    }
}
