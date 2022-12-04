CREATE TABLE IF NOT EXISTS gateway_route (
    id BIGSERIAL PRIMARY KEY,
    request_path VARCHAR NOT NULL UNIQUE,
    forward_uri VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS route_path (
    id BIGSERIAL PRIMARY KEY,
    path VARCHAR NOT NULL,
    method VARCHAR(25) NOT NULL,
    gateway_route_id BIGSERIAL NOT NULL,
    FOREIGN KEY (gateway_route_id) REFERENCES gateway_route (id)
);

CREATE TABLE IF NOT EXISTS app_role (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS route_path_app_role (
    id BIGSERIAL PRIMARY KEY,
    route_path_id BIGSERIAL NOT NULL,
    app_role_id BIGSERIAL NOT NULL,
    FOREIGN KEY (route_path_id) REFERENCES route_path (id) ON DELETE CASCADE,
    FOREIGN KEY (app_role_id) REFERENCES app_role (id)
);

