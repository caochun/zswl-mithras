package cn.zswltech.mithras.dto.filingmaterials;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilingEmailDTO {
    private String dueDate;
    private String contractCodeStr;
}
