import { Page } from '@zswl/components'
import BaseInfo from './BaseInfo'
import FTPTable from './FTPTable'

const Index = ({ params: { id } }) => {
  return (
    <Page>
      <BaseInfo ftpInterestId={id}></BaseInfo>
      <div style={{ marginTop: 20 }}>
        <div className="ant-descriptions-title" style={{ marginTop: 20, marginBottom: 20 }}>
          FTP计息
        </div>
        <FTPTable ftpInterestId={id}></FTPTable>
      </div>
    </Page>
  )
}

export default Index
