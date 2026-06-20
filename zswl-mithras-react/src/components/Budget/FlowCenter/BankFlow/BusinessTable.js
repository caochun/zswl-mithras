import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Table } from '@zswl/components'
import Store from './store.tsx'
import { Alert, Space, Tag, Tooltip } from 'antd'
import { useEffect, useMemo } from 'react'
import WriteOffDrawer from './WriteOffDrawer.js'
import DiffRefundModal from './DiffRefundModal'
import ConfirmIncomeModal from './ConfirmIncomeModal'
import { options } from '@/utils'
import AutomaticMatch from './AutomaticMatch'
import IconFont from '@/components/Icon'
import { saveServer } from '@/utils'

const { financingFlowWriteOffStatusEnum } = options

const Index = ({ type }) => {
  const isCenter = ['PROCESSING_CENTER_WRITE', 'PROCESSING_CENTER'].includes(type)
  const isWriteOff = type === 'PROCESSING_CENTER_WRITE'
  const isNoProcess = type === 'NO_PROCESSING_REQUIRE'
  const store = useMemo(() => new Store({ type }), [type])
  const { rows, keys } = store.table.getSelected()
  const canDelete = !['PROCESSED_PROJ_SIDE', 'PROCESSED_FUNDS_END'].includes(type)
  const columns = [
    !isNoProcess && {
      title: '核销类型',
      dataIndex: 'flowStatus',
      width: 120,
      render: (val) => {
        const { label, color } = financingFlowWriteOffStatusEnum.find((v) => v.value === val) ?? {}
        return (
          <Tag style={{ border: 0 }} color={color}>
            {label}
          </Tag>
        )
      },
    },
    InputColumn({
      title: '交易明细编号',
      dataIndex: 'transactionDetailsNumber',
      search: true,
      width: 240,
    }),
    InputColumn({ title: '资金组织', dataIndex: 'financialOrganization' }),
    InputColumn({ title: '银行帐号', dataIndex: 'bankAccount', search: true }),
    InputColumn({ title: '开户银行', dataIndex: 'bankName', search: true }),
    InputColumn({ title: '币别', dataIndex: 'currency', width: 80 }),
    // InputColumn({ title: '业务项目', dataIndex: 'projName' }),
    DateColumn({ title: '交易时间', dataIndex: 'transactionDate', search: true, width: 120 }),
    InputColumn({ title: '摘要', dataIndex: 'mainInfo', search: true }),
    AmountColumn({ title: '收款金额', dataIndex: 'collectionAmount', width: 140 }),
    AmountColumn({ title: '付款金额', dataIndex: 'paymentAmount', width: 140 }),
    AmountColumn({ title: '余额', dataIndex: 'depositAmount', width: 120 }),
    AmountColumn({ title: '手续费', dataIndex: 'handingFees', width: 120 }),
    InputColumn({ title: '对方户名', dataIndex: 'otherName', search: true }),
    InputColumn({ title: '对方账号', dataIndex: 'otherBankAccount', search: true }),
    InputColumn({ title: '对方开户行', dataIndex: 'otherBankName', search: true }),
    InputColumn({ title: '明细流水号', dataIndex: 'detailSerialNumber' }),
    InputColumn({ title: '数据来源', dataIndex: 'dataSource', width: 120 }),
    InputColumn({ title: '最后更新时间', dataIndex: 'updateTime' }),
    InputColumn({ title: '保融流水', dataIndex: 'cicoBruid' }),
    {
      title: '操作',
      fixed: 'right',
      actions: (record) => {
        return [
          canDelete && {
            name: '删除',
            onClick: () => store.delete([record.id]),
            confirm,
            style: { width: 30, display: 'inline-block' },
          },
          isNoProcess && {
            name: '还原',
            onClick: () => store.restore([record.id]),
            style: { width: 30, display: 'inline-block' },
          },
          record.promptLabel === '1' && <Tag color="red">苍穹已删除</Tag>,
        ]
      },
    },
  ]
  const searchItem = [
    !isWriteOff &&
      MatchOptionColumn({
        title: '收付款类型',
        dataIndex: 'collectionPaymentType',
        matchOption: 'bankFlowPaymentCollectionTypeEnum',
      }),
  ].filter(Boolean)

  useEffect(() => {
    if (isCenter || !canDelete) {
      store.table.setSelected(
        store.selectedRecord.selectedRowKeys,
        store.selectedRecord.selectedRows
      )
    }
  }, [isCenter, JSON.stringify(store.table.pagination)])
  const openManual = () => {
    window.open('/public/assets/manual/资产端自动核销操作手册.pdf')
  }
  return (
    <>
      <Table
        title={() => {
          if (isCenter) {
            return <Alert message={`已选中${rows.length}条记录`}></Alert>
          }
        }}
        store={store.table}
        columns={columns}
        columnWidth={200}
        scroll={{ x: true }}
        columnsFilter="flowCenter"
        onFilter={(key,val) => saveServer('flowCenter',val)}
        resizable
        serial
        extra={[
          isCenter && {
            name: '清空选中',
            key: 'clearSelected',
            onClick: () => store.clearSelected(true),
          },
          isCenter && {
            name: '轧差退款',
            key: 'diffRefund',
            onClick: store.diffRefund,
            disabled: !rows.length,
          },
          isCenter && {
            name: '确认收入',
            key: 'confirmIncome',
            onClick: store.confirmIncome,
            disabled: !rows.length,
          },
        ]}
        actions={[
          isCenter && (
            <Button type="primary" onClick={store.pullFlow} key={'update'}>
              流水更新
            </Button>
          ),
          isWriteOff && <AutomaticMatch baseStore={store} />,
          (isCenter || !canDelete) && (
            <Button
              type="primary"
              onClick={() => store.offDrawer.open({ rows })}
              disabled={!rows.length}
              key={'off'}
            >
              {!canDelete ? '核销明细' : '手工核销'}
            </Button>
          ),
          isCenter && (
            <Button type="primary" onClick={store.handleOff} disabled={!rows.length} key={'none'}>
              无需处理
            </Button>
          ),

          canDelete && (
            <Button.Delete onClick={() => store.delete(keys)} disabled={!rows.length} key="delete">
              删除
            </Button.Delete>
          ),
          isNoProcess && (
            <Button
              type="primary"
              disabled={!rows.length}
              key="restore"
              onClick={() => store.restore(keys)}
            >
              还原
            </Button>
          ),
          isWriteOff && (
            <Tooltip
              title={
                <span style={{ cursor: 'pointer' }} onClick={openManual}>
                  点击查看 自动核销操作手册.pdf
                </span>
              }
            >
              <div style={{ textAlign: 'center' }}>
                <IconFont type="icon-bookmarks" style={{ fontSize: 20 }} />
                <div style={{ fontSize: 12, color: '#666' }}>操作手册</div>
              </div>
            </Tooltip>
          ),
        ]}
        editable={false}
        selectable={{
          type: 'checkbox',
          selectedRowKeys: store.selectedRecord.selectedRowKeys,
          preserveSelectedRowKeys: true,
          onChange: store.handleSelectChange,
        }}
        searchbar={{
          labelCol: { span: 6 },
          initialValues: {},
          items: searchItem,
        }}
      />
      <WriteOffDrawer store={store} />
      <DiffRefundModal store={store} />
      <ConfirmIncomeModal store={store} />
    </>
  )
}

export default observer(Index)
