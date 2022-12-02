INSERT INTO app_role (role) VALUES ('CUSTOMER');
INSERT INTO app_role (role) VALUES ('RESTAURANT');
INSERT INTO app_role (role) VALUES ('COURIER');
INSERT INTO app_role (role) VALUES ('MANAGEMENT');

INSERT INTO gateway_route (request_path, forward_uri)
VALUES ('/gateway/**', 'INTERNAL');

INSERT INTO route_path (path, method, gateway_route_id)
VALUES ('/gateway/ensure-route', 'BASIC', 1);

INSERT INTO route_path_app_role (route_path_id, app_role_id)
VALUES (1, 4);

INSERT INTO route_path (path, method, gateway_route_id)
VALUES ('/gateway/route', 'BASIC', 1);

INSERT INTO route_path_app_role (route_path_id, app_role_id)
VALUES (2, 4);

INSERT INTO route_path (path, method, gateway_route_id)
VALUES ('/gateway/fallback/catch-all-fallback', 'NONE', 1);

INSERT INTO route_path_app_role (route_path_id, app_role_id)
VALUES (3, 4)
