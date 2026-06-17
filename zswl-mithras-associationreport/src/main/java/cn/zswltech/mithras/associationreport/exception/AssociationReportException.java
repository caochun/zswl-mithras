package cn.zswltech.mithras.associationreport.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/24
 * @description
 */
@Getter
public class AssociationReportException extends RuntimeException {
    private List<String> errorMessageList;

    public AssociationReportException(List<String> list) {
        super();
        this.errorMessageList = list;
    }
}
