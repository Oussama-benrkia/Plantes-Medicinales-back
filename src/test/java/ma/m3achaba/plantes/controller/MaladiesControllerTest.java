package ma.m3achaba.plantes.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.services.imp.MaladiesService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MaladiesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MaladiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaladiesService maladiesService;

    @Autowired
    private ObjectMapper objectMapper;

    private MaladiesResponse mockResponse;
    private MaladiesRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockResponse = MaladiesResponse.builder()
                .id(1L)
                .name("Test Maladie")
                .build();

        mockRequest = new MaladiesRequest("Test Maladie");
    }

    @Test
    void findById_ExistingId_ReturnsOk() throws Exception {
        when(maladiesService.findById(1L)).thenReturn(Optional.of(mockResponse));

        mockMvc.perform(get("/api/maladies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Maladie"));
    }

    @Test
    void findById_NonExistingId_ThrowsResourceNotFoundException() throws Exception {
        when(maladiesService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/maladies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_WithoutSearch_ReturnsPageResponse() throws Exception {
        PageResponse<MaladiesResponse> pageResponse = PageResponse.<MaladiesResponse>builder()
                .content(Arrays.asList(mockResponse))
                .build();

        when(maladiesService.findAll(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/maladies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Maladie"));
    }
    @Test
    void findAll_WithSearch_ReturnsPageResponse() throws Exception {
        String searchTerm = "test";
        PageResponse<MaladiesResponse> pageResponse = PageResponse.<MaladiesResponse>builder()
                .content(Arrays.asList(mockResponse))
                .build();

        when(maladiesService.findAllWithsearch(0, 10,searchTerm)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/maladies?search=" + searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Maladie"));
    }

    @Test
    void saveMaladie_ValidRequest_CreatesResource() throws Exception {
        when(maladiesService.save(any(MaladiesRequest.class))).thenReturn(Optional.of(mockResponse));

        mockMvc.perform(post("/api/maladies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Maladie"));
    }
    @Test
    void findAllMaladies_WithSearch_ReturnsList() throws Exception {
        String searchTerm = "test";
        List<MaladiesResponse> responses = Arrays.asList(mockResponse);
        when(maladiesService.findAllWithsearch(searchTerm)).thenReturn(responses);

        mockMvc.perform(get("/api/maladies/list?search=" + searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Maladie"));
    }
    @Test
    void findAllMaladies_WithoutSearch_ReturnsList() throws Exception {
        List<MaladiesResponse> responses = Arrays.asList(mockResponse);
        when(maladiesService.findAll()).thenReturn(responses);

        mockMvc.perform(get("/api/maladies/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Maladie"));
    }
    @Test
    void updateMaladie_ValidRequest_UpdatesResource() throws Exception {
        when(maladiesService.update(any(MaladiesRequest.class), eq(1L)))
                .thenReturn(Optional.of(mockResponse));

        mockMvc.perform(put("/api/maladies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Maladie"));
    }

    @Test
    void deleteMaladie_ExistingId_DeletesResource() throws Exception {
        when(maladiesService.delete(1L)).thenReturn(Optional.of(mockResponse));

        mockMvc.perform(delete("/api/maladies/1"))
                .andExpect(status().isNoContent());
    }
}