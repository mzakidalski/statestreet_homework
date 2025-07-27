-- ----------------------------
-- Populate reservation users
-- ----------------------------
INSERT INTO RESERVATION_USER (ID, NAME, SURNAME) VALUES (1, 'John',    'Doe');
INSERT INTO RESERVATION_USER (ID, NAME, SURNAME) VALUES (2, 'Jane',    'Smith');
INSERT INTO RESERVATION_USER (ID, NAME, SURNAME) VALUES (3, 'Carlos',  'Gonzalez');

-- ----------------------------
-- Populate cars (2 of each TYPE)
-- ----------------------------
-- Sedans
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (1, 'Toyota',  'Camry',   'SEDAN');
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (2, 'Honda',   'Accord',  'SEDAN');

-- SUVs
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (3, 'Ford',    'Explorer','SUV');
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (4, 'Jeep',    'Cherokee','SUV');

-- Vans
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (5, 'Honda',   'Odyssey', 'VAN');
INSERT INTO CAR (ID, MAKE, MODEL, TYPE) VALUES (6, 'Mercedes','Sprinter','VAN');

