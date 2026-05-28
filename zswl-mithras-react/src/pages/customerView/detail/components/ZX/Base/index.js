import React from 'react'
import { Descriptions, Button } from 'antd'
import styles from './styles.less'

export default function BasicInformation({ store, id }) {
  const tableData = {
    enterpriseName: '湖州市城市投资发展有限公司',
    censusCode: '9133050014697721XW',
    creditCode: '9133050014697721XW',
    queryInstitution: '浙江浙商融资租赁有限公司',
    queryReason: '贷前(贷前)审查',
    reportTime: '2023-10-24 13:00:00',
  }

  const descriptionsItems = [
    { label: '企业名称', dataIndex: 'enterpriseName', span: 2 },
    { label: '中征码', dataIndex: 'censusCode' },
    { label: '统一社会信用代码', dataIndex: 'creditCode', span: 2 },
    { label: '查询原因', dataIndex: 'queryReason' },
    { label: '查询机构', dataIndex: 'queryInstitution', span: 2 },
    { label: '报告时间', dataIndex: 'reportTime' },
  ]

  return (
    <div id={id} className={styles['basic-info-container']}>
      <div className={styles['header']}>
        <div className={styles['title']}>基本信息</div>
        <Button type="primary" className={styles['report-button']}>
          报告全文
        </Button>
      </div>
      <Descriptions bordered column={3}>
        {descriptionsItems.map((item) => (
          <Descriptions.Item key={item.label} label={item.label} span={item.span || 1}>
            {tableData[item.dataIndex]}
          </Descriptions.Item>
        ))}
      </Descriptions>
    </div>
  )
}
