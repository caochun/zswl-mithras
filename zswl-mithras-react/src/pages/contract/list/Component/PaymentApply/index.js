import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { useEffect, useMemo, useState } from 'react'
import Api from './api'

const Index = ({ contractId, businessVersion, canEdit, taskStatus, taskActivityId }) => {
  const [detailList, setDetailList] = useState([])

  const getDetailList = async () => {
    const res = await Api.getContractPaymentAll({ contractId, businessVersion })
    setDetailList(res)
  }

  useEffect(() => {
    contractId && getDetailList()
  }, [contractId])

  const nameColumns = useMemo(() => {
    return [
      '付款金额(元)',
      '首期租金(元)',
      '客户保证金(元)',
      '厂商保证金(元)',
      '服务费/咨询费(元)',
      ['ZL', 'ZZ'].includes(detailList?.[0]?.bizType) && '手续费(元)',
      ['ZL', 'ZZ'].includes(detailList?.[0]?.bizType) && '首期利息(元)',
      '名义价款(元)',
      '付款日期',
      // '资金FTP成本',
      // '票据FTP成本',
    ].filter(Boolean)
  }, [detailList])
  const columns = getDescColumns(ALL_COLUMNS(), nameColumns)
  const FtpColumns = getDescColumns(ALL_COLUMNS(), [
    'FTP基础价格',
    'FTP山区调整',
    'FTP评级调整',
    'FTP指引价格',
    'FTP是否质押',
    'FTP手工调整',
    '杭甬特殊调整',
    'FTP考核价格',
    '票据FTP价格',
    // '是否为特殊事项',
    'FTP备注',
  ])

  const saveData = async (values, initialValues) => {
    await Api.modifyContractPayment({
      ...values,
      id: initialValues?.ftpAssessDto?.id,
    })
    getDetailList()
  }

  return (
    <div>
      {detailList.map((item, index) => {
        return (
          <div key={index}>
            <EditDescription
              title={
                <div>
                  {index === 0 && <div>付款信息</div>}
                  <div
                    className={'z-sub-title'}
                    style={{
                      marginTop: 20,
                    }}
                  >
                    付款申请编号 {item.paymentCode ?? '-'}
                  </div>
                </div>
              }
              detail={item}
              // saveData={(values) => saveData(values, item)}
              canEdit={false}
              columns={columns}
            />
            {!!item?.ftpAssessDto && (
              <div style={{ marginTop: 10 }}>
                <EditDescription
                  column={3}
                  title={<div style={{ fontSize: 14, marginLeft: 10 }}>FTP考核信息</div>}
                  detail={item?.ftpAssessDto || {}}
                  saveData={(values) => saveData(values, item)}
                  columns={FtpColumns}
                  // 何时不能编辑呢？
                  // 在财务审批的节点，本次新增的ftp成本可编辑，之前已审批通过的ftp成本置灰不可修改
                  canEdit={
                    canEdit || (taskActivityId === 'userTask_financeManager' && taskStatus == '1')
                  }
                />
              </div>
            )}
          </div>
        )
      })}
    </div>
  )
}

export default Index
