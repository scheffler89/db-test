# Database Application Testing with db-test

_db-test_ is a light weight __Java framework__ and __Command Line Tool__ for continously testing database application like data warehouses. Based on native SQL scripting, you are free to define any test case using the full power of SQL. Every Test Case is executed directly on the target database host, using the whole set of ressources available, so there is no need for an additional server component in your infrastructure.

## Using the Command Line Tool
There two ways of using db-test eihter embedded as a Java library or via the Command Line Tool. This paragraph with give you a quick start in how to define and run tests using the Command Line.

Change your Terminal to the directory where you would like to place your Test Cases and create a poject folder. In your Terminal type:
```
$ dbtest -init
```
Now you can start defining your database targets. Run the dbtest with the _addtarget_ option an follow the steps.
```
$ dbtest -addtarget
Database Type: mysql 
Host: localhost
Port: 3306
Database / Schema: information_schema
Username: dbtest-user
Password: password

Would you like to add the Target? (yes)
```
After defining the target, you are able to run tests on this target database host and you can go on with defining your first test case.

In your project folder create a package structure you like. Each Package or Sub-Package is expressed by a directory. For example:

```
.
+-- de
|  +-- example
|      +-- package1
|          +-- TestCase.dbtest
|      +-- package2
|          +-- TestCase2.dbtest
``` 