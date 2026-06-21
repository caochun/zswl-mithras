import { useMemo } from 'react'
import Store from './store'
import { Spin } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import { BusinessMaterialTable } from '@/components/ClientMaterialTable/BusinessMaterialTableEntries'
import { ProjectReviewMaterialTable as DataTable } from '@/components/Project/ReviewMaterialTableEntries'

//项目评审创建、项目评审更新资料清单选项卡
const MaterialList = () => {
    const { detailData, canEditFlag, materialObj, isRiskManagerProj } = useFlowData()
    const { businessVersion, businessKey } = detailData
    const store = useMemo(() => new Store(businessKey, businessVersion), [])
    const dataObj = useMemo(() => materialObj, [materialObj])
    let _canEditFlag = canEditFlag ? 'true' : 'false'
    const isFormApproval = getQuery('typeId') == 'approval'
    const canEdit = isFormApproval ? _canEditFlag === 'true' : !['CLOSED'].includes(dataObj.dtempDetail.projReviewStatus)
    const commentGuideLine = (params) => store.commentGuideLine(params)
    if(!dataObj){
        return <Spin spinning/>
    }
    return (
        <div>
            {dataObj.list.filter(fit => fit.businessType !== "PROJ_ESTABLISH").map((item, index) => {
                if (item.businessType === 'PROJ_REVIEW_CLIENT')
                    return (
                        <BusinessMaterialTable
                            uploadModule="PROJ_REVIEW"
                            mainId={businessKey}
                            canEdit={canEdit}
                            key={index}
                            isProjSponsor={dataObj.dtempDetail.isProjSponsor}
                            item={item}
                            commentGuideLine={(param) => {
                                commentGuideLine({
                                     ...param,
                                     id:item.projReviewMaterialId
                                })
                            }}
                            getProjectDataDetail={() => store.getProjectDataDetail(businessKey, businessVersion)}
                            isRiskManagerProj={isRiskManagerProj}
                        />
                    )
                return (
                    <DataTable
                        mainId={businessKey}
                        canEdit={canEdit}
                        key={index}
                        {...item}
                        isProjSponsor={dataObj.dtempDetail.isProjSponsor}
                        isRiskManagerProj={isRiskManagerProj}
                    />
                )})
            }
        </div>
    )
}

export default observer(MaterialList)
