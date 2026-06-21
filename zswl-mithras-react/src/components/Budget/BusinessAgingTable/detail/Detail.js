import { Button, DatePicker, Form, Page, Select, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { getTableColumns, getFormColumns, rangePresets, saveServer } from '@/utils'
import ALL_COLUMNS from '../Column'
import { useMemo } from 'react'
import AddModal from './AddModal'
import { Space } from 'antd'
import { FileExportAction as ExportBtn } from '@/components/Actions'
import { FormAmount } from '@/components/Form'

function BudgetBusinessAgingDetail({ params, path }) {
  const { id } = params
  const { rows, keys } = store.table.getSelected()
  const detail = store.page.getData()
  const hasSelected = rows.length > 0
  const editStatus = detail.status === 'NEW'

  const canEditItem = rows.length === 1 && rows[0].sendStatus !== 'SUCCESS' && rows[0].source === 1

  const canEdit =
    editStatus && hasSelected && rows.every((item) => ['FAIL', 'NEW'].includes(item.sendStatus))
  const columns = useMemo(() => {
    const nameColumns = [
      '核算组织名称',
      '苍穹状态',
      '期初款项原值',
      '本期增加额',
      '本期减少额',
      '期末款项原值（余额）',
      '币别',
      '科目名称',
      '款项内容',
      '客商编码（苍穹）',
      '业务日期',
      { title: '账龄截止日', dataIndex: 'agingDeadline' },
      '账龄',
      '合同编号',
      '项目名称',
      '合同逾期日期',
    ]

    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [])

  const formColumns = getFormColumns(ALL_COLUMNS, [
    {
      title: '苍穹状态',
      itemProps: {
        required: false,
      },
    },
    {
      title: '客商编码（苍穹）',
      itemProps: {
        required: false,
      },
    },
    '合同编号',
    '项目名称',
  ])

  return (
    <Page params={{ id }} store={store.page}>
      <Form layout="vertical" store={store.form}>
        <Space gutter={24}>
          <Form.Item label="账龄截止日" name={'deadline'}>
            <DatePicker disabled />
          </Form.Item>
          <Form.Item label="状态" name={'status'}>
            <Select options={'financialAccountAgeRecordStatus'} disabled style={{ width: 140 }} />
          </Form.Item>
          <FormAmount.Item
            disabled
            label="期初款项原值"
            initFormat={1}
            name={'originalValueInitial'}
            isRequired={false}
          ></FormAmount.Item>
          <FormAmount.Item
            disabled
            initFormat={1}
            label="本期增加额"
            name={'originalValueIncrease'}
            isRequired={false}
          ></FormAmount.Item>
          <FormAmount.Item
            disabled
            label="本期减少额"
            initFormat={1}
            name={'originalValueReduce'}
            isRequired={false}
          ></FormAmount.Item>
          <FormAmount.Item
            disabled
            label="期末款项原值"
            initFormat={1}
            name={'originalValueFinal'}
            isRequired={false}
          ></FormAmount.Item>
        </Space>
      </Form>
      <Table
        columnsFilter="businessAgingTable_detail_idjs"
        onFilter={(key, val) => saveServer('businessAgingTable_detail_idjs', val)}
        store={store.table}
        editable={false}
        rowClassName={(record) => {
          return record.sendStatus === 'FAIL' ? 'red-row' : ''
        }}
        selectable
        searchbar={{
          items: [
            ...formColumns,
            {
              title: '合同逾期日',
              dataIndex: 'planCollectionDate',
              dateFormat: 'yyyy-MM-DD',
              type: 'rangePicker',
              ranges: rangePresets,
              itemProps: {
                transform: (val) => {
                  const [startDataTime, endDataTime] = val || []
                  return {
                    planCollectionDate: undefined,
                    planCollectionDateFrom: startDataTime?.format('yyyy-MM-DD'),
                    planCollectionDateTo: endDataTime?.format('yyyy-MM-DD'),
                  }
                },
              },
            },
          ],
        }}
        actions={[
          <ExportBtn
            tableStore={store.table}
            extraParams={{ accountAgeId: id }}
            businessType={'FINANCE_ACCOUNT_AGE_ITEM'}
            ids={keys}
            functionCode={'financeAccountAgeFileExport'}
          >
            导出
          </ExportBtn>,
          <Button.Delete onClick={store.delete} key="close" disabled={!canEdit}>
            删除
          </Button.Delete>,
          <Button.Add onClick={store.add} key="add" disabled={!editStatus}>
            新增
          </Button.Add>,
          <Button.Edit onClick={store.edit} key="edit" disabled={!canEditItem}>
            编辑
          </Button.Edit>,
          <Button onClick={store.reload} key=" reload" disabled={!editStatus}>
            重新生成
          </Button>,
          <Button onClick={store.push} key="push" disabled={!editStatus}>
            推送苍穹
          </Button>,
          <Button onClick={store.complete} key="complete" disabled={!editStatus}>
            完成
          </Button>,
        ]}
        scroll={{ x: 'auto' }}
        resizable
        // columnsFilter
        columns={columns}
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(BudgetBusinessAgingDetail)
