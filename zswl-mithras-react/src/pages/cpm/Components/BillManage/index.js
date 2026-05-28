import { Drawer, Table, TableStore, ModalStore, Modal } from '@zswl/components'
import { useMemo } from 'react'
import { amountFormat, formatPercent, hasValue } from '@/utils'
import Api from '@/api/cpm/payment/billManage/billManagement'
import { message } from 'antd'
import moment from 'moment'
import Edit from './Edit'
import { saveServer } from '@/utils'

const Index = ({ store, curBillParams = {}, canEdit = true }) => {
  const IS_PAYMENT = curBillParams?.billType === 'PAYMENT'
  const deleteApi = async (data) => {
    Modal.confirm({
      title: '是否删除？',
      onOk: async () => {
        await Api.postManagementRemove({
          ...data,
        })
        message.success('删除成功')
        table.search()
      },
    })
  }

  const addModal = new ModalStore({
    onOpen: (record) => {
      if (!record) return
      const { billExpireDate, billBuyRateType } = record
      return {
        ...record,
        billBuyRateType: !!billBuyRateType,
        billExpireDate: billExpireDate && moment(billExpireDate),
      }
    },
    onFinish: async (values) => {
      const { id, billBuyRateType } = values
      id
        ? await Api.postManagementModify({
            ...values,
            contractId: curBillParams.contractId,
            billBuyRateType: billBuyRateType ? 1 : 0,
            id,
          })
        : await Api.postManagementAdd({
            ...values,
            billBuyRateType: billBuyRateType ? 1 : 0,
            ...curBillParams,
          })
      const msg = id ? '修改成功' : '添加成功'
      message.success(msg)
      table.search()
      addModal.close()
    },
  })

  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        return Api.postManagementList({ ...params, ...curBillParams })
      },
    })
  }, [curBillParams?.mainId])

  return (
    <Drawer store={store} width={800} destroyOnClose extra={null} title="票据管理">
      <Table
              columnsFilter={'Components_BillManage_1'}
              onFilter={(key,val) => saveServer('Components_BillManage_1',val)}
        scroll={{
          x: 700,
        }}
        store={table}
        extra={[
          {
            name: '新增',
            type: 'primary',
            onClick: addModal.open,
          },
        ]}
        columns={[
          {
            title: '票据号',
            dataIndex: 'billCode',
          },
          {
            title: '票据面额(元)',
            dataIndex: 'billAmount',
            align: 'right',
            render: (val) => {
              return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
            },
          },
          {
            title: '票据到期日',
            dataIndex: 'billExpireDate',
          },
          !IS_PAYMENT && {
            title: '票据买入价',
            dataIndex: 'billBuyRate',
            render: (val, { billBuyRateType }) => {
              return billBuyRateType === 1
                ? '同项目FTP'
                : hasValue(val)
                ? amountFormat(formatPercent(val)) + '%'
                : '-'
            },
          },
          {
            title: '操作',
            width: 120,
            actions: (record) => {
              return [
                {
                  name: '编辑',
                  onClick: () => addModal.open(record),
                },
                {
                  name: '删除',
                  onClick: deleteApi,
                },
              ]
            },
          },
        ].filter(Boolean)}
      ></Table>
      <Edit store={addModal} IS_PAYMENT={IS_PAYMENT}></Edit>
    </Drawer>
  )
}

export default Index
