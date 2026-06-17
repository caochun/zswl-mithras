import { Table, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import DataUpload from '@/components/DataUpload'
import Api from '../api'
import { message } from 'antd'
import { amountFormat, downFile, formatPercent } from '@/utils'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import React, { forwardRef, useImperativeHandle, useState } from 'react'
import { validateModal } from '@/utils/modal'
import { saveServer } from '@/utils'
import { downLoadExcel } from '@/components/Excel'

const SplitTable = observer(({ item }) => {
  const columns = [
    { title: '期项', dataIndex: 'phase', width: 60 },
    { title: '日期 (转付日)', dataIndex: 'repayDate', dateFormat: 'YYYY-MM-DD' },
    AmountColumn({ title: '应还总额（元)', dataIndex: 'repayAmount' }),
    AmountColumn({ title: '本金（元)', dataIndex: 'principalAmount' }),
    AmountColumn({ title: '利息（元)', dataIndex: 'interestAmount' }),
    AmountColumn({ title: '剩余本金（元)', dataIndex: 'remainingPrincipalAmount' }),
    MatchOptionColumn({
      title: '核销状态',
      dataIndex: 'writeOffStatus',
      matchOption: 'fundReceiptRepayCashFlowState',
    }),
  ]
  const title = `实际还款计划-${item.abbreviation} (${item.securitiesCode})`
  const table = Table.useStore(
    {
      request: async (params) => {
        return item.cashFlowList
      },
    },
    [item.cashFlowList]
  )
  return (
    <Table
      actions={<h4>{title}</h4>}
      columns={columns}
      extra={
        <Button
          type="primary"
          onClick={() =>
            downLoadExcel({
              fileName: title,
              dataSource: item.cashFlowList,
              columns,
            })
          }
        >
          导出
        </Button>
      }
      store={table}
      pagination={false}
    />
  )
})
function Index({ id: financingId, disabled, detail, store }, ref) {
  const { obsolete, directFinancingType } = detail
  const isABS = ['ABS', 'ABN'].includes(directFinancingType)
  const [splitList, setSplitList] = useState([])
  const tableStore = Table.useStore({
    request: async (params) => {
      const res = await Api.getRepayList({ ...params, financingId })
      if (isABS) {
        const splitRes = await Api.getSplitList({ financingId })
        console.log('splitRes: ', splitRes)
        setSplitList(splitRes)
      }
      return res
    },
  })
  const exportRepay = async () => {
    const res = await Api.exportRepay({
      financingId,
    })
    if (res?.code === 200) {
      downFile(res)
      message.success('导出成功')
    } else if (res?.msg) {
      message.error(res.msg)
    }
  }

  const importTable = async (files) => {
    const { fileList } = DataUpload.classify(files)
    const { interestDiff } = await Api.importRepay({
      file: fileList[0],
      financingId,
      isCheck: true,
    })
    const isMore = interestDiff > 10 * 10000
    await validateModal(
      {
        content: `导入的报价利息与合同利率计算结果差额为${amountFormat(
          formatPercent(interestDiff)
        )}元，请确认是否导入，点击【确认】继续导入，点击【取消】则关闭弹窗。`,
      },
      isMore
    )
    await Api.importRepay({
      file: fileList[0],
      financingId,
      isCheck: false,
    })
    message.success('导入成功')
    store.financeTable.search()
    tableStore.search()
  }
  const calculation = async () => {
    const res = await Api.postCalculate({ financingId })
    message.success('计算成功')
    tableStore.search()
  }
  useImperativeHandle(ref, () => ({
    tableStore,
  }))
  const columns = [
    { title: '期项', dataIndex: 'phase', width: 60 },

    { title: '日期 (转付日)', dataIndex: 'repayDate', dateFormat: 'YYYY-MM-DD' },
    AmountColumn({ title: '应还总额（元)', dataIndex: 'repayAmount' }),
    AmountColumn({ title: '本金（元)', dataIndex: 'principleAmount' }),
    AmountColumn({ title: '利息（元)', dataIndex: 'interestAmount' }),
    AmountColumn({ title: '剩余本金（元)', dataIndex: 'remainingPrincipleAmount' }),
    AmountColumn({ title: '预付差额 （元）', dataIndex: 'prePayDifference' }),
    MatchOptionColumn({
      title: '核销状态',
      dataIndex: 'writeOffStatus',
      matchOption: 'fundReceiptRepayCashFlowState',
    }),
    MatchOptionColumn({
      title: '确认状态',
      dataIndex: 'isConfirmed',
      matchOption: 'isConfirmedEnum',
    }),
    MatchOptionColumn({
      title: '还款状态',
      dataIndex: 'isPaid',
      matchOption: 'isConfirmedEnum',
    }),
  ]
  return (
    <div>
      <Table
              columnsFilter={'detail_Repay_1'}
              onFilter={(key,val) => saveServer('detail_Repay_1',val)}
        scroll={{ x: 1200 }}
        actions={<h3>实际还款计划</h3>}
        extra={[
          !isABS && (
            <Button type="primary" onClick={calculation} key="calculation">
              测算
            </Button>
          ),
          <DownloadTemplate
            key="download"
            params={{
              templateName: 'TEMPLATE_OSS_NAME_FINANCING_REPAY',
              moduleType: 'FUND_FINANCING',
            }}
          />,
          <DataUpload
            disabled={disabled}
            key="import"
            maxCount={1}
            onChange={importTable}
            accept="*"
          >
            <Button disabled={disabled}>导入还款计划</Button>
          </DataUpload>,
          {
            name: '导出',
            type: 'primary',
            onClick: exportRepay,
          },
        ]}
        store={tableStore}
        columns={columns}
      />
      {splitList.length > 0 &&
        splitList.map((item) => {
          return <SplitTable key={item.securitiesCode} item={item} columns={columns} />
        })}
    </div>
  )
}

export default observer(forwardRef(Index))
