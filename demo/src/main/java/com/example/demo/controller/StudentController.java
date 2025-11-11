package com.example.demo.controller;

import com.example.demo.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentController {

    @GetMapping("/liststudents")
    public String listStudents(Model model)
    {
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(101,"ABC"));
        studentList.add(new Student(102,"XYZ"));
        studentList.add(new Student(103,"PQR"));
        studentList.add(new Student(104,"LMN"));
        model.addAttribute("listStudents",studentList);

        return "studentlist";
    }

}
