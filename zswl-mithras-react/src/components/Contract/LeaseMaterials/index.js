import { useEffect, useMemo, useState } from 'react'
import { Space, message } from 'antd'
import { observer } from '@zswl/admin'
import { hasPermission } from '@/utils'
import Store from './store'
import styles from './index.less'
import { FileTable } from '@/components'

const Index = ({ id }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const columns = [
    {
      title: '租赁物审核资料',
      dataIndex: 'name',
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
      width: 180,
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
      width: 180,
    },
  ]

  const params = {
    moduleType: 'CONTRACT',
    mainId: id,
  }

  if (hasPermission('materialscontractleaselist')) {
    return (
      <div className={styles.page}>
        <FileTable
          isNewLayout
          canEdit={false}
          columns={columns}
          tableApi={() => store.getList(id)}
          canBatchDownload
          params={params}
          hasFormApproval={false}
          functionCodeList={{
            download: 'newContractFileDownload',
            batchDownload: 'contractFileBatchDownload',
          }}
        />
      </div>
    )
  }
  return null
}

export default observer(Index)
