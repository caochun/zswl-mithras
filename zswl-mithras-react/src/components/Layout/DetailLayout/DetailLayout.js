import { observer, getQuery } from '@zswl/admin'
import { Space, Anchor, Tooltip, Affix } from 'antd'
import { Children, useEffect, useState } from 'react'
import styles from './DetailLayout.less'
import { AlignRightOutlined, DoubleLeftOutlined } from '@ant-design/icons'
import classNames from 'classnames'

const { Link } = Anchor

const Index = (props) => {
  const {
    children,
    anchorList = [],
    title,
    extra,
    width,
    offsetTop,
    anchorSwitch = false,
    style,
    getContainer = () => document.body,
  } = props
  let moduleName = props.moduleName || ''
  const isFormApproval = getQuery('typeId') == 'approval'
  const anchorSuffix = isFormApproval ? `_${moduleName}_process_` : `_${moduleName}_`

  const hasAnchorList = anchorList?.length > 0
  const [isFold, setIsFold] = useState(isFormApproval)

  useEffect(() => {
    if (anchorSwitch) {
      setIsFold(false)
    }
  }, [anchorSwitch])
  return (
    <div className={styles.wrap}>
      {(title || extra) && (
        <div className={styles.nav} style={style}>
          <div className={styles.title}>{title}</div>
          <Space className={styles.btnWrap} wrap>
            {extra}
          </Space>
        </div>
      )}
      <div>
        {hasAnchorList && (
          <div className={styles.foldWrap}>
            {isFold ? (
              <Tooltip title="展开目录">
                <AlignRightOutlined onClick={() => setIsFold(false)} className={styles.fold} />
              </Tooltip>
            ) : (
              <Tooltip title="收起目录">
                <DoubleLeftOutlined onClick={() => setIsFold(true)} className={styles.fold} />
              </Tooltip>
            )}
          </div>
        )}
        <div className={styles.contentWrap}>
          {hasAnchorList && (
            <Affix offsetTop={offsetTop}>
              <div
                className={classNames(styles.anchorWrap, isFold && styles.collapsed)}
                style={{ width, flex: !isFold && `0 0 ${width}px` }}
              >
                <Anchor className={styles.anchor} style={{ width }} target={getContainer}>
                  {anchorList
                    .map(
                      ({ href, label, isHide }, index) =>
                        !isHide && (
                          <Tooltip title={label}>
                            <Link
                              href={`#${href || label}${anchorSuffix}`}
                              title={label}
                              key={href || label}
                            />
                          </Tooltip>
                        )
                    )
                    .filter(Boolean)}
                </Anchor>
              </div>
            </Affix>
          )}
          <div className={styles.content}>
            {hasAnchorList
              ? anchorList
                  .map(({ href, label, isHide }, index) => {
                    if (isHide) return
                    return (
                      <div id={`${href || label}${anchorSuffix}`} key={href || label}>
                        {Children.toArray(children)?.[index]}
                      </div>
                    )
                  })
                  .filter(Boolean)
              : children}
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(Index)
