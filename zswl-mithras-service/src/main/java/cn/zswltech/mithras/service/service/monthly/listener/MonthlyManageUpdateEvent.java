package cn.zswltech.mithras.service.service.monthly.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/8/4/16:56
 * @description
 */
@Getter
public class MonthlyManageUpdateEvent extends ApplicationEvent {
    private DataObject dataObject;

    public MonthlyManageUpdateEvent(Object source, DataObject dataObject) {
        super(source);
        this.dataObject = dataObject;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataObject {
        /**
         * 模型类型 {@link cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum#name}
         */
        @NotNull
        private String modelType;

        @NotNull
        private Long recordId;

        @NotNull
        private String yearAndMonth;
    }
}
