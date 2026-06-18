import { Button, Page, Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import store from './store'
import { getDescColumns, getTableColumns } from '@/utils'
import ALL_COLUMNS from '../PaymentColumns'
import { useMemo, useRef } from 'react'
import { Space } from 'antd'
import DataList from './DataList'
import { EditDescription, Summary } from '@/components'
import { saveServer } from '@/utils'

function Index({ params: { id }, query: { canEditFlags = 'true', businessVersion, repayMonth } }) {
  const detail = store.page.getData()
  const { sumData } = store
  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '融资编号',
        actions: ({ receiptRepayCode: name, id: receiptRepayId }) => [
          { name, to: `/financial/payment/detail/${receiptRepayId}` },
        ],
      },
      '融资机构',
      '融资金额（元）',
      { title: '本月计划还款合计（元）', rename: '本月应还金额(元)' },
      { title: '本月计划还款本金（元）', rename: '本月应还本金(元)' },
      { title: '本月计划还款利息（元）', rename: '本月应还利息(元)' },
      { title: '计划还本日', rename: '本月计划还款日' },
      '借款日期',
      '到期日期',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [])

  const canEdit = canEditFlags === 'true'
  const isRevocation = getQuery('typeId') == 'approval' && getQuery('tab') === 'revocation'
  const formColumns = getDescColumns(ALL_COLUMNS, ['备注'])
  const canDownload = store.table.getSelected().keys.length > 0
  const ref = useRef()
  const submit = async () => {
    const { remark } = await ref.current?.form?.submit()
    store.submit(remark, repayMonth)
  }
  return (
    <Page params={{ id, businessVersion }} store={store}>
      <div className="z-flex-jsb" style={{ padding: '10px 0' }}>
        <h3>还款详情</h3>
        <Space>
          <Button.Download onClick={store.download} disabled={!canDownload}>
            下载
          </Button.Download>
          {canEdit && !isRevocation && (
            <>
              <Button onClick={store.cancel}>取消</Button>
              <Button.Submit type="primary" onClick={submit}>
                提交审批
              </Button.Submit>
            </>
          )}
        </Space>
      </div>
      <Table
        columnsFilter={'payment_batchApproval_idjs'}
        onFilter={(key, val) => saveServer('payment_batchApproval_idjs', val)}
        store={store.table}
        editable={false}
        selectable={{
          type: 'checkbox',
        }}
        columnWidth={160}
        columns={columns}
        summary={() => {
          return (
            <Summary
              columns={store.table.getOptimizedColumns()}
              sumData={sumData}
              startIndex={0}
            ></Summary>
          )
        }}
      />
      <EditDescription
        columns={formColumns}
        initEdit={canEdit}
        canEdit={false}
        ref={ref}
        detail={detail}
      />
      <DataList id={id} canEdit={canEdit} />
    </Page>
  )
}

export default observer(Index)
