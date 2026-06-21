import { amountFormat, hasValue } from '@/utils'
import { observer, getQuery } from '@zswl/admin'
import { Button, Table, Access } from '@zswl/components'
import { Descriptions } from 'antd'
import PaymentModal from './PaymentModal'
import styles from '../../index.less'
import { useMemo, useState } from 'react'
import { Tabs } from 'antd'
import BillManage from '../../../BillManage/CpmBillManage'
import { BankAccount } from '@/components/Form'
import WriteOffModal from './WriteOffModal'
import { saveServer } from '@/utils'

const PaymentTable = ({ store, isFormApproval, canEdit, pagePage, taskActivityId }) => {
  const { actualDetail } = store
  const detail = store.page.getData()
  const isProcessQueryPage = getQuery().nav === 'myquery'
  const canWriteoff = taskActivityId === 'userTask_cashier'
  const isPending = getQuery().nav === 'pending'
  const [curTab, setCurTab] = useState('1')
  const [curBillParams, setCurBillParams] = useState()
  const descriptionColumns = useMemo(() => {
    if (!actualDetail) {
      return []
    }
    const { payableAmount, paidAmount, obligation } = actualDetail
    return [
      {
        label: '应付金额(元)',
        value: hasValue(payableAmount) ? amountFormat(payableAmount / 10000) : '-',
      },
      {
        label: '已付金额(元)',
        value: hasValue(paidAmount) ? amountFormat(paidAmount / 10000) : '-',
      },

      {
        label: '待付金额(元)',
        value: hasValue(obligation) ? amountFormat(obligation / 10000) : '-',
      },
    ]
  }, [actualDetail])

  const columns = useMemo(() => {
    return [
      {
        title: '序号',
        width: 80,
        dataIndex: 'seqCode',
      },
      {
        title: '信息来源',
        width: 120,
        dataIndex: 'infoSource',
      },
      {
        title: '状态',
        width: 140,
        dataIndex: 'writeOffStatus',
        matchOption: 'writeOffStatus',
      },
      {
        title: '付款方式',
        width: 120,
        dataIndex: 'paymentMethod',
      },
      {
        title: '实付金额(元)',
        width: 150,
        dataIndex: 'paidInAmount',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? amountFormat(val / 10000) : '-'
        },
      },
      {
        title: '实付日期',
        width: 180,
        dataIndex: 'paidInDate',
      },
      {
        title: '我方银行账户名',
        dataIndex: 'ourAccountName',
        width: 200,
      },
      {
        title: '银行账号',
        dataIndex: 'ourAccountNumber',
        width: 260,
        render: (val) => BankAccount.Format({ value: val }),
      },
      {
        title: '开户行',
        dataIndex: 'ourAccountBank',
        width: 300,
      },
      {
        title: '对方银行账户名',
        dataIndex: 'oppositeAccountName',
        width: 200,
      },
      {
        title: '对方银行账号',
        dataIndex: 'oppositeAccountNumber',
        width: 260,
        render: (val) => BankAccount.Format({ value: val }),
      },
      {
        title: '对方开户行',
        dataIndex: 'oppositeAccountBank',
        width: 300,
      },
    ]
  }, [])

  return (
    <>
      <div className={styles.nav} style={{ marginBottom: 20 }}>
        <div className={styles.title}>付款记录汇总</div>
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
        className={styles.summaryDescription}
      >
        {descriptionColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <div className={styles.subTitle}>付款记录明细</div>
      <Tabs
        defaultActiveKey={curTab}
        onChange={setCurTab}
        activeKey={curTab}
        tabBarExtraContent={
          !isFormApproval &&
          canEdit &&
          pagePage.paymentStatus === 'TAKE_EFFECT' && (
            <Button
              access={'paymentaddactualdetail'}
              disabled={actualDetail?.writeOffUserName?.length > 0}
              type="primary"
              onClick={() =>
                store.collectionModal.open({
                  _pageStatus: 'create',
                })
              }
            >
              新增
            </Button>
          )
        }
        items={[
          {
            key: '1',
            label: '未确认',
            children: (
              <Table
              columnsFilter={'detail_Components_RecordList_1'}
              onFilter={(key,val) => saveServer('detail_Components_RecordList_1',val)}
                resizable
                store={store.unConfirmCollectionTable}
                columns={[
                  ...columns,
                  {
                    title: '操作',
                    width: 200,
                    fixed: 'right',
                    dataIndex: 'contractCode',
                    actions: (record) => {
                      const { id, writeOffStatus } = record
                      return [
                        canWriteoff && (
                          <WriteOffModal
                            record={record}
                            detail={detail}
                            store={store}
                            actualDetail={actualDetail}
                          />
                        ),
                        {
                          name: '查看',
                          onClick: () =>
                            store.getCollectionDetailData({
                              id,
                              isConfirmed: 0,
                              status: 'read',
                            }),
                        },
                        canEdit &&
                          writeOffStatus !== 'CLOSED' && {
                            name: '编辑',
                            onClick: () =>
                              store.getCollectionDetailData({
                                id,
                                isConfirmed: 0,
                                status: 'edit',
                              }),
                            disabled: !['NO_PAID'].includes(detail.writeOffStatus),
                            access: 'paymentmodifyactualdetail',
                            fallback: null,
                          },
                        !isFormApproval &&
                          canEdit &&
                          writeOffStatus !== 'CLOSED' && {
                            name: '删除',
                            onClick: () => store.deleteCollectItem(id),
                            access: 'paymentremoveactualdetail',
                            fallback: null,
                          },
                      ].filter(Boolean)
                    },
                  },
                ]}
              />
            ),
          },
          (!isFormApproval || isProcessQueryPage) && {
            key: '2',
            label: '已确认',
            children: <Table
                        columnsFilter={'detail_Components_RecordList_2'}
                        onFilter={(key,val) => saveServer('detail_Components_RecordList_2',val)}
                        resizable
                        store={store.confirmTable} columns={[...columns]}
                      />,
          },
          (!isFormApproval || isProcessQueryPage) && {
            key: '3',
            label: '已核销',
            children: (
              <Table
              columnsFilter={'detail_Components_RecordList_3'}
              onFilter={(key,val) => saveServer('detail_Components_RecordList_3',val)}
                resizable
                store={store.collectionTable}
                columns={[
                  ...columns,
                  {
                    title: '操作',
                    width: 200,
                    fixed: 'right',
                    dataIndex: 'contractCode',
                    actions: ({ id, paymentWay }) => {
                      return [
                        {
                          name: '查看',
                          onClick: () =>
                            store.getCollectionDetailData({
                              id,
                              isConfirmed: 1,
                              status: 'read',
                            }),
                        },

                        paymentWay === 1 &&
                          canEdit && {
                            name: '关联票据',
                            onClick: () => {
                              setCurBillParams({
                                mainId: id,
                                billType: 'PAYMENT',
                                contractId: actualDetail.contractId,
                              })
                              store.billManageDraw.open()
                            },
                          },
                      ].filter(Boolean)
                    },
                  },
                ]}
              />
            ),
          },
        ].filter(Boolean)}
      ></Tabs>
      <PaymentModal store={store} />
      <BillManage store={store.billManageDraw} curBillParams={curBillParams}></BillManage>
    </>
  )
}

export default observer(PaymentTable)
