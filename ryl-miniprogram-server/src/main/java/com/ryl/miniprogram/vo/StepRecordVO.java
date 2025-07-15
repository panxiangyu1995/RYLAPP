package com.ryl.miniprogram.vo;

import com.ryl.miniprogram.entity.TaskStep;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 步骤记录视图对象，包含图片和附件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StepRecordVO extends TaskStep {
    private List<ImageVO> images;
    private List<FileVO> files;
}
