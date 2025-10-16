package org.example.notificationserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    
    @Email(message = "发件人邮箱格式不正确")
    private String from;
    
    @NotBlank(message = "收件人不能为空")
    private List<String> to;
    
    private List<String> cc;
    
    private List<String> bcc;
    
    @NotBlank(message = "邮件主题不能为空")
    @Size(max = 200, message = "邮件主题长度不能超过200个字符")
    private String subject;
    
    @NotBlank(message = "邮件内容不能为空")
    @Size(max = 10000, message = "邮件内容长度不能超过10000个字符")
    private String content;
    
    private String contentType = "text/html"; // text/html 或 text/plain
    
    private List<String> attachments; // 附件URL列表
    
    private String templateId; // 邮件模板ID
    
    private java.util.Map<String, Object> templateParams; // 模板参数
}
