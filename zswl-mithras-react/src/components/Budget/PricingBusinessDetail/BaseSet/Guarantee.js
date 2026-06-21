import { Access, Button, Form, Modal, ModalStore, Page, Table, TableStore } from '@zswl/components'
import { http, observer } from '@zswl/admin'
import { getTableColumns, rules } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, DatePicker, Input, Tooltip, message } from 'antd'

import moment from 'moment'
import Api from '@/api/budget/pricing/ftp/pricingBusinessDetailApi'
import { saveServer } from '@/utils'

const EditModal = ({ store }) => {
  return (
    <Modal
      destroyOnClose
      store={store}
      propsBy={(data) => {
        return {
          title: data ? '编辑担保成本' : '创建担保成本',
        }
      }}
    >
      <Form labelCol={{ span: 8 }} preserve={false}>
        <Form.Item
          label="时间"
          name="month"
          rules={[rules.required('请选择')]}
          transform={(val) => {
            return moment(val).format('YYYY-MM-01')
          }}
        >
          <DatePicker.MonthPicker disabled />
        </Form.Item>
        <Form.Item label="当期均值(%)" name="currentAverage" rules={[rules.required()]}>
          <FormAmount addonAfter="%" step={0.01} />
        </Form.Item>
        <Form.Item name="id" hidden>
          <Input></Input>
        </Form.Item>
      </Form>
    </Modal>
  )
}
function BudgetPricingBusinessBaseSetGuarantee({ path, params, canEdit }) {
  const reload = async () => {
    await Api.postGuaranteeCost(params)
    message.success('刷新成功')
    table.search()
  }
  // const hasValuation = Access.validate('newFtpGuaranteeCostPricingModify')
  const columns = useMemo(() => {
    const nameColumns = getTableColumns(ALL_COLUMNS, [
      {
        title: '时间',
        dataIndex: 'month',
        width: 100,
        render: (value) => {
          return value ? moment(value).format('YYYY-MM') : '-'
        },
      },
      { title: '当期均值(%)', rename: '月末存量融资的加权平均费率', width: 100 },
    ])

    return [
      ...nameColumns,
      canEdit && {
        title: '操作',
        dataIndex: 'action',
        width: 100,
        actions: (record) => [
          {
            name: '编辑',
            onClick: () => modal.open(record),
          },
        ],
      },
    ].filter(Boolean)
  }, [canEdit])
  const table = useMemo(() => {
    return new TableStore({
      request: async (tableParams) => {
        const res = await Api.postGuaranteeCostList({
          ...tableParams,
          ...params,
        })
        return res
      },
    })
  }, [])

  const modal = useMemo(() => {
    return new ModalStore({
      onOpen: async (values) => {
        if (!values) {
          return {}
        }
        const { month } = values
        return {
          ...values,
          month: month && moment(month),
        }
      },
      onFinish: async (values) => {
        const { month, ...rest } = values
        const res = await Api.postGuaranteeCostModify(rest)
        message.success('修改成功')
        modal.close()
        table.search()
      },
    })
  }, [])
  return (
    <Card
      title="担保成本"
      style={{ marginTop: 12 }}
      extra={
        canEdit && (
          <Tooltip title="刷新重新计算最近一个结束月均值">
            <Button type="primary" onClick={reload} access={'newFtpGuaranteeCostPricingFlash'}>
              刷新
            </Button>
          </Tooltip>
        )
      }
    >
      <Table onFilter={(key,val) => saveServer('detail_BaseSet_Guarantee',val)} columnsFilter="detail_BaseSet_Guarantee" store={table} editable={false} columns={columns} />
      <EditModal store={modal} />
    </Card>
  )
}

export default observer(BudgetPricingBusinessBaseSetGuarantee)
