# codesearch 
Library for searching large code base

# Features

- Index source folders
- Allow searching based on file names.
- Allow searching based on file content.

#Design
TODO

# How to run
This project ships shell & REST API to explore code. 

Shell client 
```
 java org.search.codesearch.main.CodeSearchCLI -source code1path;code2path
```

REST Server
```
java org.search.codesearch.main.CodeSearchServer -source ..\github\jdk;..\github\h2database
```

Rest API 

http://localhost:8080/search?pattern=concurrenthashmap&limit=100
