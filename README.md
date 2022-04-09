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
