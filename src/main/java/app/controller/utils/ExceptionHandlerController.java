package app.controller.utils;

import app.error.BusinessError;
import app.error.RestError;
import app.error.TechnicalError;
import app.exception.UserException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestError handleBusinessRuleValidationError(
            HttpServletRequest request, HttpServletResponse response, Exception exception) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        if (exception instanceof UserException) {
            UserException userException = (UserException) exception;
            return new BusinessError(userException.getMessageKey(), userException.getArguments());
        } else {
            return new TechnicalError(exception);
        }
    }
}
