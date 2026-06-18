import { Empty, Space, Spin } from 'antd'
import { observer } from '@zswl/admin'
import { useEffect, useRef, useState, forwardRef, useImperativeHandle } from 'react'
import { getLocalColumnsFilter } from '@/utils'
import { isEmpty, isFunction } from 'lodash'
import cls from 'classnames'
import ManagementFields from '../ManagementFields'
import styles from './index.less'

/**
 * 卡片模块面板
 * @param {*String} title 标题名称
 * @param {*Array} extra 面板右侧操作
 * @param {*String} updateTime 更新时间
 * @param {*String} columnsFilterKey 本地存储key
 * @param {*Function} getFieldsApi 获取数据源的方法
 * @returns
 */

const Index = (
  {
    title,
    extra,
    children,
    updateTime,
    columnsFilterKey,
    getFieldsApi,
    style,
    innerModule,
    queryParams,
  },
  ref
) => {
  const [loading, setLoading] = useState(false)
  const allListRef = useRef([])
  const [viewList, setViewList] = useState([])

  const getData = async () => {
    if (!isFunction(getFieldsApi)) return
    setLoading(true)
    const res = await getFieldsApi({ ...queryParams })
    allListRef.current = res
    if (columnsFilterKey) {
      handleFilterColumn()
    } else {
      setViewList(res)
    }
    setLoading(false)
  }

  const handleFilterColumn = () => {
    const localData = getLocalColumnsFilter(columnsFilterKey)
    if (isEmpty(localData)) {
      setViewList(allListRef.current)
      return
    }

    const titleHideList = localData
      ?.map((item) => {
        if (!item.show) {
          return item.groupCode
        }
      })
      .filter(Boolean)

    const viewFieldsList = allListRef.current
      ?.map((item) => {
        if (titleHideList.includes(item.groupCode)) {
          return
        }
        return item
      })
      ?.filter(Boolean)

    setViewList(viewFieldsList)
  }

  useEffect(() => {
    getData()
  }, [JSON.stringify(queryParams)])

  useImperativeHandle(ref, () => ({
    refresh: () => getData(),
  }))

  return (
    <div className={styles.wrap}>
      <div className={cls(styles.header, innerModule && styles.innerModule)} style={style}>
        <div>{title && <div className={styles.header_title}>{title}</div>}</div>
        <div className={styles.header_extra}>
          <Space>
            {extra}
            {updateTime && <div className={styles.updateTime}>数据更新时间：{updateTime}</div>}
            {columnsFilterKey && !innerModule && (
              <ManagementFields
                initFieldsConfig={allListRef.current}
                columnsFilterKey={columnsFilterKey}
                onChange={handleFilterColumn}
              ></ManagementFields>
            )}
          </Space>
        </div>
      </div>
      {isFunction(getFieldsApi) ? (
        <Spin spinning={loading}>
          {viewList.length > 0 ? (
            <div className={styles.content}>{children(viewList)}</div>
          ) : (
            <div className={styles.empty}>
              <Empty></Empty>
            </div>
          )}
        </Spin>
      ) : (
        <div className={styles.content}>{children}</div>
      )}
    </div>
  )
}

export default observer(forwardRef(Index))
