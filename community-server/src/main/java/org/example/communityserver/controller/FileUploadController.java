package org.example.communityserver.controller;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.communityserver.dto.UploadResponse;
import org.example.communityserver.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf"
    );

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public Result<UploadResponse> uploadFile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "上传文件不能为空");
            }
            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "不支持的文件类型: " + file.getContentType());
            }
            UploadResponse response = ossService.uploadPostObject(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType());
            return Result.success("文件上传成功", response);
        } catch (Exception e) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "文件上传失败: " + e.getMessage());
        }
    }
}
