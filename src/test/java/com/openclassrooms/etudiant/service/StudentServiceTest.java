package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    // =========================
    //  Mock student
    // =========================

    private Student buildStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john@doe.com");
        student.setBirthDate(LocalDate.of(2000, 1, 1));
        return student;
    }

    private static Stream<Consumer<Student>> invalidStudentsProvider() {
        return Stream.of(
                student -> student.setFirstName(null),
                student -> student.setLastName(null),
                student -> student.setEmail(null),
                student -> student.setBirthDate(null)
        );
    }

    // =========================
    // findById
    // =========================

    @Test
    void test_findById_return_studentDTO_when_student_exists() {

        // GIVEN
        Student student = buildStudent();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // WHEN
        StudentDTO result = studentService.findById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john@doe.com");
    }

    @Test
    void test_findById_throw_EntityNotFoundException_when_student_is_notFound() {

        // GIVEN
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // THEN
        assertThrows(EntityNotFoundException.class,
                () -> studentService.findById(1L));
    }

    // =========================
    // findAll
    // =========================

    @Test
    void test_findAll_return_listOf_studentDTO() {

        // GIVEN
        List<Student> students = List.of(buildStudent(), buildStudent());
        when(studentRepository.findAll()).thenReturn(students);

        // WHEN
        List<StudentDTO> result = studentService.findAll();

        // THEN
        assertThat(result).hasSize(2);
    }

    // =========================
    // create
    // =========================

    @Test
    void test_create_student_when_student_is_valid() {

        // GIVEN
        Student student = buildStudent();
        when(studentRepository.existsByEmail(student.getEmail())).thenReturn(false);

        // WHEN
        studentService.create(student);

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(student);
    }

    @Test
    void test_create_throw_IllegalArgumentException_when_student_is_null() {

        // THEN
        assertThatThrownBy(() -> studentService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student must not be null");
    }

    @ParameterizedTest
    @MethodSource("invalidStudentsProvider")
    void test_create_throw_IllegalArgumentException_when_student_field_is_null(Consumer<Student> modifier) {

        // GIVEN
        Student student = buildStudent();
        modifier.accept(student); // applique la modification (null field)

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.create(student));
    }

    @Test
    void test_create_student_throw_IllegalArgumentException_when_email_already_exists() {
        Student student = buildStudent();

        when(studentRepository.existsByEmail(student.getEmail())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.create(student));
    }

    // =========================
    // update
    // =========================

    @Test
    void test_update_student_when_valid() {

        // GIVEN
        Student existing = buildStudent();
        Student updated = buildStudent();
        updated.setFirstName("Jane");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));

        // WHEN
        studentService.update(1L, updated);

        // THEN
        assertThat(existing.getFirstName()).isEqualTo("Jane");
        verify(studentRepository).save(existing);
    }

    @ParameterizedTest
    @MethodSource("invalidStudentsProvider")
    void test_update_throw_IllegalArgumentException_when_field_is_null(Consumer<Student> modifier) {
        // GIVEN
        Long id = 1L;
        Student student = buildStudent();
        modifier.accept(student);

        // WHEN + THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.update(id, student));
    }

    @Test
    void test_update_throw_EntityNotFoundException_when_student_is_notFound() {

        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> studentService.update(1L, student));
    }

    // =========================
    // delete
    // =========================

    @Test
    void test_delete_student() {

        // GIVEN
        Long studentId = 1L;
        Student student = buildStudent();
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // WHEN
        studentService.delete(studentId);

        // THEN
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void test_delete_throw_EntityNotFoundException_when_student_is_notFound() {

        // GIVEN
        Student student = buildStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // THEN
        assertThatThrownBy(() -> studentService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void test_delete_throw_IllegalArgumentException_when_id_toDelete_is_null() {

        // THEN
        assertThatThrownBy(() -> studentService.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id must not be null");
    }
}