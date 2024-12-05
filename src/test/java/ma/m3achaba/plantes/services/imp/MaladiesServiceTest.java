package ma.m3achaba.plantes.services.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.mapper.MaladiesMapper;
import ma.m3achaba.plantes.model.Maladies;
import ma.m3achaba.plantes.repo.MaladiesRepository;
import ma.m3achaba.plantes.services.imp.MaladiesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MaladiesServiceTest {

    @Mock
    private MaladiesRepository maladiesRepository;

    @Mock
    private MaladiesMapper maladiesMapper;

    @InjectMocks
    private MaladiesService maladiesService;

    private Maladies mockMaladie;
    private MaladiesResponse mockMaladieResponse;
    private MaladiesRequest mockMaladieRequest;

    @BeforeEach
    void setUp() {
        mockMaladie = Maladies.builder()
                .id(1L)
                .name("Test Maladie")
                .createdDate(LocalDateTime.now())
                .build();

        mockMaladieResponse = MaladiesResponse.builder()
                .id(1L)
                .name("Test Maladie")
                .dateCreated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();

        mockMaladieRequest = new MaladiesRequest("Test Maladie");
    }

    @Test
    void findById_ExistingId_ReturnsOptionalMaladieResponse() {
        // Arrange
        when(maladiesRepository.findById(1L)).thenReturn(Optional.of(mockMaladie));
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        Optional<MaladiesResponse> result = maladiesService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockMaladieResponse, result.get());
        verify(maladiesRepository).findById(1L);
        verify(maladiesMapper).toResponse(mockMaladie);
    }

    @Test
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        when(maladiesRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> maladiesService.findById(99L));
    }

    @Test
    void findAll_ReturnsPageOfMaladiesResponses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Maladies> mockPage = new PageImpl<>(Arrays.asList(mockMaladie));

        when(maladiesRepository.findAll(pageable)).thenReturn(mockPage);
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        PageResponse<MaladiesResponse> result = maladiesService.findAll(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockMaladieResponse, result.getContent().get(0));
    }

    @Test
    void findAll_ReturnsListOfMaladiesResponses() {
        // Arrange
        List<Maladies> mockList = Arrays.asList(mockMaladie);

        when(maladiesRepository.findAll()).thenReturn(mockList);
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        List<MaladiesResponse> result = maladiesService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockMaladieResponse, result.get(0));
    }

    @Test
    void save_NewMaladie_ReturnsOptionalMaladieResponse() {
        // Arrange
        when(maladiesRepository.existsByName("Test Maladie")).thenReturn(false);
        when(maladiesMapper.toEntity(mockMaladieRequest)).thenReturn(mockMaladie);
        when(maladiesRepository.save(mockMaladie)).thenReturn(mockMaladie);
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        Optional<MaladiesResponse> result = maladiesService.save(mockMaladieRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockMaladieResponse, result.get());
    }

    @Test
    void save_ExistingMaladie_ThrowsEntityNotFoundException() {
        // Arrange
        when(maladiesRepository.existsByName("Test Maladie")).thenReturn(true);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> maladiesService.save(mockMaladieRequest));
    }

    @Test
    void update_ExistingMaladie_ReturnsUpdatedMaladieResponse() {
        // Arrange
        Maladies existingMaladie = Maladies.builder()
                .id(1L)
                .name("Old Name")
                .build();

        MaladiesRequest updateRequest = new MaladiesRequest("New Name");

        when(maladiesRepository.findById(1L)).thenReturn(Optional.of(existingMaladie));
        when(maladiesRepository.save(existingMaladie)).thenReturn(existingMaladie);
        when(maladiesMapper.toResponse(existingMaladie)).thenReturn(
                MaladiesResponse.builder()
                        .id(1L)
                        .name("New Name")
                        .build()
        );

        // Act
        Optional<MaladiesResponse> result = maladiesService.update(updateRequest, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        verify(maladiesRepository).save(existingMaladie);
    }

    @Test
    void delete_ExistingMaladie_ReturnsDeletedMaladieResponse() {
        // Arrange
        when(maladiesRepository.findById(1L)).thenReturn(Optional.of(mockMaladie));
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        Optional<MaladiesResponse> result = maladiesService.delete(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockMaladieResponse, result.get());
        verify(maladiesRepository).delete(mockMaladie);
    }

    @Test
    void findAllWithSearch_ReturnsFilteredPageOfMaladiesResponses() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Maladies> mockPage = new PageImpl<>(Arrays.asList(mockMaladie));

        when(maladiesRepository.findAllByNameContainingIgnoreCase("Test", pageable))
                .thenReturn(mockPage);
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        PageResponse<MaladiesResponse> result = maladiesService.findAllWithsearch(0, 10, "Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockMaladieResponse, result.getContent().get(0));
    }

    @Test
    void findAllWithSearch_ReturnsFilteredListOfMaladiesResponses() {
        // Arrange
        List<Maladies> mockList = Arrays.asList(mockMaladie);

        when(maladiesRepository.findAllByNameContainingIgnoreCase("Test"))
                .thenReturn(mockList);
        when(maladiesMapper.toResponse(mockMaladie)).thenReturn(mockMaladieResponse);

        // Act
        List<MaladiesResponse> result = maladiesService.findAllWithsearch("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockMaladieResponse, result.get(0));
    }
}