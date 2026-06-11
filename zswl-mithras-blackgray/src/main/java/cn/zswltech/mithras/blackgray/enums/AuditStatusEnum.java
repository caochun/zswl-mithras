package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: xinhao.hu
 * @date: 2022/6/16 1:56 下午
 * @description:
 **/
@Getter
@AllArgsConstructor
public enum AuditStatusEnum implements PullDown {
    //
    WAIT((byte) 0, "待提交"),
    AUDIT((byte) 1, "审批中"),
    WITHDRAW((byte) 2, "已撤回"),
    REJECT((byte) 3, "已退回"),
    FINISH((byte) 4, "已完成"),
    ;

    private final byte code;
    private final String name;

    public static AuditStatusEnum find(Byte code) {
        if (code == null) {
            return null;
        }
        for (AuditStatusEnum item : AuditStatusEnum.values()) {
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据code查找对应枚举的{@link Enum#name}
     *
     * @param code
     * @return
     */
    public static String findEnumName(Byte code) {
        AuditStatusEnum auditStatusEnum = find(code);
        if (auditStatusEnum == null) {
            return null;
        } else {
            return auditStatusEnum.name();
        }
    }

    /**
     * 审批流中发送待办时根据状态获取中文名
     */
    public static String noticeStatusName(Byte code){
        AuditStatusEnum auditStatusEnum = find(code);
        if (auditStatusEnum == null) {
            return null;
        } else if (AuditStatusEnum.AUDIT.equals(auditStatusEnum)) {
            return "待审批";
        } else if (AuditStatusEnum.WAIT.equals(auditStatusEnum)) {
            return "待处理";
        } else {
            return auditStatusEnum.getName();
        }
    }


    public static List<DictionaryDTO> toDictionaryDTO(){
        return Arrays.stream(values()).map(t ->
                DictionaryDTO.builder()
                        .value(t.getCode())
                        .label(t.getName())
                        .build()
        ).collect(Collectors.toList());
    }

    /*
        用于审批列表的下拉框状态枚举字典，排除待提交和已完成
     */
    public static List<DictionaryDTO> toAuditListDictionaryDTO(){
        return Arrays.stream(values()).filter(t->!t.equals(WAIT) && !t.equals(FINISH)).map(t ->
                DictionaryDTO.builder()
                        .value(t.getCode())
                        .label(t.getName())
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * 判断审批状态是本地状态还是进入审批流的非本地状态
     */
    public static Boolean isLocal(byte code) {
        return WAIT.getCode() == code || WITHDRAW.getCode() == code || REJECT.getCode() == code;
    }

    /**
     * 判断审批状态是本地状态还是进入审批流的非本地状态
     */
    public static List<Byte> getLocalStatus() {
        return Arrays.asList(WAIT.getCode(), WITHDRAW.getCode(), REJECT.getCode());
    }

    @Override
    public String display() {
        return name;
    }

    public String valueKey() {
        return String.valueOf(code);
    }
}
