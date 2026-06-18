import styles from './index.less'
import DataTable from './DataTable'
import { useEffect } from 'react'
import { ClientMaterialTable } from '@/components/ClientMaterialTable/ClientMaterialTableEntries'
import { observer } from '@zswl/admin'

const Data = ({ id, canEdit = true, rootStore }) => {
  const { getProjectDataDetail, projectDataDetail = [], loading } = rootStore

  useEffect(() => {
    getProjectDataDetail(id)
  }, [id])

  return (
    <>
      <h4 className={styles.title}>资料清单</h4>

      {projectDataDetail.map((item, index) => {
        if (item.businessType === 'PROJ_ESTABLISH_CLIENT')
          return (
            <ClientMaterialTable
              uploadModule="PROJ_ESTABLISH"
              mainId={id}
              canEdit={canEdit}
              key={item.clientId}
              item={item}
              getProjectDataDetail={getProjectDataDetail}
            />
          )
        return <DataTable mainId={id} canEdit={canEdit} key={index} {...item} />
      })}
    </>
  )
}

export default observer(Data)
