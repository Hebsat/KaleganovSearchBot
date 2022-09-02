package main.controllers;

import main.exceptions.ErrorMessages;
import main.model.Site;
import main.services.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    void searchPages() throws Exception {
        when(searchService.queryValidation(any())).thenReturn(true);
        when(searchService.getSite(any())).thenReturn(new Site());
        mockMvc.perform(get("/admin/search")
                        .param("query",  "q")
                        .param("site",  "s")
                        .param("offset",  "0"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void searchPagesBlankRequest() throws Exception {
        mockMvc.perform(get("/admin/search")
                        .param("query",  "")
                        .param("site",  "")
                        .param("offset",  "0"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.EMPTY_QUERY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchPagesIncorrectRequest() throws Exception {
        when(searchService.queryValidation(any())).thenReturn(false);
        mockMvc.perform(get("/admin/search")
                        .param("query",  "q")
                        .param("site",  "")
                        .param("offset",  "0"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.INCORRECT_QUERY + "q"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchPagesBlankSite() throws Exception {
        when(searchService.queryValidation(any())).thenReturn(true);
        mockMvc.perform(get("/admin/search")
                        .param("query",  "q")
                        .param("site",  "")
                        .param("offset",  "0"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.EMPTY_SITE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchPagesSiteNotIndexed() throws Exception {
        when(searchService.queryValidation(any())).thenReturn(true);
        when(searchService.getSite(any())).thenReturn(null);
        mockMvc.perform(get("/admin/search")
                        .param("query",  "q")
                        .param("site",  "s")
                        .param("offset",  "0"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.UNINDEXED_SITE))
                .andExpect(status().isBadRequest());
    }
}