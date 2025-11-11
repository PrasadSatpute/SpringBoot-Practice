package com.pmt.ProjectManagement.controller;

import com.pmt.ProjectManagement.dto.DocumentDTO;
import com.pmt.ProjectManagement.entity.Document;
import com.pmt.ProjectManagement.repository.DocumentRepository;
import com.pmt.ProjectManagement.security.CustomUserDetails;
import com.pmt.ProjectManagement.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    @Autowired
    private DocumentService fileService;

    @Autowired
    private DocumentRepository fileRepository;

    @GetMapping
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("files", fileService.getAllFiles());
        model.addAttribute("currentUser", userDetails.getUser());
        return "upload";
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            fileService.saveFile(file);
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        }
        return "redirect:/documents"; // ✅ Correct redirect
    }



    @PostMapping("/upload-multiple")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes) {
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                fileService.saveFile(file);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorMessages.append("File '").append(file.getOriginalFilename())
                        .append("' failed: ").append(e.getMessage()).append("<br/>");
            }
        }

        redirectAttributes.addFlashAttribute("message",
                "Successfully uploaded: " + successCount + " file(s). Failed: " + failCount + " file(s).");

        if (failCount > 0) {
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages.toString());
        }

        return "redirect:/documents"; // ✅ Correct redirect
    }



    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Document> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            Document file = optionalFile.get();

            // Delete file from disk
            if (file.getFilePath() != null) {
                File diskFile = new File(file.getFilePath());
                if (diskFile.exists()) {
                    if (diskFile.delete()) {
                        System.out.println("Deleted file from disk: " + file.getFilePath());
                    } else {
                        System.out.println("⚠️ Failed to delete file from disk: " + file.getFilePath());
                    }
                }
            }

            // Delete from DB
            fileService.deleteFileById(id);
            redirectAttributes.addFlashAttribute("message", "File deleted successfully!");

        } else {
            redirectAttributes.addFlashAttribute("message", "File not found!");
        }

        return "redirect:/documents";

    }


    @GetMapping("/preview/{id}")
    public ResponseEntity<UrlResource> previewFile(@PathVariable Long id) {
        Optional<Document> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            Document fileEntity = optionalFile.get();

            System.out.println("Path ------------ "+fileEntity.getFilePath());
            try {
                java.io.File file = new java.io.File(fileEntity.getFilePath());
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }

                Path path = file.toPath();
                UrlResource resource = new UrlResource(path.toUri());

                String contentType = fileEntity.getFileType();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDisposition(ContentDisposition.inline().filename(fileEntity.getFileName()).build());

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/download/{id}")
    public String downloadFileToDisk(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Document> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            Document file = optionalFile.get();

            // Define the save directory (relative path)
            String saveDir = System.getProperty("user.dir") + File.separator + "uploads";

            // Create directory if it doesn't exist
            File directory = new File(saveDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create target file
            File outputFile = new File(saveDir + File.separator + file.getFileName());

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(file.getData());
                redirectAttributes.addFlashAttribute("message", "File downloaded to disk: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Failed to write file: " + e.getMessage());
            }

        } else {
            redirectAttributes.addFlashAttribute("message", "File not found!");
        }

        return "redirect:/documents";

    }
}
