import { Collapse } from 'antd'
import { DownOutlined, UpOutlined } from '@ant-design/icons'
import styles from './Collapse.less'

const { Panel } = Collapse

const LayoutCollapse = ({ header, children, extra, folded, forceRender = false, ...rest }) => {
  return (
    <div className={styles.collapse}>
      <Collapse
        ghost
        defaultActiveKey={folded ? [] : ['1']}
        expandIconPosition="end"
        expandIcon={({ isActive }) => {
          return (
            <div className={styles.expandIcon}>
              {extra}
              {isActive ? (
                <div>
                  收起
                  <UpOutlined></UpOutlined>
                </div>
              ) : (
                <div>
                  展开
                  <DownOutlined></DownOutlined>
                </div>
              )}
            </div>
          )
        }}
        {...rest}
      >
        <Panel header={header} key="1" forceRender={forceRender}>
          <div className={styles.expandPanel}>{children}</div>
        </Panel>
      </Collapse>
    </div>
  )
}

export default LayoutCollapse
