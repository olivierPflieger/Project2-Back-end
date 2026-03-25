package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.dto.UpdateStudentDTO;
import com.openclassrooms.etudiant.mapper.CreateStudentDtoMapper;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.mapper.UpdateStudentDtoMapper;
import com.openclassrooms.etudiant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final CreateStudentDtoMapper createStudentDtoMapper;
    private final UpdateStudentDtoMapper updateStudentDtoMapper;

    @GetMapping("/api/students/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        StudentDTO student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/api/students")
    public ResponseEntity<?> findAll() {
        List<StudentDTO> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    @PostMapping("/api/students")
    public ResponseEntity<?> create(@Valid @RequestBody CreateStudentDTO createStudentDTO) {
        studentService.create(createStudentDtoMapper.toEntity(createStudentDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/api/students/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateStudentDTO updateStudentDTO) {
        studentService.update(id, updateStudentDtoMapper.toEntity(updateStudentDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/api/students/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        studentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
