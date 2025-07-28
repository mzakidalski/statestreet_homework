DELETE FROM RESERVATION;
DELETE FROM RESERVATION_USER;
DELETE FROM CAR;


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

COMMIT;

-- ----------------------------
-- Reservations
-- ----------------------------

INSERT INTO RESERVATION (ID, CAR_ID, USER_ID, START_TIME, END_TIME) VALUES (1,1, 1,'2025-07-28 11:00:00', '2025-07-29 11:00:00');
INSERT INTO RESERVATION (ID, CAR_ID, USER_ID, START_TIME, END_TIME) VALUES (2,2, 3,'2025-07-28 11:00:00', '2025-08-03 12:30:00');


--
ALTER TABLE reservation_user ALTER COLUMN id RESTART WITH 4;
ALTER TABLE CAR ALTER COLUMN id RESTART WITH 7;
ALTER TABLE RESERVATION ALTER COLUMN id RESTART WITH 3;