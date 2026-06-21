import rentCollectionApi from '@/api/afterLease/rentCollectionApi'
import { DetailLayout } from '@/components/Layout'
import { TextAreaReadOnly } from '@/components/Form'
import { AmountColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Form, Table, TableStore } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import { isObject, uniqueId } from 'lodash'
import { message } from 'antd'
import DataFileList from './DataFileList'
import { saveServer } from '@/utils'

const AfterLeaseRentCollectionBatchInterest = ({ id: reduceBaseId, canEditFlag }) => {
  const canEdit = canEditFlag === 'true'
  const [form] = Form.useForm()
  const handleSubmit = async () => {
    const values = await form.validateFields()
    const { list } = await table.submit()
    const required = list.some((v) =>
      isObject(v) ? [undefined, null].includes(v.reducePenaltyInterest) : false
    )

    if (required) {
      message.error('请填写所有减免的罚息金额')
      return
    }
    const items = list.map(({ uuid, reducePenaltyInterest, ...rest }) => ({
      reducePenaltyInterest: Math.round(reducePenaltyInterest * 10000),
      ...rest,
    }))
    const res = await rentCollectionApi.postPenaltyModify({ ...values, items, id: reduceBaseId })
    message.success('保存成功')
  }
  const table = useMemo(() => {
    return new TableStore({
      pagination: false,
      request: async (params) => {
        if (!params?.reduceBaseId) return []
        const res = await rentCollectionApi.postReductionList(params)
        form.setFieldsValue({ notes: res?.notes })
        const list = res?.items?.map(({ penaltyInterest, reducePenaltyInterest, ...rest }) => ({
          ...rest,
          penaltyInterest,
          reducePenaltyInterest,
          arrearsAmount: penaltyInterest - reducePenaltyInterest,
          uuid: uniqueId(),
        }))
        return list
      },
    })
  }, [])
  useEffect(() => {
    table.search({ reduceBaseId })
  }, [reduceBaseId])
  const columns = [
    /*
    客户名称	合同编号	借据编号	期项	合同金额	应收日期	应收金额	实收金额	罚息计算截止日期	应收罚息	申请减免罚息金额	减免后应收罚息 */
    { title: '客户名称', dataIndex: 'clientName', editable: false, width: 200 },
    { title: '合同编号', dataIndex: 'contractCode', editable: false, width: 200 },
    { title: '借据编号', dataIndex: 'receiptCode', editable: false, width: 200 },
    { title: '期项', dataIndex: 'phase', editable: false },
    AmountColumn({ title: '合同金额', dataIndex: 'applyCreditAmount', editable: false }),
    { title: '应收日期', dataIndex: 'planCollectionDate', editable: false },
    AmountColumn({ title: '应收金额', dataIndex: 'planCollectionAmount', editable: false }),
    AmountColumn({ title: '实收金额', dataIndex: 'collectionAmount', editable: false }),
    { title: '罚息计算截止日期', dataIndex: 'penaltyCloseDate', editable: false },
    AmountColumn({ title: '应收罚息', dataIndex: 'penaltyInterest', editable: false }),
    AmountColumn({
      title: '申请减免罚息金额',
      dataIndex: 'reducePenaltyInterest',
      editable: true,
      wrapItemProps: {
        inputConfig: {
          onChange: (e, { dataIndex, index, dataSource }) => {
            const { penaltyInterest } = dataSource
            const reducePenaltyInterest = Math.round(e?.replace(/\$\s?|(,*)/g, '') * 10000)
            const arrearsAmount = penaltyInterest - reducePenaltyInterest
            table?.setRowByIndex(index, { ...dataSource, arrearsAmount })
          },
        },
      },
    }),
    AmountColumn({ title: '减免后应收罚息', dataIndex: 'arrearsAmount' }),
  ]

  const anchorList = [{ label: '申请减免罚息金额' }, { label: '申请原因' }].filter(Boolean)
  return (
    <DetailLayout title="罚息减免申请" moduleName="adjust" anchorList={anchorList}>
      <div>
        <div style={{ fontSize: 16, fontWeight: 600 }}>申请减免罚息金额</div>
        <Table
          columnsFilter={'rentCollection_BatchInterest_1'}
          onFilter={(key, val) => saveServer('rentCollection_BatchInterest_1', val)}
          columns={columns}
          store={table}
          autoRequest={false}
          resizable
          columnWidth={120}
          editable={canEdit}
          scroll={{ x: 'auto' }}
          rowKey={'uuid'}
        ></Table>
      </div>
      <Form form={form}>
        <div style={{ fontSize: 16, fontWeight: 600 }}>申请原因</div>
        <Form.Item name="notes" rules={[{ required: true, message: '请输入申请原因' }]}>
          <TextAreaReadOnly onlyRead={!canEdit} />
        </Form.Item>
        {canEdit && (
          <Button type="primary" onClick={handleSubmit}>
            保存修改
          </Button>
        )}
        <DataFileList canEdit={false} id={reduceBaseId}></DataFileList>
      </Form>
    </DetailLayout>
  )
}

export default observer(AfterLeaseRentCollectionBatchInterest)
