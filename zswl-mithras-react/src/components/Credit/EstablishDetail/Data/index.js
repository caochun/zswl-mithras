import styles from '../index.less'
import store from './store'
import { history, observer, getQuery } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { ClientMaterialTable } from '@/components/ClientMaterialTable/ClientMaterialTableEntries'

const Data = ({ id, canEdit = true, isProjSponsor, businessVersion }) => {
  store.businessVersion = businessVersion
  const { getProjectDataDetail, projectDataDetail = [], loading } = store

  useEffect(() => {
    getProjectDataDetail(id)
  }, [id])

  return (
    <>
      <h3 className={styles.title}>资料清单</h3>

      {projectDataDetail.map((item, index) => {
        return (
          <ClientMaterialTable
            uploadModule="GROUP_CREDIT_ESTABLISH"
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
