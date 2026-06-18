import { useEffect, useState } from 'react'
import { Form, Space, Button } from 'antd'
import { observer } from '@zswl/admin'
import Desc from './Desc'
import styles from '../index.less'

const Index = ({ canEditFlag, store }) => {
  const [form] = Form.useForm()
  const [detailShowData, setDetailShowData] = useState({})
  const { page, showValue, setShowValue, onSave } = store

  useEffect(() => {
    const {
      adjustExplain,
      projSponsorUserName,
      projCosponsorUserNames,
      bizDeptName,
      bizDeptLeaderName,
      bizDivisionLeaderName,
      riskControlManagerId,
      riskControlManagerName,
      legalManagerUserId,
      legalManagerName,
    } = page.getData()

    const currentFormValue = {
      adjustExplain,
      projSponsorUserName,
      projCosponsorUserNames,
      bizDeptName,
      bizDeptLeaderName,
      bizDivisionLeaderName,
      riskControlManagerId,
      legalManagerUserId,
      // riskControlManagerId: riskControlManagerId
      //   ? {
      //       value: riskControlManagerId,
      //       label: riskControlManagerName,
      //     }
      //   : undefined,
      // legalManagerUserId: legalManagerUserId
      //   ? {
      //       value: legalManagerUserId,
      //       label: legalManagerName,
      //     }
      //   : undefined,
    }

    form.setFieldsValue(currentFormValue)
    setDetailShowData(currentFormValue)
  }, [page.getData()])

  return (
    <>
      <div className={styles.module}>
        <Form form={form} onFinish={onSave}>
          <div className={styles.moduleHeader}>
            <div className={styles.moduleTitle}>调整方案</div>
            {canEditFlag && (
              <div className={styles.moduleAction}>
                {showValue ? (
                  <Button
                    type="primary"
                    onClick={() => {
                      setShowValue(false)
                    }}
                  >
                    编辑
                  </Button>
                ) : (
                  <Space>
                    <Button
                      onClick={() => {
                        setShowValue(true)
                        form.setFieldsValue(detailShowData)
                      }}
                    >
                      取消
                    </Button>
                    <Button type="primary" htmlType="submit">
                      保存
                    </Button>
                  </Space>
                )}
              </div>
            )}
          </div>
          <Desc form={form} showValue={showValue} detail={page.getData()}></Desc>
        </Form>
      </div>
    </>
  )
}

export default observer(Index)
