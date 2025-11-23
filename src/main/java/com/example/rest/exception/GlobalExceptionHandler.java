package com.example.rest.exception;

import com.example.rest.dto.CustomerResponseDto;
import com.example.rest.dto.api.JsonApiData;
import com.example.rest.dto.api.JsonApiResponse;
import com.example.rest.model.ExceptionLog;
import com.example.rest.repository.ExceptionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionLogRepository logRepo;

    public GlobalExceptionHandler(ExceptionLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("not_found", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("bad_request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("server_error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult br = ex.getBindingResult();
        String errors = br.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        // Persist exception log
        ExceptionLog log = new ExceptionLog();
        log.setPath(request.getRequestURI());
        log.setExceptionType(MethodArgumentNotValidException.class.getSimpleName());
        log.setMessage(ex.getMessage());
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.setRequestPayload(payload);
        } catch (Exception e) {
            // ignore
        }
        log.setValidationErrors(errors);
        logRepo.save(log);

        JsonApiData<Object> data = new JsonApiData<>();

        data.type = "VALIDATION_ERROR";
        data.attributes = errors;
        JsonApiResponse<Object> response = new JsonApiResponse<>();
        response.data = data;

        return ResponseEntity.badRequest()
                .contentType(MediaType.parseMediaType("application/vnd.api+json"))
                .body(response);
    }
}
