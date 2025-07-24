package com.ryl.engineer.dto;

import lombok.Data;
import java.util.List;

@Data
public class StepRecordDTO {
    private Long id;
    private String content;
    private Integer spentTime;
    private String creatorName;
    private String createTime;
    private List<FileDTO> images;
    private List<FileDTO> attachments;
}