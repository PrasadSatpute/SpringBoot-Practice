package com.example.upload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
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
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setData(file.getBytes());

        // 4. Save file to disk and get path
        String filePath = saveFileToDisk(fileEntity);
        fileEntity.setFilePath(filePath); // Set path

        // 5. Save file to database
        fileRepository.save(fileEntity);
    }

    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFileById(Long id) {
        fileRepository.deleteById(id);
    }

    private String saveFileToDisk(FileEntity fileEntity) throws Exception {
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
