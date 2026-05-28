INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondelete', '删除催收信息', 0, id, 'POST', '/overduecollection/action/delete', 2
from bifrost_menu
where code = 'overduecollection';

