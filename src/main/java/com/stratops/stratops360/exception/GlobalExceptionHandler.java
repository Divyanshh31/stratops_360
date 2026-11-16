package com.stratops.stratops360.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(Exception ex, HttpServletRequest request, Model model) {
        log.warn("Resource not found: {} on URI: {}", ex.getMessage(), request.getRequestURI());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("errorCode", 404);
        return "error/404";
    }

    @ExceptionHandler(InvalidDecisionTransitionException.class)
    public String handleInvalidTransition(InvalidDecisionTransitionException ex, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        log.warn("Invalid decision transition attempted: {}", ex.getMessage());
        redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/decision/pipeline");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled internal server exception on URI: {}", request.getRequestURI(), ex);
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred.");
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("errorCode", 500);
        return "error/500";
    }
}
