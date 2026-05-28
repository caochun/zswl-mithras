import { use, useEffect, useMemo, useRef } from 'react'
import { Table, Modal, Form, Select, Input } from 'antd'
import { Button } from '@zswl/components';
import { amountFormat, getKeyOptionsLabelMapPlus, formatPercent, hasValue } from '@/utils'
import { observer, getQuery, history } from '@zswl/admin'
import Store from './store'

const ALL_COLUMNS = [
  {
    title: '序号',
    dataIndex: 'sort',
    key: 'sort',
    render: (_val, _record, index) => {
      return index + 1
    },
    width: 80,
  },
  {
    title: '现金流编号',
    dataIndex: 'code',
    key: 'code',
    width: 180,
  },
  {
    title: '日期',
    dataIndex: 'planCollectionDate',
    key: 'planCollectionDate',
    width: 200,
  },
  {
    title: '期项',
    dataIndex: 'phase',
    key: 'phase',
  },
  {
    title: '租金(元)',
    dataIndex: 'planCollectionAmount',
    key: 'planCollectionAmount',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
    },
  },
  {
    title: '本金(元)',
    dataIndex: 'principal',
    key: 'principal',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
    },
  },
  {
    title: '利息(元)',
    dataIndex: 'interest',
    key: 'interest',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
    },
  },
  {
    title: '已收租金(元)',
    dataIndex: 'collectionAmount',
    key: 'collectionAmount',
    align: 'right',
    // width: 200,
    render: (val) => {
      return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
    },
  },
  {
    title: '未收租金（元）',
    dataIndex: 'restAmount',
    key: 'restAmount',
    align: 'right',
    render: (val) => {
      return hasValue(val) ? amountFormat(formatPercent(val)) : '-'
    },
  },
];
const Index = ({ retreatInfoId, contractId, clientName, canEdit, setRentListSize }) => {
  const formRef = useRef(null)
  const store = useMemo(() => {
    return new Store(contractId)
  }, [contractId])
  useEffect(() => {
    store.rentList(retreatInfoId)
  }, [retreatInfoId])
  const handleFormChange = async(values) => {
    const res = await store.getFlowNumDetail(values.code)
    formRef.current.setFieldsValue({
      ...res,
      clientName:clientName,
      cashFlowAmount:hasValue(res.cashFlowAmount) ? amountFormat(formatPercent(res.cashFlowAmount)) : '',
      principal:hasValue(res.principal) ? amountFormat(formatPercent(res.principal)) : '',
      restAmount:hasValue(res.restAmount) ? amountFormat(formatPercent(res.restAmount)) : '',
      interest:hasValue(res.interest) ? amountFormat(formatPercent(res.interest)) : '',
      planCollectionAmount:hasValue(res.planCollectionAmount) ? amountFormat(formatPercent(res.planCollectionAmount)) : '',
    })
  }
  let columns = ALL_COLUMNS
  if(canEdit){
    columns = columns.concat({
      title: '操作',
      dataIndex: 'opt',
      key: 'opt',
      render: (_val, _record) => {
        return <Button type="link" block onClick={()=>store.delRentItem(_record.id)}>删除</Button>
      },
    })
  }
  setRentListSize(store.dataSource?.length)
  const { planCollectionAmount, principal, interest, collectionAmount, restAmount } = store.summary
  return (
    <div>
      <div  style={{display:'flex',justifyContent:'space-between', alignItems:'center', padding:'8px'}}>
        <div>
          <div class="z-title">抵扣租金信息</div>
          <span style={{fontSize:'12px'}}>提示：按照抵扣租金信息列表从上往下按顺序抵扣</span>
        </div>
        {canEdit && <Button type='primary' onClick={store.open}>新增</Button>}
      </div>
      <Table
        dataSource={store.dataSource}
        columns={columns}
        pagination={false}
        scroll={{
          x: 1200,
        }}
        summary={()=>{
          return (
            <Table.Summary.Row>
              <Table.Summary.Cell index={0} colSpan={4}>
                合计
              </Table.Summary.Cell>
              <Table.Summary.Cell index={1} align='right'>
                {hasValue(planCollectionAmount, true) ? amountFormat(formatPercent(planCollectionAmount)) : '-'}
              </Table.Summary.Cell>
              <Table.Summary.Cell index={2} align='right'>
                {hasValue(principal, true) ? amountFormat(formatPercent(principal)) : '-'}
              </Table.Summary.Cell>
              <Table.Summary.Cell index={3} align='right'>
                {hasValue(interest, true) ? amountFormat(formatPercent(interest)) : '-'}
              </Table.Summary.Cell>
              <Table.Summary.Cell index={4} align='right'>
                {hasValue(collectionAmount, true) ? amountFormat(formatPercent(collectionAmount)) : '-'}
              </Table.Summary.Cell>
              <Table.Summary.Cell index={5} align='right'>
                {hasValue(restAmount, true) ? amountFormat(formatPercent(restAmount)) : '-'}
              </Table.Summary.Cell>
              {canEdit&&<Table.Summary.Cell index={6}></Table.Summary.Cell>}
            </Table.Summary.Row>
          )
        }}
      />
      <Modal
        title="新增现金流项目"
        open={store.visible}
        onOk={()=>store.add(clientName)}
        onCancel={store.close}
        width={'45%'}
        destroyOnClose
      > 
        <Form
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 12 }}
          onValuesChange={handleFormChange}
          ref={formRef}
        >
          <Form.Item
              label="客户名称"
              name="clientName"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="合同编号"
              name="contractCode"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="现金流编号"
              name="code"
              required
          >
              <Select options={store.flowList}/>
          </Form.Item>
          <Form.Item
              label="应收金额（元）"
              name="planCollectionAmount"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="应收日期"
              name="planCollectionDate"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="未收金额（元）"
              name="restAmount"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="本金（元）"
              name="principal"
          >
              <Input disabled/>
          </Form.Item>
          <Form.Item
              label="利息（元）"
              name="interest"
          >
              <Input disabled/>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default observer(Index)
