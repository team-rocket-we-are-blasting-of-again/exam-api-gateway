INSERT INTO gateway_route (request_path, forward_uri)
VALUES ('/actuator/**', 'INTERNAL');

INSERT INTO route_path (path, method, gateway_route_id)
VALUES ('/actuator/**', 'NONE', 2);

INSERT INTO route_path_app_role (route_path_id, app_role_id)
VALUES (4, 4);