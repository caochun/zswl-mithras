import { Table, Select } from '@zswl/components'
import styles from './index.less'
import { Radio, Tooltip } from 'antd'
import store from './store'
import CashFlowTable from '../CashFlowTable'
import { QuestionCircleOutlined } from '@ant-design/icons'
import { useEffect, useState } from 'react'
import Api from '@/api/cpm/contractCpmApi'
import { saveServer } from '@/utils'

const PaymentRecords = ({ id, contractDetailData }) => {
  const [cList, setClist] = useState([])
  const [cashArr, setCashArr] = useState([])
  const [cDetail, setcDetail] = useState({ ...contractDetailData })
  const contractList = async (p) => {
    const data = await Api.contractList({ contractId: id })
    setClist(data)
  }

  const contractDetail = async (contractId) => {
    const data = await Api.contractDetail({ contractId })
    setcDetail(data)
  }

  const cashList = async (contractId) => {
    const data = await Api.cashList({ contractId })
    setCashArr(data)
  }
  useEffect(() => {
    setcDetail(contractDetailData)
  }, [contractDetailData])
  useEffect(() => {
    //contractDetail(id)
    contractList(id)
    cashList(id)
  }, [id])
  const onChange = async (e) => {
    contractDetail(e.target.value)
    //const data = await Api.contractDetailList(e.target.value, '')
    cashList(e.target.value)
    store.cashFlowTable.setParams({ contractId: e.target.value })
    store.cashFlowTable.search()
  }

  return (
    <div className={styles.contractDownPayment}>
      <div className={styles.contractWrap}>
        <div className={styles.contractNum}>
          {cList.length > 0 && (
            <Radio.Group
              onChange={onChange}
              options={cList}
              optionType={'button'}
              defaultValue={id}
              //defaultValue={'164'}
            ></Radio.Group>
          )}
        </div>
        <div className={styles.contractTitle}>合同状态：{cDetail.contractStatus}（单位：元）</div>
      </div>
      <div>
        <Table
          columnsFilter={'contractCpm_ContractDownPayment_1'}
          onFilter={(key, val) => saveServer('contractCpm_ContractDownPayment_1', val)}
          bordered
          scroll={{
            x: 1200,
          }}
          dataSource={[{ ...cDetail }]}
          columns={[
            {
              title: '已收租金',
              children: [
                {
                  title: '已收本金',
                  align: 'right',
                  dataIndex: 'receivedPrincipal',
                  key: 'receivedPrincipal',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
                {
                  title: '已收利息',
                  align: 'right',
                  dataIndex: 'receivedInterest',
                  key: 'receivedInterest',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
              ],
            },
            {
              title: '剩余未还金额',
              children: [
                {
                  title: '剩余本金',
                  align: 'right',
                  dataIndex: 'lastPrincipal',
                  key: 'lastPrincipal',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
                {
                  title: '剩余利息',
                  align: 'right',
                  dataIndex: 'lastInterest',
                  key: 'lastInterest',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
                {
                  title: '名义价款',
                  align: 'right',
                  dataIndex: 'nominalLoanPrice',
                  key: 'nominalLoanPrice',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
              ],
            },
            // {
            //   title: '逾期情况',
            //   children: [
            //     {
            //       title: '逾期未还金额',
            //       align: 'right',
            //       dataIndex: 'overdueAmount',
            //       key: 'overdueAmount',
            //       render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
            //     },
            //     {
            //       title: '逾期利息',
            //       align: 'right',
            //       dataIndex: 'overdueInterest',
            //       key: 'overdueInterest',
            //       render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
            //     },
            //     {
            //       title: '已收逾期利息',
            //       align: 'right',
            //       dataIndex: 'receivedOverdueInterest',
            //       key: 'receivedOverdueInterest',
            //       render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
            //     },
            //     {
            //       title: '未收逾期利息',
            //       align: 'right',
            //       dataIndex: 'uncollectedOverdueInterest',
            //       key: 'uncollectedOverdueInterest',
            //       render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
            //     },
            //   ],
            // },
            {
              title: '其他金额',
              children: [
                {
                  title: '剩余保证金',
                  align: 'right',
                  dataIndex: 'lastMargin',
                  key: 'lastMargin',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
                {
                  title: (
                    <>
                      <span style={{ paddingRight: '8px' }}>已收资费</span>
                      <Tooltip title="已收资费为手续费/咨询费/服务费的已收金额">
                        <QuestionCircleOutlined />
                      </Tooltip>
                    </>
                  ),
                  dataIndex: 'otherSum',
                  key: 'otherSum',
                  align: 'right',
                  render: (v) => <Tooltip title={v / 10000}>{v / 10000}</Tooltip>,
                },
              ],
            },
          ]}
        />
      </div>
      <div>
        <CashFlowTable cashArr={cashArr} store={store} />
      </div>
    </div>
  )
}
export default PaymentRecords
