import mysql.connector
import os
mydb =mysql.connector.connect(
	host = "24.6.91.55",
	user = "root",
	passwd = "suppcheck",
	database = "cruzhack",
	auth_plugin = "mysql_native_password"
	)

# def query(*args):
mycursor = mydb.cursor()

mycursor.execute("select list_name from foods")

for i in mycursor:
    print(i)
	#for i in args:
		#print(i)



# conn = pyodbc.connection(
# 	"Driver={SQL Server Native Client 11.0};"
# 	"Server=;"
# 	"Database=;"
# 	"Trusted_Connect= yes"
# )