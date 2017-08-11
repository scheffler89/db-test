# DB Test

## Project Structure

```
.
+-- .dwplus
    +--status.json
+-- de
|  +-- example
|      +-- package
|          + -- TestCase.dbtest
+-- dwplus.json
```

## Command Line Usage

Execute a single Test Case:
```
$ dbtest -execute -case de.example.package.TestCase -target root@localhost:3306/information_schema
```

Execute all Test Cases in a package.
```
$ dbtest -execute -set de.example -target root@localhost:3306/information_schema
```

Get the current status of a Test Case:
```
$ dbtest -status -case de.example.package.TestCase
```


## Target Configuration

The target configuration file is placed in the project root directory named __dwplus.json__.

```JSON
{
    "targets": [
        {
            "databaseType": 1,
            "host": "localhost",
            "port": 3306,
            "database": "information_schema",
            "username": "root",
            "password": ""
        }
    ]
}
```

__Database Types:__  
1 = MySQL  
2 = Oracle  