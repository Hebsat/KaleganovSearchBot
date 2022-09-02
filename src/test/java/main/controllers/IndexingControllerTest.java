package main.controllers;

import main.exceptions.ErrorMessages;
import main.response.DetailedStatistics;
import main.response.ResponseStatistics;
import main.response.TotalStatistics;
import main.services.IndexingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.anything;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IndexingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingService indexingService;

    @Test
    void failedStartIndexing() throws Exception {
        when(indexingService.isIndexing()).thenReturn(true);
        mockMvc.perform(get("/admin/startIndexing"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.INDEXING_STARTED_YET))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startIndexing() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        mockMvc.perform(get("/admin/startIndexing"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void failedStopIndexing() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        mockMvc.perform(get("/admin/stopIndexing"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.INDEXING_NOT_STARTED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stopIndexing() throws Exception {
        when(indexingService.isIndexing()).thenReturn(true);
        mockMvc.perform(get("/admin/stopIndexing"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void indexSingleSite() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        when(indexingService.indexSiteValidation(any())).thenReturn(true);
        mockMvc.perform(post("/admin/indexPage").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void failedIndexSingleSite() throws Exception {
        when(indexingService.isIndexing()).thenReturn(true);
        mockMvc.perform(post("/admin/indexPage").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.INDEXING_STARTED_YET))
                .andExpect(status().isBadRequest());
    }

    @Test
    void failedIndexSingleSiteOutOfRange() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        when(indexingService.indexSiteValidation(any())).thenReturn(false);
        mockMvc.perform(post("/admin/indexPage").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.SITE_OUT_OF_RANGE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void indexSinglePage() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        when(indexingService.indexPageValidation(any())).thenReturn(true);
        mockMvc.perform(post("/admin/indexPage1").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void failedIndexSinglePage() throws Exception {
        when(indexingService.isIndexing()).thenReturn(true);
        mockMvc.perform(post("/admin/indexPage1").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.INDEXING_STARTED_YET))
                .andExpect(status().isBadRequest());
    }

    @Test
    void failedIndexSinglePageOutOfRange() throws Exception {
        when(indexingService.isIndexing()).thenReturn(false);
        when(indexingService.indexPageValidation(any())).thenReturn(false);
        mockMvc.perform(post("/admin/indexPage1").param("url",  ""))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error").value(ErrorMessages.PAGE_OUT_OF_RANGE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatistics() throws Exception {
        when(indexingService.getStatistics()).thenReturn(new ResponseStatistics());
        mockMvc.perform(get("/admin/statistics"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.statistics", anything()))
                .andExpect(status().isOk());
    }
}