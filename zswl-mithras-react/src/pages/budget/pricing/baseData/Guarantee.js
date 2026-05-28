import { Access, Button, Form, Modal, ModalStore, Page, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns, rules } from '@/utils'
import ALL_COLUMNS from './Column'
import { useMemo } from 'react'
import { Card, DatePicker, Input, Tooltip, message } from 'antd'
import guaranteeCostApi from '@/api/newFtp/guaranteeCostApi'
import FormAmount from '@/components/Form/FormAmount'
import moment from 'moment'
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
function Index({ path }) {
  const reload = async () => {
    await guaranteeCostApi.postPricingFlash({
      month: moment().subtract(1, 'months').format('YYYY-MM-01'),
    })
    message.success('刷新成功')
    table.search()
  }
  const hasValuation = Access.validate('newFtpGuaranteeCostPricingModify')
  const columns = useMemo(() => {
    const nameColumns = getTableColumns(ALL_COLUMNS, [
      {
        title: '时间',
        dataIndex: 'month',
        render: (value) => {
          return value ? moment(value).format('YYYY-MM') : '-'
        },
      },
      '当期均值(%)',
    ])

    return [
      ...nameColumns,
      // hasValuation && {
      //   title: '操作',
      //   dataIndex: 'action',
      //   width: 100,
      //   actions: (record) => [
      //     {
      //       name: '编辑',
      //       onClick: () => modal.open(record),
      //     },
      //   ],
      // },
    ].filter(Boolean)
  }, [])
  const table = useMemo(() => {
    return new TableStore({
      request: async (params = {}) => {
        const res = await guaranteeCostApi.postPricingList(params)
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
        await guaranteeCostApi.postPricingModify(rest)
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
      // extra={
      //   <Tooltip title="刷新重新计算最近一个结束月均值">
      //     <Button type="primary" onClick={reload} access={'newFtpGuaranteeCostPricingFlash'}>
      //       刷新
      //     </Button>
      //   </Tooltip>
      // }
    >
      <Table onFilter={(key,val) => saveServer('pricing_baseData_Guarantee',val)} columnsFilter="pricing_baseData_Guarantee" store={table} editable={false} columns={columns} />
      <EditModal store={modal} />
    </Card>
  )
}

export default observer(Index)
