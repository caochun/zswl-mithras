import { Input, Checkbox, Button, Row, Col } from 'antd'
import { observer } from '@zswl/admin'
import { Form, Modal } from '@zswl/components'
import { useEffect, useState } from 'react'
import styles from './index.less'

const CompanyConfig = ({ configModal }) => {
  const allCompanies = configModal?.getInitialValues() ?? []
  const [searchValue, setSearchValue] = useState('')
  const defaultSelectedCompanies = allCompanies
    .filter((item) => item.choice)
    .map((item) => item.enterpriseCode)

  const [form] = Form.useForm()
  const [selectedCompanies, setSelectedCompanies] = useState([])

  // 过滤公司列表
  const filteredCompanies = allCompanies
    .filter((company) => company.enterpriseName?.toLowerCase().includes(searchValue.toLowerCase()))
    .map((company) => company.enterpriseCode)

  // 处理选择变化
  const handleChange = (checkedValues) => {
    setSelectedCompanies(checkedValues)
  }

  useEffect(() => {
    setSelectedCompanies(defaultSelectedCompanies)
  }, [JSON.stringify(defaultSelectedCompanies)])
  const disabledCompanies = ['T001985444']
  return (
    <Modal title="对标公司配置" width={800} store={configModal} okText="保存">
      <Form form={form} initialValues={{ enterpriseCodes: defaultSelectedCompanies }}>
        <div className={styles.configContainer}>
          <div className={styles.header}>
            <Input.Search
              placeholder="请输入公司名称"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              style={{ width: 300 }}
            />
          </div>
          <div className={styles.tip}>最多可选5家对标公司（{selectedCompanies.length - 1}/5）</div>

          <div className={styles.content}>
            <Form.Item name="enterpriseCodes">
              <Checkbox.Group onChange={handleChange}>
                {/* <Row> */}
                {allCompanies.map(({ enterpriseName, enterpriseCode }) => (
                  // <Col span={8}>
                  <Checkbox
                    value={enterpriseCode}
                    style={{
                      display: filteredCompanies.includes(enterpriseCode) ? 'inline-flex' : 'none',
                    }}
                    disabled={
                      disabledCompanies.includes(enterpriseCode) ||
                      (!selectedCompanies.includes(enterpriseCode) && selectedCompanies.length >= 6)
                    }
                  >
                    {enterpriseName}
                  </Checkbox>
                  // </Col>
                ))}
                {/* </Row> */}
              </Checkbox.Group>
            </Form.Item>
            {selectedCompanies.length > 5 && (
              <div className={styles.warning}>已选择5家对标公司，请取消已选公司后再选择</div>
            )}
          </div>
        </div>
      </Form>
    </Modal>
  )
}

export default observer(CompanyConfig)
