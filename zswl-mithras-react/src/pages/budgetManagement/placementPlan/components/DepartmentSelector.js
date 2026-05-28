import { Row, Col, Tooltip } from 'antd'
import { observer } from '@zswl/admin'
import styles from './style.less'

// 引入状态图标
const STATUS_ICONS = {
  1: '/public/assets/budgetManage/confirm.svg',
  0: '/public/assets/budgetManage/pending.svg',
}

const DepartmentSelector = observer(
  ({ value, onChange, departments = [], needBusinessHead = true, needLeader = true }) => {
    const handleDepartmentClick = (value) => {
      onChange?.(value)
    }

    const renderStatus = (status) => {
      const statusMap = {
        1: '已确认',
        0: '待确认',
      }

      return (
        <Tooltip title={statusMap[status]}>
          <div className={`${status ? styles.confirmed : styles.pending} ${styles.statusWrapper}`}>
            <img src={STATUS_ICONS[status]} alt={statusMap[status]} className={styles.statusIcon} />
            <span>{statusMap[status]}</span>
          </div>
        </Tooltip>
      )
    }

    return (
      <div className={styles.departmentSelector}>
        <Row className={styles.statusRow}>
          {(needBusinessHead || needLeader) && (
            <Col span={3}>
              {needBusinessHead && <div className={styles.text}>部门总</div>}
              {needLeader && <div className={styles.text}>分管总</div>}
              <div
                className={`${styles.departmentName} ${value === '' ? styles.active : ''}`}
                onClick={() => handleDepartmentClick('')}
              >
                公司
              </div>
            </Col>
          )}

          <div wrap={false} className={styles.departmentRow}>
            {departments.map((dept) => (
              <div key={dept.bizDeptId} className={styles.departmentCol}>
                {needBusinessHead && (
                  <div className={styles.text}>{renderStatus(dept.isBusinessheadConfirm)}</div>
                )}
                {needLeader && (
                  <div className={`${styles.text} `}>
                    {renderStatus(dept.isLeaderinchargeConfirm)}
                  </div>
                )}

                <div
                  className={`${styles.departmentName} ${
                    value === dept.bizDeptId ? styles.active : ''
                  }`}
                  onClick={() => handleDepartmentClick(dept.bizDeptId)}
                >
                  {dept.bizDeptName} ({dept.count})
                </div>
              </div>
            ))}
          </div>
        </Row>
      </div>
    )
  }
)

export default DepartmentSelector
