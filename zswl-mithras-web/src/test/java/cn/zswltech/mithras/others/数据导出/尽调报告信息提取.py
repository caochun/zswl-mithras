from docx import Document
import pandas as pd
import os

# 要获取文件名的目录路径
directory_path = '/Users/luyi/Downloads/jdbg/'  # 替换为你的目录路径

# 读取word

def read_docx_table_remove_duplicates(docx_file):
    doc = Document(docx_file)
    excel_data = []
    tables = doc.tables
    for table in tables:
        seen_data = set()  # 用来跟踪已经出现过的数据
        for row in table.rows:
            row_data = []
            for cell in row.cells:
                cell_text = cell.text.strip()
                if cell_text not in seen_data:
                    seen_data.add(cell_text)
                    row_data.append(cell_text)
            # 如果这一行不是完全重复的内容，则打印
            if row_data:
                excel_data.append(row_data)
    return excel_data

def find_nextrow_element(matrix, target):
    for i in range(len(matrix)):
        row = matrix[i]
        if target in row:
            index = row.index(target)
            if index < len(row) - 1:
                return matrix[i+1][index-1]
    return None

def find_next_element(matrix, target):
    for row in matrix:
        if target in row:
            index = row.index(target)
            if index < len(row) - 1:
                return row[index + 1]
    return None


# 使用 os.listdir() 方法获取目录下的所有文件名
all_files = os.listdir(directory_path)

# 仅保留扩展名为 .docx 的文件名
docx_files = [file for file in all_files if file.endswith('.docx')]



path_list = docx_files

# 创建空的 DataFrame
columns = ['考察时间', '人员', '对应公司', '经营地点', '注册地点', '项目名称']  # 列名
docx_df = pd.DataFrame(columns=columns)

for i in path_list:

    # 替换 'your_file.docx' 为你的文件路径
    excel_data =  read_docx_table_remove_duplicates(directory_path + i)

    # 查找时间
    target_element = '尽调时间'
    date_element = find_nextrow_element(excel_data, target_element)

    # 查找人员
    target_element = '现场尽调人员'
    user_element = find_nextrow_element(excel_data, target_element)

    # 查找公司
    target_element = '承租人'
    company_element = find_next_element(excel_data, target_element)

    # 查找地区
    target_element = '实际经营地址'
    loc_element = find_next_element(excel_data, target_element)

    # 查找地区
    target_element = '注册地址'
    loc2_element = find_next_element(excel_data, target_element)

    # 查找地区
    target_element = '项目名称'
    project_element = find_next_element(excel_data, target_element)

    docx_list = [date_element, user_element, company_element, loc_element, loc2_element, project_element]
    docx_df.loc[len(docx_df)] = docx_list

docx_df.to_excel('docx_ouput.xlsx', index=False)
print('docx文件已写入')

# 读取excel


# 使用 os.listdir() 方法获取目录下的所有文件名
all_files = os.listdir(directory_path)

# 仅保留扩展名为 .docx 的文件名
docx_files = [file for file in all_files if file.endswith('.xlsx')]

# 打印 docx 文件名列表
path_list = docx_files

# 创建空的 DataFrame
columns = ['考察时间', '人员', '对应公司', '经营地点', '注册地点', '项目名称']  # 列名
docx_df = pd.DataFrame(columns=columns)


for m in path_list:
    df = pd.read_excel(directory_path + m)
    df = df.fillna('0')
    #项目名称只取第一个值
    project_num = 0

    for i in range(len(df)):
        for ix in range(len(df.loc[i])):
            if '考察人员' in str(df.iloc[i, ix]):
                user_element = df.iloc[i, ix+1]
            if '考察时段' in str(df.iloc[i, ix]):
                date_element = df.iloc[i, ix+1]
            if '承租人' in str(df.iloc[i, ix]):
                company_element = df.iloc[i, ix+1]
            if '经营地址' in str(df.iloc[i, ix]):
                loc_element = df.iloc[i, ix+1]
            if '注册地址' in str(df.iloc[i, ix]):
                loc2_element = df.iloc[i, ix+1]
            if '项目名称' in str(df.iloc[i, ix]):
                if project_num == 0:
                    project_element = df.iloc[i, ix+1]
                    project_num = project_num + 1
    docx_list = [date_element, user_element, company_element, loc_element, loc2_element, project_element]
    docx_df.loc[len(docx_df)] = docx_list

docx_df.to_excel('excel_ouput.xlsx', index=False)
print('excel文件已写入')


