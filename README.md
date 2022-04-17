# Anti-Fraud System
JetBrains Academy. Project: Anti-Fraud System.</br></br>

In the fourth stage enabled our anti-fraud system to retrieve a list of</br>
prohibited card numbers and suspicious IP addresses to ban them</br>
from carrying out any transactions.

Implemented the role model for system:</br>
<table>
<tr>
    <th></th>
    <th>Anonymous</th>
    <th>MERCHANT</th>
    <th>ADMINISTRATOR</th>
    <th>SUPPORT</th>
</tr>
<tr>
    <td>POST /api/auth/user</td>
    <td>+</td>
    <td>+</td>
    <td>+</td>
    <td>+</td>
</tr>
<tr>
    <td>DELETE /api/auth/user</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>GET /api/auth/list</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>+</td>
</tr>
<tr>
    <td>POST /api/antifraud/transaction</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td>POST /api/auth/access</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>POST /api/auth/role</td>
    <td>-</td>
    <td>-</td>
    <td>+</td>
    <td>-</td>
</tr>
<tr>
    <td>POST, DELETE, GET api/antifraud/suspicious-ip</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
</tr>
<tr>
    <td>POST, DELETE, GET api/antifraud/stolencard</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
</tr>
</table>

ADMINISTRATOR is the user who has registered first, all other users</br>
should receive the MERCHANT roles. All users added after ADMINISTRATOR</br>
must be locked by default and unlocked later by ADMINISTRATOR.</br>
The SUPPORT role should be assigned by ADMINISTRATOR to one of</br>
the users later.

In our service, we will check IP addresses for compliance with IPv4.</br>
Any address following this format consists of four series of numbers</br>
from 0 to 255 separated by dots.

Card numbers must be checked according to the Luhn algorithm.