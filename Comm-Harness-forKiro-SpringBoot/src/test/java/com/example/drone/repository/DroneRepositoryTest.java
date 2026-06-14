package com.example.drone.repository;

import com.example.drone.domain.dto.DroneQueryConditions;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.enums.DroneStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DroneRepository 集成测试类.
 * <p>
 * 使用 @SpringBootTest 注解进行 MyBatis 层的集成测试，
 * 测试所有数据访问方法的正确性。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class DroneRepositoryTest {

    @Autowired
    private DroneRepository droneRepository;

    private Drone testDrone;

    @BeforeEach
    void setUp() {
        testDrone = Drone.builder()
                .serialNumber("TEST-001")
                .modelName("Test Model")
                .manufacturer("Test Manufacturer")
                .purchaseDate(LocalDate.of(2024, 1, 15))
                .status(DroneStatus.AVAILABLE)
                .maxFlightTime(30)
                .maxFlightDistance(5000)
                .weight(500)
                .remarks("Test drone")
                .build();
    }

    @Test
    void testInsert_Success() {
        // When
        int result = droneRepository.insert(testDrone);

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(testDrone.getId()).isNotNull();
        assertThat(testDrone.getId()).isGreaterThan(0L);
    }

    @Test
    void testSelectById_Success() {
        // Given
        droneRepository.insert(testDrone);
        Long id = testDrone.getId();

        // When
        Drone found = droneRepository.selectById(id);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getSerialNumber()).isEqualTo("TEST-001");
        assertThat(found.getModelName()).isEqualTo("Test Model");
        assertThat(found.getManufacturer()).isEqualTo("Test Manufacturer");
        assertThat(found.getStatus()).isEqualTo(DroneStatus.AVAILABLE);
    }

    @Test
    void testSelectById_NotFound() {
        // When
        Drone found = droneRepository.selectById(999999L);

        // Then
        assertThat(found).isNull();
    }

    @Test
    void testSelectBySerialNumber_Success() {
        // Given
        droneRepository.insert(testDrone);

        // When
        Drone found = droneRepository.selectBySerialNumber("TEST-001");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getSerialNumber()).isEqualTo("TEST-001");
        assertThat(found.getModelName()).isEqualTo("Test Model");
    }

    @Test
    void testSelectBySerialNumber_NotFound() {
        // When
        Drone found = droneRepository.selectBySerialNumber("NON-EXISTENT");

        // Then
        assertThat(found).isNull();
    }

    @Test
    void testSelectByConditions_WithModelName() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .modelName("Test")
                .build();

        // When
        List<Drone> results = droneRepository.selectByConditions(conditions);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(d -> d.getSerialNumber().equals("TEST-001"));
    }

    @Test
    void testSelectByConditions_WithManufacturer() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .manufacturer("Test Manufacturer")
                .build();

        // When
        List<Drone> results = droneRepository.selectByConditions(conditions);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(d -> d.getSerialNumber().equals("TEST-001"));
    }

    @Test
    void testSelectByConditions_WithStatus() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .status("AVAILABLE")
                .build();

        // When
        List<Drone> results = droneRepository.selectByConditions(conditions);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(d -> d.getSerialNumber().equals("TEST-001"));
    }

    @Test
    void testSelectByConditions_WithPagination() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .offset(0)
                .limit(10)
                .build();

        // When
        List<Drone> results = droneRepository.selectByConditions(conditions);

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isLessThanOrEqualTo(10);
    }

    @Test
    void testSelectByConditions_NoMatch() {
        // Given
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .modelName("NonExistent")
                .build();

        // When
        List<Drone> results = droneRepository.selectByConditions(conditions);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void testCountByConditions_WithFilters() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .modelName("Test")
                .build();

        // When
        int count = droneRepository.countByConditions(conditions);

        // Then
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void testCountByConditions_NoFilters() {
        // Given
        droneRepository.insert(testDrone);
        DroneQueryConditions conditions = DroneQueryConditions.builder().build();

        // When
        int count = droneRepository.countByConditions(conditions);

        // Then
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void testUpdateById_Success() {
        // Given
        droneRepository.insert(testDrone);
        Long id = testDrone.getId();

        // When
        testDrone.setStatus(DroneStatus.UNDER_MAINTENANCE);
        testDrone.setRemarks("Updated remarks");
        int result = droneRepository.updateById(testDrone);

        // Then
        assertThat(result).isEqualTo(1);

        Drone updated = droneRepository.selectById(id);
        assertThat(updated.getStatus()).isEqualTo(DroneStatus.UNDER_MAINTENANCE);
        assertThat(updated.getRemarks()).isEqualTo("Updated remarks");
    }

    @Test
    void testUpdateById_PartialUpdate() {
        // Given
        droneRepository.insert(testDrone);
        Long id = testDrone.getId();

        // When - 只更新状态字段
        Drone partialUpdate = Drone.builder()
                .id(id)
                .status(DroneStatus.SCRAPPED)
                .build();
        int result = droneRepository.updateById(partialUpdate);

        // Then
        assertThat(result).isEqualTo(1);

        Drone updated = droneRepository.selectById(id);
        assertThat(updated.getStatus()).isEqualTo(DroneStatus.SCRAPPED);
        assertThat(updated.getSerialNumber()).isEqualTo("TEST-001"); // 其他字段不变
        assertThat(updated.getModelName()).isEqualTo("Test Model");
    }

    @Test
    void testUpdateById_NotFound() {
        // Given
        Drone nonExistent = Drone.builder()
                .id(999999L)
                .status(DroneStatus.SCRAPPED)
                .build();

        // When
        int result = droneRepository.updateById(nonExistent);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testDeleteById_Success() {
        // Given
        droneRepository.insert(testDrone);
        Long id = testDrone.getId();

        // When
        int result = droneRepository.deleteById(id);

        // Then
        assertThat(result).isEqualTo(1);

        Drone deleted = droneRepository.selectById(id);
        assertThat(deleted).isNull();
    }

    @Test
    void testDeleteById_NotFound() {
        // When
        int result = droneRepository.deleteById(999999L);

        // Then
        assertThat(result).isEqualTo(0);
    }
}
