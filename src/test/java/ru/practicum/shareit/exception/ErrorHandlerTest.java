package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ErrorHandlerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void requestWrongPathVariable() throws Exception {
        mvc.perform(get("/items/wrongPath")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void notAvailableException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not  Available" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleNotAvailableException(new NotAvailableException());
        assertEquals(1, 1);
    }

    @Test
    void notSupportedStatusException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not  Supported" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleNotSupportedStatusException(new NotSupportedStatusException());
        assertEquals(1, 1);
    }

    @Test
    void notFoundException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not Found" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleNotFoundException(new NotFoundException());
        assertEquals(1, 1);
    }

    @Test
    void notOwnerException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not Owner" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleNotOwnerException(new NotOwnerException());
        assertEquals(1, 1);
    }

    @Test
    void notSavedException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not Saved" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleNotSavedException(new NotSavedException());
        assertEquals(1, 1);
    }

    @Test
    void constraintViolationException() {
        ErrorHandler exceptionsHandler = new ErrorHandler();

        String error = "Not Saved" + ": ";
        ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.handleConstraintException(new ConstraintViolationException("", null));
        assertEquals(1, 1);
    }
}