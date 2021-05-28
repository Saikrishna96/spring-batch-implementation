This project contains all main usecase implementations of Spring Batch.

Can be used for Job batch processing.

Features implemented:
1. Read data from csv file using FlatFileItemReader
2. Read data from Postgres Db table user_details
3. Processing the read data to uppercaseLetter(any processing can be done in this step)
4. Write processed data to file
5. Write processed data to postgres db table user_details
6. We can use and populate 1 lakh entries using the file Sample-SQL-File-100000-Rows.sql
7. I have populated 1 Million entries using this above 1 lakh data, just used a counter in ItemProcessor(chunksize = 1L, page_size=1L) 