# Sample REST API using Spring Boot, JPA and H2 in-memory database
This sample Srping Boot application provides a couple of REST endpoints to import Company/Dialogs/Threads information and query the imported data.

## Requirements

1. Maven
2. Java 8 or later.

## How to build

1. Clone the repository and go to the code directory
```
git clone https://github.com/dabreu/spring-boot-rest-api-example.git

cd spring-boot-rest-api-example
```

2. Build the application in a single jar using maven. This step will also run the tests
```
mvn clean install 
```

## How to run
To start the spring boot application run the command below, the HTTP server will be listening on port 8080
```
java -jar target/dialogs-0.0.1-SNAPSHOT.jar
```

## REST endpoints

### Import Companies and Dialogs
This endpoint allows to import a list of companies with their corresponding dialogs and threads. 
While importing the data, a couple of filters are applied to dialog's threads:
1. Remove those threads having duplicated Id (and store them on a conflict table)
2. Remove those threads having duplicated Text, only keep the first one

Under the test/resources folder it can be found a sample JSON file with test data.

#### Example of invocation
```
curl -X POST http://localhost:8080/company/import -H "Content-Type: application/json" -d "@src/test/resources/sample.json" 
```

#### Sample Data
```json
[
  {
    "id": 1901,
    "name": "Company name",
    "signedUp": "2018-07-04T20:21:18",
    "dialogs": [
      {
        "id": 1405,
        "number": 1505,
        "userId": 322,
        "from": "somebody@mail.com",
        "received": "2019-08-04T13:52:22",
        "threads": [
          {
            "id": 4726,
            "conversationId": 350,
            "payload": "Hello world"
          }
        ]
      }
    ]
  },
  {
    "id": 1200,
    "name": "Another Company name",
    "signedUp": "2017-09-09T22:25:18",
    "dialogs": []
  }
]
```

### Get Company information
This endpoint allows to get the summary information of a Company given its ID (the one passed on the JSON data imported). It returns the company name, the number of dialogs 
imported and its most popular customer, a.k.a. the one with more occurrences within the dialogs.

#### Example of invocation
```
curl -X GET http://localhost:8080/company/info/1901
```

#### Sample Response Data
```json
{
  "name": "Company name",
  "dialogsCount": 1,
  "mostPopularCustomer": 322
}
```

### Get Dialog information
This endpoint allows to get the information of a Dialog, and its corresponding Threads, given its ID (the one passed on the JSON data imported). It returns the transcript for 
the dialog, including all of its threads
```
curl -X GET http://localhost:8080/dialog/info/1405
```

#### Sample Response Data
```json
{
  "id": 1405,
  "transcript": [
    {
      "order": 1,
      "text": "Hello world"
    }
  ]
}
```
