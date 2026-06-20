import { amountFormat, hasValue } from '@/utils'
import { history, observer } from '@zswl/admin'
import { App, Button, Select, Table } from '@zswl/components'
import { Descriptions, Popconfirm, Tooltip } from 'antd'
import CollectionModal from './CollectionModal'
import styles from '../../index.less'
import { useMemo, useState } from 'react'
import BillManage from '../../../../BillManage/CpmBillManage'
import { saveServer } from '@/utils'

const CollectionTable = ({ store }) => {
  const { recordData } = store
  const [activeData, setActiveData] = useState()
  const [curBillParams, setCurBillParams] = useState()

  const descriptionColumns = useMemo(() => {
    const { collectionAmount, interest, penaltyInterest, principal } = recordData?.sum || {}

    return [
      {
        label: '实收金额(元)',
        value: hasValue(collectionAmount) ? amountFormat(collectionAmount / 10000) : '-',
      },
      {
        label: '本金(元)',
        value: hasValue(principal) ? amountFormat(principal / 10000) : '-',
      },

      {
        label: '利息(元)',
        value: hasValue(interest) ? amountFormat(interest / 10000) : '-',
      },
      {
        label: '罚息(元)',
        value: hasValue(penaltyInterest) ? amountFormat(penaltyInterest / 10000) : '-',
      },
      {
        label: '利息(元)',

        labelStyle: {
          display: 'none',
        },
      },
      {
        label: '利息(元)',

        labelStyle: {
          display: 'none',
        },
      },
    ]
  }, [recordData])

  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>收款记录汇总</div>
      </div>
      <Descriptions
        title=""
        bordered
        column={3}
        style={{
          marginBottom: 20,
        }}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
      >
        {descriptionColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.subTitle}>收款记录明细</div>
      </div>
      <Table
              columnsFilter={'Components_RecordList_CollectionTable'}
              onFilter={(key,val) => saveServer('Components_RecordList_CollectionTable',val)}
        store={store.collectionTable}
        columns={[
          {
            title: '序号',
            width: 80,
            dataIndex: 'sortId',
          },
          {
            title: '信息来源',
            width: 120,
            dataIndex: 'dataSource',
            render: (_, { marginId }) => {
              if (marginId) {
                return (
                  <a target="_self" href={`/cpm/marginManagement/detail/${marginId}`}>
                    <Tooltip title={_}>
                      <div className={styles.customerTitle}>{_}</div>
                    </Tooltip>
                  </a>
                )
              }
              return _
            },
          },
          {
            title: '收款方式',
            width: 120,
            dataIndex: 'collectionType',
          },
          {
            title: '实收日期',
            width: 180,
            dataIndex: 'collectionDate',
            render: (val) => val,
          },
          {
            title: '实收金额',
            width: 150,
            dataIndex: 'collectionAmount',
            render: (val) => (hasValue(val) ? amountFormat(val / 10000) : '-'),
          },
          {
            title: '本金',
            width: 150,
            dataIndex: 'principal',
            render: (val) => (hasValue(val) ? amountFormat(val / 10000) : '-'),
          },
          {
            title: '利息',
            width: 150,
            dataIndex: 'interest',
            render: (val) => (hasValue(val) ? amountFormat(val / 10000) : '-'),
          },
          {
            title: '罚息',
            width: 150,
            dataIndex: 'penaltyInterest',
            render: (val) => (hasValue(val) ? amountFormat(val / 10000) : '-'),
          },
          {
            title: '是否开票',
            width: 150,
            dataIndex: 'invoice',
          },
          {
            title: '被抵扣的合同编号',
            width: 150,
            dataIndex: 'contractCode',
          },
          {
            title: '操作',
            width: 160,
            fixed: 'right',
            dataIndex: 'contractCode',
            render: (_, { id, marginId, writeOffStatus, collectionWay }) => {
              return (
                <div>
                  <Button
                    type="link"
                    onClick={() =>
                      store.getCollectionDetailData(
                        id,
                        !!marginId || writeOffStatus !== 'TO_BE_WRITE_OFF',
                        setActiveData
                      )
                    }
                  >
                    查看
                  </Button>
                  <Button
                    disabled={collectionWay !== 1}
                    type="link"
                    onClick={() => {
                      setCurBillParams({
                        mainId: id,
                        billType: 'COLLECTION',
                        contractId: store.page.getData().contractInfo.contractId,
                      })
                      store.billManageDraw.open()
                    }}
                  >
                    关联票据
                  </Button>
                </div>
              )
            },
          },
          // {
          //   title: '单项核销',
          //   width: 150,
          //   dataIndex: 'writeOffStatus',
          //   fixed: 'right',
          //   render: (_, { id, marginId }) => {
          //     return (
          //       <Select
          //         value={_}
          //         showSearch={false}
          //         disabled={!!marginId}
          //         onChange={(val) => store.onWriteoffChange(val, id)}
          //         options={App.getData().optionsType.collectionRecordWriteOffStatus}
          //       />
          //     )
          //   },
          // },
        ]}
      />
      <CollectionModal activeData={activeData} store={store} />
      <BillManage store={store.billManageDraw} curBillParams={curBillParams}></BillManage>
    </>
  )
}

export default observer(CollectionTable)
