# Overview and Requirements

This  project is a preliminary API service to support rental car booking. A typical user may send requests to

* query the status of car models available during a specified period (e.g., "what models are available from 2022-03-25 to 2022-03-28");
* add reservation: reserve a car for a specified period of time (e.g., "reserve a Toyota Camry from 2022-03-25 to 2022-03-28");

For simplicity, we make the following assumptions:

* each user is allowed to reserve one car at a time (in a single request);



# Major APIs

Here we sketch the major APIs with the key fields in the requests/responses.

### GET /availableModels

Request parameters: input the target period. 

* fromDate
  * (string, YYYY-DD-MM) the first day of reservation;
* toDate
  * (string, YYYY-DD-MM) the last day of reservation;

Example url: https://shary-carrental.azurewebsites.net/availableModels?fromDate=2022-03-25&toDate=2022-03-28

Response JSON: return the response status and a list of models available during the given period.

```json
{
    "status": "success",
    "data": {
        "availableModels": [
            {
                "modelName": "Toyota Camry",
                "numLeft": 2
            },
            {
                "modelName": "BMW 650",
                "numLeft": 2
            }
        ]
    }
}
```

* availableModels
  * (list of dictionaries) each dictionary in the list represents a model with the following attributes:
    * modelName
      * (string) the name of the model;
    * numLeft
      * (int) how many cars are left available within this period; ***This number should be greater than 0, since by definition, only available models will be returned;***

### POST /reserve

Request JSON: input the user id, the target model and the target period.

```json
{
    "modelId": 1,
    "fromDate": "2022-03-25",
    "toDate": "2022-03-28"
}
```
* modelId
  * (long) the unique model id;
* fromDate
  * (string, MM/DD/YYYY) the first day of reservation;
* toDate
  * (string, MM/DD/YYYY) the last day of reservation;

Response JSON: return the resposne status and related message; if the target model is available in the time slot, return success status; otherwise, return fail status;

```json
{
    "status": "fail",
    "message": "This car model is not available in this time slot."
}
{
  "status": "success",
  "message": "Reservation is successful."
}
```

* status
  * (string) "failed" or "successful". Can be replaced by a integer code;
* message
  * (string) detailed message
