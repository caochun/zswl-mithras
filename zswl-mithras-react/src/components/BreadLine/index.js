import { Breadcrumb } from 'antd'
import { breadPageBack, pathnameToPathInfo, setBreadList, getBreadList } from './config'

const BreadLine = ({ breadcrumbList }) => {
  return (
    <Breadcrumb>
      {breadcrumbList?.map((item, index) => {
        return breadcrumbList.length - 1 === index ? (
          <Breadcrumb.Item key={index}>{item?.label || item}</Breadcrumb.Item>
        ) : (
          <Breadcrumb.Item key={index}>
            <a
              onClick={() => breadPageBack({ pagePath: item?.path })}
              style={{ color: 'rgba(0, 0, 0, 0.45)' }}
            >
              {item?.label || item}
            </a>
          </Breadcrumb.Item>
        )
      })}
    </Breadcrumb>
  )
}

export default BreadLine

BreadLine.breadPageBack = breadPageBack
BreadLine.setBreadList = setBreadList
BreadLine.getBreadList = getBreadList
BreadLine.pathnameToPathInfo = pathnameToPathInfo
