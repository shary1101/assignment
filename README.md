# Overview and Requirements

This project is a preliminary API service to support rental car booking. A typical user may send requests to

* query the status of car models available during a specified period (e.g., "what models are available from 2022-03-25 to 2022-03-28");
* add reservation: reserve a car for a specified period of time (e.g., "reserve a Toyota Camry from 2022-03-25 to 2022-03-28");

For simplicity, we make the following assumptions:

* each user is allowed to reserve one car at a time (in a single request);


# API Specs

### GET cars/availability 
get all available models within the target period

Request parameters: input the target period. 

* fromDate
  * (string, YYYY-MM-DD) the first day of reservation;
* toDate
  * (string, YYYY-MM-DD) the last day of reservation;

Example url: https://shary-carrental.azurewebsites.net/availableModels?fromDate=2022-03-25&toDate=2022-03-28

Response JSON: return the response status and a list of models available during the given period.

```json
{
    "code": 200,
    "data": {
        "availableModels": [
            {
                "modelId": 1,
                "modelName": "Toyota Camry",
                "numLeft": 2
            },
            {
                "modelId": 2,
                "modelName": "BMW 650",
                "numLeft": 2
            }
        ]
    }
}
{
    "code": 400,
    "message": "The input param 'fromDate' should be no later than 'toDate'."
}
```
* code
  * (int) the status code. 200 is SUCCESS, 400 is FAILED, 500 is INTERNAL_SERVER_ERROR;
* message
  * (string) the detailed message
* availableModels
  * (list of dictionaries) each dictionary in the list represents a model with the following attributes:
    * modelId
      * (long) the unique id of the model;
    * modelName
      * (string) the name of the model;
    * numLeft
      * (int) how many cars are left available within this period; ***This number should be greater than 0, since by definition, only available models will be returned;***

### POST cars/reservation
add a car reservation

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
  * (string, YYYY-MM-DD) the first day of reservation;
* toDate
  * (string, YYYY-MM-DD) the last day of reservation;

Response JSON: return the resposne status and related message; if the target model is available in the time slot, return success status; otherwise, return fail status;

```json
{
    "code": 200,
    "message": "Reservation is successful."
}
{
    "code": 400,
    "message": "This car model is not available in this time slot."
}
```

* code
  * (int) the status code. 200 is SUCCESS, 400 is FAILED, 500 is INTERNAL_SERVER_ERROR;
* message
  * (string) the detailed message

# Swagger url
You can try the API calls directly in the browser with the interactive API documentation.

https://shary-carrental.azurewebsites.net/swagger-ui.html

