import { observer, getQuery, history } from '@zswl/admin'
import styles from './style.less'
import { App } from '@zswl/components'
import { Breadcrumb } from 'antd'
import { useMemo } from 'react'
import _ from 'lodash'
import { breadPageBack, pathnameToPathInfo } from '@/components/BreadLine/config'
import SpecialPath from './SpecialPath'

export const getPurePath = (pathname) => {
  const { pagePath } = pathnameToPathInfo()
  const pathArr = pagePath?.split('?') ?? []
  let purePath = pathArr[0]
  // 兼容带query参数的path
  if (pathArr[1] === '') {
    purePath += '?'
  }
  return purePath
}
/* 
默认给所有 menu 的路径生成 详情、版本日志、变更日志
如果有其他批量生成的，但不是以上路径的写在setBreadcrumb
如果只是特殊路径的处理，可以写在SpecialPath，比如某个页面：以及那个页面的面包屑组
*/
const Index = ({ pathname, children }) => {
  const { menuMap } = App.getData()

  const setBreadcrumb = (item, obj) => {
    const path = item.key
    obj[path] = [item]
    let defaultBreadcrumb = []

    // // 兼容特殊情况
    if (['/process/receive', '/process/query', '/process/application'].includes(path)) {
      defaultBreadcrumb = [
        { key: `${path}/detail/:id?`, title: '详情' },
        { key: `${path}/detail/snapshoot/:id?`, title: '审批快照' },
      ]
    } else if (['/afterLease/checkPlan'].includes(path)) {
      defaultBreadcrumb = [
        { key: `${path}/planDetail/:id?`, title: '详情' },
        { key: `${path}/template/:id?`, title: '模板详情' },
      ]
    } else {
      //详情 、版本日志、变更日志
      defaultBreadcrumb = [
        { key: `${path}/detail/:id?`, title: '详情' },
        { key: `${path}/detail/log/:id?`, title: '版本日志' },
        { key: `${path}/detail/log/diffInfo/:id?`, title: '变更日志' },
      ]
    }

    defaultBreadcrumb.forEach((v, index) => {
      obj[v.key] = [item, ...defaultBreadcrumb.slice(0, index + 1)]
    })
  }
  // 为每个 key 添加 /detail/log/diffInfo 这种路径
  const newMenuMapList = useMemo(() => {
    const newMenuMap = {}
    Object.keys(menuMap).forEach((key) => {
      const item = _.cloneDeep(menuMap[key])
      const lastItem = _.last(item)
      setBreadcrumb(lastItem, newMenuMap)
    })

    return { ...newMenuMap, ...SpecialPath }
  }, [menuMap])

  const purePath = getPurePath(pathname)
  const breadcrumbList = useMemo(() => {
    const menuPath = newMenuMapList[purePath] || []
    return menuPath.map((item) => {
      const { key, title, isSub } = item
      return (
        <Breadcrumb.Item
          key={key}
          className={styles.zAppBreadcrumbItem}
          onClick={() => {
            breadPageBack({ pagePath: item?.key })
          }}
        >
          {title}
        </Breadcrumb.Item>
      )
    })
  }, [pathname])

  return (
    <Breadcrumb className={styles.zAppBreadcrumb}>
      {children}
      {breadcrumbList}
    </Breadcrumb>
  )
}

export default observer(Index)
