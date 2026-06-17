import { isEmpty, options } from '@/utils'
import { getQuery, observer } from '@zswl/admin'
import { Input, Radio, Space } from 'antd'
import { App, Form } from '@zswl/components'
const { approvalStatus } = options
import styles from './styles.less'

const Index = ({ list = [], store, canApproval }) => {
  const isApproval = getQuery('typeId') == 'approval'
  const disabled = !(isApproval && canApproval)
  const fieldName = list?.[0]?.fieldName
  const hasApprovalOption = store.reportData?.formData?.[`${fieldName}_approvalStatus`]
  const isShow = isApproval && (canApproval ? true : !isEmpty(hasApprovalOption))
  return (
    <>
      <div style={{ fontWeight: 800, fontSize: 16, padding: '12px 0' }}>调整事项</div>
      {list?.length ? (
        <div className={styles.rows}>
          <Space direction="vertical" style={{ width: '100%' }}>
            {list.map(({ fieldComment, enumList, value }) => (
              <div style={{ backgroundColor: '#f4f8ff', padding: 12, color: '#5b90fa' }}>
                {fieldComment} - {App.matchOption(enumList, value).label}
              </div>
            ))}
          </Space>
          {isShow && (
            <div style={{ marginTop: 12, width: '40%', marginLeft: 12 }}>
              <Form.Item label="审核意见" name={`${fieldName}_approvalStatus`}>
                <Radio.Group
                  options={approvalStatus}
                  disabled={disabled}
                  onBlur={(e) =>
                    store.saveApprovalInfo(
                      { fieldName: fieldName },
                      e.target.value,
                      'approvalStatus'
                    )
                  }
                />
              </Form.Item>
              <Form.Item label="说明" name={`${fieldName}_approvalOpinion`}>
                <Input.TextArea
                  maxLength={200}
                  disabled={disabled}
                  rows={2}
                  onBlur={(e) =>
                    store.saveApprovalInfo(
                      { fieldName: fieldName },
                      e.target.value,
                      'approvalOpinion'
                    )
                  }
                />
              </Form.Item>
            </div>
          )}
        </div>
      ) : (
        <div style={{ backgroundColor: '#ffd0ce', padding: 12, color: '#5b90fa' }}>无</div>
      )}
    </>
  )
}

export default observer(Index)
