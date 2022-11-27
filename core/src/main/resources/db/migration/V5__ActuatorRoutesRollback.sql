UPDATE route_path
SET method = 'NONE'
WHERE path = '/actuator/**';