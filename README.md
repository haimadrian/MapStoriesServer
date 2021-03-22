# Map Stories Server
A repository to maintain the backend of Map Stories application

## Table of Contents

* [Features](#features)
* [Requirements](#requirements)
  * [Run](#requirements-run)
  * [Develop](#requirements-develop)
* [Installation](#installation)
  * [Installation Appendix](#installation-appendix)
* [Security](#security)
* [RESTful Web Services](#restful)
  * [User API](#restful-user)
    * [Sign Up](#restful-user-signup)
    * [Sign In](#restful-user-signin)
    * [Sign Out](#restful-user-signout)
    * [User Info](#restful-user-info)
  * [Coordinate API](#restful-coordinate)
    * [Upload](#restful-coordinate-upload)
    * [Update](#restful-coordinate-update)
    * [Get All](#restful-coordinate-getall)
    * [Get One](#restful-coordinate-getcoordinate)
    * [Get Within Range](#restful-coordinate-getbydist)
  * [Story API](#restful-story)
    * [Upload](#restful-story-upload)
    * [Update](#restful-story-update)
    * [Story Info](#restful-story-getstory)
    * [Stories by hero name](#restful-story-getstorybyhero)
    * [Stories by title](#restful-story-getstorybytitle)
    * [Stories by user](#restful-story-getstorybyuser)
    * [Stories by location name](#restful-story-getstorybylocation)
    * [Stories by coordinate identifier](#restful-story-getstorybycoordinate)

<a name="features"></a>
## Features

* Spring boot application which is packed as an executable jar with embedded Tomcat server
* The server accepts HTTPS requests only
* Services (requests) are proected using JWT
* Available for installation as a service, and can able to run on both Linux and Windows systems

<a name="requirements"></a>
## Requirements

<a name="requirements-run"></a>
### Run
* JRE 11+
* MySQL server 5+
    * Create new schema for Map Stories. Name it: `mapstories`
    * [Create ms_user table](https://github.com/haimadrian/MapStoriesServer/blob/master/Help/SQL/CreateUserTable.sql)
    * [Create ms_coordinate table](https://github.com/haimadrian/MapStoriesServer/blob/master/Help/SQL/CreateCoordinateTable.sql)
    * [Create ms_story table](https://github.com/haimadrian/MapStoriesServer/blob/master/Help/SQL/CreateStoryTable.sql)
* Open port 8443 for incoming requests
    * It is possible to configure another port at [application.properties](https://github.com/haimadrian/MapStoriesServer/blob/master/Project/src/main/resources/application.properties)
* Environment Variables - Sensitive data is passed to the server through environment variables, so we will not have to deal with encryption of data in the source control. So you have to add the environment variables below and make sure they are available for the JVM. (Restart the machine if needed)
    * `MAP_STORIES_DB_HOST` - Hostname or IP address of MySQL server. (I used 127.0.0.1 for localhost). Do NOT specify schema name. Schema name is hard coded: mapstories
    * `MAP_STORIES_DB_PORT` - The port that MySQL server listens to. (3306)
    * `MAP_STORIES_DB_USERNAME` - User name to connect to the DB
    * `MAP_STORIES_DB_PASSWORD` - Password to use
    * `MAP_STORIES_KEYSTORE_PASSWORD` - Password to the keystore. We need a signed key store in order to be trusted. (Server runs in secured mode (HTTPS))
        * Same password is used for the key itself. Refer to [application.properties](https://github.com/haimadrian/MapStoriesServer/blob/master/Project/src/main/resources/application.properties) in order to change it.
    * `MAP_STORIES_KEYSTORE_ALIAS` - Alias name used for the keystore

<a name="requirements-develop"></a>
### Develop
* JDK 11+
* Gradle 6+
* Intellij/Eclipse
* Import the project as a Gradle project by selecting [build.gradle](https://github.com/haimadrian/MapStoriesServer/blob/master/Project/build.gradle) file
    * It is required in order to resolve all of our dependencies and plugins
    * Note: Run the `bootJar` Gradle task in order to package the project into executable jar file
* Lombok
    * We use [Project Lombok](https://projectlombok.org/) for generating constructors/getters/setters/toString, etc. automatically.
* Make sure the project is being compiled and built using Gradle. Otherwise you won't be able to compile it.

<a name="installation"></a>
## Installation

Follow these instructions in order to install Map Stories Server as a Windows Service.

This way Map Stories Server will be launched immediately when Windows starts up, and there is no need to login in order to launch it.

* Refer [here](https://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/) to download winsw.exe
* Make sure you have defined JAVA_HOME environment variable and put %JAVA_HOME%\bin at the PATH environment variable. We depend on a JVM (version 11+) in order to run.
* Create a configuration file at the installation directory, and name it as the service identifier: MapStoriesServer.xml
    * Content of the XML:
```xml
<service>
    <id>MapStoriesServer</id>
    <name>Map Stories Server</name>
    <description>This runs Map Stories Server as a Service.</description>
    <env name="MYAPP_HOME" value="%BASE%"/>
    <executable>java</executable>
    <arguments>-XX:+HeapDumpOnOutOfMemoryError -Xms64m -Xmx4G -showversion -jar "%BASE%\map-stories-server-1.0.0.jar"</arguments>
    <logmode>rotate</logmode>
</service>
```
* Rename winsw.exe file to MapStoriesServer.exe and move it to the installation directory, next to the xml file.
* Copy the map-stories-server-1.0.0.jar file to the same folder, next to MapStoriesServer.exe
    * Build this executable jar using the `bootJar` Gradle task
    * ![bootJar](https://github.com/haimadrian/MapStoriesServer/blob/master/Help/Build%20the%20executable%20jar.png)
* Open cmd at the folder you have saved MapStoriesServer.exe
* Write: MapStoriesServer.exe install
* Press enter
* Good job, you have map-stories-server installed as a service.

<a name="installation-appendix"></a>
### Installation appendix

* Refer to [Run](#requirements-run) for instructions about how to run and what other requirements there are.
* Explanation about the runtime arguments we use: (In MapStoriesServer.xml)
    * `-XX:+HeapDumpOnOutOfMemoryError` - To have a heapdump when there is OutOfMemory, so we can analyze it and find memory leaks, if we have such...
    * `-Xms64m` Minimum memory: 64 Mega.
    * `-Xmx4G` Maximum memory: 4 Giga.
* Note that we log information to C:\BraveTogether\log by default. 
Log folder is modifiable, to support running the server on a Linux machine as well. 
For this, you need to specify a jvm system property: org.bravetogether.mapstories.logdir that refers to the log folder. 
For example: (This will use a log directory under base installation directory.)
```xml
<arguments>-XX:+HeapDumpOnOutOfMemoryError "-Dorg.bravetogether.mapstories.logdir=%BASE%\log" -Xms64m -Xmx4G -showversion -jar "%BASE%\map-stories-server-1.0.0.jar"</arguments>
```

<a name="security"></a>
## Security

* Certificate
    * I use a self signed certificate for the Hackathon. It is required to specify a signed certificate when building a server executable for production.
    * Put the certificate at [resources\keystore\bravetogether.p12](https://github.com/haimadrian/MapStoriesServer/blob/master/Project/src/main/resources)
    * Build using `bootJar`.
* JWT
    * Homepage, /user/signin, and /user/signup are public paths. All other paths will be validated in order to recognize the user performing operations.
    * Authentication is done using user identifier and password, which are being sent as the body of a `POST /user/sinin` request, with body: `{ "id": "haim@gmail.com", "pwd": "myPass" }`
    * Before being able to sign in, you must sign up ofcourse. `PUT /user/signup` request, with body: `{ "id": "haim@gmail.com", "pwd": "myPass", "name": "Haim Adrian", "dateOfBirth": "0000-00-00" }`. Note that the date format must be `yyyy-MM-dd`.
    * The response of `POST /user/sinin` will contain the JWT to use for later authorization of a client, without needing to sign in over and over. Though it is very basic and not persistable, so in case the server is restarted, client must sign in again in order to get a new JWT.
    * JWT contains user identifier and user name, in case you want to decode it and verify that you are communicating with the server, and not a man in the middle.
* Passwords
    * Passwords are encrypted before we save them to the database, to avoid of saving passwords as clear text.
    * We use an asymmetric key to protect the passwords such that they won't be decryptable.

<a name="restful"></a>
## RESTful Web Services

<a name="restful-user"></a>
### User

<a name="restful-user-signup"></a>
#### Sign Up

Method: `PUT`

Path: `https://HOST:PORT/user/signup`

Body:
```json
{
    "id": "haim@gmail.com",
    "pwd": "myPass",
    "name": "Haim Adrian",
    "dateOfBirth": "1970-01-01"
}
```

Response:
```json
{
    "id": "haim@gmail.com",
    "name": "Haim Adrian",
    "dateOfBirth": "1970-01-01"
}
```

<a name="restful-user-signin"></a>
#### Sign In

Method: `POST`

Path: `https://HOST:PORT/user/signin`

Body:
```json
{
    "id": "haim@gmail.com",
    "pwd": "myPass"
}
```

Response: (You must use this token as `Authorization` header in subsequent requests)
```json
{ "token" : "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA" }
```

<a name="restful-user-signout"></a>
#### Sign Out

Method: `PUT`

Path: `https://HOST:PORT/user/signout`

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty. We'll extract user identifier out of the Bearer token

<a name="restful-user-info"></a>
#### User Info

Method: `GET`

Path: `https://HOST:PORT/user/info/{userId}` replace {userId} with the user identifier. e.g. haim@gmail.com

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty.

Response:
```json
{
    "id": "haim@gmail.com",
    "name": "Haim Adrian",
    "dateOfBirth": "1970-01-01",
    "coins": 2
}
```

<a name="restful-coordinate"></a>
### Coordinate

<a name="restful-coordinate-upload"></a>
#### Upload Coordinate

Method: `POST`

Path: `https://HOST:PORT/coordinate`

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: 
```json
{
	"coordinateId": null,
	"latitude": 32.01623990507656,
	"longitude": 34.773109201554945,
	"locationName": "Holon Institute of Technology",
	"image": "/9j/4QA4RXhpZgAASUkqAAgAAAABAJiCAgAUAAAAGgAAAAAAAABPbWVyYml0YXNAZ21haWwuY29tAAAA/+wAEUR1Y2t5AAEABAAAADwAAP/hBGJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTQ1IDc5LjE2MzQ5OSwgMjAxOC8wOC8xMy0xNjo0MDoyMiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1wTU06T3JpZ2luYWxEb2N1bWVudElEPSI0MDRENDk4QjFCNzgzNkE1QkQ1ODdDQUE0NUU4RUM3NCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpENUY1NzFDRkIxRTcxMUU5QkVDMUY0NjkxQkQ2MDBEMyIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpENUY1NzFDRUIxRTcxMUU5QkVDMUY0NjkxQkQ2MDBEMyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgTGlnaHRyb29tIDYuNyAoV2luZG93cykiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5YzQyNTNkNi1iMWVlLTAyNDItOGIxYi1mYzk5MjdkNDZjM2EiIHN0UmVmOmRvY3VtZW50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"
}
```

Response: (You can get the generated coordinate identifier out of the response)
```json
{
    "coordinateId": 1,
    "latitude": 32.01623990507656,
    "longitude": 34.773109201554945,
    "locationName": "Holon Institute of Technology",
    "image": "The byte array data here. I'd like to avoid of copying it again"
}
```

<a name="restful-coordinate-update"></a>
#### Update Coordinate

Method: `POST`

Path: `https://HOST:PORT/coordinate/{coordinateId}` (Replace {coordinateId} with the coordinate identifier)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: 
```json
{
	"coordinateId": 1,
	"latitude": 32.01623990507656,
	"longitude": 34.773109201554945,
	"locationName": "Holon Institute of Technology",
	"image": "The byte array data here. I'd like to avoid of copying it again. Or null to delete"
}
```

Response: (You can get the generated coordinate identifier out of the response)
```json
{
    "coordinateId": 1,
    "latitude": 32.01623990507656,
    "longitude": 34.773109201554945,
    "locationName": "Holon Institute of Technology",
    "image": "The byte array data here. I'd like to avoid of copying it again"
}
```

<a name="restful-coordinate-getall"></a>
#### Get all Coordinates

Method: `GET`

Path: `https://HOST:PORT/coordinate`

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: Note that we avoid of returning images when requesting all coordinates, to reduce response size. Use [Get coordinate by identifier](#restful-coordinate-getcoordinate) if you want the image
```json
[
    {
        "coordinateId": 1,
        "latitude": 32.01623990507656,
        "longitude": 34.773109201554945,
        "locationName": "Holon Institute of Technology"
    }, 
    {
        "coordinateId": 2,
        "latitude": 32.015343027689276,
        "longitude": 34.770769562549276,
        "locationName": "Israeli Cartoon Museum"
    },
  ...
]
```

<a name="restful-coordinate-getcoordinate"></a>
#### Get Coordinate Info

Method: `GET`

Path: `https://HOST:PORT/coordinate/{coordinateId}` (Replace {coordinateId} with the coordinate identifier)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: Here you have the whole information, including image data.
```json
{
    "coordinateId": 1,
    "latitude": 32.01623990507656,
    "longitude": 34.773109201554945,
    "locationName": "Holon Institute of Technology",
    "image": "The byte array data here. I'd like to avoid of copying it again"
}
```

<a name="restful-coordinate-getbydist"></a>
#### Get all Coordinates within some range around specified point

Method: `GET`

Path: `https://HOST:PORT/coordinate/dist?lat={latValue}&lng={lngValue}&dist={distanceInKm}` (Note that dist param is optional. We will use 1KM by default.)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: Note that we avoid of returning images when requesting all coordinates, to reduce response size. Use [Get coordinate by identifier](#restful-coordinate-getcoordinate) if you want the image
```json
[
    {
        "coordinateId": 1,
        "latitude": 32.01623990507656,
        "longitude": 34.773109201554945,
        "locationName": "Holon Institute of Technology"
    }, 
    {
        "coordinateId": 2,
        "latitude": 32.015343027689276,
        "longitude": 34.770769562549276,
        "locationName": "Israeli Cartoon Museum"
    },
  ...
]
```

<a name="restful-story"></a>
### Story

<a name="restful-story-upload"></a>
#### Upload Story

Method: `POST`

Path: `https://HOST:PORT/story`

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: (Note that user and coordinate contain the identifiers only, and they must be existing at the server)
```json
{
	"storyId": null,
	"user": {
		"id": "haim@gmail.com"
	},
	"coordinate": {
		"coordinateId": 1202
	},
	"since": "2019-11-10",
	"heroName": "Chrissy Costanza",
	"title": "Phoenix",
	"content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
	"linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
}
```

Response: (The response will contain the new story identifier, and all of the information, including user (with up-to-date amount of coins) and coordinate info)
```json
{
    "storyId": 708,
    "user": {
        "id": "haim@gmail.com",
        "name": "Haim Adrian",
        "dateOfBirth": "1970-01-01",
        "coins": 2
    },
    "coordinate": {
        "coordinateId": 1202,
        "latitude": 32.01623990507656,
        "longitude": 34.773109201554945,
        "locationName": "Holon Institute of Technology",
        "image": "image data as byte array here"
    },
    "since": "2019-11-10",
    "heroName": "Chrissy Costanza",
    "title": "Phoenix",
    "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
    "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
}
```

<a name="restful-story-update"></a>
#### Update Story

Method: `POST`

Path: `https://HOST:PORT/story/{storyId}` (Replace {storyId} with story identifier)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body and Response are the same as for Upload Story, with only one difference. Body will contain a real story identifier and not null.

<a name="restful-story-getstory"></a>
#### Get Story info

Method: `GET`

Path: `https://HOST:PORT/story/{storyId}` (Replace {storyId} with story identifier. e.g. `708`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: Contains all of the information, including content and image.
```json
{
    "storyId": 708,
    "user": {
        "id": "haim@gmail.com",
        "name": "Haim Adrian",
        "dateOfBirth": "1970-01-01",
        "coins": 2
    },
    "coordinate": {
        "coordinateId": 1202,
        "latitude": 32.01623990507656,
        "longitude": 34.773109201554945,
        "locationName": "Holon Institute of Technology",
        "image": "image data as byte array here"
    },
    "since": "2019-11-10",
    "heroName": "Chrissy Costanza",
    "title": "Phoenix",
    "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
    "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
}
```

<a name="restful-story-getstorybyhero"></a>
#### Get Stories by Hero Name field

Method: `GET`

Path: `https://HOST:PORT/story/hero/{heroName}` (Replace {heroName} with the name of the hero. It does not have to be full name. e.g. `costanza`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: The result is a list of stories, without content/image! To reduce size. Use [Get story by identifier](#restful-story-getstory) to find the details.
```json
[
    {
        "storyId": 708,
        "user": {
            "id": "haim@gmail.com",
            "name": "Haim Adrian",
            "dateOfBirth": "1970-01-01",
            "coins": 2
        },
        "coordinate": {
            "coordinateId": 1202,
            "latitude": 32.01623990507656,
            "longitude": 34.773109201554945,
            "locationName": "Holon Institute of Technology",
            "image": "image data as byte array here"
        },
        "since": "2019-11-10",
        "heroName": "Chrissy Costanza",
        "title": "Phoenix",
        "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
        "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
    },
  ...
]
```

<a name="restful-story-getstorybytitle"></a>
#### Get Stories by Title field

Method: `GET`

Path: `https://HOST:PORT/story/title/{title}` (Replace {title} with the text to lookup for. It does not have to be full title. e.g. `pho`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: The result is a list of stories, without content/image! To reduce size. Use [Get story by identifier](#restful-story-getstory) to find the details.
```json
[
    {
        "storyId": 708,
        "user": {
            "id": "haim@gmail.com",
            "name": "Haim Adrian",
            "dateOfBirth": "1970-01-01",
            "coins": 2
        },
        "coordinate": {
            "coordinateId": 1202,
            "latitude": 32.01623990507656,
            "longitude": 34.773109201554945,
            "locationName": "Holon Institute of Technology",
            "image": "image data as byte array here"
        },
        "since": "2019-11-10",
        "heroName": "Chrissy Costanza",
        "title": "Phoenix",
        "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
        "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
    },
  ...
]
```

<a name="restful-story-getstorybyuser"></a>
#### Get Stories by UserId field

Method: `GET`

Path: `https://HOST:PORT/story/user/{userId}` (Replace {userId} with the user identifier to lookup for. It has to be the full user identifier. e.g. `haim@gmail.com`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: The result is a list of stories, without content/image! To reduce size. Use [Get story by identifier](#restful-story-getstory) to find the details.
```json
[
    {
        "storyId": 708,
        "user": {
            "id": "haim@gmail.com",
            "name": "Haim Adrian",
            "dateOfBirth": "1970-01-01",
            "coins": 2
        },
        "coordinate": {
            "coordinateId": 1202,
            "latitude": 32.01623990507656,
            "longitude": 34.773109201554945,
            "locationName": "Holon Institute of Technology",
            "image": "image data as byte array here"
        },
        "since": "2019-11-10",
        "heroName": "Chrissy Costanza",
        "title": "Phoenix",
        "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
        "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
    },
  ...
]
```

<a name="restful-story-getstorybylocation"></a>
#### Get Stories by location name field (from coordinate)

Method: `GET`

Path: `https://HOST:PORT/story/location/{locationName}` (Replace {locationName} with the name of the location to lookup for. It does not have to be the full location name. e.g. `holon`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: The result is a list of stories, without content/image! To reduce size. Use [Get story by identifier](#restful-story-getstory) to find the details.
```json
[
    {
        "storyId": 708,
        "user": {
            "id": "haim@gmail.com",
            "name": "Haim Adrian",
            "dateOfBirth": "1970-01-01",
            "coins": 2
        },
        "coordinate": {
            "coordinateId": 1202,
            "latitude": 32.01623990507656,
            "longitude": 34.773109201554945,
            "locationName": "Holon Institute of Technology",
            "image": "image data as byte array here"
        },
        "since": "2019-11-10",
        "heroName": "Chrissy Costanza",
        "title": "Phoenix",
        "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
        "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
    },
  ...
]
```

<a name="restful-story-getstorybycoordinate"></a>
#### Get Stories by coordinate identifier

Method: `GET`

Path: `https://HOST:PORT/story/coordinate/{coordinateId}` (Replace {coordinateId} with the identifier to lookup for. e.g. `1202`)

Header: `Authorization = Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWltIiwidXNlck5hbWUiOiJIYWltIEFkcmlhbiJ9.J28593Lq7IbO_Jvz4tK3GaP3K2FnSNqSq9O9SK2I3lA`

Body: Empty

Response: The result is a list of stories, without content/image! To reduce size. Use [Get story by identifier](#restful-story-getstory) to find the details.
```json
[
    {
        "storyId": 708,
        "user": {
            "id": "haim@gmail.com",
            "name": "Haim Adrian",
            "dateOfBirth": "1970-01-01",
            "coins": 2
        },
        "coordinate": {
            "coordinateId": 1202,
            "latitude": 32.01623990507656,
            "longitude": 34.773109201554945,
            "locationName": "Holon Institute of Technology",
            "image": "image data as byte array here"
        },
        "since": "2019-11-10",
        "heroName": "Chrissy Costanza",
        "title": "Phoenix",
        "content": "So are you gonna die today or make it out alive?\nYou gotta conquer the monster in your head and then you'll fly\nFly, phoenix, fly\nIt's time for a new empire\nGo bury your demons then tear down the ceiling\nPhoenix, fly",
        "linkToVideo": "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends"
    },
  ...
]
```
