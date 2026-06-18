import { useMemo, useEffect } from 'react'
import { Button, Table } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { Space, Tooltip } from 'antd'
import { history, observer, ErrorBoundary } from '@zswl/admin'
import LeaseTotalMoney from './LeaseTotalMoney'
import Store from './store'
import styles from './index.less'
import { hasPermission } from '@/utils'
import { saveServer } from '@/utils'
import DupModal from './DupModal'

const Index = ({ id, canEdit = true, businessVersion, isFormApproval, baseStore = {}, projCode, taskActivityId, disable = false }) => {
  const yunyingCode = ['userTask_operationManagementReview', 'yyglbDeptLeader', 'operationManagement'].includes(taskActivityId)
  const { leaseTypeStr } = baseStore
  const store = useMemo(() => {
    return new Store({ id, businessVersion, isFormApproval, baseStore, projCode })
  }, [id, businessVersion, isFormApproval, baseStore, projCode])

  const { headerList, totalCost, totalAmount, saveTotalAmount, uploadedOcrFile } = store

  const columns = useMemo(() => {
    return headerList?.map((item) => {
      return {
        title: item,
        dataIndex: item,
        width: item.indexOf('序号') > -1 ? 80 : 180,
        render: (val, record) => {
          const isDup = record.matchColumns && record.matchColumns.includes(item.replace('*',''))
          return (
            <Tooltip title={val}>
              <span style={{color:!disable && isDup?'red':''}}>{val ? String(val) : '-'}</span>
            </Tooltip>
          )
        },
      }
    })
  }, [JSON.stringify(headerList)])

  const goOcr = () => {
    window.open(`/ocr/${uploadedOcrFile ? 'list' : 'recognition'}?id=${id}`)
  }
  return (
    <ErrorBoundary
      fallback={
        <div>
          <h3>租赁物清单</h3>
          <div>
            <Table
              columnsFilter={'detail_LeaseList_1'}
              onFilter={(key, val) => saveServer('detail_LeaseList_1', val)}
              columns={columns}
              dataSource={[]}
              scroll={{
                x: 1800,
                y: 400,
              }}
            />
          </div>
        </div>
      }
    >
      <div className={styles.page}>
        <div className={styles.header}>
          <div className={styles.title}>
            租赁物清单 {leaseTypeStr ? `(模版类型：${leaseTypeStr})` : ''}
          </div>
          <Space>
            {yunyingCode && <Button onClick={store.checkDuplicate}>内部查重</Button>}
            <Button onClick={goOcr}>OCR 识别</Button>
            <Button onClick={store.exportTemplate}>模版下载</Button>
            {canEdit && hasPermission('ledgerdetailleaseitemimport') && (
              <DataUpload accept=".xlsx" maxCount={1} onChange={store.onFileChange}>
                <Button type="primary">导入</Button>
              </DataUpload>
            )}
            {canEdit && hasPermission('ledgerdetailleaseitemremove') && (
              <Button onClick={store.onBatchRemove}>批量删除</Button>
            )}
            <Button onClick={store.onBatchExport}>批量导出</Button>
          </Space>
        </div>
        <Table
          rowKey={'id'}
          selectable={{
            type: 'checkbox',
          }}
          columnWidth={180}
          columnsFilter="lease_maintain_leaseList"
          onFilter={(key, val) => saveServer('lease_maintain_leaseList', val)}
          resizable
          columns={columns}
          store={store.$table}
          scroll={{
            x: headerList?.length * 200,
          }}
        />
        <DupModal modalStore={store}/>
      </div>
    </ErrorBoundary>
  )
}

export default observer(Index)
