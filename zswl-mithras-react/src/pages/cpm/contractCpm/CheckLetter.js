import { useState } from 'react'
import { observer } from '@zswl/admin'
import { Modal, Table } from '@zswl/components'
import { hasValue, amountFormat, formatPercent, timeFormat } from '@/utils'
import store from './store'
import { message } from 'antd'
import moment from 'moment'
import Api from '@/api/cpm/contractCpmApi'
import { saveServer } from '@/utils'

const { useStore } = Table
const { Row, Cell } = Table.Summary

const Index = () => {
  const [result, setResult] = useState({})
  const [date, setDate] = useState('')
  const $table = useStore({
    pagination: false,
    request: async (params) => {
      setDate(params.date)
      const res = await Api.postLetterList(params)
      setResult(res)
      return res.reconciliationLetterBOS || []
    },
  })

  const handleExport = async () => {
    if (!date) {
      message.warn('请先选择日期')
      return
    }
    await Api.postLetterExport({ date })
  }
  const formatVal = (val) => {
    return hasValue(val) ? <div style={{ textAlign: 'right' }}>{amountFormat(val)}</div> : '-'
  }
  return (
    <Modal title={'对账函'} store={store.$checkLetter} destroyOnClose footer={null} width={800}>
      <Table
              columnsFilter={'cpm_contractCpm_CheckLetter'}
              onFilter={(key,val) => saveServer('cpm_contractCpm_CheckLetter',val)}
        rowKey={'clientId'}
        scroll={{
          x: false,
          y: 400,
        }}
        resizable
        store={$table}
        searchbar={{
          initialValues: {
            date: moment(),
          },
          resetButton: false,
          searchButton: false,
          items: [
            {
              label: '截止至',
              name: 'date',
              type: 'datePicker',
              allowClear: false,
              itemProps: {
                rules: [{ required: true, message: '请选择日期' }],
                transform: (val) => {
                  return val ? timeFormat(val) : undefined
                },
              },
            },
          ],
        }}
        extra={[
          {
            name: '导出全部',
            type: 'primary',
            onClick: handleExport,
          },
        ]}
        columns={[
          {
            title: '客户名称',
            dataIndex: 'clientName',
            width: 260,
            fixed: 'left',
          },
          {
            title: '剩余租金及留购价款(元)',
            dataIndex: 'rentAndNominalPrice',
            width: 200,
            align: 'right',
            render: formatVal,
          },
          {
            title: '保证金(元)',
            dataIndex: 'margin',
            align: 'right',
            width: 140,
            render: formatVal,
          },
          {
            title: '剩余本息(元)',
            dataIndex: 'blPrincipalAndInterest',
            align: 'right',
            width: 140,
            render: formatVal,
          },
        ]}
        summary={() => {
          return (
            <Table.Summary fixed>
              <Row>
                <Cell index={0}>汇总：{result.reconciliationLetterBOS?.length ?? 0}个</Cell>
                <Cell index={1}>{formatVal(result.rentAndNominalPriceTotal)}</Cell>
                <Cell index={2}>{formatVal(result.marginTotal)}</Cell>
                <Cell index={3}>{formatVal(result.blPrincipalAndInterestTotal)}</Cell>
              </Row>
            </Table.Summary>
          )
        }}
      ></Table>
    </Modal>
  )
}

export default observer(Index)
