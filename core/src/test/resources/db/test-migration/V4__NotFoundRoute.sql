INSERT INTO gateway_route (request_path, forward_uri)
VALUES ('/customer/**', 'http://localhost:8768');

INSERT INTO route_path (path, method, gateway_route_id)
VALUES ('/customer/**', 'BEARER', 3);

INSERT INTO route_path_app_role (route_path_id, app_role_id)
VALUES (5, 1);
