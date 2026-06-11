package cn.zswltech.mithras.dto.rating.ratingclient;

import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RatingClientDetailLibRSP extends ListBaseRSP {


    private Long reportId;

    private Long snapshotId;

    private Long clientId;

    private String modelCode;

    private String modelName;

    private String score;

    private String finalScore;

    private Long belongDeptId;

    private String processStatus;

    private Boolean ratingStatus;

    private Boolean overturn;

    private String overturnOpinion;

    private Long overturnUserId;

    private String overturnScore;

    private Boolean adjust;

    private String adjustOpinion;

    private Long adjustUserId;

    private String adjustScore;

    private LocalDate effectTime;

    private LocalDate abandonTime;

    private Boolean deleted;

    private String version;

    private Long originId;

    private LocalDateTime dataCreateTime;

    private LocalDateTime createTime;

    private Long dataCreateBy;

    private LocalDateTime dataUpdateTime;

    private LocalDateTime updateTime;

    private Long dataUpdateBy;

    private Integer versionType;

    private Long createBy;

    private String createByName;

}
