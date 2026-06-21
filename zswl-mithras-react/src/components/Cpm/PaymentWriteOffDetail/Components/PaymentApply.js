import { EditDescription } from '@/components/Table'
import { FtpAssessmentColumns as ALL_COLUMNS } from '@/components/PaymentFtpColumns/FtpAssessmentColumnsEntries'
import { getDescColumns } from '@/utils'
import { useEffect, useMemo, useState } from 'react'
import Api from '@/api/cpm/payment/contractPaymentFtpApi'

const CpmPaymentWriteOffPaymentApply = ({ detail, taskStatus, taskActivityId, store }) => {
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
  ])

  const saveData = async (values) => {
    await Api.modifyContractPayment({
      ...values,
      id: detail?.id,
    })
    store?.page?.init?.()
  }

  return (
    <div>
      <div style={{ marginTop: 10 }}>
        <EditDescription
          column={3}
          title={<div style={{ fontSize: 14, marginLeft: 10 }}>FTP考核信息</div>}
          detail={detail || {}}
          saveData={(values) => saveData(values, detail)}
          columns={FtpColumns}
          // 何时不能编辑呢？
          // 在财务审批的节点，本次新增的ftp成本可编辑，之前已审批通过的ftp成本置灰不可修改
          canEdit={taskActivityId === 'userTask_cashier' && taskStatus == '1'}
        />
      </div>
    </div>
  )
}

export default CpmPaymentWriteOffPaymentApply
