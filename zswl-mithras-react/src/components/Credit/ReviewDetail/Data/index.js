import ClientFileTable from '@/components/Project/ClientFileTable'
import styles from '../index.less'
import store from './store'
import { observer } from '@zswl/admin'
import { useEffect } from 'react'

const Data = ({ id, canEdit = true, businessVersion }) => {
  store.businessVersion = businessVersion
  const { getProjectDataDetail, projectDataDetail = [] } = store

  useEffect(() => {
    getProjectDataDetail(id)
  }, [id])

  return (
    <>
      <div className={styles.title}>资料清单</div>
      {projectDataDetail.map((item, index) => {
        return (
          <ClientFileTable
            uploadModule="GROUP_CREDIT_REVIEW"
            mainId={id}
            canEdit={
              canEdit &&
              ['GROUP_CREDIT_REVIEW_CLIENT', 'GROUP_CREDIT_ESTABLISH_CLIENT'].includes(
                item.businessType
              )
            }
            key={index}
            item={item}
            getProjectDataDetail={getProjectDataDetail}
          />
        )
      })}
    </>
  )
}

export default observer(Data)
