import { Descriptions, Tag, Space } from 'antd'
import { useMemo, useEffect, useState } from 'react'
import styles from '../index.less'
import { amountFormat } from '@/utils'
import Api from '@/api/cpm/payment/paymentApplicationDetail'
import TransactionInfo from './TransactionInfo'
import { BlackInfo } from '@/components/BlackGray/BlackInfo'

const BaseInfo = ({ store, id, goProcess = () => {} }) => {
  const [riskCount, setRiskCount] = useState()
  const data = store.page.getData()

  useEffect(() => {
    if (data) {
      getMonitorCount()
    }
  }, [data])

  const getMonitorCount = async () => {
    const res = await Api.getMonitorCount({ clientId: data.clientId })
    setRiskCount(res)
  }

  const baseInfoColumns = useMemo(() => {
    const {
      contractCode,
      projName,
      projCode,
      clientName,
      bizDeptName,
      projSponsorUserName,
      applyCreditAmount,
      contractDownPayment,
      contractEarnestMoney,
      contractCommission,
      contractConsultingFee,
      contractNominalPrice,
      leaseType,
      approvedAmount,
      firstInstallmentInterest,
    } = data
    return [
      { label: '合同编号', value: contractCode },
      { label: '项目名称', value: projName },
      { label: '项目编号', value: projCode },
      { label: '类别', value: leaseType },
      {
        label: '客户名称',
        value: (
          <Space>
            <span>{clientName}</span>
            {riskCount ? (
              <Tag color="#f50" onClick={goProcess}>
                <a>{riskCount}条舆情未完成处理</a>
              </Tag>
            ) : null}
            <BlackInfo params={{ clientId: data.clientId }} />
          </Space>
        ),
      },
      { label: '业务部门', value: bizDeptName },
      { label: '项目主办', value: projSponsorUserName },
      { label: '项目批复金额(元)', value: amountFormat(approvedAmount / 10000) },
      { label: '合同金额', value: amountFormat(applyCreditAmount / 10000) },
      { label: '首期租金', value: amountFormat(contractDownPayment / 10000) },
      { label: '首期利息', value: amountFormat(firstInstallmentInterest / 10000) },
      { label: '保证金', value: amountFormat(contractEarnestMoney / 10000) },
      { label: '手续费', value: amountFormat(contractCommission / 10000) },
      { label: '服务费/咨询费', value: amountFormat(contractConsultingFee / 10000) },
      { label: '名义价款', value: amountFormat(contractNominalPrice / 10000) },
    ]
  }, [data, riskCount])

  const plannedPayMentColumns = useMemo(() => {
    const {
      payables,
      planedPaidDate,
      planedPaidAmount,
      amountPaid,
      amountApplied,
      remainingApplyAmount,
    } = data
    return [
      { label: '现金流项目', value: payables },
      { label: '合同生效日期', value: planedPaidDate || '-' },
      { label: '计划付款金额(元)', value: amountFormat(planedPaidAmount / 10000) },
      { label: '已付金额(元)', value: amountFormat(amountPaid / 10000) },
      { label: '已申请通过金额(元)', value: amountFormat(amountApplied / 10000) },
      { label: '剩余可申请金额(元)', value: amountFormat(remainingApplyAmount / 10000) },
    ]
  }, [data])

  return (
    <>
      <Descriptions
        title="合同信息"
        bordered
        column={2}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.des}
      >
        {baseInfoColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <Descriptions
        title=""
        bordered
        column={3}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
      >
        {plannedPayMentColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <TransactionInfo store={store} />
    </>
  )
}

export default BaseInfo
