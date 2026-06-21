import { Tooltip } from 'antd'
import classNames from 'classnames'
import { useState } from 'react'
import styles from './index.less'
const BaseModule = ({
  content,
  processStatus,
  processType,
  currentNode,
  useColumnHoverLayout = false,
  style,
  ...props
}) => {
  const [isHovering, setIsHovering] = useState(false)
  return (
    <div
      {...props}
      className={classNames(styles.moduleWrap, {
        [styles.approvalRefuseHover]: isHovering,
      })}
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
      style={style}
    >
      {content}
      {(processStatus === '审批拒绝' ||
        processStatus === '审批中' ||
        processStatus === '未提交' ||
        processStatus === '退回') && (
        <div
          className={classNames({
            [styles.refuse]: processStatus === '审批拒绝' || processStatus === '退回',
            [styles.under]: processStatus === '审批中',
            [styles.noSubmit]: processStatus === '未提交',
          })}
        >
          <div className={styles.text}>{processStatus}</div>
        </div>
      )}

      {isHovering &&
        (processStatus === '审批拒绝' ||
          processStatus === '审批中' ||
          processStatus === '未提交' ||
          processStatus === '退回') && (
          <div
            className={classNames({
              [styles.hoveringRefuse]: processStatus === '审批拒绝' || processStatus === '退回',
              [styles.hoveringUnder]: processStatus === '审批中',
              [styles.hoveringNoSubmit]: processStatus === '未提交',
              [styles.columnType]: useColumnHoverLayout,
            })}
          >
            <div
              className={classNames({
                [styles.hoveringRefuseTag]:
                  processStatus === '审批拒绝' || processStatus === '退回',
                [styles.hoveringUnderTag]: processStatus === '审批中',
                [styles.noSubmit]: processStatus === '未提交',
              })}
            >
              <div className={styles.text}>{processStatus}</div>
            </div>
            <div className={styles.item}>
              <div className={styles.label}>流程类型：</div>
              <Tooltip title={processType}>
                <div className={styles.content}>{processType || '-'}</div>
              </Tooltip>
            </div>
            <div className={styles.item}>
              <div className={styles.label}>当前节点：</div>
              <div className={styles.content}>{currentNode || '-'}</div>
            </div>
          </div>
        )}
    </div>
  )
}

export default BaseModule
