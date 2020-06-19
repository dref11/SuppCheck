import mysql.connector

import codecs


mydb =mysql.connector.connect(
	host = "127.0.0.1",
	user = "root",
	passwd = "suppcheck",
	database = "ingredient",
	auth_plugin = "mysql_native_password"
	)

# def query(*args):
mycursor = mydb.cursor()

mycursor.execute("select name from compounds")

for i in mycursor:
	print(i)


# conn = pyodbc.connection(
# 	"Driver={SQL Server Native Client 11.0};"
# 	"Server=;"
# 	"Database=;"
# 	"Trusted_Connect= yes"
# )