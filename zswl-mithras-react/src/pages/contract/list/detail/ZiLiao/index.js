import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import Store from './store'
import styles from './index.less'
import { FileTable } from '@/components'

const Index = ({ id, businessVersion, title }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const columns = [
    {
      title: '项目评审资料',
      dataIndex: 'name',
      width: 400,
    },
  ]

  const params = {
    moduleType: 'CONTRACT',
    mainId: id,
    businessVersion,
  }

  return (
    <div className={styles.page}>
      <FileTable
        title={<div className={styles.title}>{title}</div>}
        canEdit={false}
        columns={columns}
        tableApi={() => store.getList(id, businessVersion)}
        canBatchDownload
        hasFormApproval={false}
        params={params}
        functionCodeList={{
          download: 'newContractFileDownload',
          batchDownload: 'contractFileBatchDownload',
        }}
      />
    </div>
  )
}

export default observer(Index)
