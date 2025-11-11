package com.example.upload;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Controller
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("files", fileService.getAllFiles());
        return "upload";
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            fileService.saveFile(file);
            model.addAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "File upload failed: " + e.getMessage());
        }
        return "upload";
    }


    @PostMapping("/upload-multiple")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Model model) {
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                fileService.saveFile(file);
                successCount++;
            } catch (Exception e) {
                failCount++;
                // Add detailed error message per file
                errorMessages.append("File '").append(file.getOriginalFilename())
                        .append("' failed: ").append(e.getMessage()).append("<br/>");
            }
        }

        model.addAttribute("message",
                "Successfully uploaded: " + successCount + " file(s). Failed: " + failCount + " file(s).");

        // Only add error messages if there are failures
        if (failCount > 0) {
            model.addAttribute("errorMessages", errorMessages.toString());
        }

        return "upload";
    }


    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            FileEntity file = optionalFile.get();

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

        return "redirect:/";
    }


    @GetMapping("/preview/{id}")
    public ResponseEntity<UrlResource> previewFile(@PathVariable Long id) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            FileEntity fileEntity = optionalFile.get();

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
        Optional<FileEntity> optionalFile = fileRepository.findById(id);

        if (optionalFile.isPresent()) {
            FileEntity file = optionalFile.get();

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

        return "redirect:/";
    }


}
