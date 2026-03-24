package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public List<StudentDTO> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(student -> {
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setId(student.getId());
                    studentDTO.setFirstName(student.getFirstName());
                    studentDTO.setLastName(student.getLastName());
                    return studentDTO;
                })
                .toList();
    }

    public void create(Student student) {
        Assert.notNull(student, "Student must not be null");
        studentRepository.save(student);
    }

    public void update(Long id, Student student) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(student.getFirstName(), "FirstName must not be null");
        Assert.notNull(student.getLastName(), "LastName must not be null");

        Student studentToUpdate = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));;

        studentToUpdate.setFirstName(student.getFirstName());
        studentToUpdate.setLastName(student.getLastName());
        studentRepository.save(studentToUpdate);
    }

    public void delete(Long id) {
        Assert.notNull(id, "id must not be null");
        studentRepository.deleteById(id);
    }
}
