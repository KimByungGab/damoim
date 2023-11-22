package com.sideproject.damoim.advice;

import com.sideproject.damoim.advice.exception.*;
import com.sideproject.damoim.response.CommonResult;
import com.sideproject.damoim.service.ResponseService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Iterator;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    // Swagger 3의 2.2.0버전에서는 @ResponseStatus를 사용하면 자동으로 response에 작성된다. (공식 문서)
    // 때문에 이와 같이 전역으로 작성되는 controller advice에는 swagger 문서 내의 모든 요청의 모든 응답에 이 예외들이 전부 등록된다.
    // 그렇기 때문에 @Hidden을 사용함으로서 response에서 제외시킨다.

    // 기본 예외처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Hidden
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("unKnown.code")), getMessage("unKnown.msg"));
    }

    // Request DTO Valid 검사 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Hidden
    protected CommonResult validationDTOException(HttpServletRequest request, MethodArgumentNotValidException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("Invalid.code")), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    // RequestParam 혹은 PathVariable 등 controller에 적용되는 Valid 검사 예외처리
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Hidden
    protected CommonResult validationException(HttpServletRequest request, ConstraintViolationException e) {
        Iterator iterator = e.getConstraintViolations().iterator();
        ConstraintViolationImpl impl = (ConstraintViolationImpl) iterator.next();

        return responseService.getFailResult(Integer.parseInt(getMessage("Invalid.code")), impl.getMessageTemplate());
    }

    // Jwt 인증 예외처리
    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    protected CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("ErrorJwtToken.code")), getMessage("ErrorJwtToken.msg"));
    }

    // Jwt 권한 예외처리
    @ExceptionHandler(CAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Hidden
    protected CommonResult accessDeniedException(HttpServletRequest request, CAccessDeniedException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("AccessDenied.code")), getMessage("AccessDenied.msg"));
    }

    // 이메일 중복 시
    @ExceptionHandler(CDuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult duplicateEmailException(HttpServletRequest request, CDuplicateEmailException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("duplicateEmail.code")), getMessage("duplicateEmail.msg"));
    }

    // 닉네임 중복 시
    @ExceptionHandler(CDuplicateNicknameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult duplicateNicknameException(HttpServletRequest request, CDuplicateNicknameException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("duplicateNickname.code")), getMessage("duplicateNickname.msg"));
    }

    // 비밀번호와 확인번호가 불일치 시
    @ExceptionHandler(CNotSamePasswordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult notSamePasswordException(HttpServletRequest request, CNotSamePasswordException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("notSamePassword.code")), getMessage("notSamePassword.msg"));
    }

    // 로그인 실패 시
    @ExceptionHandler(CLoginFailException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @Hidden
    protected CommonResult loginFailException(HttpServletRequest request, CLoginFailException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("loginFail.code")), getMessage("loginFail.msg"));
    }

    // 사용자 검색을 못 했을 때
    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    // 찾는 자료가 없을 때 예외
    @ExceptionHandler(CContentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Hidden
    protected CommonResult contentNotFoundException(HttpServletRequest request, CContentNotFoundException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("notFoundContent.code")), getMessage("notFoundContent.msg"));
    }

    // 중복된 컨텐츠일 떄 예외
    @ExceptionHandler(CAlreadyExistContentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult alreadyExistContentException(HttpServletRequest request, CAlreadyExistContentException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("alreadyExistContent.code")), getMessage("alreadyExistContent.msg"));
    }

    // 처리 완료된 건일 때 예외
    @ExceptionHandler(CAlreadyProcessedContentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult alreadyProcessedContentException(HttpServletRequest request, CAlreadyProcessedContentException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("alreadyProcessedContent.code")), getMessage("alreadyProcessedContent.msg"));
    }

    // 본인의 것에 작업하면 안 되는 경우 예외
    @ExceptionHandler(CCanNotProcessYourOwnException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @Hidden
    protected CommonResult canNotProcessYourOwnException(HttpServletRequest request, CCanNotProcessYourOwnException e) {
        return responseService.getFailResult(Integer.parseInt(getMessage("canNotProcessYourOwn.code")), getMessage("canNotProcessYourOwn.msg"));
    }

    // code 정보에 해당하는 메시지 조회
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code 정보, 추가 argument로 현재 locale에 맞는 메시지 조회
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
