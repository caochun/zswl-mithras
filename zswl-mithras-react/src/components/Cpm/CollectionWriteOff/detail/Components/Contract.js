import { observer } from '@zswl/admin'
import { App } from '@zswl/components'
import { Descriptions } from 'antd'
import { useMemo } from 'react'
import styles from '../index.less'

const Contract = ({ store }) => {
  const { contractInfo } = store.page.getData()

  const columns = useMemo(() => {
    const { contractCode, projName, contractType, bizDept, projSponsorUserName, clientName } =
      contractInfo
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
        value: contractType,
      },
      {
        label: '业务部门',
        value: bizDept,
      },
      {
        label: '项目主办',
        value: projSponsorUserName,
      },
    ]
  }, [contractInfo])

  return (
    <>
      <Descriptions
        title="所属合同"
        bordered
        column={3}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
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
