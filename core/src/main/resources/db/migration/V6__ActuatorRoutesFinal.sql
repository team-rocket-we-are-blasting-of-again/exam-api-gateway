UPDATE route_path
SET method = 'BASIC'
WHERE path = '/actuator/**';