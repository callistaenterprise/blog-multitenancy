package se.callista.blog.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import se.callista.blog.service.model.ErrorMessage;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class AbstractBaseApiController {

    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<ErrorMessage> handleApiException(ApiException ex, WebRequest request) {
        HttpStatus status = ex.getStatus();
        ErrorMessage errorDetails =
                ErrorMessage.builder()
                        .timestamp(ex.getTimestamp())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(ex.getMessage())
                        .build();
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            HttpServletRequest servletRequest = servletWebRequest.getNativeRequest(HttpServletRequest.class);
            errorDetails.setPath(servletRequest.getRequestURI());
        }
        return new ResponseEntity<>(errorDetails, status);
    }

}
