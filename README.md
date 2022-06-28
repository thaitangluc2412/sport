### Database connection guide
###### Tools:
- PostgreSQL v10.4
- PgAdmin4

###### Steps:
- Use PgAdmin4 connect to your PostgreSQL server instance, right-click on `Login/Group Roles` and select Create then click `Login/Group Role`. At General tab, you'll fill `admin` to the Name field. At Definition tab, you'll fill `admin` to the Password field. At Privileges tab, you'll active `Can login?`.
- Use PgAdmin4 create database name `MGMSPORT` and set owner by `admin`.
- Open PgAdmin4, in MGMSPORT database, copy and paste the content of 'schema.sql' and 'trigger.sql' file from resources into Query Tool then execute it.

> Note: For Windows, if Database connection has some problems with authentication. Open application.properties in resource package, change `jdbc:postgresql://localhost:5432/MGMSPORT` to 'jdbc:postgresql://localhost:5432/MGMSPORT?account=admin&password=admin'
