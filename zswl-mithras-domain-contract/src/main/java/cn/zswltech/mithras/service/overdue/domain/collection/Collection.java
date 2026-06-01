package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.zswltech.mithras.service.overdue.domain.share.Aggregate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:38
 */
@Data
public class Collection implements Aggregate<CollectionId> {
    private CollectionId collectionId;
    private Long clientId;
    private String clientName;
    private Long riskExposure;
    private Long overdueRent;
    private Integer curMaxOverdueDays;
    private Long lateCharge;
    private Long projectSponsor;
    private String projectSponsorName;
    private Long bizDept;
    private String bizDeptName;
    private String latestProgress;
    private String processPerson;
    private String processTime;
    private List<CollectionAction> collectionActionList;

    private Long lockVersion;

    @Override
    public boolean sameIdentityAs(CollectionId other) {
        return collectionId.equals(other);
    }

    @Override
    public CollectionId getBizId() {
        return collectionId;
    }

    @Override
    public String bizIdString() {
        return collectionId.getId().toString();
    }

    public void addAction(CollectionAction collectionAction) {
        collectionActionList.add(collectionAction);
    }

    public void replaceAction(CollectionAction collectionAction) {
        List<CollectionAction> newList = new ArrayList<>();
        for (CollectionAction action : collectionActionList) {
            if(action.getActionId().equals(collectionAction.getActionId())){
                newList.add(collectionAction) ;
            }else {
                newList.add(action);
            }
        }
        setCollectionActionList(newList);
    }

    @Override
    public void createNewId(CollectionId collectionId) {
        setCollectionId(collectionId);
    }
}
