package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final LocalDate BIRTH_DATE = LocalDate.parse("2000-01-01");

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @AfterEach
    void cleanup() {
        studentRepository.deleteAll();
    }

    // --------------------------------------
    // CREATE
    // --------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void createStudentSuccess() throws Exception {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setBirthDate(BIRTH_DATE);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void createStudentInvalidData() throws Exception {
        // all fields are null
        CreateStudentDTO dto = new CreateStudentDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // --------------------------------------
    // UPDATE
    // --------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateStudentSuccess() throws Exception {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setBirthDate(BIRTH_DATE);
        studentService.create(student);

        UpdateStudentDTO dto = new UpdateStudentDTO();
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmail("updated@email.com");
        dto.setBirthDate(BIRTH_DATE);

        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateStudentInvalidData() throws Exception {
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setBirthDate(BIRTH_DATE);
        studentService.create(student);

        UpdateStudentDTO dto = new UpdateStudentDTO(); // invalid

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // --------------------------------------
    // FIND
    // --------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void findStudentByIdSuccess() throws Exception {
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setBirthDate(BIRTH_DATE);
        studentService.create(student);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(FIRST_NAME));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void findStudentByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/9999")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void findAllStudents() throws Exception {
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setBirthDate(BIRTH_DATE);
        studentService.create(student);

        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(FIRST_NAME));
    }

    // --------------------------------------
    // DELETE
    // --------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteStudentSuccess() throws Exception {
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        student.setBirthDate(BIRTH_DATE);
        studentService.create(student);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteStudentNotFound() throws Exception {
        // GIVEN
        Long nonExistentId = 9999L;

        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + nonExistentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}