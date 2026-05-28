import { Page } from '@zswl/components'
import BaseInfo from './BaseInfo'
import FTPTable from './FTPTable'

const Index = ({ params: { id }, query: { financingType } }) => {
  return (
    <Page>
      <BaseInfo id={id} financingType={financingType} />
      <div style={{ marginTop: 20 }}>
        <div className="ant-descriptions-title" style={{ marginTop: 20, marginBottom: 20 }}>
          FTP计息
        </div>
        <FTPTable id={id} financingType={financingType} />
      </div>
    </Page>
  )
}

export default Index
