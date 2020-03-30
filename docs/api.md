- #### [File](#1.0-file)
  - ##### [Get](#1.1-get)
  - ##### [Open](#1.2-open)
  - ##### [New](#1.3-new)
  - ##### [Modify](#1.4-modify)
  - ##### [Delete](#1.5-delete)
- #### [Exchange](#2.0-exchange)
  - ##### [URI](#2.1-URI)
  - ##### [Address](#2.2-address)
  - ##### [Handler](#2.3-handler)
  - ##### [Principal](#2.4-principal)
  - ##### [Protocol](#2.5-protocol)
  - ##### [Request Headers](#2.6-request-headers)
  - ##### [Request Method](#2.7-request-method)
  - ##### [GET](#2.8-GET)
  - ##### [POST](#2.9-POST)
  - ##### [Cookies](#2.10-cookies)
- #### [Server](#3.0-server)
  - ##### [Address](#3.1-address)
  - ##### [Context](#3.2-context)
  - ##### [Stop](#3.3-stop)

## 1.0 File

#### 1.1 Get
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*`get`|`api/file/get`|`api/file get`|Returns raw file
|`attr`|`api/file/get/attr`|`api/file get attr`|Returns file attributes
|`list`|`api/file/get/list`|`api/file get list`|Lists files in a folder

#### 1.2 Open
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*`open`|`api/file/open?path=`|*not allowed*|Opens a file
|`open`|`api/file/open?path=&with=`|*not allowed*|Opens a file with a program

#### 1.3 New
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|`folder`|`api/file/new?path=&type=folder&name=`|*not allowed*|Creates a folder at the path|
|`file`|`api/file/new?path=&type=file&name=`|*not allowed*|Creates a file at the path|

#### 1.4 Modify
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|`name`|`api/file/modify?path=&name=`|*not allowed*|Renames a file at the path|

#### 1.5 Delete
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/file/delete?path=`|*not allowed*|Deletes a file at the path|

## 2.0 Exchange

#### 2.1 URI
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*`uri`|`api/exchange/uri`|`api/exchange uri`|Request URI|
|`scheme`|`api/exchange/uri/scheme`|`api/exchange uri scheme`|URI scheme|
|`authority`|`api/exchange/uri/authority`|`api/exchange uri authority`|URI authority|
|`path`|`api/exchange/uri/path`|`api/exchange uri path`|URI path
|`fragment`|`api/exchange/uri/fragment`|`api/exchange uri fragment`|URI fragment
|`query`|`api/exchange/uri/query`|`api/exchange uri query`|URI query|

#### 2.2 Address
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*`address`|`api/exchange/address`|`api/exchange address`|User's address|
|`hostname`|`api/exchange/address/hostname`|`api/exchange address hostname`|User's hostname|
|`port`|`api/exchange/address/port`|`api/exchange address port`|User's port|

#### 2.3 Handler
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/handler`|`api/exchange handler`|The handler associated with the exchange|

#### 2.4 Principal
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/principal`|`api/exchange principal`|Exchange principal|

#### 2.5 Protocol
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/protocol`|`api/exchange protocol`|Exchange protocol|

#### 2.6 Request Headers
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/headers`|`api/exchange headers`|List of headers|
|`name`|`api/exchange/headers?name=`|`api/exchange headers get %name%`|Value of header for name|
|`count`|`api/exchange/headers/count`|`api/exchange headers count`|Number of headers|

#### 2.7 Request Method
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/method`|`api/exchange method`|Exchange method

#### 2.8 GET
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/get`|`api/exchange get`|Map of `GET` request|
|`raw`|`api/exchange/get/raw`|`api/exchange get raw`|`GET` request as a string|
|`has`|`api/exchange/get/has`|`api/exchange get has`|If a `GET` request exists|
|`has`|`api/exchange/get/has?key=`|`api/exchange get has %key%`|If a `GET` parameter exists|
|`key`|`api/exchange/get?key=`|`api/exchange get %key%`|Value of a key in the request|

#### 2.9 POST
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/post`|`api/exchange post`|Map of `POST` request|
|`raw`|`api/exchange/post/raw`|`api/exchange post raw`|`POST` request as a string|
|`has`|`api/exchange/post/has`|`api/exchange post has`|If a `POST` request exists|
|`has`|`api/exchange/post/has?key=`|`api/exchange post has %key%`|If a `POST` parameter exists|
|`key`|`api/exchange/post?key=`|`api/exchange post %key%`|Value of a key in the request|

#### 2.10 Cookies
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/exchange/cookies`|`api/exchange cookies`|Client cookies|
|`has`|`api/exchange/cookies/has`|`api/exchange cookies has`|If any cookies exist|
|`has`|`api/exchange/cookies/has?key=`|`api/exchange cookies has %key%`|If a certain cookie exists|
|`key`|`api/exchange/cookies/get?key=`|`api/exchange cookies get %key%`|Value of a cookie|
|`count`|`api/exchange/cookies/count`|`api/exchange cookies count`|Amount of cookies|
|`create`|`api/exchange/cookies/create?key=&value=`|*not allowed*|Creates a cookie with key and value|
|`delete`|`api/exchange/cookies/delete?key=`|*not allowed*|Deletes a cookie|

## 3.0 Server

#### 3.1 Address
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/server/address`|`api/server address`|Server address|
|`hostname`|`api/server/address/hostname`|`api/server address hostname`|Server hostname|
|`port`|`api/server/address/port`|`api/server address port`|Server port|

#### 3.2 Context
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*`list`|`api/server/context`<br />`api/server/context/list`|`api/server context`<br />`api/server context list`|List of the server's contexts|
|`handler`|`api/server/context/handler?context=`|`api/server context handler %context%`|The handler associated with a context
|`count`|`api/server/context/count`|`api/server context count`|Number of registered contexts|
|`create`|`api/server/create?context=`|*not allowed*|Creates a context
|`create`|`api/server/create?context=&temp=`|*not allowed*|Creates a single use context for a limited time (or unlimited of `temp=0`). Can be used with all creation methods|
|`create`|`api/server/create?context=&file=`|*not allowed*|Creates a context hosting a file at the address|
|`create`|`api/server/create?context=&redir=`|*not allowed*|Creates a redirect|
|`delete`|`api/server/delete?context=`|*not allowed*|Deletes a context|

#### 3.3 Stop
|modifier|api-endpoint|liquid-endpoint|description|
|---|---|---|---|
|*`default`*|`api/server/stop`|*not allowed*|Stops the server
