import { observer } from '@zswl/admin'
import { Anchor, Affix, Space } from 'antd'
import { cloneElement } from 'react'
import cls from 'classnames'
import styles from './index.less'

const { Link } = Anchor

// 看板页面有用到
const DashboardAnchorScrollNav = (props) => {
  const {
    align = 'left',
    anchorList = [],
    anchorConfig,
    affixConfig,
    childrenStyle,
    extra,
    anchorContentStyle,
  } = props

  const validAnchorList = anchorList.filter(Boolean)
  const overOneAnchor = validAnchorList?.length > 1

  return (
    <div className={styles.contentWrap}>
      {overOneAnchor && (
        <Affix {...affixConfig}>
          <div className={styles.anchorWrap}>
            <div
              className={cls(styles.anchorContent, styles[align])}
              style={{ ...anchorContentStyle }}
            >
              <Anchor className={styles.anchor} affix={false} {...anchorConfig}>
                {validAnchorList
                  .map(
                    ({ href, label, isHide, onAnchorClick }) =>
                      !isHide && (
                        <Link
                          href={
                            onAnchorClick ? '#' : `#${href || label}`
                          }
                          title={
                            onAnchorClick ? (
                              <span
                                role="button"
                                onClick={(e) => {
                                  e.preventDefault()
                                  e.stopPropagation()
                                  onAnchorClick(e)
                                }}
                              >
                                {label}
                              </span>
                            ) : (
                              label
                            )
                          }
                          key={href || label}
                        />
                      )
                  )
                  .filter(Boolean)}
              </Anchor>
              <Space className={styles.anchorExtra}>{extra}</Space>
            </div>
          </div>
        </Affix>
      )}
      <div className={styles.content}>
        {validAnchorList
          .map(({ href, label, isHide, isContentHide, component, ...rest }) => {
            if (isHide || isContentHide) return
            return (
              <div id={`${href || label}`} key={href || label} style={{ ...childrenStyle }}>
                {cloneElement(component, {
                  title: label,
                  ...rest,
                })}
              </div>
            )
          })
          .filter(Boolean)}
      </div>
    </div>
  )
}

export default observer(DashboardAnchorScrollNav)
