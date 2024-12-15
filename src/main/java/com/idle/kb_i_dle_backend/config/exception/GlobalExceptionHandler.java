package com.idle.kb_i_dle_backend.config.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.response.ErrorResponse;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    /**
     * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField()).append(":");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(", ");
        }
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, String.valueOf(stringBuilder));
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     *
     * @param ex MissingRequestHeaderException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        log.error("AuthenticationException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_UNAUTHOR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_UNAUTHOR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    /**
     * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
     *
     * @param ex HttpMessageNotReadableException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
     *
     * @param ex MissingServletRequestParameterException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMissingRequestHeaderExceptionException(
            MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR,
                ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * [Exception] 잘못된 서버 요청일 경우 발생한 경우
     *
     * @param e HttpClientErrorException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<ErrorResponseDTO> handleBadRequestException(HttpClientErrorException e) {
        log.error("HttpClientErrorException.BadRequest", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);

        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }


    /**
     * [Exception] 잘못된 주소로 요청 한 경우
     *
     * @param e NoHandlerFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundExceptionException(NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundExceptionException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingPathVariableException(MissingPathVariableException e) {
        log.error("handleMissingPathVariableException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, e.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] NULL 값이 발생한 경우
     *
     * @param e NullPointerException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNullPointerException(NullPointerException e) {
        log.error("handleNullPointerException", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, e.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);

        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * Input / Output 내에서 발생한 경우
     *
     * @param ex IOException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponseDTO> handleIOException(IOException ex) {
        log.error("handleIOException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.IO_ERROR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);

        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }


    /**
     * com.google.gson 내에 Exception 발생하는 경우
     *
     * @param ex JsonParseException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ErrorResponseDTO> handleJsonParseExceptionException(JsonParseException ex) {
        log.error("handleJsonParseExceptionException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.JSON_PARSE_ERROR, ex.getMessage());
        final ErrorResponseDTO response = new ErrorResponseDTO(errorResponse);

        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * com.fasterxml.jackson.core 내에 Exception 발생하는 경우
     *
     * @param ex JsonProcessingException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ErrorResponseDTO> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("handleJsonProcessingException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(errorResponseDTO, HTTP_STATUS_OK);
    }

    @ExceptionHandler(ParseException.class)
    protected ResponseEntity<ErrorResponseDTO> hadleParseException(ParseException ex) {
        log.error("hadleParseException", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(errorResponseDTO, HTTP_STATUS_OK);
    }

//    @ExceptionHandler(MissingRequestHeaderException.class)
//    public ResponseEntity<SuccessResponseDTO> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
//        SuccessResponseDTO response = new SuccessResponseDTO(false, "인증 토큰이 필요합니다.");
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//    }

    //CustomExeption

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseDTO> handleCustomException(CustomException ex) {
        log.error("handleCustomException", ex);
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(errorResponseDTO, HTTP_STATUS_OK);
    }

    // ==================================================================================================================

    /**
     * [Exception] 모든 Exception 경우 발생
     *
     * @param ex Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
        log.error("Exception", ex);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        return new ResponseEntity<>(errorResponseDTO, HTTP_STATUS_OK);
    }
}
