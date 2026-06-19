import { useState } from 'react'
import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { hasValue, amountFormat, formatPercent, timeFormat, monthFormat } from '@/utils'
import { DatePicker, message } from 'antd'
import moment from 'moment'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'

const { useStore } = Table

const Index = ({ store }) => {
  const [planCollectionDate, setPlanCollectionDate] = useState('')
  const $table = useStore({
    pagination: false,
    request: async (params) => {
      setPlanCollectionDate(params.planCollectionDate)
      const res = await flowCenterApi.postExportList(params)
      return res || []
    },
  })

  const { rows, keys } = $table.getSelected()
  const handleExport = async () => {
    if (!rows.length) {
      message.error('请选择需导出的客户')
      return
    }
    await flowCenterApi.postBusinessExport({ planCollectionDate, collectionIds: keys })
  }

  return (
    <Modal title={'打印收据'} store={store.$checkLetter} destroyOnClose footer={null} width={800}>
      <Table
        columnsFilter="flowCenter_ProjectSide_CheckLetter"
                onFilter={(key,val) => saveServer('flowCenter_ProjectSide_CheckLetter',val)}
        
        rowKey={'collectionId'}
        scroll={{ x: 'auto', y: 400 }}
        resizable
        store={$table}
        serial
        selectable={{ type: 'checkbox' }}
        searchbar={{
          initialValues: {
            planCollectionDate: moment(),
          },
          resetButton: false,
          searchButton: false,
          items: [
            {
              label: '请选择年月',
              name: 'planCollectionDate',
              element: <DatePicker.MonthPicker />,

              itemProps: {
                rules: [{ required: true, message: '请选择日期' }],
                transform: (val) => {
                  return val ? monthFormat(val) : undefined
                },
              },
            },
          ],
        }}
        extra={[
          {
            name: '导出',
            type: 'primary',
            onClick: handleExport,
          },
        ]}
        columns={[
          { title: '客户名称', dataIndex: 'clientName', width: 100 },
          { title: '合同编号', dataIndex: 'contractCode', width: 200 },
          { title: '现金流编号', dataIndex: 'code' },

          MatchOptionColumn({
            title: '收据内容',
            dataIndex: 'cashFlowItem',
            matchOption: 'cashFlowItemEnum',
          }),
          AmountColumn({ title: '应收金额', dataIndex: 'planCollectionAmount' }),
          { title: '应收日期', dataIndex: 'planCollectionDate' },
        ]}
      ></Table>
    </Modal>
  )
}

export default observer(Index)
