package com.ryl.engineer.dto;

import com.ryl.engineer.entity.Task;
import com.ryl.engineer.vo.EngineerSimpleVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskDetailDTO extends Task {
    private List<EngineerSimpleVO> engineers;
} 