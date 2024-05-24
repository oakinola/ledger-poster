### Spring Boot Ledger Posting Application

## App summary

The project is a double-entry ledger account posting app. Each posting request has to transactions. One for the account to be credited and the other for the account to be debited

It is a simple account in that it only provides 2 rest calls
1. Post transactions to Ledgers
2. To get the balance by date for a particular account.

## Design and implementation
The app is designed and implemented to allow for heavy write loads and scalability.

I have employed CQRS with Event sourcing and some DDD concepts as well. The app uses KafkaEvents to propagate posting events to the `Read` side of the project

I also decide to use 2 databases to show better performance can be achieved. 
On the `Write` side I use a NoSQL db `(mongo)` as it allows for faster writes
And on the `Read` side I used a SQL based db `(postgres)` as `subjectively` they provide fast access.


## How to run.
I had a few challenges with getting the app to connect to mongo DB, and as I had ran out of time, I provided a workaround.

1. **Start the services by running docker-compose from the project root**
 ```docker-compose up ```
2. **Start the App after by running the `start_app.sh` script**
 ```./ledger-poster-app/start_app.sh``` from the project root OR
 ```./start_app.sh``` from the `ledger-poster-app` directory.

## Request samples
The following sample scripts serve as an example 
1. **GET call** ``` curl "http://localhost:8082/ledger-poster/v1/account-balance/1700?timestamp=2024-05-22T23:00:00" ```
2. **POST request** 
 ```
    curl -X POST http://localhost:8082/ledger-poster/v1/post-ledger -H "Content-Type: application/json" -d '[
    {
        "transactionAccount": {
          "accountNumber": 1000,
          "accountName": "Cash A/c"
        },
        "transactionAmount":1500,
        "transactionType": "CR",
        "description": "Purchase of machinery",
        "transactionTime":"2024-05-23T23:00:00"
      },
      {
        "transactionAccount": {
          "accountNumber": 1200,
          "accountName": "Machinery A/c"
        },
        "transactionAmount":1500,
        "transactionType": "DB",
        "description": "Purchase of machinery",
        "transactionTime":"2024-05-23T23:00:00"
      }
    ]'
 ```
3. **Swagger can be access from the links below**
  ``` http://localhost:8082/ledger-poster/swagger-ui/index.html ```
  ``` http://localhost:8082/ledger-poster/v3/api-docs ```

## Thoughts on the project
I would have really loved to do the following:

1. Have separate controllers: 1 to cater to all `commands` e.g. `LedgerPostingCommandController` and the other for `queries` i.e.`LedgerPostingQueryController`
 That would have enabled the system to be even more scalable as taking advantage of Springs `@ConditionalOnProperty` the app could be deployed several times across several nodes/regions and making them focused on Read or Write alone depending on the need. 
