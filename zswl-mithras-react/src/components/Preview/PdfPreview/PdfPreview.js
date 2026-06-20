import { observer, getQuery } from '@zswl/admin'
import { baseURL } from '@/utils'

const Index = ({ params: { id } }) => {
  const { idType } = getQuery()
  let url = `${baseURL()}/materials/pdf/preview?id=${id}`
  if (idType) url += `&idType=${idType}`
  return <iframe src={url} width="100%" height="100%" title="PDF Viewer"></iframe>
}
export default observer(Index)
