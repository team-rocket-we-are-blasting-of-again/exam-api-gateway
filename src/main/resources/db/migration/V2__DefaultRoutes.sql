INSERT INTO app_role (id, role) VALUES (1, 'CUSTOMER');
INSERT INTO app_role (id, role) VALUES (2, 'RESTAURANT');
INSERT INTO app_role (id, role) VALUES (3, 'COURIER');
INSERT INTO app_role (id, role) VALUES (4, 'MANAGEMENT');

INSERT INTO gateway_route (id, request_path, forward_uri)
VALUES (1, '/gateway/**', 'INTERNAL');

INSERT INTO route_path (id, path, method, gateway_route_id)
VALUES (1, '/gateway/ensure-route', 'BASIC', 1);

INSERT INTO route_path_app_role (id, route_path_id, app_role_id)
VALUES (1, 1, 4);

INSERT INTO route_path (id, path, method, gateway_route_id)
VALUES (2, '/gateway/route', 'BASIC', 1);

INSERT INTO route_path_app_role (id, route_path_id, app_role_id)
VALUES (2, 2, 4);

INSERT INTO route_path (id, path, method, gateway_route_id)
VALUES (3, '/gateway/fallback/catch-all-fallback', 'NONE', 1);

INSERT INTO route_path_app_role (id, route_path_id, app_role_id)
VALUES (3, 3, 4)
