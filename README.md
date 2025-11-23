# sample-rest-service
sample-rest-service for POC 

http://localhost:8080/rest-service/customer

{
"data": {
"type": "customers",
"attributes": {
"firstName": "John",
"lastName": "Doe",
"ssn": "111-22-3333",
"addresses": [
{
"street": "1 Main St",
"city": "Austin",
"state": "TX",
"postalCode": "73301"
}
],
"contacts": [
{
"type": "email",
"value": "john@example.com"
}
]
}
}
}