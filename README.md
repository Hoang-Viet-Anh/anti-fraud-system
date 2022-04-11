# Anti-Fraud System
JetBrains Academy. Project: Anti-Fraud System.</br></br>

In the first stage, created a simple rest endpoint that calculates whether</br>
a transaction is ALLOWED, PROHIBITED, or requires MANUAL_PROCESSING</br>
by evaluating the amount of the transaction.

1.Transactions with a sum of lower or equal to 200 are ALLOWED;</br>
2.Transactions with a sum of greater than 200 but lower or equal than 1500</br>
require MANUAL_PROCESSING;</br>
3.Transactions with a sum of greater than 1500 are PROHIBITED.</br>
4.The transaction amount must be greater than 0.</br>
5.If the validation process was successful, the endpoint should respond with</br>
the status HTTP OK (200) and return the following JSON:</br>
{
"result": "String"
}</br>
6.In case of wrong data in the request, the endpoint should respond with the</br>
status HTTP Bad Request (400).

In second stage, provided the HTTP Basic authentication for our REST</br>
service with the JDBC implementations of UserDetailService for user</br>
management. Created an endpoint for registering users at POST /api/auth/user.

Added the POST /api/auth/user endpoint. In this stage, It must be available</br>
to unauthorized users for registration nd accept data in the JSON format:

{</br>
"name": "<String value, not empty>",</br>
"username": "<String value, not empty>",</br>
"password": "<String value, not empty>"</br>
}

If a user has been successfully added, the endpoint must respond with the</br>
HTTP CREATED status (201) and the following body:

{</br>
"id": <Long value, not empty>,</br>
"name": "<String value, not empty>",</br>
"username": "<String value, not empty>"</br>
}

If an attempt to register an existing user was a failure, the endpoint must</br>
respond with the HTTP CONFLICT status (409);

If a request contains wrong data, the endpoint must respond with the BAD</br>
REQUEST status (400);

Add the GET /api/auth/list endpoint. It must be available to all authorized users;

The endpoint must respond with the HTTP OK status (200) and the body with an</br>
array of objects representing the users sorted by ID in ascending order. Return</br>
an empty JSON array if there's no information:

[</br>
{</br>
"id": <user1 id>,</br>
"name": "<user1 name>",</br>
"username": "<user1 username>"</br>
},</br>
...</br>
{</br>
"id": <userN id>,</br>
"name": "<userN name>",</br>
"username": "<userN username>"</br>
}</br>
]

Add the DELETE /api/auth/user/{username} endpoint, where {username}</br>
specifies the user that should be deleted. The endpoint must be available to all</br>
authorized users. The endpoint must delete the user and respond with the HTTP</br>
OK status (200) and the following body:

{</br>
"username": "<username>",</br>
"status": "Deleted successfully!"</br>
}

If a user is not found, respond with the HTTP Not Found status (404);

Change the POST /api/antifraud/transaction endpoint; it must be available only</br>
to all authorized users.