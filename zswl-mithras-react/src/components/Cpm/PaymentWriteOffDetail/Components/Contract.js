import { observer } from '@zswl/admin'
import { Descriptions } from 'antd'
import { App } from '@zswl/components'
import { useMemo } from 'react'
import styles from '../index.less'

const Contract = ({ store }) => {
  const data = store.page.getData()
  const columns = useMemo(() => {
    const {
      contractCode,
      projName,
      clientName,
      leaseTypeCode,
      bizTypeCode,
      bizDeptName,
      projSponsorUserName,
    } = data

    const bizType = App.matchOption('projEstablishBizType', bizTypeCode).label || ''
    const leaseType = App.matchOption('leaseType', leaseTypeCode).label || ''
    return [
      {
        label: '合同编号',
        value: contractCode,
      },
      {
        label: '项目名称',
        value: projName,
      },
      {
        label: '客户名称',
        value: clientName,
      },
      {
        label: '类别',
        value: bizType + leaseType,
      },
      {
        label: '业务部门',
        value: bizDeptName,
      },
      {
        label: '项目主办',
        value: projSponsorUserName,
      },
    ]
  }, [data])

  return (
    <>
      <Descriptions
        title="所属合同"
        bordered
        column={3}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.summaryDescription}
      >
        {columns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
    </>
  )
}

export default observer(Contract)
