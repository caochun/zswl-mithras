import styles from './index.less'
import DataTable from '@/components/Project/ReviewMaterialTable'
import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import ClientFileTable from '@/components/ClientFileTable'
import { Collapse } from 'antd'

const Data = ({ id, canEdit = true, isProjSponsor, businessVersion, title, store, isRiskManagerProj, setMaterialObj }) => {
  const { getProjectDataDetail, projectDataDetail = [] } = store ?? {}
  useEffect(async() => {
    const list = await getProjectDataDetail?.(id)
    setMaterialObj({list, dtempDetail: store.tempDtempDetail})
  }, [id])
  const _projectDataDetail = useMemo(() => {
    if(isRiskManagerProj){
      return projectDataDetail.filter(item => ["PROJ_ESTABLISH"].includes(item.businessType))
    }
    return projectDataDetail
  }, [projectDataDetail, isRiskManagerProj])
  return (
    <>
      <div className={styles.title}>{title}</div>
      <Collapse defagetProjectDataDetailultActiveKey={['1']} ghost>
        {_projectDataDetail.map((item, index) => {
          if (item.businessType === 'PROJ_REVIEW_CLIENT')
            return (
              <ClientFileTable
                uploadModule="PROJ_REVIEW"
                mainId={id}
                canEdit={canEdit}
                key={index}
                isProjSponsor={isProjSponsor}
                item={item}
                getProjectDataDetail={getProjectDataDetail}
              />
            )
          return (
            <DataTable
              mainId={id}
              canEdit={canEdit}
              key={index}
              {...item}
              isProjSponsor={isProjSponsor}
              isRiskManagerProj={isRiskManagerProj}
            />
          )
        })}
      </Collapse>
    </>
  )
}

export default observer(Data)
