import { history, setSessionStorage, getSessionStorage, matchRoute, getQuery } from '@zswl/admin'

// 返回上一页
const BREAD_LIST = 'BREAD_LIST'
/**
 *
 * @param {*} path 下一级页面的path
 * @param {*} pathname 当前页面的pathname
 */
export const setBreadList = (path, pathname) => {
  const pageBreadList = getSessionStorage(BREAD_LIST) || {}
  pageBreadList[path] = pathname
  setSessionStorage(BREAD_LIST, pageBreadList)
}
export const getBreadList = () => {
  return getSessionStorage(BREAD_LIST)
}
/**
 * pagePath 页面的path
 */
export const breadPageBack = ({ pagePath }) => {
  const pageBreadList = getSessionStorage(BREAD_LIST) || {}
  if (pageBreadList[pagePath]) {
    history.push(pageBreadList[pagePath] || '/')
  } else {
    history.push(pagePath)
  }
}
export const pathnameToPathInfo = () => {
  const currentPath = `${window.location.pathname}${window.location.search}`
  const pathInfo = matchRoute(currentPath)
  const { path, url } = pathInfo || {}
  return {
    pageUrl: url,
    pagePath: path,
  }
}

export const isFromFlow = getQuery('typeId') === 'approval'
