package com.pmt.ProjectManagement.service;

import com.pmt.ProjectManagement.entity.Document;
import com.pmt.ProjectManagement.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class DocumentService {
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void saveFile(MultipartFile file) throws Exception {
        // 1. Validate file type
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new Exception("Only PDF files are allowed.");
        }

        // 2. Validate file size (less than 2MB)
        long maxSizeInBytes = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSizeInBytes) {
            throw new Exception("File size must be less than 2MB.");
        }

        // 3. Create file entity (without saving to DB yet)
        Document fileEntity = new Document();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setData(file.getBytes());

        // 4. Save file to disk and get path
        String filePath = saveFileToDisk(fileEntity);
        fileEntity.setFilePath(filePath); // Set path

        // 5. Save file to database
        documentRepository.save(fileEntity);
    }

    public List<Document> getAllFiles() {
        return documentRepository.findAll();
    }

    public void deleteFileById(Long id) {
        documentRepository.deleteById(id);
    }

    private String saveFileToDisk(Document fileEntity) throws Exception {
        String folderPath = System.getProperty("user.dir") + "/files";

        // Create folder if it doesn't exist
        java.io.File directory = new java.io.File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Define the target file
        java.io.File outputFile = new java.io.File(folderPath + "/" + fileEntity.getFileName());

        // Write bytes to disk
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile)) {
            fos.write(fileEntity.getData());
            String absolutePath = outputFile.getAbsolutePath();
            System.out.println("✅ File saved to: " + absolutePath);
            return absolutePath;
        }
    }
}
