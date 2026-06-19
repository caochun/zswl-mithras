import styles from '../index.less'
import DataTable from './DataTable'
import { observer } from '@zswl/admin'
import { BusinessMaterialTable } from '@/components/ClientMaterialTable/BusinessMaterialTableEntries'
import { useEffect } from 'react'

const Data = ({ id, canEdit = true, isProjSponsor, businessVersion, title, store }) => {
  const { getProjectDataDetail, projectDataDetail = [] } = store
  useEffect(() => {
    getProjectDataDetail(id)
  }, [id])

  return (
    <>
      <div className={styles.title}>{title}</div>
      {projectDataDetail.map((item, index) => {
        if (item.businessType === 'PROJ_REVIEW_CLIENT')
          return (
            <BusinessMaterialTable
              uploadModule="PROJ_PRICING"
              mainId={id}
              canEdit={false}
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
          />
        )
      })}
    </>
  )
}

export default observer(Data)
